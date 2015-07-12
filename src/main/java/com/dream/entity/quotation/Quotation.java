package com.dream.entity.quotation;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.User;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户 询价订单 引证
 * Created by Knight on 2015/7/6 11:22.
 */
@Entity
@Table
public class Quotation extends AbstractPersistable<Long> {

    @ManyToOne
    private User user;

    @ManyToOne
    private Inquiry inquiry;

    /**
     * 总价格 单位（元）
     */
    private Double totalPrice;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 询价确认状态
     * 状态 0-待定；1-同意；2-拒绝
     */
    private int status;

    private int round;

    /**
     * 0-未归档；1-归档
     */
    private int archived;

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

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getArchived() {
        return archived;
    }

    public void setArchived(int archived) {
        this.archived = archived;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
