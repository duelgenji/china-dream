package com.dream.service.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.quotation.Quotation;
import com.dream.entity.quotation.QuotationFile;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import com.dream.entity.user.UserIndex;
import com.dream.repository.inquiry.InquiryFileRepository;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.quotation.QuotationFileRepository;
import com.dream.repository.quotation.QuotationRepository;
import com.dream.repository.user.UserIndexRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
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

    @Autowired
    UserIndexRepository userIndexRepository;

    private void putPropertiesByName(Map<String, Object> res, User user , Inquiry inquiry,String name,boolean isAuthorize){
        String value="";

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties( name + "Open");

        if(openStatus== OPEN || inquiry.getUser().getId().equals(user.getId()) || (openStatus == OpenStatus.AUTHORIZE && isAuthorize) || openStatus==null){
            if(inquiry.getProperties(name)!=null){
                value=inquiry.getProperties(name).toString();
            }
        }
        assert openStatus != null;
        res.put(name, value + "(" + openStatus.getName() +")");

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

        if(openStatus== OPEN || inquiry.getUser().getId().equals(user.getId()) || (openStatus == OpenStatus.AUTHORIZE && isAuthorize) || openStatus==null){
            fileList = inquiryFileRepository.findByInquiry(inquiry);
        }
        assert openStatus != null;
        res.put("files",  "(" + openStatus.getName() +")");

        res.put("fileList", fileList);

    }

    /**
     * 他人出价 我的出价
     */
    public void putQuotationList(Map<String, Object> res, User user , Inquiry inquiry){

        List<Quotation> quotationList=quotationRepository.findByInquiry(inquiry);
        List<QuotationFile> quotationFileList;

        List<Map<String, Object>> myList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> histList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> techFileList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> businessFileList = new ArrayList<Map<String,Object>>();

        for( Quotation quotation : quotationList ){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("quotationId",quotation.getId());
            map.put("createTime", DateFormatUtils.format(quotation.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("round",quotation.getRound());
            map.put("totalPrice",quotation.getTotalPrice());
            map.put("status",quotation.getStatus());
            map.put("userId",quotation.getUser().getId());
            map.put("userNickname",quotation.getUser().getNickName());
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
            businessFileList = new ArrayList<Map<String,Object>>();
            techFileList = new ArrayList<Map<String,Object>>();
            for(QuotationFile quotationFile : quotationFileList){
                Map<String, Object> fileMap = new HashMap<String, Object>();
                fileMap.put("id",quotationFile.getId());
                fileMap.put("type",quotationFile.getType());
                fileMap.put("fileUrl",quotationFile.getFileUrl());
                fileMap.put("remark",quotationFile.getRemark());
                if(quotationFile.getType()==0){
                    businessFileList.add(fileMap);
                }else if(quotationFile.getType()==1){
                    techFileList.add(fileMap);
                }
            }
            map.put("businessFileList",businessFileList);
            map.put("techFileList",techFileList);
        }

        res.put("myList",myList);
        res.put("hisList",histList);
    }


    public void calcUserIndex(User user){

        UserIndex userIndex = userIndexRepository.findOne(user.getId());

        if(userIndex==null){
            userIndex = new UserIndex();
            userIndex.setId(user.getId());
        }


        int quotationDoneTime= quotationRepository.countByInquiryAndUser(user.getId());
        int quotationSuccessTime= quotationRepository.countByInquiryAndUserAndStatus(user.getId(), 1);

        int inquiryDoneTime = inquiryRepository.countByUser(user);
        int inquirySuccessTime = inquiryRepository.countByUserAndStatus(user, 1);



        userIndex.setQuotationDoneTime(quotationDoneTime);
        userIndex.setQuotationSuccessTime(quotationSuccessTime);
        userIndex.setInquiryDoneTime(inquiryDoneTime);
        userIndex.setInquirySuccessTime(inquirySuccessTime);


        if(quotationDoneTime == 0 || quotationSuccessTime == 0 ){
            userIndex.setQuotationSuccessRate(0);
        }else{
            userIndex.setQuotationSuccessRate((double)quotationSuccessTime/(double)quotationDoneTime * 100);
        }

        if(inquiryDoneTime == 0l || inquirySuccessTime == 0l ){
            userIndex.setInquirySuccessRate(0);
        }else{
            userIndex.setInquirySuccessRate((double)inquirySuccessTime/(double)inquiryDoneTime * 100);
        }

        userIndexRepository.save(userIndex);
    }

}
