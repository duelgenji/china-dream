//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonders.xlab.framework.repository;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class MyRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements MyRepository<T, ID> {
    private final Class<T> domainClass;
    private final EntityManager entityManager;

    public MyRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.domainClass = entityInformation.getJavaType();
        this.entityManager = entityManager;
    }

    public T find(Map<String, ?> filters) {
        try {
            return this.getQuery(filters, (Pageable)null).getSingleResult();
        } catch (NoResultException var3) {
            return null;
        }
    }

    public List<T> findAll(Map<String, ?> filters) {
        return this.getQuery(filters, (Pageable)null).getResultList();
    }

    public List<T> findAll(Map<String, ?> filters, Sort sort) {
        return this.getQuery(filters, sort).getResultList();
    }

    public Page<T> findAll(Map<String, ?> filters, Pageable pageable) {
        TypedQuery query = this.getQuery(filters, pageable);
        return (Page)(pageable == null?new PageImpl(query.getResultList()):this.readPage(query, pageable, filters));
    }

    protected TypedQuery<T> getQuery(Map<?, ?> filters, Pageable pageable) {
        Sort sort = pageable == null?null:pageable.getSort();
        return this.getQuery(filters, sort);
    }

    protected TypedQuery<T> getQuery(Map<?, ?> filters, Sort sort) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(this.domainClass);
        Root root = this.applyFiltersToCriteria(filters, query);
        query.select(root);
        if(sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }

        return this.entityManager.createQuery(query);
    }

    protected TypedQuery<Long> getCountQuery(Map<?, ?> filters) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(Long.class);
        Root root = this.applyFiltersToCriteria(filters, query);
        if(query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return this.entityManager.createQuery(query);
    }

    protected <S> Root<T> applyFiltersToCriteria(Map<?, ?> filters, CriteriaQuery<S> query) {
        Root root = query.from(this.domainClass);
        if(filters == null) {
            return root;
        } else {
            CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
            ArrayList predicates = new ArrayList();
            Iterator var6 = filters.entrySet().iterator();

            while(var6.hasNext()) {
                Entry entry = (Entry)var6.next();
                String key = (String)entry.getKey();
                String name = StringUtils.substringBefore(key, "_");
                String op = StringUtils.substringAfter(key, "_");
                String[] names = StringUtils.split(name, '.');
                Path path = root.get(names[0]);

                for(int value = 1; value < names.length; ++value) {
                    path = path.get(names[value]);
                }

                Object var16 = entry.getValue();
                if(StringUtils.contains(op, "like")) {
                    var16 = "%" + var16 + "%";
                }

                try {
                    Predicate e;
                    if(StringUtils.startsWithAny(op, new CharSequence[]{"is", "isNot"})) {
                        e = (Predicate)MethodUtils.invokeMethod(cb, op, new Object[]{path});
                    } else {
                        e = (Predicate)MethodUtils.invokeMethod(cb, op, new Object[]{path, var16});
                    }

                    predicates.add(e);
                } catch (Exception var15) {
                    throw new RuntimeException(var15);
                }
            }

            if(!predicates.isEmpty()) {
                query.where((Predicate[])predicates.toArray(new Predicate[predicates.size()]));
            }

            return root;
        }
    }

    protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Map<?, ?> filters) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        Long total = executeCountQuery(this.getCountQuery(filters));
        List content = total.longValue() > (long)pageable.getOffset()?query.getResultList():Collections.emptyList();
        return new PageImpl(content, pageable, total.longValue());
    }

    private static Long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query);
        List totals = query.getResultList();
        Long total = Long.valueOf(0L);

        Long element;
        for(Iterator var3 = totals.iterator(); var3.hasNext(); total = Long.valueOf(total.longValue() + (element == null?0L:element.longValue()))) {
            element = (Long)var3.next();
        }

        return total;
    }
}
