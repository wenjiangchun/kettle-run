package com.bkht.kettle.job;

import com.bkht.kettle.KettleLog;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.TransLogTable;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class KettleRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunner.class);

    private  static KettleDatabaseRepository repository;

    public static KettleDatabaseRepository getConnection() {
        if (repository == null || !repository.isConnected()) {
            //链接资源库
            try {
                KettleEnvironment.init();
                //数据库连接元对象
                DatabaseMeta dataMeta = new DatabaseMeta("ETL", "MYSQL", "Native(JDBC)", "188.9.25.151", "kettle", "3306", "root", "1234");
                dataMeta.setUsingConnectionPool(true);
                Properties p = new Properties();

                p.setProperty("useSSL", "false");
                p.setProperty("autoReconnectForPools", "true");
                p.setProperty("autoReconnect", "true");
                p.setProperty("validationQuery", "select 1");
                p.setProperty("testOnBorrow", "true");
                dataMeta.setConnectionPoolingProperties(p);
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

    public static void close() {
        if (repository != null) {
            repository.disconnect();
            repository = null;
        }
    }

    public static void runJob(KettleDatabaseRepository rep, String jobName) {

        try {
            RepositoryDirectoryInterface dir = rep.findDirectory("/publish/job");//根据指定的字符串路径 找到目录
            //加载指定的job
            JobMeta jobMeta = rep.loadJob(rep.getJobId(jobName, dir), null);
            Job job = new Job(rep, jobMeta);
            //设置参数
            jobMeta.setParameterValue("method", "update");
            jobMeta.setParameterValue("tsm5", "07bb40f7200448b3a544786dc5e28845");

            job.setLogLevel(LogLevel.ERROR);
            //启动执行指定的job
            job.run();
            job.waitUntilFinished();//等待job执行完；
            job.setFinished(true);
            System.out.println(job.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*public static void runTrans(String transName, String startDate, String endDate) {
        //getConnection().findDirectory("/").getChildren().get(0).
        try {
            RepositoryDirectoryInterface dir = getConnection().findDirectory("/预现售相关");//根据指定的字符串路径 找到目录
            TransMeta tmeta = repository.loadTransformation(repository.getTransformationID(transName, dir), null);
            //设置参数
            //tmeta.setParameterValue("", "");
            Trans trans = new Trans(tmeta);
            trans.setVariable("ct.core.password", "fbtest");
            trans.setVariable("ct.core.username", "REG_CORE");
            trans.setVariable("ct.dict.password", "fbtest");
            trans.setVariable("ct.dict.username", "REG_DICT");
            trans.setVariable("ct.ip", "192.168.0.175");
            trans.setVariable("ct.port", "1521");
            trans.setVariable("ct.sid", "xmbdcdg");
            trans.setVariable("db.sid", "orcl");
            trans.setVariable("db.username", "bkht");
            trans.setVariable("db.password", "123456");
            trans.setVariable("db.ip", "188.9.25.151");
            trans.setVariable("startDate", startDate);
            trans.setVariable("endDate", endDate);
            trans.execute(null);//执行trans
            trans.waitUntilFinished();
            if (trans.getErrors() > 0) {
                LOGGER.warn("执行异常～～～～～～～～～～～～～～～～～～～～～～～：有异常"+ ",转换名称=" + transName);
            }
            LOGGER.debug("执行成功===========================================：startDate = " + startDate + "," + "endDate = " + endDate + ",转换名称=" + transName);
        } catch (Exception e) {
            LOGGER.debug("执行失败-------------------------------------------：startDate = " + startDate + "," + "endDate = " + endDate + ",转换名称=" + transName);
            e.printStackTrace();
        }
    }*/

    public List<RepositoryElementMetaInterface> getAllTrans(String directory) throws KettleException {
        List<RepositoryElementMetaInterface> elements = new ArrayList<>();
        if (directory == null) {
            directory = "/";
        }
        RepositoryDirectoryInterface rootDirectory = getConnection().findDirectory(directory);
        for (RepositoryDirectoryInterface f : rootDirectory.getChildren()) {
            String[] transNames = repository.getTransformationNames(f.getObjectId(),false);
            /*for (String transName : transNames) {
                TransMeta tmeta = repository.loadTransformation(repository.getTransformationID(transName, f), null);
                Trans tran = new Trans(tmeta);
                trans.add(tran);
            }*/

            elements.addAll(repository.getJobAndTransformationObjects(f.getObjectId(), true));
            if (!f.getChildren().isEmpty()) {
                elements.addAll(getAllTrans(f.getName()));
            }
        }
        return elements;
    }



    /*public static void runTrans(String transName, Map<String, String> variables) {
        //getConnection().findDirectory("/").getChildren().get(0).
        try {
            RepositoryDirectoryInterface dir = getConnection().findDirectory("/城镇不动产相关/楼市相关");//根据指定的字符串路径 找到目录
            TransMeta tmeta = repository.loadTransformation(repository.getTransformationID(transName, dir), null);
            //设置参数
            //tmeta.setParameterValue("", "");
            Trans trans = new Trans(tmeta);
            trans.setVariable("ct.core.password", "fbtest");
            trans.setVariable("ct.core.username", "REG_CORE");
            trans.setVariable("ct.dict.password", "fbtest");
            trans.setVariable("ct.dict.username", "REG_DICT");
            trans.setVariable("ct.ip", "192.168.0.175");
            trans.setVariable("ct.port", "1521");
            trans.setVariable("ct.sid", "xmbdcdg");
            trans.setVariable("db.sid", "orcl");
            trans.setVariable("db.username", "bkht");
            trans.setVariable("db.password", "123456");
            trans.setVariable("db.ip", "188.9.25.151");
            variables.forEach(trans::setVariable);
            trans.execute(null);//执行trans
            trans.waitUntilFinished();
            trans.setLogLevel(LogLevel.BASIC);
            if (trans.getErrors() > 0) {
                LOGGER.warn("执行异常～～～～～～～～～～～～～～～～～～～～～～～：有异常" + ",转换名称=" + transName);
            }
            LOGGER.debug("执行成功=========================================== ");
        } catch (Exception e) {
            LOGGER.debug("执行失败------------------------------------------- ");
            e.printStackTrace();
        }
    }*/

    public TransMeta getTransMetaByObjectId(String objectId) throws KettleException {
        Assert.notNull(objectId, "转换ID不能为空");
        return getConnection().loadTransformation(new StringObjectId(objectId), null);
    }

    public JobMeta getJobMetaByObjectId(String objectId) throws KettleException {
        Assert.notNull(objectId, "作业ID不能为空");
        return getConnection().loadJob(new StringObjectId(objectId), null);
    }

    public KettleLog runTrans(String transObjectId, Map<String, String> parameters) throws KettleException, SQLException {
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

        trans.setVariable("ct.core.password", "fbtest");
        trans.setVariable("ct.core.username", "REG_CORE");
        trans.setVariable("ct.dict.password", "fbtest");
        trans.setVariable("ct.dict.username", "REG_DICT");
        trans.setVariable("ct.ip", "192.168.0.175");
        trans.setVariable("ct.port", "1521");
        trans.setVariable("ct.sid", "xmbdcdg");
        trans.setVariable("db.sid", "orcl");
        trans.setVariable("db.username", "bkht");
        trans.setVariable("db.password", "123456");
        trans.setVariable("db.ip", "188.9.25.151");
        trans.setVariable("ct.otp.username", "ES_OTP");
        trans.setVariable("ct.otp.password", "fbtest");
        trans.setLogLevel(LogLevel.BASIC);
        trans.execute(null);//执行trans
        trans.waitUntilFinished();
        KettleLog kettleLog = new KettleLog();
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
        close();
        return kettleLog;
    }

    @CacheEvict(value="getAllTrans", allEntries=true)
    public void clearCache() {
         LOGGER.debug("清空缓存");
    }

    public Object getTransMetaLogByObjectId(String objectId) throws KettleException {
        Assert.notNull(objectId, "转换ID不能为空");
        TransMeta transMeta = getConnection().loadTransformation(new StringObjectId(objectId), null);
        return null;
    }


    public List<KettleLog> getTransMetaLogs(String objectId) throws KettleException, SQLException {
        List<KettleLog> logList = new ArrayList<>();
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
                KettleLog log = new KettleLog();
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
}