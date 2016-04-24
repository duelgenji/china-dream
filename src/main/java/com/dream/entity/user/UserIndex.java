package com.dream.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Knight on 2015/7/7 16:33.
 */
@Entity
@Table
public class UserIndex implements Serializable {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private User user;

    private int quotationDoneTime;

    private int quotationSuccessTime;

    private double quotationSuccessRate;

    private int inquiryDoneTime;

    private int inquirySuccessTime;

    private double inquirySuccessRate;

    /*余额*/
    private double amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuotationDoneTime() {
        return quotationDoneTime;
    }

    public void setQuotationDoneTime(int quotationDoneTime) {
        this.quotationDoneTime = quotationDoneTime;
    }

    public int getQuotationSuccessTime() {
        return quotationSuccessTime;
    }

    public void setQuotationSuccessTime(int quotationSuccessTime) {
        this.quotationSuccessTime = quotationSuccessTime;
    }

    public double getQuotationSuccessRate() {
        return quotationSuccessRate;
    }

    public void setQuotationSuccessRate(double quotationSuccessRate) {
        this.quotationSuccessRate = quotationSuccessRate;
    }

    public int getInquiryDoneTime() {
        return inquiryDoneTime;
    }

    public void setInquiryDoneTime(int inquiryDoneTime) {
        this.inquiryDoneTime = inquiryDoneTime;
    }

    public int getInquirySuccessTime() {
        return inquirySuccessTime;
    }

    public void setInquirySuccessTime(int inquirySuccessTime) {
        this.inquirySuccessTime = inquirySuccessTime;
    }

    public double getInquirySuccessRate() {
        return inquirySuccessRate;
    }

    public void setInquirySuccessRate(double inquirySuccessRate) {
        this.inquirySuccessRate = inquirySuccessRate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
