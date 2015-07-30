package com.dream.controller.dream;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.Manager;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
}
