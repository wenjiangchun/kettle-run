package com.bkht.core.jpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 根据对象属性查询对象信息列表
     *
     * @param propertyName 属性名称
     * @param value        属性值
     * @param sorts        排序对象
     * @return List<T>     满足查询条件的对象信息列表
     */
    List<T> findByProperty(String propertyName, Object value, Sort... sorts);

    List<T> findByJql(String jpql, Map<String, Object> queryParams);

    List<T> findBySql(String sql, Map<String, Object> queryParams);
}
