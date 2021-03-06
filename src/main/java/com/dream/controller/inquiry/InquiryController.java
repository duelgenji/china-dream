package com.dream.controller.inquiry;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyProvince;
import com.dream.entity.inquiry.*;
import com.dream.entity.message.Message;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import com.dream.repository.company.CompanyIndustryRepository;
import com.dream.repository.company.CompanyProvinceRepository;
import com.dream.repository.dream.SensitiveWordRepository;
import com.dream.repository.inquiry.*;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.quotation.QuotationRepository;
import com.dream.repository.user.UserCompanyInfoRepository;
import com.dream.repository.user.UserGroupInfoRepository;
import com.dream.repository.user.UserPersonalInfoRepository;
import com.dream.repository.user.UserRepository;
import com.dream.service.inquiry.InquiryService;
import com.dream.utils.CommonEmail;
import com.dream.utils.SensitiveWordFilter;
import com.dream.utils.UploadUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    CommonEmail commonEmail;

    @Autowired
    SensitiveWordRepository sensitiveWordRepository;


    @Value("${file_url}")
    private String file_url;

    @Value("${inquiry_url}")
    private String inquiry_url;

    /**
     * 发布询价/梦想
     */
    @Transactional
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

        user = userRepository.findOne(user.getId());

        Inquiry inquiry = new Inquiry();
        inquiry.setUser(user);

        /*敏感词判断*/
        SensitiveWordFilter filter = new SensitiveWordFilter(sensitiveWordRepository.findAll());

        if(filter.isContainSensitiveWord(title,1)){
            res.put("success", "0");
            res.put("message", "标题包含敏感词");
            return res;
        }


        inquiry.setTitle(title);
        if(round==0){
            inquiry.setRound(0);
        }else{
            inquiry.setRound(1);
        }

        CompanyIndustry companyIndustry = companyIndustryRepository.findOne(industryCode);
        if(companyIndustry==null){
            res.put("success", "0");
            res.put("message", "项目行业选择有误，请返回第一步重新勾选");
            return res;
        }
        inquiry.setCompanyIndustry(companyIndustry);
        CompanyProvince companyProvince = companyProvinceRepository.findOne(provinceCode);
        if(companyProvince==null){
            res.put("success", "0");
            res.put("message", "项目地区选择有误，请返回第一步重新勾选");
            return res;
        }
        inquiry.setCompanyProvince(companyProvince);


        try {
            inquiry.setLimitDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(limitDate));
        } catch (ParseException e) {
            e.printStackTrace();
            res.put("success", "0");
            res.put("message", "项目截止时间格式有误，正确范例：2015-5-10");
            return res;
        }
        inquiry.setTotalPrice(Math.abs(totalPrice));

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

        //测试用户 只能发测试标
        if(user.isTest()){
            inquiry.setTest(1);
        }

        Date date = new Date();

        inquiry.setInquiryNo(companyProvince.getAlias()+DateFormatUtils.format(date, "yyyyMMddHHmmssSSSS"));



        if (null != logoFile) {
            if(!UploadUtils.isImage(logoFile)){
                res.put("success", "0");
                res.put("message", "该附件不是图片类型");
                return res;
            }
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

        inquiry.setAuditStatus(0);
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

        if(userList!=null){
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

                    //邀请 邮件
                    commonEmail.sendEmail(invitedUser,commonEmail.getContent(CommonEmail.TYPE.INVITE_B,inquiry,invitedUser));


                    messageRepository.save(message);

                }
            }
        }

        //发送审核通知邮件
        commonEmail.auditEmail(inquiry);


        res.put("success",1);
        res.put("inquiryNo",inquiry.getInquiryNo());
        return res;

    }


    /**
     * 获取询价列表
     */
    @RequestMapping("retrieveInquiryList")
    public Map<String, Object> retrieveInquiryList(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer direction,
            @RequestParam(required = false) List<Long> industryCode,
            @RequestParam(required = false) List<Long> provinceCode,
            @RequestParam(required = false) List<Integer> round,
            @RequestParam(required = false) List<Integer> inquiryStatus,
            @RequestParam(required = false) List<Long> inquiryMode,
            @RequestParam(required = false) List<Integer> userType,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();


        Sort.Direction pageDirection = Sort.Direction.DESC;
        if(direction!=null && direction>=1){
            pageDirection = Sort.Direction.ASC;
        }

        if(type!=null){
            switch (type){
                case 1:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "goods");
                    break;
                case 2:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "totalPrice");
                    break;
                case 3:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "user.userIndex.inquirySuccessRate");
                    break;
                case 4:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "user.userIndex.inquiryDoneTime");
                    break;
                case 5:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "limitDate");
                    break;
                default:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "id");
                    break;
            }
        }

        Map<String, Object> filters = new HashMap<>();

        if(industryCode!=null && industryCode.size()>0){
            filters.put("companyIndustry_in", industryCode);
        }
        if(provinceCode!=null && provinceCode.size()>0){
            filters.put("companyProvince_in", provinceCode);
        }
        if(round!=null && round.size()>0){
            filters.put("round_in", round);
        }
        if(inquiryStatus!=null && inquiryStatus.size()>0){
            filters.put("status_in", inquiryStatus);
        }
        if(inquiryMode!=null && inquiryMode.size()>0){
            filters.put("inquiryMode_in", inquiryMode);
        }
        if(userType!=null && userType.size()>0){
            filters.put("user.type_in", userType);
        }
        if(minPrice!=null){
            filters.put("totalPrice_greaterThanOrEqualTo", minPrice);
        }
        if(maxPrice!=null){
            filters.put("totalPrice_lessThanOrEqualTo", maxPrice);
        }


        filters.put("removed_equal", 0);
        filters.put("user.removed_equal", 0);
        filters.put("auditStatus_equal", 2);

        Page<Inquiry> inquiryList= inquiryRepository.findAll(filters,pageable);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (Inquiry inquiry : inquiryList) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", inquiry.getId());
            map.put("userId", inquiry.getUser().getId());
            map.put("userName", inquiry.getUser().getNickName());
            map.put("VIP", inquiry.getUser().getVIP());
            map.put("title", inquiry.getTitle());
            map.put("inquiryNo", inquiry.getInquiryNo());
            map.put("status", inquiry.getStatus());
            map.put("totalPrice", inquiry.getTotalPrice());
            map.put("round", inquiry.getRound());
            map.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryMode", inquiry.getInquiryMode().getName());
            map.put("industryCode", inquiry.getCompanyIndustry()!=null ? inquiry.getCompanyIndustry().getName():"");
            map.put("provinceCode", inquiry.getCompanyProvince()!=null ? inquiry.getCompanyProvince().getName():"");
            map.put("test", inquiry.getTest());
            map.put("isGoods", (user.getId()!=null && inquiryGoodsRepository.findByInquiryAndUser(inquiry,user).size()>=1));
            map.put("adjustAmountRate", inquiry.getAdjustAmountRate());

//            inquiry.setGoods(inquiryGoodsRepository.countByInquiry(inquiry));
//            inquiryRepository.save(inquiry);
            map.put("goods",inquiry.getGoods());
            map.put("successRate", String.format("%.2f", inquiry.getUser().getUserIndex().getInquirySuccessRate()) + "%");
            map.put("inquiryTimes", inquiry.getUser().getUserIndex().getInquiryDoneTime());
            if(inquiry.getLogoUrl()==null || "".equals(inquiry.getLogoUrl())){
                map.put("logoUrl",inquiry.getCompanyIndustry().getLogoUrl()+"?imageView2/2/w/120&name=dl.jpg)");
            }else{
                map.put("logoUrl",inquiry.getLogoUrl()+"?imageView2/2/w/120&name=dl.jpg)" );
            }
            if(inquiry.getStatus()==1 && inquiry.isOpenWinner()){
                map.put("winner",inquiry.getWinner()!=null?inquiry.getWinner().getNickName():"");
            }

            if(inquiry.getStatus()==1 && inquiry.isOpenPrice()){
                map.put("winnerPrice", inquiry.getWinnerPrice());
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
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer direction,
            @RequestParam(required = false) List<Long> industryCode,
            @RequestParam(required = false) List<Long> provinceCode,
            @RequestParam(required = false) List<Integer> round,
            @RequestParam(required = false) List<Long> inquiryMode,
            @RequestParam(required = false) List<Integer> userType,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String key,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        Sort.Direction pageDirection = Sort.Direction.DESC;
        if(direction!=null && direction>=1){
            pageDirection = Sort.Direction.ASC;
        }

        if(type!=null){
            switch (type){
                case 1:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "goods");
                    break;
                case 2:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "totalPrice");
                    break;
                case 3:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "user.userIndex.inquirySuccessRate");
                    break;
                case 4:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "user.userIndex.inquiryDoneTime");
                    break;
                case 5:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "limitDate");
                    break;
                default:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageDirection, "id");
                    break;
            }
        }

        //fixme filter没使用
        Map<String, Object> filters = new HashMap<>();

        if(industryCode!=null && industryCode.size()>0){
            filters.put("companyIndustry_in", industryCode);
        }
        if(provinceCode!=null && provinceCode.size()>0){
            filters.put("companyProvince_in", provinceCode);
        }
        if(round!=null && round.size()>0){
            filters.put("round_in", round);
        }
        if(inquiryMode!=null && inquiryMode.size()>0){
            filters.put("inquiryMode_in", inquiryMode);
        }
        if(userType!=null && userType.size()>0){
            filters.put("user.type_in", userType);
        }
        if(minPrice!=null){
            filters.put("totalPrice_greaterThanOrEqualTo", minPrice);
        }
        if(maxPrice!=null){
            filters.put("totalPrice_lessThanOrEqualTo", maxPrice);
        }
//        if(key!=null && !key.equals("")){
//            filters.put("titleOrName_like", key);
//        }

        if(key!=null && !key.equals("")){
            key = "%" + key + "%";
        }

        Page<Inquiry> inquiryList= inquiryRepository.findByInquiryNoLikeOrTitleLike(key, key, pageable);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (Inquiry inquiry : inquiryList) {

            //被删除 未通过审核 不显示
            if(inquiry.getAuditStatus()!=2 || inquiry.isRemoved() || inquiry.getUser().isRemoved())
                continue;

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", inquiry.getId());
            map.put("userId", inquiry.getUser().getId());
            map.put("userName", inquiry.getUser().getNickName());
            map.put("VIP", inquiry.getUser().getVIP());
            map.put("title", inquiry.getTitle());
            map.put("inquiryNo", inquiry.getInquiryNo());
            map.put("status", inquiry.getStatus());
            map.put("totalPrice", inquiry.getTotalPrice());
            map.put("round", inquiry.getRound());
            map.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryMode", inquiry.getInquiryMode().getName());
            map.put("industryCode", inquiry.getCompanyIndustry()!=null ? inquiry.getCompanyIndustry().getName():"");
            map.put("provinceCode", inquiry.getCompanyProvince()!=null ? inquiry.getCompanyProvince().getName():"");
            map.put("test", inquiry.getTest());
            map.put("isGoods", (user.getId()!=null && inquiryGoodsRepository.findByInquiryAndUser(inquiry,user).size()>=1));
            map.put("adjustAmountRate", inquiry.getAdjustAmountRate());

//            inquiry.setGoods(inquiryGoodsRepository.countByInquiry(inquiry));
//            inquiryRepository.save(inquiry);
            map.put("goods",inquiry.getGoods());
            map.put("successRate", String.format("%.2f", inquiry.getUser().getUserIndex().getInquirySuccessRate()) + "%");
            map.put("inquiryTimes", inquiry.getUser().getUserIndex().getInquiryDoneTime());
            if(inquiry.getLogoUrl()==null || "".equals(inquiry.getLogoUrl())){
                map.put("logoUrl",inquiry.getCompanyIndustry().getLogoUrl()+"?imageView2/2/w/120&name=dl.jpg)");
            }else{
                map.put("logoUrl",inquiry.getLogoUrl()+"?imageView2/2/w/120&name=dl.jpg)" );
            }
            if(inquiry.getStatus()==1 && inquiry.isOpenWinner()){
                map.put("winner",inquiry.getWinner()!=null?inquiry.getWinner().getNickName():"");
            }
            if(inquiry.getStatus()==1 && inquiry.isOpenPrice()){
                map.put("winnerPrice", inquiry.getWinnerPrice());
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
        if(inquiry==null || inquiry.getAuditStatus()!=2){
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
        res.put("industryCode", inquiry.getCompanyIndustry()!=null ? inquiry.getCompanyIndustry().getName():"");
        res.put("provinceCode", inquiry.getCompanyProvince()!=null ? inquiry.getCompanyProvince().getName():"");
        res.put("userLimit", inquiry.getUserLimit());
        res.put("logoUrl", inquiry.getLogoUrl());
        res.put("test", inquiry.getTest());
        res.put("modifyDate", inquiry.getModifyDate());
        res.put("defaultAmountRate", inquiry.getDefaultAmountRate());
        res.put("adjustAmountRate", inquiry.getAdjustAmountRate());


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

        //不做这一步 user.type 是错误的0 原因待查
        user = userRepository.findOne(user.getId());


        Inquiry inquiry = inquiryRepository.findOne(inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","数据未查到");
            return res;
        }

        //出价 限制 0 不限 1 个人/群  2公司      用户类型  1个人  2企业 3群
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
        commonEmail.sendEmail(inquiry.getUser(), commonEmail.getContent(CommonEmail.TYPE.REQUEST_A, inquiry, user));


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
            if(status<=2){
                filters.put("status_equal", status);
                filters.put("auditStatus_equal", 2);
            }else if(status==3){
                //待审批
                filters.put("auditStatus_equal", 0);
            }else if(status==4){
                //审批退回
                filters.put("auditStatus_equal", 1);
            }
        }

        List<Inquiry> inquiryList = inquiryRepository.findAll(filters);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        for ( Inquiry inquiry : inquiryList ){

            //被删除 不显示
            if(inquiry.isRemoved())
                continue;

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("inquiryId", inquiry.getId());
            map.put("inquiryNo", inquiry.getInquiryNo());
            map.put("inquiryTitle", inquiry.getTitle());
            map.put("inquiryRound", inquiry.getRound());
            map.put("inquiryStatus", inquiry.getStatus());
            map.put("inquiryMode", inquiry.getInquiryMode().getName());
            map.put("inquiryIndustry", inquiry.getCompanyIndustry()!=null ? inquiry.getCompanyIndustry().getName():"");
            map.put("inquiryProvince", inquiry.getCompanyProvince()!=null ? inquiry.getCompanyProvince().getName():"");
            map.put("inquiryPrice", inquiry.getTotalPrice());
            map.put("auditStatus", inquiry.getAuditStatus());
            map.put("failReason", inquiry.getFailReason());
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
        res.put("industryCode", inquiry.getCompanyIndustry() != null ? inquiry.getCompanyIndustry().getId() : "");
        res.put("provinceCode", inquiry.getCompanyProvince() != null ? inquiry.getCompanyProvince().getId() : "");
        res.put("userLimit", inquiry.getUserLimit());
        res.put("round", inquiry.getRound());
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
        res.put("modifyDate", inquiry.getModifyDate());
        res.put("fileList", inquiryFileRepository.findByInquiryAndRound(inquiry,inquiry.getRound()));


        res.put("success",1);
        return res;
    }


    /**
     * 进入下一轮
     */
    @Transactional
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
        user = userRepository.findOne(user.getId());

        Inquiry inquiry = inquiryRepository.findByUserAndId(user, inquiryId);
        int auditStatus = inquiry.getAuditStatus();
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

        if(auditStatus==2){
            int round =inquiry.getRound();
            if(round<3){
                inquiryService.saveInquiryHistory(inquiry);
                inquiry.setRound(round+1);
                inquiry.setAuditStatus(0);

            }else{
                res.put("success",0);
                res.put("message","已经最后一轮");
                return res;
            }
        }

        /*敏感词判断*/
        SensitiveWordFilter filter = new SensitiveWordFilter(sensitiveWordRepository.findAll());
        if(filter.isContainSensitiveWord(title,1)){
            res.put("success", "0");
            res.put("message", "标题包含敏感词");
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
        inquiry.setTotalPrice(Math.abs(totalPrice));

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
        inquiry.setSendFailEmail(false);

        if (null != logoFile) {
            if(!UploadUtils.isImage(logoFile)){
                res.put("success", "0");
                res.put("message", "该附件不是图片类型");
                return res;
            }
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

        if(userList!=null){
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

                    //邀请 邮件
                    commonEmail.sendEmail(invitedUser,commonEmail.getContent(CommonEmail.TYPE.INVITE_B,inquiry,invitedUser));

                    messageRepository.save(message);
                }
            }
        }

        //发送下一轮 邮件
        if(auditStatus==2) {
            List<Message> messages = messageRepository.findByInquiryAndRoundAndStatus(inquiry, inquiry.getRound()-1,1);
            for(Message m : messages){
                commonEmail.sendEmail(m.getUser(),commonEmail.getContent(CommonEmail.TYPE.ROUND_B,inquiry,m.getUser()));
            }
        }else if(auditStatus==1){
            commonEmail.auditEmail(inquiry);
        }




        res.put("success",1);
        res.put("inquiryNo",inquiry.getInquiryNo());

        return res;
    }

    /**
     * 询价 成功 流标
     */
    @Transactional
    @RequestMapping("changeInquiryStatus")
    public Map<String, Object> changeInquiryStatus(
            @RequestParam(required = false) long inquiryId,
            @RequestParam(required = false) int status,
            @RequestParam(required = false) String failReason,
            @RequestParam(required = false) Boolean openWinner,
            @RequestParam(required = false) Boolean openPrice,
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
                //乙方用户
                User user_b = userRepository.findOne(userId);
                if(userId!=null && user_b!=null){
                    if(messageRepository.countByInquiryAndUserAndRoundAndStatusAndType(inquiry,userRepository.findOne(userId),inquiry.getRound(),0,1)>=1){
                        res.put("success",0);
                        res.put("message","已经发送过,请等待对方确认！");
                        return res;
                    }

                    int count = messageRepository.countByInquiryAndUserAndRoundAndStatusAndType(inquiry, user_b, inquiry.getRound(), 2, 1);
                    if(count>=2){
                        res.put("success",0);
                        res.put("message","您已经被拒绝2次，此用户不可以再被选择！");
                        return res;
                    }


                    inquiry.setOpenWinner(openWinner);
                    inquiry.setOpenPrice(openPrice);
                    inquiry.setFailReason(failReason);
                    //甲方同意后自动结束流程
                    inquiryService.chooseAndFinish(user_b,inquiry,price);

//                  改版之前的； MessageController.modifyMessageStatus 没有修改 因为不需要乙方站内信点击同意了
//                    Message message = new Message();
//                    message.setUser(user_b);
//                    message.setType(1);
//                    message.setContent(price.toString());
//                    message.setRound(inquiry.getRound());
//                    message.setInquiry(inquiry);
//                    message.setInquiryUser(user);
//                    messageRepository.save(message);
//                    inquiry.setOpenWinner(openWinner);
//                    inquiry.setOpenPrice(openPrice);
//
//                    //发送  选中用户邮件
//                    commonEmail.sendEmail(user_b,commonEmail.getContent(CommonEmail.TYPE.CHOSEN_B, inquiry, user_b));

                }
            }else if(status==2){
                /* 流标流程 */
                inquiry.setStatus(status);
                inquiry.setFailReason(failReason);
                //发送  流标邮件
                //获取所有授权用户(有站内信授权)
                List<Message> messages = messageRepository.findByInquiryAndStatus(inquiry,1);
                for(Message m : messages){
                    commonEmail.sendEmail(m.getUser(),commonEmail.getContent(CommonEmail.TYPE.FAIL_B,inquiry,m.getUser()));
                }

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

        List<InquiryGoods> inquiryGoodsList = inquiryGoodsRepository.findByInquiryAndUser(inquiry,user);
        if(inquiryGoodsList==null || inquiryGoodsList.size()==0){
            InquiryGoods inquiryGoods = new InquiryGoods();
            inquiryGoods.setInquiry(inquiry);
            inquiryGoods.setUser(user);
            inquiryGoodsRepository.save(inquiryGoods);
            res.put("isGoods",1);

        }else{
            for(InquiryGoods inquiryGoods :  inquiryGoodsList){
                inquiryGoodsRepository.delete(inquiryGoods);
            }
            res.put("isGoods", 0);
        }
        inquiry.setGoods(inquiryGoodsRepository.countByInquiry(inquiry));
        inquiryRepository.save(inquiry);

        res.put("goods",inquiry.getGoods());
        res.put("success",1);
        return res;
    }

    /**
     * 导出
     */
    @RequestMapping("export")
    public Map<String, Object> export(
//            @RequestParam(required = false) long userId,
            @RequestParam(required = false) String os
            , @ModelAttribute("currentUser") User user
    ){
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        String zipUrl = inquiryService.exportToExcel(user, os);
        if(zipUrl==null || zipUrl.equals("")){
            res.put("message","打包失败 请联系管理员");
            res.put("success",0);
            return res;
        }

        res.put("zipUrl",zipUrl);
        res.put("success",1);
        return res;
    }

}
