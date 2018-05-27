package com.bkht.kettle.job;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class JobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

    private static KettleDatabaseRepository repository;

    public static KettleDatabaseRepository getConnection() {
        if (repository == null) {
            //链接资源库
            try {
                KettleEnvironment.init();
                //数据库连接元对象
                DatabaseMeta dataMeta = new DatabaseMeta("ETL", "MYSQL", "Native(JDBC)", "188.9.25.151", "kettle", "3306", "root", "1234");
                //数据库形式的资源库元对象
                KettleDatabaseRepositoryMeta repInfo = new KettleDatabaseRepositoryMeta();
                repInfo.setConnection(dataMeta);
                //数据库形式的资源库对象
                repository = new KettleDatabaseRepository();
                repository.init(repInfo);
                repository.connect("admin", "admin");
                if (repository.isConnected()) {
                    LOGGER.debug("连接成功");
                } else {
                    LOGGER.error("连接失败");
                }
            } catch (KettleException e) {
                e.printStackTrace();
            }
        }
        return repository;
    }

    public static void close() {
        if (repository != null) {
            repository.disconnect();
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


    public static void runTrans(String transName, String startDate, String endDate) {
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
    }

    public static List<Trans> getAllTrans(String directory) throws KettleException {
        List<Trans> trans = new ArrayList<>();
        if (directory == null) {
            directory = "/";
        }
        RepositoryDirectoryInterface rootDirectory = getConnection().findDirectory(directory);
        /*for (ObjectId objectId : rootDirectory.getDirectoryIDs()) {
            LOGGER.debug(objectId.getId());
        }*/

        for (RepositoryDirectoryInterface f : rootDirectory.getChildren()) {

            String[] transNames = repository.getTransformationNames(f.getObjectId(),false);
            for (String transName : transNames) {
                TransMeta tmeta = repository.loadTransformation(repository.getTransformationID(transName, f), null);
                Trans tran = new Trans(tmeta);
                trans.add(tran);
            }
            if (!f.getChildren().isEmpty()) {
                trans.addAll(getAllTrans(f.getName()));
            }
        }
        return trans;
    }
}