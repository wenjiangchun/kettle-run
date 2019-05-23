package com.haze.kettle.service;

import com.haze.core.service.AbstractBaseService;
import com.haze.kettle.dao.KettleLogDao;
import com.haze.kettle.entity.KettleLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class KettleLogService extends AbstractBaseService<KettleLog, Long> {

	private KettleLogDao kettleLogDao;

	@Autowired
	public void setKettleLogDao(KettleLogDao kettleLogDao) {
		this.kettleLogDao = kettleLogDao;
		super.setDao(kettleLogDao);
	}

	@Transactional(readOnly = true)
	public KettleLog getKettleLog(String jobId) {
		Assert.notNull(jobId, "JobId不能为空");
        List<KettleLog> kettleLogList = kettleLogDao.findByProperty("jobId", jobId);
        return kettleLogList.isEmpty() ? null : kettleLogList.get(0);
	}

}
