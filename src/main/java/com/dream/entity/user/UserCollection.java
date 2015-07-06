package com.dream.entity.user;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 收藏 用户 表
 * Created by Knight on 2015/7/6 10:36.
 */

@Entity
@Table
public class UserCollection extends AbstractPersistable<Long> {

    @ManyToOne
    private User user;

    @ManyToOne
    private User targetUser;


    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
