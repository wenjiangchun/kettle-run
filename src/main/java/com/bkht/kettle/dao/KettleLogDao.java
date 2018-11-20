package com.bkht.kettle.dao;

import com.bkht.core.jpa.BaseRepository;
import com.bkht.kettle.entity.KettleLog;
import com.bkht.system.entity.Config;
import org.springframework.stereotype.Repository;

@Repository
public interface KettleLogDao extends BaseRepository<KettleLog, Long> {

}
