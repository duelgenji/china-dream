package com.dream.entity.inquiry;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyProvince;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * 标
 * Created by Knight on 2015/6/25 11:08.
 */
@Entity
@Table
public class Inquiry  extends AbstractPersistable<Long> {

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

    /**
     * 是否是测试表  0否1是
     */
    private int test;

    /**
     * 中标者
     */
    @ManyToOne
    private User winner;

    /**
     * 是否公开中标者
     */
    private boolean openWinner;

    /**
     * 是否公开中标者
     */
    private boolean openPrice;

    /**
     * 中标金额
     */
    private Long winnerPrice;

    /**
     * 流标原因
     */
    private String failReason;

    /**
     * 间隔时间  用于暗询价
     */
    private int intervalHour;

    /**
     * 是否 发送过 流标提示email 60天提示 67天真正流标
     */
    private boolean sendFailEmail;

    /**
     * 删除标记
     */
    private boolean removed;

    /**
     * 被赞数
     */
    private int goods;

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

    public void setTest(int test) {
        this.test = test;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public boolean isOpenWinner() {
        return openWinner;
    }

    public void setOpenWinner(boolean openWinner) {
        this.openWinner = openWinner;
    }

    public boolean isOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(boolean openPrice) {
        this.openPrice = openPrice;
    }

    public Long getWinnerPrice() {
        return winnerPrice;
    }

    public void setWinnerPrice(Long winnerPrice) {
        this.winnerPrice = winnerPrice;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public int getIntervalHour() {
        return intervalHour;
    }

    public void setIntervalHour(int interval) {
        this.intervalHour = interval;
    }

    public boolean isSendFailEmail() {
        return sendFailEmail;
    }

    public void setSendFailEmail(boolean sendFailEmail) {
        this.sendFailEmail = sendFailEmail;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public int getGoods() {
        return goods;
    }

    public void setGoods(int goods) {
        this.goods = goods;
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
                return this.getContactNameOpen();
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
