package com.dream.entity.company;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 公司 行业
 * Created by Knight on 2015/6/11 10:42.
 */
@Entity
@Table
@Audited
public class CompanyIndustry extends AbstractPersistable<Long> {

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String name;

    private String logoUrl;

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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
