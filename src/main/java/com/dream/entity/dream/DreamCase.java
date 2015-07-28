package com.dream.entity.dream;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 梦想案列 (案例分析)
 * Created by Knight on 2015/7/28 14:54.
 */
@Entity
@Table
public class DreamCase extends AbstractPersistable<Long> {

    private String title;

    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
