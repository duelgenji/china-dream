package com.dream.dto.excel;

import java.util.List;

public class ExportDto {

    private String name;

    private boolean protect = true;

    private List<InquiryExportDto> inquiryExportDtoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isProtect() {
        return protect;
    }

    public void setProtect(boolean protect) {
        this.protect = protect;
    }

    public List<InquiryExportDto> getInquiryExportDtoList() {
        return inquiryExportDtoList;
    }

    public void setInquiryExportDtoList(List<InquiryExportDto> inquiryExportDtoList) {
        this.inquiryExportDtoList = inquiryExportDtoList;
    }
}
