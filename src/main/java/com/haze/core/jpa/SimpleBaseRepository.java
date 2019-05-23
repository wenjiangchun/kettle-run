package com.haze.core.jpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * BaseRepository接口实现类
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public class SimpleBaseRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private EntityManager em;

    //private Class<T> domainClass;

    private JpaEntityInformation<T, ?> entityInformation;

    public SimpleBaseRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

   /* public SimpleBaseRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
    }*/

    @Override
    public List<T> findByProperty(String propertyName, Object value, Sort... sorts) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityInformation.getJavaType());
        Root<T> root = criteriaQuery.from(entityInformation.getJavaType());
        //TODO 增加父子关系查询使用.分割
        Predicate predicate = criteriaBuilder.equal(root.get(propertyName), value);
        criteriaQuery.where(predicate);
        for (Sort sort : sorts) {
            Iterator<Sort.Order> it = sort.iterator();
            while (it.hasNext()) {
                Sort.Order order = it.next();
                Sort.Direction direction = order.getDirection();
                String orderPropertyName = order.getProperty();
                switch (direction) {
                    case ASC:
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(orderPropertyName)));
                        break;
                    case DESC:
                        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(orderPropertyName)));
                        break;
                    default:
                        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(orderPropertyName)));
                }
            }
        }
        Query query = em.createQuery(criteriaQuery);
        //query.setHint("javax.persistence.fetchgraph",em.getEntityGraphs(entityInformation.getJavaType()).get(0));
        return query.getResultList();
    }

    @Override
    public List<T> findByJql(String jpql, Map<String, Object> queryParams) {
        Assert.notNull(jpql, "查询语句不能为空!");
        Query query = em.createQuery(jpql);
        setParams(queryParams, query);
        return query.getResultList();
    }

    private void setParams(Map<String, Object> queryParams, Query query) {
        for (Parameter param : query.getParameters()) {
            String name = param.getName();
            if (StringUtils.hasText(name)) {
                query.setParameter(name, queryParams.get(name));
            } else {
                throw new IllegalArgumentException("查询出错, Query查询语句命名参数不能为空");
            }
        }
    }

    @Override
    public List<T> findBySql(String sql, Map<String, Object> queryParams) {
        Assert.notNull(sql, "查询语句不能为空!");
        Query query = em.createNativeQuery(sql);
        setParams(queryParams, query);
        return query.getResultList();
    }
}