package com.dream.entity.user;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyProvince;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户主表
 * Created by Knight on 2015/6/10 9:46.
 */
@Entity
@Table
public class User extends AbstractPersistable<Long> {

    private String email;

    private String nickName;

    private String headImage;

    @JsonIgnore
    private String password;

    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    /**
     * 用户类型  1个人  2企业 3群
     */
    private int type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime = new Date();

    private int inquiryTimes;

    private int inquirySuccessTimes;

    private String logoUrl;

    private String IDCardNumber;

    private String bank;

    private String bankAccount;

    @Enumerated
    private OpenStatus bankAccountOpen = OpenStatus.OPEN;

    private String zhifubaoAccount;

    @Enumerated
    private OpenStatus zhifubaoAccountOpen = OpenStatus.OPEN;

    private String tel;

    @Enumerated
    private OpenStatus telOpen = OpenStatus.OPEN;

    private String telephone;

    @Enumerated
    private OpenStatus telephoneOpen = OpenStatus.OPEN;

    private String keywords;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private UserPersonalInfo userPersonalInfo;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private UserCompanyInfo userCompanyInfo;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private UserGroupInfo userGroupInfo;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private UserIndex userIndex;

    /**
     * vip 标记位
     */
    private int VIP = 0;

    /**
     * 验证码
     */
    @JsonIgnore
    private String captcha;

    /**
     * 简介
     */
    @Column(columnDefinition = "TEXT")
    private String description;


    /**
     * 测试用户
     */
    private boolean test;

    /**
     * 删除标记
     */
    private boolean removed;

    /**
     * 行业
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyIndustry companyIndustry;

    /**
     * 所在地
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyProvince companyProvince;

    /**
     * 不关注的 行业 （新标发布时 不推送使用）
     */
    @Column(columnDefinition = "varchar(255) default ''")
    private String removedIndustry;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getInquiryTimes() {
        return inquiryTimes;
    }

    public void setInquiryTimes(int inquiryTimes) {
        this.inquiryTimes = inquiryTimes;
    }

    public int getInquirySuccessTimes() {
        return inquirySuccessTimes;
    }

    public void setInquirySuccessTimes(int inquirySuccessTimes) {
        this.inquirySuccessTimes = inquirySuccessTimes;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getIDCardNumber() {
        return IDCardNumber;
    }

    public void setIDCardNumber(String IDCardNumber) {
        this.IDCardNumber = IDCardNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public OpenStatus getBankAccountOpen() {
        return bankAccountOpen;
    }

    public void setBankAccountOpen(OpenStatus bankAccountOpen) {
        this.bankAccountOpen = bankAccountOpen;
    }

    public String getZhifubaoAccount() {
        return zhifubaoAccount;
    }

    public void setZhifubaoAccount(String zhifubaoAccount) {
        this.zhifubaoAccount = zhifubaoAccount;
    }

    public OpenStatus getZhifubaoAccountOpen() {
        return zhifubaoAccountOpen;
    }

    public void setZhifubaoAccountOpen(OpenStatus zhifubaoAccountOpen) {
        this.zhifubaoAccountOpen = zhifubaoAccountOpen;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public OpenStatus getTelOpen() {
        return telOpen;
    }

    public void setTelOpen(OpenStatus telOpen) {
        this.telOpen = telOpen;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public OpenStatus getTelephoneOpen() {
        return telephoneOpen;
    }

    public void setTelephoneOpen(OpenStatus telephoneOpen) {
        this.telephoneOpen = telephoneOpen;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public UserPersonalInfo getUserPersonalInfo() {
        return userPersonalInfo;
    }

    public void setUserPersonalInfo(UserPersonalInfo userPersonalInfo) {
        this.userPersonalInfo = userPersonalInfo;
    }

    public UserCompanyInfo getUserCompanyInfo() {
        return userCompanyInfo;
    }

    public void setUserCompanyInfo(UserCompanyInfo userCompanyInfo) {
        this.userCompanyInfo = userCompanyInfo;
    }

    public UserGroupInfo getUserGroupInfo() {
        return userGroupInfo;
    }

    public void setUserGroupInfo(UserGroupInfo userGroupInfo) {
        this.userGroupInfo = userGroupInfo;
    }

    public int getVIP() {
        return VIP;
    }

    public void setVIP(int VIP) {
        this.VIP = VIP;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public UserIndex getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(UserIndex userIndex) {
        this.userIndex = userIndex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public CompanyIndustry getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(CompanyIndustry companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public CompanyProvince getCompanyProvince() {
        return companyProvince;
    }

    public void setCompanyProvince(CompanyProvince companyProvince) {
        this.companyProvince = companyProvince;
    }

    public String getRemovedIndustry() {
        return removedIndustry;
    }

    public void setRemovedIndustry(String removedIndustry) {
        this.removedIndustry = removedIndustry;
    }
}