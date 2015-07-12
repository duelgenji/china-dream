package com.dream.entity.user;

import com.dream.entity.company.CompanyIndustry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 个人用户 信息
 * Created by Knight on 2015/6/10 14:50.
 */
@Entity
@Table
public class UserPersonalInfo implements Serializable {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private User user;

    private int sex;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    private String realName;

    @Enumerated
    private OpenStatus realNameOpen = OpenStatus.OPEN;

    /**
     * 最高学历
     */
    private String degree;

    @Enumerated
    private OpenStatus degreeOpen = OpenStatus.OPEN;

    /**
     * 毕业学校
     */
    private String school;

    @Enumerated
    private OpenStatus schoolOpen = OpenStatus.OPEN;

    /**
     * 所学专业
     */
    private String major;

    @Enumerated
    private OpenStatus majorOpen = OpenStatus.OPEN;

    /**
     * 个人微博地址
     */
    private String weiboUrl;

    /**
     * 个人微信号
     */
    private String weixin;


    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyIndustry companyIndustry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public OpenStatus getRealNameOpen() {
        return realNameOpen;
    }

    public void setRealNameOpen(OpenStatus realNameOpen) {
        this.realNameOpen = realNameOpen;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public OpenStatus getDegreeOpen() {
        return degreeOpen;
    }

    public void setDegreeOpen(OpenStatus degreeOpen) {
        this.degreeOpen = degreeOpen;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public OpenStatus getSchoolOpen() {
        return schoolOpen;
    }

    public void setSchoolOpen(OpenStatus schoolOpen) {
        this.schoolOpen = schoolOpen;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public OpenStatus getMajorOpen() {
        return majorOpen;
    }

    public void setMajorOpen(OpenStatus majorOpen) {
        this.majorOpen = majorOpen;
    }

    public String getWeiboUrl() {
        return weiboUrl;
    }

    public void setWeiboUrl(String weiboUrl) {
        this.weiboUrl = weiboUrl;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public CompanyIndustry getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(CompanyIndustry companyIndustry) {
        this.companyIndustry = companyIndustry;
    }
}
