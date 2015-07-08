package com.dream.entity.message;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.User;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 站内信
 * Created by Knight on 2015/6/29 13:25.
 */

@Entity
@Table
public class Message extends AbstractPersistable<Long> {

    @ManyToOne
    private User user;

    @ManyToOne
    private User inquiryUser;

    @ManyToOne
    private Inquiry inquiry;

    private String content;

    /**
     * 申请轮次
     */
    private int round;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 站内信 询价确认状态
     * 0 未确认  1 同意  2 拒绝
     */
    private int status;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getInquiryUser() {
        return inquiryUser;
    }

    public void setInquiryUser(User inquiryUser) {
        this.inquiryUser = inquiryUser;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
