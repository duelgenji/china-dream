package com.dream.service.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.inquiry.InquiryHistory;
import com.dream.entity.quotation.Quotation;
import com.dream.entity.quotation.QuotationFile;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import com.dream.entity.user.UserIndex;
import com.dream.repository.inquiry.InquiryFileRepository;
import com.dream.repository.inquiry.InquiryHistoryRepository;
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

    @Autowired
    InquiryHistoryRepository inquiryHistoryRepository;

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
            fileList = inquiryFileRepository.findByInquiryAndRound(inquiry,inquiry.getRound());
        }
        assert openStatus != null;
        res.put("files",  "(" + openStatus.getName() +")");

        res.put("fileList", fileList);

    }

    /**
     * 他人出价 我的出价
     */
    public void putQuotationList(Map<String, Object> res, User user , Inquiry inquiry){

        List<Quotation> quotationList=quotationRepository.findByInquiryAndRound(inquiry, inquiry.getRound());
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


    /**
     * 计算 个人指数
     */
    public void calcUserIndex(User user){

        UserIndex userIndex = userIndexRepository.findOne(user.getId());

        if(userIndex==null){
            userIndex = new UserIndex();
            userIndex.setId(user.getId());
        }


        int quotationDoneTime= quotationRepository.countByInquiryAndUser(user.getId());
        int quotationSuccessTime= inquiryRepository.countByWinner(user);

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


    public void saveInquiryHistory(Inquiry inquiry){
        InquiryHistory inquiryHistory = new InquiryHistory();
        inquiryHistory.setInquiry(inquiry);
        inquiryHistory.setInquiryNo(inquiry.getInquiryNo());
        inquiryHistory.setStatus(inquiry.getStatus());
        inquiryHistory.setRound(inquiry.getRound());
        inquiryHistory.setCompanyIndustry(inquiry.getCompanyIndustry());
        inquiryHistory.setCompanyProvince(inquiry.getCompanyProvince());
        inquiryHistory.setInquiryMode(inquiry.getInquiryMode());

        inquiryHistory.setRemark(inquiry.getRemark());
        inquiryHistory.setContactEmail(inquiry.getContactEmail());
        inquiryHistory.setContactWeiBo(inquiry.getContactWeiXin());
        inquiryHistory.setContactWeiXin(inquiry.getContactWeiXin());
        inquiryHistory.setContactFax(inquiry.getContactFax());
        inquiryHistory.setContactName(inquiry.getContactName());
        inquiryHistory.setContactPhone(inquiry.getContactPhone());
        inquiryHistory.setContactTel(inquiry.getContactTel());

        inquiryHistory.setRemarkOpen(inquiry.getRemarkOpen());
        inquiryHistory.setContactEmailOpen(inquiry.getContactEmailOpen());
        inquiryHistory.setContactWeiBoOpen(inquiry.getContactWeiBoOpen());
        inquiryHistory.setContactWeiXinOpen(inquiry.getContactWeiXinOpen());
        inquiryHistory.setContactFaxOpen(inquiry.getContactFaxOpen());
        inquiryHistory.setContactNameOpen(inquiry.getContactNameOpen());
        inquiryHistory.setContactPhoneOpen(inquiry.getContactPhoneOpen());
        inquiryHistory.setContactTelOpen(inquiry.getContactTelOpen());
        inquiryHistory.setFilesOpen(inquiry.getFilesOpen());

        inquiryHistory.setPurchaseCloseDate(inquiry.getPurchaseCloseDate());
        inquiryHistory.setLimitDate(inquiry.getLimitDate());
        inquiryHistory.setModifyDate(inquiry.getModifyDate());
        inquiryHistory.setCreateDate(inquiry.getCreateDate());
        inquiryHistory.setHotLevel(inquiry.getHotLevel());
        inquiryHistory.setTotalPrice(inquiry.getTotalPrice());
        inquiryHistory.setTitle(inquiry.getTitle());
        inquiryHistory.setUserLimit(inquiry.getUserLimit());
        inquiryHistory.setUser(inquiry.getUser());
        inquiryHistory.setLogoUrl(inquiry.getLogoUrl());

        inquiryHistoryRepository.save(inquiryHistory);

    }

    /**
     * 计算前两轮数据
     */
    public void putExRoundInfo(Map<String, Object> res, Inquiry inquiry){

        List<InquiryHistory> inquiryHistoryList = inquiryHistoryRepository.findByInquiry(inquiry);
        List<Map<String, Object>> historyList = new ArrayList<Map<String,Object>>();

        for (InquiryHistory inquiryHistory : inquiryHistoryList){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("round",inquiryHistory.getRound());
            map.put("userName", inquiryHistory.getUser().getNickName());
            map.put("title", inquiryHistory.getTitle());
            map.put("inquiryNo", inquiryHistory.getInquiryNo());
            map.put("status", inquiryHistory.getStatus());
            map.put("totalPrice", inquiryHistory.getTotalPrice());
            map.put("round", inquiryHistory.getRound());
            map.put("limitDate", DateFormatUtils.format(inquiryHistory.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryMode", inquiryHistory.getInquiryMode().getName());
            map.put("industryCode", inquiryHistory.getCompanyIndustry().getName());
            map.put("provinceCode", inquiryHistory.getCompanyProvince().getName());
            map.put("userLimit", inquiryHistory.getUserLimit());
            map.put("logoUrl", inquiryHistory.getLogoUrl());
            map.put("test", inquiryHistory.getTest());


            putPrivateInfo(map,inquiryHistory);
            putQuotationList(map, inquiryHistory);
            historyList.add(map);
        }

        res.put("historyList",historyList);

    }


    /**
     * 用于计算询价历史信息
     */
    private void putPropertiesByName(Map<String, Object> res ,InquiryHistory inquiry,String name){
        String value="";

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties( name + "Open");

        if(inquiry.getProperties(name)!=null){
            value=inquiry.getProperties(name).toString();
        }

        assert openStatus != null;
        res.put(name, value + "(" + openStatus.getName() +")");

    }

    /**
     * 用于计算询价历史信息
     */
    public void putPrivateInfo(Map<String, Object> res, InquiryHistory inquiry){


        String names[] = {"remark","contactName","contactEmail","contactPhone","contactTel",
                "contactFax","contactWeiBo","contactWeiXin"};

        for ( String name: names){
            putPropertiesByName(res,inquiry,name);
        }

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties("filesOpen");
        List<InquiryFile> fileList = inquiryFileRepository.findByInquiryAndRound(inquiry.getInquiry(),inquiry.getInquiry().getRound());

        assert openStatus != null;
        res.put("files",  "(" + openStatus.getName() +")");

        res.put("fileList", fileList);

    }

    /**
     *  询价历史 他人出价
     */
    public void putQuotationList(Map<String, Object> res,  InquiryHistory ih){

        List<Quotation> quotationList=quotationRepository.findByInquiryAndRound(ih.getInquiry(),ih.getRound());
        List<QuotationFile> quotationFileList;

        List<Map<String, Object>> histList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> techFileList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> businessFileList = new ArrayList<Map<String,Object>>();

        for( Quotation quotation : quotationList ){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("quotationId",quotation.getId());
            map.put("createTime", DateFormatUtils.format(quotation.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("round",quotation.getRound());
            map.put("totalPrice",quotation.getTotalPrice());
            map.put("userId",quotation.getUser().getId());
            map.put("userNickname",quotation.getUser().getNickName());
            if(quotation.getUser().getUserCompanyInfo()!=null){
                map.put("userProvince",quotation.getUser().getUserCompanyInfo().getCompanyProvince().getName());
            }else{
                map.put("userProvince","");
            }
            map.put("userNickName",quotation.getUser().getNickName());


            histList.add(map);

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
        res.put("hisList",histList);
    }

}
