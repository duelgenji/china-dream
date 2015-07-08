package com.dream.controller.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.message.Message;
import com.dream.entity.quotation.Quotation;
import com.dream.entity.quotation.QuotationFile;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.quotation.QuotationFileRepository;
import com.dream.repository.quotation.QuotationRepository;
import com.dream.repository.user.UserRepository;
import com.dream.utils.UploadUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Knight on 2015/7/6 11:30.
 */
@RestController
@RequestMapping("quotation")
@SessionAttributes("currentUser")
public class QuotationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    QuotationFileRepository quotationFileRepository;

    @Value("${file_url}")
    private String file_url;

    /**
     * 正式出价
     */
    @RequestMapping("generateQuotation")
    public Map<String, Object> generateQuotation(
            @RequestParam(required = false) long inquiryId,
            @RequestParam double totalPrice,
            MultipartHttpServletRequest request,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }


        Inquiry inquiry = inquiryRepository.findOne(inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","数据未查到");
            return res;
        }

        List<Message> list=messageRepository.findAllUserAndInquiryAndStatus(user,inquiry,1);
        if(list.size()==0){
            res.put("success",0);
            res.put("message","你没有获得授权");
            return res;
        }

        Quotation quotation = new Quotation();
        quotation.setUser(user);
        quotation.setInquiry(inquiry);
        quotation.setRound(inquiry.getRound());
        quotation.setTotalPrice(totalPrice);

        quotationRepository.save(quotation);


        Map<String,MultipartFile> fileMap = request.getFileMap();

        for (Map.Entry<String,MultipartFile> file :fileMap.entrySet()){


            String uname = file_url + inquiry.getId() + "q" + quotation.getId();

            String fileUrl  = UploadUtils.uploadTo7niu(0, uname, file.getValue());

            QuotationFile quotationFile = new QuotationFile();
            quotationFile.setQuotation(quotation);
            quotationFile.setFileUrl(fileUrl);
            quotationFile.setRemark(file.getValue().getOriginalFilename());

            if(file.getKey().contains("business")){
                quotationFile.setType(0);
                quotationFileRepository.save(quotationFile);

            }else if (file.getKey().contains("tech")){
                quotationFile.setType(1);
                quotationFileRepository.save(quotationFile);
            }
        }

        res.put("success",1);
        return res;

    }

    /**
     * 我的主页信息   我的指数等信息
     */
    @RequestMapping("retrieveUserDetail")
    public Map<String, Object> retrieveUserDetail(
            @RequestParam(required = false) long userId,
            @ModelAttribute("currentUser") User currentUser) {
        Map<String, Object> res = new HashMap<>();

        User user = userRepository.findOne(userId);
        if(user==null){
            res.put("success",0);
            res.put("message","没有该用户");
            return res;
        }


        //TODO 改为从数据库读取

        int quotationDoneTime= quotationRepository.countByInquiryAndUser(user.getId());
        int quotationSuccessTime= quotationRepository.countByInquiryAndUserAndStatus(user.getId(), 1);
        int quotationDoingTime= messageRepository.countByInquiryAndUser(user);

        long inquiryDoneTime = inquiryRepository.countByUser(user);
        long inquirySuccessTime = inquiryRepository.countByUserAndStatus(user, 1);
        long inquiryDoingTime = inquiryRepository.countByUserAndStatus(user, 0);
        long inquiryFailTime = inquiryRepository.countByUserAndStatus(user, 2);



        String qRate,iRate;
        if(quotationDoneTime == 0 || quotationSuccessTime == 0 ){
            qRate = "0";
        }else{
            qRate =String.format("%.2f", (double)quotationSuccessTime/(double)quotationDoneTime * 100);
        }

        if(inquiryDoneTime == 0l || inquirySuccessTime == 0l ){
            iRate = "0";
        }else{
            iRate =String.format("%.2f", (double)inquirySuccessTime/(double)inquiryDoneTime * 100);
        }

        res.put("quotationDoneTime",quotationDoneTime);
        res.put("quotationSuccessTime",quotationSuccessTime);
        res.put("quotationSuccessRate", qRate+"%");
        res.put("quotationDoingTime",quotationDoingTime);

        res.put("inquiryDoneTime",inquiryDoneTime);
        res.put("inquirySuccessTime",inquirySuccessTime);
        res.put("inquirySuccessRate",iRate+"%");
        res.put("inquiryDoingTime",inquiryDoingTime);

        res.put("inquiryFailTime",inquiryFailTime);

        res.put("userId",user.getId());
        res.put("logoUrl",user.getLogoUrl());
        res.put("nickname",user.getNickName());
        res.put("userType",user.getType());

        res.put("success",1);
        return res;
    }

    /**
     * 我的出价列表 type 0已报价 quotation  1 未报价 message
     */
    @RequestMapping("retrieveMyQuotationList")
    public Map<String, Object> retrieveMyQuotationList(
            @RequestParam(required = false) int type,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }


        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        if(type==0){
            List<Quotation> quotationList = quotationRepository.findByUser(user);

            for ( Quotation quotation : quotationList ){

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("quotationId", quotation.getId());
                map.put("quotationStatus", quotation.getStatus());
                map.put("quotationArchived", quotation.getArchived());
                map.put("round", quotation.getRound());
                map.put("quotationPrice", quotation.getTotalPrice());

                map.put("inquiryNo", quotation.getInquiry().getInquiryNo());
                map.put("inquiryTitle", quotation.getInquiry().getTitle());
                map.put("inquiryId", quotation.getInquiry().getId());
                map.put("inquiryMode", quotation.getInquiry().getInquiryMode().getName());
                map.put("inquiryIndustry", quotation.getInquiry().getCompanyIndustry().getName());
                map.put("inquiryProvince", quotation.getInquiry().getCompanyProvince().getName());
                map.put("inquiryNickname", quotation.getInquiry().getUser().getNickName());
                map.put("inquiryPrice", quotation.getInquiry().getTotalPrice());
                map.put("limitDate", DateFormatUtils.format(quotation.getInquiry().getLimitDate(), "yyyy-MM-dd HH:mm:ss"));

                list.add(map);
            }
        }else if(type==1){
            List<Message> messageList = messageRepository.findByUser(user);

            Inquiry inquiry;
            for ( Message message : messageList ){

                Map<String, Object> map = new HashMap<String, Object>();

                inquiry= message.getInquiry();

                map.put("quotationId", "");
                map.put("quotationStatus", "");
                map.put("quotationArchived", "");
                map.put("quotationPrice","待报价");

                map.put("round", inquiry.getRound());
                map.put("inquiryNo", inquiry.getInquiryNo());
                map.put("inquiryTitle", inquiry.getTitle());
                map.put("inquiryId", inquiry.getId());
                map.put("inquiryMode",inquiry.getInquiryMode().getName());
                map.put("inquiryIndustry", inquiry.getCompanyIndustry().getName());
                map.put("inquiryProvince", inquiry.getCompanyProvince().getName());
                map.put("inquiryNickname",inquiry.getUser().getNickName());
                map.put("inquiryPrice",inquiry.getTotalPrice());
                map.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));


                list.add(map);
            }
        }


        res.put("success",1);
        res.put("data",list);
        return res;
    }
}
