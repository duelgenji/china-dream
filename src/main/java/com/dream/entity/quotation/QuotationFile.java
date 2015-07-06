package com.dream.entity.quotation;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 正式出价 文件
 * Created by Knight on 2015/7/6 15:37.
 */
@Entity
@Table
public class QuotationFile extends AbstractPersistable<Long> {

    @ManyToOne
    private Quotation quotation;

    private String fileUrl;

    private String remark;

    /**
     * 类型 0 商务文档  1技术文档
     */
    private int type;

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
