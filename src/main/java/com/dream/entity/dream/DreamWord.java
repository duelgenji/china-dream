package com.dream.entity.dream;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 梦想语录
 * Created by Knight on 2015/6/15 11:51.
 */
@Entity
@Table
public class DreamWord extends AbstractPersistable<Long> {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
