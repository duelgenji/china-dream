package com.dream.entity.inquiry;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyProvince;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 标  历史记录 (前几轮)
 * Created by Knight on 2015/7/12 18:01.
 */
@Entity
@Table
public class InquiryHistory extends AbstractPersistable<Long> {

    @ManyToOne
    private Inquiry inquiry;

    @ManyToOne
    private User user;

    private String title;

    private int round;

    /**
     * 所在地
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyProvince companyProvince;

    /**
     * 行业
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyIndustry companyIndustry;

    /**
     * 出标 类型
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private InquiryMode inquiryMode;

    /**
     * 项目截止日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date limitDate;

    /**
     * 询价截止日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseCloseDate;

    /**
     * 创建日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();

    /**
     * 修改日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate = new Date();

    /**
     * 总价格 单位（元）
     */
    private Double totalPrice;


    /**
     * 状态 0 询价中 1完成 2流标
     */
    private int status;

    private int hotLevel;

    /**
     * 用户限制  0不限  1个人/群  2企业
     */
    private int userLimit;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;

    @Enumerated
    private OpenStatus remarkOpen = OpenStatus.OPEN;

    @Enumerated
    private OpenStatus filesOpen = OpenStatus.OPEN;


    private String contactName;

    @Enumerated
    private OpenStatus contactNameOpen = OpenStatus.OPEN;

    private String contactEmail;

    @Enumerated
    private OpenStatus contactEmailOpen = OpenStatus.OPEN;

    private String contactPhone;

    @Enumerated
    private OpenStatus contactPhoneOpen = OpenStatus.OPEN;

    private String contactTel;

    @Enumerated
    private OpenStatus contactTelOpen = OpenStatus.OPEN;

    /**
     * 传真
     */
    private String contactFax;

    @Enumerated
    private OpenStatus contactFaxOpen = OpenStatus.OPEN;

    private String contactWeiXin;

    @Enumerated
    private OpenStatus contactWeiXinOpen = OpenStatus.OPEN;

    private String contactWeiBo;

    @Enumerated
    private OpenStatus contactWeiBoOpen = OpenStatus.OPEN;

    private String inquiryNo;

    private String logoUrl;

    private int test;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
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

    public InquiryMode getInquiryMode() {
        return inquiryMode;
    }

    public void setInquiryMode(InquiryMode inquiryMode) {
        this.inquiryMode = inquiryMode;
    }

    public Date getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(Date limitDate) {
        this.limitDate = limitDate;
    }

    public Date getPurchaseCloseDate() {
        return purchaseCloseDate;
    }

    public void setPurchaseCloseDate(Date purchaseCloseDate) {
        this.purchaseCloseDate = purchaseCloseDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHotLevel() {
        return hotLevel;
    }

    public void setHotLevel(int hotLevel) {
        this.hotLevel = hotLevel;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public OpenStatus getRemarkOpen() {
        return remarkOpen;
    }

    public void setRemarkOpen(OpenStatus remarkOpen) {
        this.remarkOpen = remarkOpen;
    }

    public OpenStatus getFilesOpen() {
        return filesOpen;
    }

    public void setFilesOpen(OpenStatus filesOpen) {
        this.filesOpen = filesOpen;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public OpenStatus getContactNameOpen() {
        return contactNameOpen;
    }

    public void setContactNameOpen(OpenStatus contactNameOpen) {
        this.contactNameOpen = contactNameOpen;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public OpenStatus getContactEmailOpen() {
        return contactEmailOpen;
    }

    public void setContactEmailOpen(OpenStatus contactEmailOpen) {
        this.contactEmailOpen = contactEmailOpen;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public OpenStatus getContactPhoneOpen() {
        return contactPhoneOpen;
    }

    public void setContactPhoneOpen(OpenStatus contactPhoneOpen) {
        this.contactPhoneOpen = contactPhoneOpen;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public OpenStatus getContactTelOpen() {
        return contactTelOpen;
    }

    public void setContactTelOpen(OpenStatus contactTelOpen) {
        this.contactTelOpen = contactTelOpen;
    }

    public String getContactFax() {
        return contactFax;
    }

    public void setContactFax(String contactFax) {
        this.contactFax = contactFax;
    }

    public OpenStatus getContactFaxOpen() {
        return contactFaxOpen;
    }

    public void setContactFaxOpen(OpenStatus contactFaxOpen) {
        this.contactFaxOpen = contactFaxOpen;
    }

    public String getContactWeiXin() {
        return contactWeiXin;
    }

    public void setContactWeiXin(String contactWeiXin) {
        this.contactWeiXin = contactWeiXin;
    }

    public OpenStatus getContactWeiXinOpen() {
        return contactWeiXinOpen;
    }

    public void setContactWeiXinOpen(OpenStatus contactWeiXinOpen) {
        this.contactWeiXinOpen = contactWeiXinOpen;
    }

    public String getContactWeiBo() {
        return contactWeiBo;
    }

    public void setContactWeiBo(String contactWeiBo) {
        this.contactWeiBo = contactWeiBo;
    }

    public OpenStatus getContactWeiBoOpen() {
        return contactWeiBoOpen;
    }

    public void setContactWeiBoOpen(OpenStatus contactWeiBoOpen) {
        this.contactWeiBoOpen = contactWeiBoOpen;
    }

    public String getInquiryNo() {
        return inquiryNo;
    }

    public void setInquiryNo(String inquiryNo) {
        this.inquiryNo = inquiryNo;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getTest() {
        return test;
    }

    public Object getProperties(String name){
        switch (name){
            case "remark":
                return this.getRemark();
            case "remarkOpen":
                return this.getRemarkOpen();
            case "contactName":
                return this.getContactName();
            case "contactNameOpen":
                return this.getRemarkOpen();
            case "contactEmail":
                return this.getContactEmail();
            case "contactEmailOpen":
                return this.getContactEmailOpen();
            case "contactPhone":
                return this.getContactPhone();
            case "contactPhoneOpen":
                return this.getContactPhoneOpen();
            case "contactTel":
                return this.getContactTel();
            case "contactTelOpen":
                return this.getContactTelOpen();
            case "contactFax":
                return this.getContactFax();
            case "contactFaxOpen":
                return this.getContactFaxOpen();
            case "contactWeiBo":
                return this.getContactWeiBo();
            case "contactWeiBoOpen":
                return this.getContactWeiBoOpen();
            case "contactWeiXin":
                return this.getContactWeiXin();
            case "contactWeiXinOpen":
                return this.getContactWeiXinOpen();
            case "filesOpen":
                return this.getFilesOpen();
            default:
                return null;
        }
    }
}