package com.bkht.kettle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KettleRunApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(KettleRunApplication.class);

    public static void main(String[] args) {
        org.springframework.context.ApplicationContext ctx = SpringApplication.run(KettleRunApplication.class, args);

        String transName = ctx.getBean(KettleConfig.class).transName;
        //KettleRunner.runTrans("产权信息抽取转换(明细)", "",  "");

       /* for (int startYear = 1970; startYear < 2019; startYear ++) {
            *//*if (startYear == 2015) {
                LOGGER.debug("开始执行：startDate = " + startYear + "-07" + "-01" + "," + "endDate = " + startYear + "-12" + "-31");
                KettleRunner.runTrans(transName, startYear + "-07" + "-01",  startYear + "-12" + "-31");
            } else {*//*
                LOGGER.debug("开始执行：startDate = " + startYear + "-01" + "-01" + "," + "endDate = " + startYear + "-06" + "-30");
                KettleRunner.runTrans(transName, startYear + "-01" + "-01",  startYear + "-06" + "-30");

                LOGGER.debug("开始执行：startDate = " + startYear + "-07" + "-01" + "," + "endDate = " + startYear + "-12" + "-31");
                KettleRunner.runTrans(transName, startYear + "-07" + "-01",  startYear + "-12" + "-31");
           *//* }*//*
        }
*/
        String sql = "select * from user where name like %s";
        LOGGER.info(String.format(sql,"'%<=60m²%'"));
    }
}
