package com.dream.controller.dream;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.Manager;
import com.dream.entity.user.User;
import com.dream.entity.user.UserAccountLog;
import com.dream.entity.user.UserIndex;
import com.dream.repository.inquiry.InquiryFileRepository;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.user.UserAccountLogRepository;
import com.dream.repository.user.UserIndexRepository;
import com.dream.repository.user.UserRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Knight on 2015/7/28 17:02.
 */
@RestController
@RequestMapping("backend")
@SessionAttributes("currentManager")
public class BackendController {

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    InquiryFileRepository inquiryFileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserIndexRepository userIndexRepository;

    @Autowired
    UserAccountLogRepository userAccountLogRepository;

    /**
     * 用户删除
     */
    @RequestMapping("removeUser")
    public Map<String, Object> removeUser(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        User user = userRepository.findOne(id);

        if(user==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        if(user.isRemoved()){
            user.setRemoved(false);
        }else{
            user.setRemoved(true);
        }
        userRepository.save(user);

        res.put("success",1);
        return res;
    }

    /**
     * 用户 加v
     */
    @RequestMapping("upgradeUser")
    public Map<String, Object> upgradeUser(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        User user = userRepository.findOne(id);

        if(user==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        if(user.getVIP()==1){
            user.setVIP(0);
        }else{
            user.setVIP(1);
        }

        userRepository.save(user);

        res.put("success",1);
        return res;
    }

    /**
     * 用户 加测试
     */
    @RequestMapping("testUser")
    public Map<String, Object> testUser(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        User user = userRepository.findOne(id);

        if(user==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        if(user.isTest()){
            user.setTest(false);
        }else{
            user.setTest(true);
        }

        userRepository.save(user);

        res.put("success",1);
        return res;
    }



    /**
     * 标 删除
     */
    @RequestMapping("removeInquiry")
    public Map<String, Object> removeInquiry(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        Inquiry inquiry = inquiryRepository.findOne(id);

        if(inquiry==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        inquiry.setRemoved(true);
        inquiryRepository.save(inquiry);

        res.put("success",1);
        return res;
    }

    /**
     * 搜索 用户列表
     */
    @RequestMapping("searchUserBack")
    public Map<String, Object> searchUserBack(
            @RequestParam(required = false) String key,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("currentManager") Manager manager) {
        Map<String, Object> res = new HashMap<>();


        key =  "%"+key+"%";

        Page<User> userPage ;


        userPage = userRepository.findByNickNameLikeAndStatus(key, 0, pageable);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        String industry="",province="";
        double amount = 0;
        String logoUrl;

        for (User u : userPage) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId",u.getId());
            map.put("nickname",u.getNickName());
            map.put("status", u.getStatus());
            map.put("VIP", u.getVIP());
            map.put("test", u.isTest());
            map.put("email",u.getEmail());
            map.put("removed", u.isRemoved());

            logoUrl=u.getLogoUrl();
            if(logoUrl!=null && !logoUrl.equals("")){
                //按百分比缩放
                logoUrl+="?imageView2/2/w/40&name=dl.jpg";
//                logoUrl+="imageView2/2/w/200&attname=down2.jpg";
            }
            map.put("logoUrl",logoUrl);
            map.put("userType",u.getType());
            map.put("createDate", DateFormatUtils.format(u.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            if( u.getCompanyIndustry()!=null)
                industry = u.getCompanyIndustry().getName();
            if( u.getCompanyProvince() !=null)
                province = u.getCompanyProvince().getName();
            if( u.getUserIndex() !=null)
                amount = u.getUserIndex().getAmount();

            map.put("industry", industry);
            map.put("province", province);
            map.put("amount", amount);
            list.add(map);
        }


        res.put("success",1);
        res.put("data",list);
        return res;
    }

    /**
     * 手动激活
     */
    @RequestMapping("activateAccount")
    public Map<String, Object> activateAccount(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager) {

        Map<String, Object> res = new HashMap<>();

        User user;
        user = userRepository.findOne(id);
        if(user==null){
            res.put("success", "0");
            res.put("message", "没有该用户");
            return res;
        }


        user.setStatus(1);
        user.setCaptcha("");
        userRepository.save(user);

        UserIndex userIndex = new UserIndex();
        userIndex.setId(user.getId());
        userIndexRepository.save(userIndex);

        res.put("success", "1");
        return res;

    }


    /**
     * 更改用户余额
     */
    @Transactional
    @RequestMapping("modifyUserAccount")
    public Map<String, Object> modifyUserAccount(
            @RequestParam Long id,
            @RequestParam double amount,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String project,
            @ModelAttribute("currentManager") Manager manager) {

        Map<String, Object> res = new HashMap<>();

        User user;
        user = userRepository.findOne(id);
        UserIndex userIndex = userIndexRepository.findOne(id);

        if(user==null || userIndex ==null){
            res.put("success", "0");
            res.put("message", "没有该用户");
            return res;
        }

        double currentAmount =  userIndex.getAmount();
        DecimalFormat df=new DecimalFormat("0.00");
        currentAmount = new Double(df.format(currentAmount + amount));

        UserAccountLog userAccountLog = new UserAccountLog();
        userAccountLog.setUser(user);
        userAccountLog.setAuto(false);
        userAccountLog.setAmountChange(amount);
        userAccountLog.setProject(project);
        userAccountLog.setRemark(remark);
        userAccountLog.setCurrentAmount(currentAmount);
        userAccountLogRepository.save(userAccountLog);

        userIndex.setAmount(currentAmount);
        userIndexRepository.save(userIndex);

        res.put("success", "1");
        return res;

    }

    /**
     * 更改标 费率
     */
    @Transactional
    @RequestMapping("modifyAdjustAmountRate")
    public Map<String, Object> modifyAdjustAmountRate(
            @RequestParam Long id,
            @RequestParam double adjustAmountRate,
            @ModelAttribute("currentManager") Manager manager) {

        Map<String, Object> res = new HashMap<>();

        Inquiry inquiry = inquiryRepository.findOne(id);

        if(inquiry==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        inquiry.setAdjustAmountRate(Math.abs(adjustAmountRate));
        inquiryRepository.save(inquiry);

        res.put("success", "1");
        return res;

    }




    /**
     * 根据审核状态 获取询价列表
     */
    @RequestMapping("auditInquiryList")
    public Map<String, Object> auditInquiryList(
            @RequestParam(required = false) Integer auditStatus,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable
//            , @ModelAttribute("currentManager") Manager manager
    ) {

        Map<String, Object> res = new HashMap<>();

        Map<String, Object> filters = new HashMap<>();

        filters.put("auditStatus_equal", auditStatus);
        filters.put("user.removed_equal", 0);

        Page<Inquiry> inquiryList= inquiryRepository.findAll(filters,pageable);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Inquiry inquiry : inquiryList.getContent()) {

            Map<String, Object> map = new HashMap<>();
            map.put("id", inquiry.getId());
            map.put("userId", inquiry.getUser().getId());
            map.put("userName", inquiry.getUser().getNickName());
            map.put("title", inquiry.getTitle());
            map.put("inquiryNo", inquiry.getInquiryNo());
            map.put("status", inquiry.getStatus());
            map.put("totalPrice", inquiry.getTotalPrice());
            map.put("round", inquiry.getRound());
            map.put("limitDate", DateFormatUtils.format(inquiry.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryMode", inquiry.getInquiryMode().getName());
            map.put("industryCode", inquiry.getCompanyIndustry()!=null ? inquiry.getCompanyIndustry().getName():"");
            map.put("provinceCode", inquiry.getCompanyProvince()!=null ? inquiry.getCompanyProvince().getName():"");

            map.put("remark", inquiry.getRemark());
            map.put("contactName", inquiry.getContactName());
            map.put("contactEmail", inquiry.getContactEmail());
            map.put("contactPhone", inquiry.getContactPhone());
            map.put("contactTel", inquiry.getContactTel());
            map.put("contactFax", inquiry.getContactFax());
            map.put("contactWeiXin", inquiry.getContactWeiXin());
            map.put("contactWeiBo", inquiry.getContactWeiBo());
            map.put("intervalHour", inquiry.getIntervalHour());
            map.put("logoUrl", inquiry.getLogoUrl());
            map.put("fileList", inquiryFileRepository.findByInquiryAndRound(inquiry,inquiry.getRound()));

            if(inquiry.getLogoUrl()==null || "".equals(inquiry.getLogoUrl())){
                map.put("logoUrl",inquiry.getCompanyIndustry().getLogoUrl()+"?imageView2/2/w/120&name=dl.jpg)");
            }else{
                map.put("logoUrl",inquiry.getLogoUrl()+"?imageView2/2/w/120&name=dl.jpg)" );
            }

            list.add(map);
        }

        res.put("success",1);
        res.put("data",list);
        return res;
    }

}
