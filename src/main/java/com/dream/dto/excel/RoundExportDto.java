package com.dream.dto.excel;

import com.dream.entity.message.Message;

import java.util.List;

public class RoundExportDto {


    private List<Message> messageList;

    private List<QuotationExportDto> quotationExportDtoList;

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public List<QuotationExportDto> getQuotationExportDtoList() {
        return quotationExportDtoList;
    }

    public void setQuotationExportDtoList(List<QuotationExportDto> quotationExportDtoList) {
        this.quotationExportDtoList = quotationExportDtoList;
    }
}
