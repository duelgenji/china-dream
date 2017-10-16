package com.wonders.xlab.framework.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface MyRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    T find(Map<String, ?> var1);

    List<T> findAll(Map<String, ?> var1);

    List<T> findAll(Map<String, ?> var1, Sort var2);

    Page<T> findAll(Map<String, ?> var1, Pageable var2);
}
