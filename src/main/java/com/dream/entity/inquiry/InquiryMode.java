package com.dream.entity.inquiry;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 标 类型
 * Created by Knight on 2015/6/25 11:03.
 */
@Entity
@Table
@Audited
public class InquiryMode extends AbstractPersistable<Long> {

    private String name;

    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
