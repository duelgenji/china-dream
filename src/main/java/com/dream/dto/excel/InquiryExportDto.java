package com.dream.dto.excel;

import java.util.List;

public class InquiryExportDto {

    private String name;

    private String dateTime;

    private String status;

    private List<QuotationExportDto> quotationExportDtoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<QuotationExportDto> getQuotationExportDtoList() {
        return quotationExportDtoList;
    }

    public void setQuotationExportDtoList(List<QuotationExportDto> quotationExportDtoList) {
        this.quotationExportDtoList = quotationExportDtoList;
    }
}
