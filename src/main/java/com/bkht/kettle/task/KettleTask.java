package com.bkht.kettle.task;

import com.bkht.kettle.entity.KettleLog;
import com.bkht.kettle.service.KettleService;
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

    @Autowired
    private KettleService kettleService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void run() {
        //每天执行一次
        //String[] transObjectIds = {"19","13","16","36","32","30","33","31"};
        String[] jobObjectIds = {"2","3","4","5","6"};
        Map<String, String> params = new HashMap<>();
        //获取昨天日期
        String startDate = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyyMMdd");
        String endDate = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyyMMdd");
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        for (String jobObjectId : jobObjectIds) {
            KettleLog kettleLog = kettleService.getKettleLog(jobObjectId);
            if (kettleLog == null) {
                kettleLog = new KettleLog();
                kettleLog.setJobId(jobObjectId);
                kettleLog.setErrorCount(0);
                kettleLog.setLastDay("19000101");
            }
            try {
                    logger.debug("当前执行时间" + new Date());
                    kettleUtils.runJob(jobObjectId, params);
                    kettleLog.setLastDay(endDate);
                    logger.debug("id=" + jobObjectId + "执行成功");
            } catch (Exception e) {
                kettleLog.setLastError(e.getMessage());
                kettleLog.setErrorCount(kettleLog.getErrorCount() + 1);
                logger.error("id=[" + jobObjectId + "]任务执行异常", e);
            } finally {
                kettleUtils.close();
            }
        }
    }
}
