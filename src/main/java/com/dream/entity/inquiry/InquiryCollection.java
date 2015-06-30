package com.dream.entity.inquiry;

import com.dream.entity.user.User;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 收藏
 * Created by Knight on 2015/7/1 0:59.
 */
@Entity
@Table
public class InquiryCollection extends AbstractPersistable<Long> {

    @ManyToOne
    private User user;

    @ManyToOne
    private Inquiry inquiry;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
