package com.dream.dto.excel;

import com.dream.entity.quotation.QuotationFile;

import java.util.List;

public class QuotationExportDto {

    private String userUrl;

    private String name;

    private String province;

    private String price;

    private String dateTime;

    private String status;

    private List<QuotationFile> businessFileList;
    private List<QuotationFile> techFileList;

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<QuotationFile> getBusinessFileList() {
        return businessFileList;
    }

    public void setBusinessFileList(List<QuotationFile> businessFileList) {
        this.businessFileList = businessFileList;
    }

    public List<QuotationFile> getTechFileList() {
        return techFileList;
    }

    public void setTechFileList(List<QuotationFile> techFileList) {
        this.techFileList = techFileList;
    }
}
