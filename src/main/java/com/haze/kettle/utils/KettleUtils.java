package com.haze.kettle.utils;

import com.haze.kettle.KLog;
import com.haze.kettle.Step;
import com.haze.kettle.StepFlow;
import com.haze.kettle.StepWrapper;
import com.haze.kettle.config.KettleConfig;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.TransLogTable;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.switchcase.SwitchCaseMeta;
import org.pentaho.di.trans.steps.switchcase.SwitchCaseTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class KettleUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleUtils.class);

    private KettleDatabaseRepository repository;

    @Autowired
    private KettleConfig kettleConfig;


    public KettleDatabaseRepository getConnection() {
        if (repository == null || !repository.isConnected()) {
            //链接资源库
            try {
                KettleEnvironment.init();
                //数据库连接元对象
                DatabaseMeta dataMeta = new DatabaseMeta("ETL", kettleConfig.getRepository().getType(), "Native(JDBC)", kettleConfig.getRepository().getUrl(), kettleConfig.getRepository().getSchema(), kettleConfig.getRepository().getPort(), kettleConfig.getRepository().getUsername(), kettleConfig.getRepository().getPassword());
                dataMeta.setUsingConnectionPool(false);
                /*Properties p = new Properties();
                p.setProperty("validationQuery", "select 1 from dual");
                p.setProperty("testOnBorrow", "true");
                p.setProperty("maxIdle", "60000");
                p.setProperty("maxActive", "10");
                p.setProperty("maxWait", "60000");
                dataMeta.setConnectionPoolingProperties(p);*/
                //数据库形式的资源库元对象
                KettleDatabaseRepositoryMeta repInfo = new KettleDatabaseRepositoryMeta();
                repInfo.setConnection(dataMeta);
                repInfo.setName("kettle资源库--厦门不动产业务监管");
                //数据库形式的资源库对象
                repository = new KettleDatabaseRepository();
                repository.init(repInfo);
                repository.connect("admin", "admin", true);
                if (repository.isConnected()) {
                    LOGGER.debug("kettle资源库连接成功");
                } else {
                    LOGGER.error("kettle资源库连接失败");
                }
            } catch (KettleException e) {
                LOGGER.error("kettle资源库连接异常", e);
                e.printStackTrace();
            }
        }
        return repository;
    }

    public void close() {
        if (repository != null) {
            repository.disconnect();
            repository = null;
        }
    }


    public List<RepositoryElementMetaInterface> getAllTrans(String directory) throws KettleException {

        List<RepositoryElementMetaInterface> elements = new ArrayList<>();
        if (directory == null) {
            directory = "";
        }
        RepositoryDirectoryInterface rootDirectory = getConnection().findDirectory(directory);
        if (directory .equals("/")) {
            elements.addAll(repository.getTransformationObjects(rootDirectory.getObjectId(), true));
        }
        for (RepositoryDirectoryInterface f : rootDirectory.getChildren()) {
            elements.addAll(repository.getTransformationObjects(f.getObjectId(), true));
            if (!f.getChildren().isEmpty()) {
                elements.addAll(getAllTrans(f.getPath()));
            }
        }
        return elements;
    }

    public TransMeta getTransMetaByObjectId(String objectId) throws KettleException {
        Assert.notNull(objectId, "转换ID不能为空");
        TransMeta transMeta = getConnection().loadTransformation(new StringObjectId(objectId), null);
        LOGGER.debug(transMeta.getXML());
        for (String stepName : getConnection().loadTransformation(new StringObjectId(objectId), null).getStepNames()) {
            LOGGER.debug(stepName);
        }
        return getConnection().loadTransformation(new StringObjectId(objectId), null);
    }

    public JobMeta getJobMetaByObjectId(String objectId) throws KettleException {
        Assert.notNull(objectId, "作业ID不能为空");
        return getConnection().loadJob(new StringObjectId(objectId), null);
    }

    public KLog runTrans(String transObjectId, Map<String, String> parameters) throws KettleException, SQLException {
        Assert.notNull(transObjectId, "转换ID不能为空");
        TransMeta transMeta = getConnection().loadTransformation(new StringObjectId(transObjectId), null);
        if (transMeta == null) {
            throw new KettleException("转换不存在或已删除");
        }

        Trans trans = new Trans(transMeta);
        parameters.forEach((k, v) -> {
            try {
                trans.setParameterValue(k, v);
            } catch (UnknownParamException e) {
                e.printStackTrace();
            }
        });
        setVariables(trans, kettleConfig.getParams());
        trans.setLogLevel(LogLevel.BASIC);
        trans.execute(null);//执行trans
        trans.waitUntilFinished();
        KLog kettleLog = new KLog();
        kettleLog.setStartDate(trans.getStartDate());
        kettleLog.setEndDate(trans.getEndDate());
        if (trans.getErrors() > 0) {
            LOGGER.warn("kettle转换执行异常, 转换名称={}, 转换ID={}", trans.getName(), transObjectId);
            kettleLog.setSuccess(false);
            kettleLog.setErrors(trans.getErrors());
            throw new KettleException("kettle转换执行异常");
        } else {
            kettleLog.setSuccess(true);
            kettleLog.setErrors(0);
            LOGGER.debug("kettle转换执行成功, 转换名称={}, 转换ID={}", trans.getName(), transObjectId);
        }

        TransLogTable logTable = transMeta.getTransLogTable();
        if (logTable != null && logTable.getDatabaseMeta() != null) {
            DatabaseMeta dataBaseMeta = logTable.getDatabaseMeta();
            Database dataBase = new Database(transMeta, dataBaseMeta);
            dataBase.setVariable("db.sid", "orcl");
            dataBase.setVariable("db.username", "bkht");
            dataBase.setVariable("db.password", "123456");
            dataBase.setVariable("db.ip", "188.9.25.151");
            String tableName = logTable.getTableName();
            String key = logTable.getKeyField().getFieldName();
            LOGGER.debug(key);
            dataBase.connect();
            ResultSet result = dataBase.openQuery("select * from " + tableName + " where " + key + "=" + trans.getBatchId());
            while (result.next()) {
                kettleLog.setId(trans.getBatchId());
                kettleLog.setContent(result.getString(logTable.getLogField().getFieldName()));
                kettleLog.setName(result.getString(logTable.getNameField().getFieldName()));
            }
            dataBase.disconnect();
        }
        //close();
        return kettleLog;
    }


    public Object getTransMetaLogByObjectId(String objectId) throws KettleException {
        Assert.notNull(objectId, "转换ID不能为空");
        TransMeta transMeta = getConnection().loadTransformation(new StringObjectId(objectId), null);
        return null;
    }


    public List<KLog> getTransMetaLogs(String objectId) throws KettleException, SQLException {
        List<KLog> logList = new ArrayList<>();
        TransMeta transMeta = getTransMetaByObjectId(objectId);
        TransLogTable logTable = transMeta.getTransLogTable();
        if (logTable != null) {
            DatabaseMeta dataBaseMeta = logTable.getDatabaseMeta();
            Database dataBase = new Database(transMeta, dataBaseMeta);
            dataBase.setVariable("db.sid", "orcl");
            dataBase.setVariable("db.username", "bkht");
            dataBase.setVariable("db.password", "123456");
            dataBase.setVariable("db.ip", "188.9.25.151");
            String tableName = logTable.getTableName();
            String key = logTable.getKeyField().getFieldName();
            dataBase.connect();
            ResultSet result = dataBase.openQuery("select * from " + tableName + " where " + logTable.getNameField().getFieldName() + "= '" + transMeta.getName() + "' order by " + key + " asc ");
            while (result.next()) {
                KLog log = new KLog();
                log.setId(result.getLong(key));
                log.setContent(result.getString(logTable.getLogField().getFieldName()));
                log.setName(result.getString(logTable.getNameField().getFieldName()));
                log.setStartDate(result.getTimestamp("startDate"));
                log.setEndDate(result.getTimestamp("endDate"));
                log.setStatus(result.getString(logTable.getStatusField().getFieldName()));
                int errors = result.getInt(logTable.getErrorsField().getFieldName());
                boolean success = errors == 0;
                log.setSuccess(success);
                log.setErrors(errors);
                logList.add(log);
            }
            result.close();
            dataBase.disconnect();
        }
        close();
        return logList;
    }

    public void setVariables(Trans trans, Map<String, String> variables) {
        variables.forEach(trans::setVariable);
    }



    public void runJob(String jobObjectId, Map<String, String> parameters) {
        Assert.notNull(jobObjectId, "作业ID不能为空");
        try {
            //加载指定的job
            JobMeta jobMeta = getConnection().loadJob(new StringObjectId(jobObjectId), null);
            Job job = new Job(repository, jobMeta);
            parameters.forEach((k, v) -> {
                try {
                    jobMeta.setParameterValue(k, v);
                } catch (UnknownParamException e) {
                    e.printStackTrace();
                }
            });
            kettleConfig.getParams().forEach(job::setVariable);
            job.setLogLevel(LogLevel.BASIC);
            //启动执行指定的job
            job.run();
            job.waitUntilFinished();//等待job执行完；
            job.setFinished(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<RepositoryElementMetaInterface> getAllJobs(String directory) throws KettleException {
        List<RepositoryElementMetaInterface> elements = new ArrayList<>();
        if (directory == null) {
            directory = "/";
        }
        RepositoryDirectoryInterface rootDirectory = getConnection().findDirectory(directory);
        for (RepositoryDirectoryInterface f : rootDirectory.getChildren()) {
            elements.addAll(repository.getJobObjects(f.getObjectId(), true));
            if (!f.getChildren().isEmpty()) {
                elements.addAll(getAllJobs(f.getName()));
            }
        }
        return elements;
    }

    public StepWrapper getStepWrapper(String objectId) throws KettleException {
        TransMeta transMeta = getConnection().loadTransformation(new StringObjectId(objectId), null);
        List<StepMeta> stepMetaList = transMeta.getSteps();
        StepWrapper stepWrapper = new StepWrapper();
        List<TransHopMeta> transHopMetaList = transMeta.getTransHops();
        for (StepMeta stepMeta : stepMetaList) {
            Step step = new Step();
            step.setName(stepMeta.getName());
            step.setX(stepMeta.getLocation().x);
            step.setY(stepMeta.getLocation().y);
            step.setType(stepMeta.getTypeId());
            step.setId(stepMeta.getObjectId().getId());
            stepWrapper.getStepList().add(step);
        }
        for (TransHopMeta transHopMeta : transHopMetaList) {
            StepFlow stepFlow = new StepFlow();
            String fromId = transHopMeta.getFromStep().getObjectId().getId();
            String toId = transHopMeta.getToStep().getObjectId().getId();
            for (Step step : stepWrapper.getStepList()) {
                if (step.getId().equals(fromId)) {
                    stepFlow.setFrom(step);
                    if (step.getType().equals("SwitchCase")) {
                        SwitchCaseMeta switchCaseMeta = (SwitchCaseMeta) transHopMeta.getFromStep().getStepMetaInterface();
                        //switchCaseMeta.
                        String fieldName = switchCaseMeta.getFieldname();
                        for (SwitchCaseTarget caseTarget : switchCaseMeta.getCaseTargets()) {
                            if (caseTarget.caseTargetStep.getObjectId().getId().equals(toId)) {
                                stepFlow.setLabel(fieldName + "=" + caseTarget.caseValue);
                                break;
                            }
                        }
                        if (switchCaseMeta.getDefaultTargetStep().getObjectId().getId().equals(toId)) {
                            stepFlow.setLabel("默认步骤" );
                        }
                    }
                }
                if (step.getId().equals(toId)) {
                    stepFlow.setTo(step);
                }
            }

            stepWrapper.getStepFlowList().add(stepFlow);
        }
        return stepWrapper;
    }
}