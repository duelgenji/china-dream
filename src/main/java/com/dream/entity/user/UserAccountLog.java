package com.dream.entity.user;

import com.dream.entity.inquiry.Inquiry;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户余额记录
 * Created by Knight on 2016/4/24 11:37.
 */
@Entity
@Table
public class UserAccountLog extends AbstractPersistable<Long> {

    @ManyToOne
    private User user;

    @ManyToOne
    private Inquiry inquiry;

    /*金额变化 可以为负*/
    private Double amountChange;

    /*当前余额*/
    private Double currentAmount;

    /*备注*/
    private String remark;

    /*系统自动 or 手动 */
    private boolean auto;

    /*手动输入时 填写的项目*/
    private String project;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();

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

    public Double getAmountChange() {
        return amountChange;
    }

    public void setAmountChange(Double amountChange) {
        this.amountChange = amountChange;
    }

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
