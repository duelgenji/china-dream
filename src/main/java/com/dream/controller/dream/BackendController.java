package com.dream.controller.dream;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.Manager;
import com.dream.entity.user.User;
import com.dream.entity.user.UserIndex;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.user.UserIndexRepository;
import com.dream.repository.user.UserRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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
    UserRepository userRepository;

    @Autowired
    UserIndexRepository userIndexRepository;

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
            @RequestParam String key,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("currentManager") Manager manager) {
        Map<String, Object> res = new HashMap<>();


        key =  "%"+key+"%";

        Page<User> userPage ;


        userPage = userRepository.findByNickNameLikeAndStatus(key, 0, pageable);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        String industry="",province="";
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

            map.put("industry", industry);
            map.put("province", province);
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
}
