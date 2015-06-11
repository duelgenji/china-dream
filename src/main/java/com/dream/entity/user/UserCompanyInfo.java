package com.dream.entity.user;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyOwnership;
import com.dream.entity.company.CompanyProvince;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 企业 用户 信息
 * Created by Knight on 2015/6/10 14:50.
 */
@Entity
@Table
public class UserCompanyInfo  implements Serializable {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private User user;

    private String companyName;

    /**
     * 公司所在地
     */

    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyProvince companyProvince;

    /**
     * 公司行业
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyIndustry companyIndustry;

    /**
     * 公司性质
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyOwnership companyOwnership;

    /**
     * 企业邮箱
     */
    private String companyEmail;

    @Enumerated
    private OpenStatus companyEmailOpen = OpenStatus.OPEN;

    /**
     * 税号
     */
    private String taxNumber;

    @Enumerated
    private OpenStatus taxNumberOpen = OpenStatus.OPEN;

    /**
     * 组织机构代码
     */
    private String organizationsCode;

    @Enumerated
    private OpenStatus organizationsCodeOpen = OpenStatus.OPEN;

    /**
     * 企业网站
     */
    private String website;

    /**
     * 企业微博
     */
    private String weiboUrl;

    /**
     * 企业微信
     */
    private String weixin;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public CompanyProvince getCompanyProvince() {
        return companyProvince;
    }

    public void setCompanyProvince(CompanyProvince companyProvince) {
        this.companyProvince = companyProvince;
    }

    public CompanyIndustry getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(CompanyIndustry companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public CompanyOwnership getCompanyOwnership() {
        return companyOwnership;
    }

    public void setCompanyOwnership(CompanyOwnership companyOwnership) {
        this.companyOwnership = companyOwnership;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public OpenStatus getCompanyEmailOpen() {
        return companyEmailOpen;
    }

    public void setCompanyEmailOpen(OpenStatus companyEmailOpen) {
        this.companyEmailOpen = companyEmailOpen;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public OpenStatus getTaxNumberOpen() {
        return taxNumberOpen;
    }

    public void setTaxNumberOpen(OpenStatus taxNumberOpen) {
        this.taxNumberOpen = taxNumberOpen;
    }

    public String getOrganizationsCode() {
        return organizationsCode;
    }

    public void setOrganizationsCode(String organizationsCode) {
        this.organizationsCode = organizationsCode;
    }

    public OpenStatus getOrganizationsCodeOpen() {
        return organizationsCodeOpen;
    }

    public void setOrganizationsCodeOpen(OpenStatus organizationsCodeOpen) {
        this.organizationsCodeOpen = organizationsCodeOpen;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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
}
