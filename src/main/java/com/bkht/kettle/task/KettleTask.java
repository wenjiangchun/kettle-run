package com.bkht.kettle.task;

import com.bkht.kettle.utils.KettleUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class KettleTask {

    private Logger logger = LoggerFactory.getLogger(KettleTask.class);

    @Autowired
    private KettleUtils kettleUtils;

    //@Scheduled(cron = "0 50 15-23 * * *")
    //@Scheduled(cron = "0 25 15 * * ?")
    public void run() {
        //每天执行一次
        //String[] transObjectIds = {"19","13","16","36","32","30","33","31"};
        String[] transObjectIds = {"2","3","4","5","6"};
        Map<String, String> params = new HashMap<>();
        //获取昨天日期
        String startDate = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyyMMdd");
        String endDate = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyyMMdd");
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        for (String transObjectId : transObjectIds) {
            try {
                    logger.debug("当前执行时间" + new Date());
                    kettleUtils.runJob(transObjectId, params);
                    logger.debug("id=" + transObjectId + "执行成功");
            } catch (Exception e) {
                logger.error("id=[" + transObjectId + "]任务执行异常", e);
            } finally {
                kettleUtils.close();
            }
        }
    }
}
