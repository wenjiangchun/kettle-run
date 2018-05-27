package com.bkht.kettle;

import com.bkht.kettle.job.JobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KettleRunApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunApplication.class);

    public static void main(String[] args) {
        org.springframework.context.ApplicationContext ctx = SpringApplication.run(KettleRunApplication.class, args);

        String transName = ctx.getBean(KettleConfig.class).transName;
        //JobRunner.runTrans("产权信息抽取转换(明细)", "",  "");

        for (int startYear = 1970; startYear < 2019; startYear ++) {
            /*if (startYear == 2015) {
                LOGGER.debug("开始执行：startDate = " + startYear + "-07" + "-01" + "," + "endDate = " + startYear + "-12" + "-31");
                JobRunner.runTrans(transName, startYear + "-07" + "-01",  startYear + "-12" + "-31");
            } else {*/
                LOGGER.debug("开始执行：startDate = " + startYear + "-01" + "-01" + "," + "endDate = " + startYear + "-06" + "-30");
                JobRunner.runTrans(transName, startYear + "-01" + "-01",  startYear + "-06" + "-30");

                LOGGER.debug("开始执行：startDate = " + startYear + "-07" + "-01" + "," + "endDate = " + startYear + "-12" + "-31");
                JobRunner.runTrans(transName, startYear + "-07" + "-01",  startYear + "-12" + "-31");
           /* }*/
        }

    }
}
