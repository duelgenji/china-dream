package com.dream.controller.user;

import com.dream.entity.user.User;
import com.dream.repository.user.UserRepository;
import com.dream.service.user.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息 相关 接口
 * Created by Knight on 2015/6/11 13:53.
 */
@RestController
@RequestMapping("userInfo")
@SessionAttributes("currentUser")
public class UserInfoController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @RequestMapping("retrieveInfo")
    public Map<String, Object> retrieveInfo(@ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();



        if(user.getId()!=null){
            res = userService.User2Map(userRepository.findOne(user.getId()));
            //res.put("info",info);

        }else{
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        res.put("success",1);
        return res;

    }

    @RequestMapping("modifyInfo")
    public Map<String, Object> modifyInfo(HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        Map<String, Object> res = new HashMap<>();

        if(user.getId()!=null){

            user = userService.generateOptionalInfo(user,request);

            String message= userService.generateUserByType(user, user.getType(), request);

            if(!message.equals("")){
                res.put("success", "0");
                res.put("message", message);
                return res;
            }

        }else{
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        res.put("success",1);
        return res;
    }



    /**
     * 获取用户列表
     */
    @RequestMapping("retrieveUserList")
    public Map<String, Object> retrieveUserList(
            @RequestParam int type,
            @PageableDefault(page = 0, size = 20) Pageable pageable,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(type==1){
            pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "userIndex.quotationDoneTime");
        }  if(type==2){
            pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "userIndex.quotationSuccessRate");
        }
        Page<User> userList= userRepository.findAllUser(pageable);

        res.put("success",1);
        res.put("data",userList);
        return res;
    }

    /**
     * 修改密码
     */
    @RequestMapping("modifyPassword")
    public Map<String, Object> modifyPassword(
            @RequestParam String password,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        user.setPassword(DigestUtils.md5Hex(password));
        userRepository.save(user);

        res.put("success",1);
        return res;
    }

}
