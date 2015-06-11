package com.dream.entity.company;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 公司 省份
 * Created by Knight on 2015/6/11 10:42.
 */
@Entity
@Table
public class CompanyProvince  extends AbstractPersistable<Long>  {

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String alias;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
