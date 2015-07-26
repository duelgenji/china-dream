package com.dream.entity.dream;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 敏感词
 * Created by Knight on 2015/7/26 16:46.
 */

@Entity
@Table
public class SensitiveWord extends AbstractPersistable<Long> {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
