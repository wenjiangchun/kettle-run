package com.bkht.kettle.service;

import com.bkht.core.service.AbstractBaseService;
import com.bkht.kettle.dao.KettleLogDao;
import com.bkht.kettle.entity.KettleLog;
import com.bkht.system.dao.ConfigDao;
import com.bkht.system.entity.Config;
import com.bkht.system.exception.ConfigCannotDeleteException;
import com.bkht.system.exception.ConfigNameExistException;
import com.bkht.system.utils.ConfigType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class KettleService extends AbstractBaseService<KettleLog, Long> {

	private KettleLogDao kettleLogDao;
	
	@Autowired
	public void setKettleLogDao(KettleLogDao kettleLogDao) {
		this.kettleLogDao = kettleLogDao;
		super.setDao(kettleLogDao);
	}


	public KettleLog getKettleLog(String jobId) {
		Assert.notNull(jobId, "JobId不能为空");
		return kettleLogDao.findOne(Example.of(KettleLog.createByJobId(jobId))).get();
	}
}
