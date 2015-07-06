package com.dream.service.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.quotation.Quotation;
import com.dream.entity.quotation.QuotationFile;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryFileRepository;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.quotation.QuotationFileRepository;
import com.dream.repository.quotation.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dream.entity.user.OpenStatus.OPEN;

/**
 * Created by Knight on 2015/6/27 14:02.
 */
@Service
public class InquiryService {


    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    InquiryFileRepository inquiryFileRepository;


    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    QuotationFileRepository quotationFileRepository;

    @Autowired
    MessageRepository messageRepository;

    private void putPropertiesByName(Map<String, Object> res, User user , Inquiry inquiry,String name,boolean isAuthorize){
        String value="";
        if(inquiry.getProperties(name)!=null){
            value=inquiry.getProperties(name).toString();

        }
        OpenStatus openStatus = (OpenStatus) inquiry.getProperties( name + "Open");

        if(openStatus== OPEN || inquiry.getUser().getId().equals(user.getId()) || openStatus==null){
            res.put(name, value+" (公开)");
        }else if (openStatus == OpenStatus.CLOSED)  {
            res.put(name, "(不公开)");
        }else if (openStatus == OpenStatus.AUTHORIZE){
            if(isAuthorize)
                res.put(name, value +  " (授权后公开)");
            else
                res.put(name,  "(授权后公开)");
        }

    }

    public void putPrivateInfo(Map<String, Object> res, User user , Inquiry inquiry){

        boolean isAuthorize= user.getId() != null && messageRepository.findAllUserAndInquiryAndStatus(user, inquiry, 1).size() != 0;


        String names[] = {"remark","contactName","contactEmail","contactPhone","contactTel",
                "contactFax","contactWeiBo","contactWeiXin"};

        for ( String name: names){
            putPropertiesByName(res,user,inquiry,name,isAuthorize);
        }

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties("filesOpen");
        List<InquiryFile> fileList = new ArrayList<InquiryFile>();
        if(inquiry.getUser().getId().equals(user.getId()) || openStatus==null || openStatus== OPEN ){
            res.put("files", " (公开)");
            fileList = inquiryFileRepository.findByInquiry(inquiry);
            res.put("fileList", fileList);
        }else if (openStatus == OpenStatus.CLOSED)  {
            res.put("files", "(不公开)");
        }else if (openStatus == OpenStatus.AUTHORIZE){
            res.put("files",  "(授权后公开)");
            if(isAuthorize){
                fileList = inquiryFileRepository.findByInquiry(inquiry);
            }
        }
        res.put("fileList", fileList);


    }

    public void putQuotationList(Map<String, Object> res, User user , Inquiry inquiry){

        List<Quotation> quotationList=quotationRepository.findByInquiry(inquiry);
        List<QuotationFile> quotationFileList;

        List<Map<String, Object>> myList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> histList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> fileList = new ArrayList<Map<String,Object>>();

        for( Quotation quotation : quotationList ){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("quotationId",quotation.getId());
            map.put("createTime",quotation.getCreateTime());
            map.put("round",quotation.getRound());
            map.put("totalPrice",quotation.getTotalPrice());
            map.put("status",quotation.getStatus());
            if(quotation.getUser().getUserCompanyInfo()!=null){
                map.put("userProvince",quotation.getUser().getUserCompanyInfo().getCompanyProvince().getName());
            }else{
                map.put("userProvince","");
            }
            map.put("userNickName",quotation.getUser().getNickName());

            if(quotation.getUser().getId().equals(user.getId())){
                myList.add(map);
            }else{
                histList.add(map);
            }
            quotationFileList=quotationFileRepository.findByQuotation(quotation);
            fileList = new ArrayList<Map<String,Object>>();
            for(QuotationFile quotationFile : quotationFileList){
                Map<String, Object> fileMap = new HashMap<String, Object>();
                fileMap.put("id",quotationFile.getId());
                fileMap.put("type",quotationFile.getType());
                fileMap.put("fileUrl",quotationFile.getFileUrl());
                fileMap.put("remark",quotationFile.getRemark());
                fileList.add(fileMap);
            }
            map.put("fileList",fileList);
        }

        res.put("myList",myList);
        res.put("hisList",histList);

    }


}
