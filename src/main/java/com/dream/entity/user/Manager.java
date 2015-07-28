package com.dream.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Knight on 2015/7/27 9:33.
 */
@Entity
@Table
public class Manager extends AbstractPersistable<Long> {

    private String name;

    @JsonIgnore
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
