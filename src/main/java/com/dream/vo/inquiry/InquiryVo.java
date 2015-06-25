package com.dream.vo.inquiry;

import org.springframework.web.multipart.MultipartFile;

/**
 * 标 vo
 * Created by Knight on 2015/6/25 18:48.
 */
public class InquiryVo {



    private String contactName;


    private MultipartFile multipartFile1;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    public MultipartFile getMultipartFile1() {
        return multipartFile1;
    }

    public void setMultipartFile1(MultipartFile multipartFile1) {
        this.multipartFile1 = multipartFile1;
    }
}
