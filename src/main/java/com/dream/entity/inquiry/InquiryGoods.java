package com.dream.entity.inquiry;

import com.dream.entity.user.User;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 询价 点赞
 * Created by Knight on 2015/7/17 11:12.
 */

@Entity
@Table
public class InquiryGoods extends AbstractPersistable<Long> {

    @ManyToOne
    private Inquiry inquiry;

    @ManyToOne
    private User user;

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
