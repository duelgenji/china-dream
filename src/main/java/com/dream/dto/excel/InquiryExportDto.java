package com.dream.dto.excel;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.message.Message;
import org.joda.time.DateTime;

import java.util.List;

public class InquiryExportDto {


    private String name;

    private String inquiryNo;
    private String user;
    private String price;

    private String title;
    private String mode;
    private String dateTime;

    private String industry;
    private String province;
    private String fax;

    private String round;
    private String limitTime;
    private String remark;

    private String contactName;
    private String contactTel;
    private List<InquiryFile> inquiryFileList;

    private String contactPhone;
    private String contactMail;
    private String status;

    private String wechat;
    private String weibo;


    private List<RoundExportDto> roundExportDtoList;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInquiryNo() {
        return inquiryNo;
    }

    public void setInquiryNo(String inquiryNo) {
        this.inquiryNo = inquiryNo;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public List<InquiryFile> getInquiryFileList() {
        return inquiryFileList;
    }

    public void setInquiryFileList(List<InquiryFile> inquiryFileList) {
        this.inquiryFileList = inquiryFileList;
    }

    public List<RoundExportDto> getRoundExportDtoList() {
        return roundExportDtoList;
    }

    public void setRoundExportDtoList(List<RoundExportDto> roundExportDtoList) {
        this.roundExportDtoList = roundExportDtoList;
    }

    public void inquiry2Dto(Inquiry inquiry){

        String[] statusName= {"询价中","完成","流标"};

        this.setName("询价号_" + inquiry.getInquiryNo() + "_信息");
        this.setInquiryNo(inquiry.getInquiryNo());
        this.setUser(inquiry.getUser().getNickName());
        this.setPrice(inquiry.getTotalPrice().toString());
        this.setTitle(inquiry.getTitle());
        this.setMode(inquiry.getInquiryMode().getName());
        this.setDateTime(new DateTime(inquiry.getCreateDate()).toString("yyyy年MM月dd日"));
        this.setIndustry(inquiry.getCompanyIndustry().getName());
        this.setProvince(inquiry.getCompanyProvince().getName());
        this.setFax(inquiry.getContactFax());
        this.setRound(inquiry.getRound()+"");
        this.setLimitTime(new DateTime(inquiry.getLimitDate()).toString("yyyy年MM月dd日"));
        this.setRemark(inquiry.getRemark());
        this.setContactName(inquiry.getContactName());
        this.setContactTel(inquiry.getContactTel());
        this.setContactPhone(inquiry.getContactPhone());
        this.setContactMail(inquiry.getContactEmail());
        this.setStatus(statusName[inquiry.getStatus()]);
        this.setWechat(inquiry.getContactWeiXin());
        this.setWeibo(inquiry.getContactWeiBo());


    }
}
