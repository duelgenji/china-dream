package com.dream.controller.inquiry;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyProvince;
import com.dream.entity.inquiry.*;
import com.dream.entity.message.Message;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import com.dream.repository.company.CompanyIndustryRepository;
import com.dream.repository.company.CompanyProvinceRepository;
import com.dream.repository.inquiry.*;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.quotation.QuotationRepository;
import com.dream.repository.user.UserCompanyInfoRepository;
import com.dream.repository.user.UserGroupInfoRepository;
import com.dream.repository.user.UserPersonalInfoRepository;
import com.dream.repository.user.UserRepository;
import com.dream.service.inquiry.InquiryService;
import com.dream.utils.UploadUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 标 接口
 * Created by Knight on 2015/6/25 15:56.
 */

@RestController
@RequestMapping("inquiry")
@SessionAttributes("currentUser")
public class InquiryController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPersonalInfoRepository userPersonalInfoRepository;

    @Autowired
    UserCompanyInfoRepository userCompanyInfoRepository;

    @Autowired
    UserGroupInfoRepository userGroupInfoRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    InquiryFileRepository inquiryFileRepository;

    @Autowired
    CompanyIndustryRepository companyIndustryRepository;

    @Autowired
    CompanyProvinceRepository companyProvinceRepository;

    @Autowired
    InquiryModeRepository inquiryModeRepository;

    @Autowired
    InquiryService inquiryService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    InquiryCollectionRepository inquiryCollectionRepository;

    @Autowired
    InquiryGoodsRepository inquiryGoodsRepository;


    @Value("${file_url}")
    private String file_url;

    @Value("${inquiry_url}")
    private String inquiry_url;

    /**
     * 发布询价/梦想
     */
    @RequestMapping("generateInquiry")
    public Map<String, Object> generateInquiry(
            @RequestParam(required = true) String title,
            @RequestParam(required = false) int round,
            @RequestParam(required = false) long provinceCode,
            @RequestParam(required = false) long industryCode,
            @RequestParam(required = true) String limitDate,
            @RequestParam(required = false) double totalPrice,
            @RequestParam(required = false) long inquiryModeCode,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) int remarkOpen,
            @RequestParam(required = false) int userLimit,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) int contactNameOpen,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) int contactEmailOpen,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) int contactPhoneOpen,
            @RequestParam(required = false) String contactTel,
            @RequestParam(required = false) int contactTelOpen,
            @RequestParam(required = false) String contactFax,
            @RequestParam(required = false) int contactFaxOpen,
            @RequestParam(required = false) String contactWeiXin,
            @RequestParam(required = false) int contactWeiXinOpen,
            @RequestParam(required = false) String contactWeiBo,
            @RequestParam(required = false) int contactWeiBoOpen,
            @RequestParam(required = false) int filesOpen,
            @RequestParam(required = false) Integer intervalHour,
            @RequestParam(required = false) String[] userList,
            @RequestParam(required = false) MultipartFile file1,
            @RequestParam(required = false) MultipartFile file2,
            @RequestParam(required = false) MultipartFile file3,
            @RequestParam(required = false) MultipartFile logoFile,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setUser(user);
        inquiry.setTitle(title);
        if(round==0){
            inquiry.setRound(0);
        }else{
            inquiry.setRound(1);
        }

        CompanyProvince companyProvince = companyProvinceRepository.findOne(provinceCode);
        inquiry.setCompanyProvince(companyProvince);
        CompanyIndustry companyIndustry = companyIndustryRepository.findOne(industryCode);
        inquiry.setCompanyIndustry(companyIndustry);

        try {
            inquiry.setLimitDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(limitDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        inquiry.setTotalPrice(totalPrice);

        InquiryMode inquiryMode=inquiryModeRepository.findOne(inquiryModeCode);
        inquiry.setInquiryMode(inquiryMode);
        if(inquiryModeCode==5l){
            inquiry.setIntervalHour(intervalHour);
        }
        inquiry.setRemark(remark);
        inquiry.setRemarkOpen(OpenStatus.values()[remarkOpen]);
        inquiry.setUserLimit(userLimit);

        inquiry.setContactName(contactName);
        inquiry.setContactNameOpen(OpenStatus.values()[contactNameOpen]);
        inquiry.setContactEmail(contactEmail);
        inquiry.setContactEmailOpen(OpenStatus.values()[contactEmailOpen]);
        inquiry.setContactPhone(contactPhone);
        inquiry.setContactPhoneOpen(OpenStatus.values()[contactPhoneOpen]);
        inquiry.setContactTel(contactTel);
        inquiry.setContactTelOpen(OpenStatus.values()[contactTelOpen]);
        inquiry.setContactFax(contactFax);
        inquiry.setContactFaxOpen(OpenStatus.values()[contactFaxOpen]);
        inquiry.setContactWeiXin(contactWeiXin);
        inquiry.setContactWeiXinOpen(OpenStatus.values()[contactWeiXinOpen]);
        inquiry.setContactWeiBo(contactWeiBo);
        inquiry.setContactWeiBoOpen(OpenStatus.values()[contactWeiBoOpen]);
        inquiry.setFilesOpen(OpenStatus.values()[filesOpen]);

        Date date = new Date();

        if(companyProvince!=null){
            inquiry.setInquiryNo(companyProvince.getAlias()+DateFormatUtils.format(date, "yyyyMMddHHmmssssss"));
        }else{
            res.put("success",0);
            res.put("message","行业编号错误");
            return res;
        }


        if (null != logoFile) {
            String uname;
            if (null == inquiry.getId()) {
                uname = inquiry_url + "u" + user.getId();
            } else {
                uname = inquiry_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, logoFile);

            inquiry.setLogoUrl(fileUrl);
        }

        inquiryRepository.save(inquiry);


        if (null != file1) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file1);
            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setRound(inquiry.getRound());
            inquiryFile.setFileUrl(fileUrl);
            inquiryFile.setRemark(file1.getOriginalFilename());
            inquiryFileRepository.save(inquiryFile);

        }

        if (null != file2) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file2);


            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setRound(inquiry.getRound());
            inquiryFile.setFileUrl(fileUrl);
            inquiryFile.setRemark(file2.getOriginalFilename());
            inquiryFileRepository.save(inquiryFile);

        }

        if (null != file3) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file3);

            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setRound(inquiry.getRound());
            inquiryFile.setFileUrl(fileUrl);
            inquiryFile.setRemark(file3.getOriginalFilename());
            inquiryFileRepository.save(inquiryFile);

        }

        User invitedUser;
        for(String s : userList){
            invitedUser = userRepository.findOne(Long.parseLong(s));
            if(invitedUser!=null){
                Message message =new Message();
                message.setInquiry(inquiry);
                message.setRound(inquiry.getRound());
                message.setUser(invitedUser);
                message.setType(0);
                message.setStatus(1);
                message.setInquiryUser(inquiry.getUser());

                messageRepository.save(message);

            }
        }



        res.put("success",1);
        res.put("inquiryNo",inquiry.getInquiryNo());
        return res;

    }


    /**
     * 获取询价列表
     */
    @RequestMapping("retrieveInquiryList")
    public Map<String, Object> retrieveInquiryList(
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        Page<Inquiry> inquiryList= inquiryRepository.findAll(pageable);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (Inquiry inquiry : inquiryList) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", inquiry.getId());
            map.put("userName", inquiry.getUser().getNickName());
            map.put("VIP", inquiry.getUser().getVIP());
            map.put("title", inquiry.getTitle());
            map.put("inquiryNo", inquiry.getInquiryNo());
            map.put("status", inquiry.getStatus());
            map.put("totalPrice", inquiry.getTotalPrice());
            map.put("round", inquiry.getRound());
            map.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryMode", inquiry.getInquiryMode().getName());
            map.put("industryCode", inquiry.getCompanyIndustry().getName());
            map.put("provinceCode", inquiry.getCompanyProvince().getName());
            map.put("test", inquiry.getTest());
            map.put("winnerPrice", inquiry.getWinnerPrice());
            map.put("isGoods", inquiryGoodsRepository.findByInquiryAndUser(inquiry,user)!=null);
            map.put("goods",inquiryGoodsRepository.countByInquiry(inquiry));
            map.put("successRate", String.format("%.2f", inquiry.getUser().getUserIndex().getInquirySuccessRate()) + "%");
            map.put("inquiryTimes", inquiry.getUser().getUserIndex().getInquiryDoneTime());
            if(inquiry.getLogoUrl()==null || "".equals(inquiry.getLogoUrl())){
                map.put("logoUrl",inquiry.getCompanyIndustry().getLogoUrl());
            }else{
                map.put("logoUrl",inquiry.getLogoUrl() );
            }
            if(inquiry.getStatus()==1 && inquiry.isOpenWinner()){
                map.put("winner",inquiry.getWinner()!=null?inquiry.getWinner().getNickName():"");
            }

            list.add(map);
        }

        res.put("success",1);
        res.put("data",list);
        res.put("count",inquiryList.getTotalElements());
        return res;
    }

    /**
     * 搜索询价列表
     */
    @RequestMapping("searchInquiryList")
    public Map<String, Object> searchInquiryList(
            @RequestParam(required = false) String key,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        key =  "%"+key+"%";
        Page<Inquiry> inquiryList= inquiryRepository.findByInquiryNoLikeOrTitleLike(key, key,pageable);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (Inquiry inquiry : inquiryList) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", inquiry.getId());
            map.put("userName", inquiry.getUser().getNickName());
            map.put("VIP", inquiry.getUser().getVIP());
            map.put("title", inquiry.getTitle());
            map.put("inquiryNo", inquiry.getInquiryNo());
            map.put("status", inquiry.getStatus());
            map.put("totalPrice", inquiry.getTotalPrice());
            map.put("round", inquiry.getRound());
            map.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryMode", inquiry.getInquiryMode().getName());
            map.put("industryCode", inquiry.getCompanyIndustry().getName());
            map.put("provinceCode", inquiry.getCompanyProvince().getName());
            map.put("test", inquiry.getTest());
            map.put("winnerPrice", inquiry.getWinnerPrice());
            map.put("isGoods", inquiryGoodsRepository.findByInquiryAndUser(inquiry,user)!=null);
            map.put("goods",inquiryGoodsRepository.countByInquiry(inquiry));
            map.put("successRate", String.format("%.2f", inquiry.getUser().getUserIndex().getInquirySuccessRate()) + "%");
            map.put("inquiryTimes", inquiry.getUser().getUserIndex().getInquiryDoneTime());
            if(inquiry.getLogoUrl()==null || "".equals(inquiry.getLogoUrl())){
                map.put("logoUrl",inquiry.getCompanyIndustry().getLogoUrl());
            }else{
                map.put("logoUrl",inquiry.getLogoUrl() );
            }
            if(inquiry.getStatus()==1 && inquiry.isOpenWinner()){
                map.put("winner",inquiry.getWinner()!=null?inquiry.getWinner().getNickName():"");
            }
            list.add(map);
        }

        res.put("success",1);
        res.put("data",list);
        res.put("count", inquiryList.getTotalElements());
        return res;
    }

    /**
     * 获取询价 详细信息
     */
    @RequestMapping("retrieveInquiryDetail")
    public Map<String, Object> retrieveInquiryDetail(
            @RequestParam(required = false) long inquiryId,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();
//
//        if(user.getId()==null){
//            res.put("success",0);
//            res.put("message","请先登录");
//            return res;
//        }


        Inquiry inquiry = inquiryRepository.findOne(inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","查询错误");
            return res;
        }
        res.put("id", inquiry.getId());
        res.put("userName", inquiry.getUser().getNickName());
        res.put("title", inquiry.getTitle());
        res.put("inquiryNo", inquiry.getInquiryNo());
        res.put("status", inquiry.getStatus());
        res.put("totalPrice", inquiry.getTotalPrice());
        res.put("round", inquiry.getRound());
        res.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
        res.put("inquiryMode", inquiry.getInquiryMode().getName());
        res.put("industryCode", inquiry.getCompanyIndustry().getName());
        res.put("provinceCode", inquiry.getCompanyProvince().getName());
        res.put("userLimit", inquiry.getUserLimit());
        res.put("logoUrl", inquiry.getLogoUrl());
        res.put("test", inquiry.getTest());


        inquiryService.putPrivateInfo(res,user,inquiry);
        inquiryService.putQuotationList(res,user,inquiry);
        if(user.getId()==null){
            res.put("isCollection", false);
            res.put("applyStatus", 0);
            res.put("isMe", 0);
            res.put("success",1);
            return res;
        }

        InquiryCollection ic = inquiryCollectionRepository.findByUserAndInquiry(user, inquiry);
        if(ic==null){
            res.put("isCollection", false);
        }else{
            res.put("isCollection", true);
            res.put("collectionId", ic.getId());
        }

        List<Message> messageList = messageRepository.findAllUserAndInquiry(user, inquiry);
        if(messageList.size()==0 || messageList.get(0).getStatus()==2){
            res.put("applyStatus", 0);
        }else if(messageList.get(0).getStatus()==0) {
            res.put("applyStatus", 1);
        }else if(messageList.get(0).getStatus()==1) {
            res.put("applyStatus", 2);
        }


        if(inquiry.getUser().getId().equals(user.getId())){
            res.put("isMe", 1);
            inquiryService.putExRoundInfo(res,inquiry);
        }else{
            res.put("isMe", 0);
        }


        res.put("success",1);
        return res;
    }



    /**
     * 申请出价 发送站内信
     */
    @RequestMapping("sendInquiryMessage")
    public Map<String, Object> sendInquiryMessage(
            @RequestParam(required = false) long inquiryId,
            @RequestParam(required = false) String description,
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

        //出价 限制 0 不限 1 个人/群  2公司
        if(inquiry.getUserLimit()==2 && user.getType()!=2){
            res.put("success",0);
            res.put("message","该标只允许公司用户出价！");
            return res;
        }else if(inquiry.getUserLimit()==1 && user.getType()==2){
            res.put("success",0);
            res.put("message","该标只允许(个人/群)用户出价！");
            return res;
        }


        List<Message> list=messageRepository.findAllUserAndInquiryAndStatus(user,inquiry,0);
        if(list.size()>=1){
            res.put("success",0);
            res.put("message","已经发送过,请等待对方确认");
            return res;
        }

        int count = messageRepository.countByInquiryAndUserAndRoundAndStatusAndType(inquiry, user, inquiry.getRound(), 2, 0);
        if(count>=2){
            res.put("success",0);
            res.put("message","您已经被拒绝2次，此标该轮不可再申请！");
            return res;
        }


        Message message =new Message();
        message.setInquiry(inquiry);
        message.setRound(inquiry.getRound());
        message.setUser(user);
        message.setType(0);
        message.setInquiryUser(inquiry.getUser());
        message.setContent(description);

        messageRepository.save(message);

        res.put("success",1);
        return res;

    }

    /**
     * 我的询价列表
     */
    @RequestMapping("retrieveMyInquiryList")
    public Map<String, Object> retrieveMyInquiryList(
            @RequestParam(required = false) Integer status,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Map<String, Object> filters = new HashMap<>();

        filters.put("user_equal", user);
        if(status!=null){
            filters.put("status_equal", status);
        }

        List<Inquiry> inquiryList = inquiryRepository.findAll(filters);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        for ( Inquiry inquiry : inquiryList ){

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("inquiryId", inquiry.getId());
            map.put("inquiryNo", inquiry.getInquiryNo());
            map.put("inquiryTitle", inquiry.getTitle());
            map.put("inquiryRound", inquiry.getRound());
            map.put("inquiryStatus", inquiry.getStatus());
            map.put("inquiryMode", inquiry.getInquiryMode().getName());
            map.put("inquiryIndustry", inquiry.getCompanyIndustry().getName());
            map.put("inquiryProvince", inquiry.getCompanyProvince().getName());
            map.put("inquiryPrice", inquiry.getTotalPrice());
            map.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));

            list.add(map);

        }
        res.put("success",1);
        res.put("data",list);
        return res;
    }



    /**
     * 获取询价信息 用于修改进入下一轮
     */
    @RequestMapping("retrieveInquiryInfo")
    public Map<String, Object> retrieveInquiryInfo(
            @RequestParam(required = false) long inquiryId,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Inquiry inquiry = inquiryRepository.findByUserAndId(user, inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","查询错误");
            return res;
        }
        if(inquiry.getStatus()!=0){
            res.put("success",0);
            res.put("message","该标已经结束。");
            return res;
        }
        res.put("inquiryId", inquiry.getId());
        res.put("userName", inquiry.getUser().getNickName());
        res.put("title", inquiry.getTitle());
        res.put("totalPrice", inquiry.getTotalPrice());
        res.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
        res.put("inquiryModeCode", inquiry.getInquiryMode().getId());
        res.put("industryCode", inquiry.getCompanyIndustry().getId());
        res.put("provinceCode", inquiry.getCompanyProvince().getId());
        res.put("userLimit", inquiry.getUserLimit());
        res.put("logoUrl", inquiry.getLogoUrl());

        res.put("remark", inquiry.getRemark());
        res.put("remarkOpen", inquiry.getRemarkOpen().ordinal());
        res.put("contactName", inquiry.getContactName());
        res.put("contactNameOpen", inquiry.getContactNameOpen().ordinal());
        res.put("contactEmail", inquiry.getContactEmail());
        res.put("contactEmailOpen", inquiry.getContactEmailOpen().ordinal());
        res.put("contactPhone", inquiry.getContactPhone());
        res.put("contactPhoneOpen", inquiry.getContactPhoneOpen().ordinal());
        res.put("contactTel", inquiry.getContactTel());
        res.put("contactTelOpen", inquiry.getContactTelOpen().ordinal());
        res.put("contactFax", inquiry.getContactFax());
        res.put("contactFaxOpen", inquiry.getContactFaxOpen().ordinal());
        res.put("contactWeiBo", inquiry.getContactWeiBo());
        res.put("contactWeiBoOpen", inquiry.getContactWeiBoOpen().ordinal());
        res.put("contactWeiXin", inquiry.getContactWeiXin());
        res.put("contactWeiXinOpen", inquiry.getContactWeiXinOpen().ordinal());
        res.put("filesOpen", inquiry.getFilesOpen().ordinal());
        res.put("intervalHour", inquiry.getIntervalHour());

        res.put("success",1);
        return res;
    }


    /**
     * 进入下一轮
     */
    @RequestMapping("inquiryNextRound")
    public Map<String, Object> inquiryNextRound(
            @RequestParam(required = false) long inquiryId,
            @RequestParam(required = true) String title,
            @RequestParam(required = false) long provinceCode,
            @RequestParam(required = false) long industryCode,
            @RequestParam(required = true) String limitDate,
            @RequestParam(required = false) double totalPrice,
            @RequestParam(required = false) long inquiryModeCode,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) int remarkOpen,
            @RequestParam(required = false) int userLimit,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) int contactNameOpen,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) int contactEmailOpen,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) int contactPhoneOpen,
            @RequestParam(required = false) String contactTel,
            @RequestParam(required = false) int contactTelOpen,
            @RequestParam(required = false) String contactFax,
            @RequestParam(required = false) int contactFaxOpen,
            @RequestParam(required = false) String contactWeiXin,
            @RequestParam(required = false) int contactWeiXinOpen,
            @RequestParam(required = false) String contactWeiBo,
            @RequestParam(required = false) int contactWeiBoOpen,
            @RequestParam(required = false) int filesOpen,
            @RequestParam(required = false) Integer intervalHour,
            @RequestParam(required = false) String[] userList,
            @RequestParam(required = false) MultipartFile file1,
            @RequestParam(required = false) MultipartFile file2,
            @RequestParam(required = false) MultipartFile file3,
            @RequestParam(required = false) MultipartFile logoFile,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Inquiry inquiry = inquiryRepository.findByUserAndId(user, inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","查询错误");
            return res;
        }

        if(inquiry.getStatus()!=0){
            res.put("success",0);
            res.put("message","此标已经结束，请重新申请新标！");
            return res;
        }

        int round =inquiry.getRound();
        if(round<3){
            inquiryService.saveInquiryHistory(inquiry);
            inquiry.setRound(round+1);
        }else{
            res.put("success",0);
            res.put("message","已经最后一轮");
            return res;
        }


        inquiry.setTitle(title);
        CompanyProvince companyProvince = companyProvinceRepository.findOne(provinceCode);
        if(companyProvince!=null)
            inquiry.setCompanyProvince(companyProvince);
        CompanyIndustry companyIndustry = companyIndustryRepository.findOne(industryCode);
        if(companyIndustry!=null)
            inquiry.setCompanyIndustry(companyIndustry);

        try {
            inquiry.setLimitDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(limitDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        inquiry.setTotalPrice(totalPrice);

        InquiryMode inquiryMode=inquiryModeRepository.findOne(inquiryModeCode);
        inquiry.setInquiryMode(inquiryMode);
        if(inquiryModeCode==5l){
            inquiry.setIntervalHour(intervalHour);
        }
        inquiry.setRemark(remark);
        inquiry.setRemarkOpen(OpenStatus.values()[remarkOpen]);
        inquiry.setUserLimit(userLimit);

        inquiry.setContactName(contactName);
        inquiry.setContactNameOpen(OpenStatus.values()[contactNameOpen]);
        inquiry.setContactEmail(contactEmail);
        inquiry.setContactEmailOpen(OpenStatus.values()[contactEmailOpen]);
        inquiry.setContactPhone(contactPhone);
        inquiry.setContactPhoneOpen(OpenStatus.values()[contactPhoneOpen]);
        inquiry.setContactTel(contactTel);
        inquiry.setContactTelOpen(OpenStatus.values()[contactTelOpen]);
        inquiry.setContactFax(contactFax);
        inquiry.setContactFaxOpen(OpenStatus.values()[contactFaxOpen]);
        inquiry.setContactWeiXin(contactWeiXin);
        inquiry.setContactWeiXinOpen(OpenStatus.values()[contactWeiXinOpen]);
        inquiry.setContactWeiBo(contactWeiBo);
        inquiry.setContactWeiBoOpen(OpenStatus.values()[contactWeiBoOpen]);
        inquiry.setFilesOpen(OpenStatus.values()[filesOpen]);

        inquiry.setModifyDate(new Date());

        if (null != logoFile) {
            String uname;
            if (null == inquiry.getId()) {
                uname = inquiry_url + "u" + user.getId();
            } else {
                uname = inquiry_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, logoFile);

            inquiry.setLogoUrl(fileUrl);
        }

        inquiryRepository.save(inquiry);


        if (null != file1) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file1);
            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setRound(inquiry.getRound());
            inquiryFile.setFileUrl(fileUrl);
            inquiryFile.setRemark(file1.getOriginalFilename());
            inquiryFileRepository.save(inquiryFile);

        }

        if (null != file2) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file2);


            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setRound(inquiry.getRound());
            inquiryFile.setFileUrl(fileUrl);
            inquiryFile.setRemark(file2.getOriginalFilename());
            inquiryFileRepository.save(inquiryFile);

        }

        if (null != file3) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file3);

            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setRound(inquiry.getRound());
            inquiryFile.setFileUrl(fileUrl);
            inquiryFile.setRemark(file3.getOriginalFilename());
            inquiryFileRepository.save(inquiryFile);

        }
        User invitedUser;
        for(String s : userList){
            invitedUser = userRepository.findOne(Long.parseLong(s));
            if(invitedUser!=null){
                Message message =new Message();
                message.setInquiry(inquiry);
                message.setRound(inquiry.getRound());
                message.setUser(invitedUser);
                message.setType(0);
                message.setStatus(1);
                message.setInquiryUser(inquiry.getUser());
                messageRepository.save(message);
            }
        }


        res.put("success",1);
        res.put("inquiryNo",inquiry.getInquiryNo());

        return res;
    }

    /**
     * 询价 成功 流标
     */
    @RequestMapping("changeInquiryStatus")
    public Map<String, Object> changeInquiryStatus(
            @RequestParam(required = false) long inquiryId,
            @RequestParam(required = false) int status,
            @RequestParam(required = false) String failReason,
            @RequestParam(required = false) Boolean openWinner,
            @RequestParam(required = false) Long price,
            @RequestParam(required = false) Long userId,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Inquiry inquiry = inquiryRepository.findByUserAndId(user, inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","查询错误");
            return res;
        }

        int curStatus =inquiry.getStatus();
        if(curStatus!=0){
            res.put("success",0);
            res.put("message","当前状态不能更改");
            return res;
        }else{
            /* status 1 完成  2流标 */
            if(status==1){
                if(userId!=null && userRepository.findOne(userId)!=null){
                    if(messageRepository.countByInquiryAndUserAndRoundAndStatusAndType(inquiry,userRepository.findOne(userId),inquiry.getRound(),0,1)>=1){
                        res.put("success",0);
                        res.put("message","已经发送过,请等待对方确认！");
                        return res;
                    }
                    Message message = new Message();
                    message.setUser(userRepository.findOne(userId));
                    message.setType(1);
                    message.setContent(price.toString());
                    message.setRound(inquiry.getRound());
                    message.setInquiry(inquiry);
                    message.setInquiryUser(user);
                    messageRepository.save(message);
                    inquiry.setOpenWinner(openWinner);
                }
            }else if(status==2){
                /* 流标流程 */
                inquiry.setStatus(status);
                inquiry.setFailReason(failReason);
            }
            inquiryRepository.save(inquiry);
        }

        res.put("success",1);
        return res;
    }


    /**
     * 询价 赞 取消赞
     */
    @RequestMapping("inquiryGood")
    public Map<String, Object> inquiryGood(
            @RequestParam(required = false) long inquiryId,
            @ModelAttribute("currentUser") User user){
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Inquiry inquiry = inquiryRepository.findOne(inquiryId);

        InquiryGoods inquiryGoods = inquiryGoodsRepository.findByInquiryAndUser(inquiry,user);
        if(inquiryGoods==null){
            inquiryGoods = new InquiryGoods();
            inquiryGoods.setInquiry(inquiry);
            inquiryGoods.setUser(user);
            inquiryGoodsRepository.save(inquiryGoods);
            res.put("isGoods",1);

        }else{
            inquiryGoodsRepository.delete(inquiryGoods);
            res.put("isGoods", 0);
        }

        res.put("goods",inquiryGoodsRepository.countByInquiry(inquiry));
        res.put("success",1);
        return res;
    }

}
