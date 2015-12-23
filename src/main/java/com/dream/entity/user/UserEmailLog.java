package com.dream.entity.user;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by knight on 15/12/3.
 */
public class UserEmailLog extends AbstractPersistable<Long> {

    @ManyToOne
    private User user;

    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
