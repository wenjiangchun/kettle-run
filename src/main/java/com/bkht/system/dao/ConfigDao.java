package com.bkht.system.dao;

import com.bkht.core.jpa.BaseRepository;
import com.bkht.system.entity.Config;
import org.springframework.stereotype.Repository;

/**
 * 系统配置Dao接口定义类
 *
 * @author sofar
 *
 */
@Repository
public interface ConfigDao extends BaseRepository<Config, Long> {

}
