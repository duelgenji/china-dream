package com.dream.entity.inquiry;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 标的文件
 * Created by Knight on 2015/6/25 17:05.
 */
@Entity
@Table
public class InquiryFile extends AbstractPersistable<Long> {

    @ManyToOne
    private Inquiry inquiry;

    private String fileUrl;

    private String remark;

    public Inquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
