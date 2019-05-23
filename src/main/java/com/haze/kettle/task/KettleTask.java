package com.haze.kettle.task;

import com.haze.kettle.entity.KettleLog;
import com.haze.kettle.service.KettleLogService;
import com.haze.kettle.utils.KettleUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class KettleTask {

    private Logger logger = LoggerFactory.getLogger(KettleTask.class);

    @Autowired
    private KettleUtils kettleUtils;

    @Autowired
    private KettleLogService kettleLogService;

    @Scheduled(cron = "0 0 8 * * ?")
    //@Scheduled(cron = "0 40 20 * * ?")
    public void run() {
        //每天执行一次
        //String[] transObjectIds = {"19","13","16","36","32","30","33","31"};
        String[] jobObjectIds = {"2","3","4","5","6"};
        Map<String, String> params = new HashMap<>();
        for (String jobObjectId : jobObjectIds) {
            KettleLog kettleLog = kettleLogService.getKettleLog(jobObjectId);
            if (kettleLog == null) {
                kettleLog = new KettleLog();
                kettleLog.setJobId(jobObjectId);
                kettleLog.setErrorCount(0);
                kettleLog.setLastDay("20181119");
            }
            String startDate = kettleLog.getLastDay();
            String endDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
            if (!startDate.equalsIgnoreCase(endDate)) {
                //计算endDate
                params.put("startDate", startDate);
                params.put("endDate", endDate);
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
                    try {
                        kettleLogService.save(kettleLog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
