package com.dream.controller.user;

import com.dream.entity.user.Manager;
import com.dream.repository.user.ManagerRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Knight on 2015/7/27 9:46.
 */

@RestController
@RequestMapping("manager")
@SessionAttributes("currentManager")
public class ManagerController {

    @Autowired
    ManagerRepository managerRepository;


    /**
     * 登录
     */
    @RequestMapping("login")
    public Map<String, Object> login(
            @RequestParam String name,
            @RequestParam String password,
            Model model) {

        Map<String, Object> res = new HashMap<>();

        Manager manager;
        manager = managerRepository.findByNameAndPassword(name, DigestUtils.md5Hex(password));
        if(manager==null){
            res.put("success", "0");
            res.put("message", "用户名密码错误");
            return res;
        }

        model.addAttribute("currentManager", manager);
        res.put("success", "1");

        return res;

    }
}
