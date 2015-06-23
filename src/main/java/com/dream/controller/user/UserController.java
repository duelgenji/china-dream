package com.dream.controller.user;

import com.dream.entity.user.User;
import com.dream.repository.user.UserRepository;
import com.dream.service.CommonEmail;
import com.dream.service.user.UserService;
import com.wonders.xlab.framework.controller.AbstractBaseController;
import com.wonders.xlab.framework.repository.MyRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户 注册登录 相关接口
 * Created by Knight on 2015/6/10 11:30.
 */

@RestController
@RequestMapping("user")
@SessionAttributes("currentUser")
public  class UserController extends AbstractBaseController<User, Long> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    CommonEmail commonEmail;

    @Override
    protected MyRepository<User, Long> getRepository() {
        return userRepository;
    }

    /**
     * 注册
     */
    @RequestMapping("register")
    public Map<String, Object> register(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password,
            @RequestParam(required = true) String nickName,
            @RequestParam int type,
            HttpServletRequest request) {

        Map<String, Object> res = new HashMap<>();

        User user;
        user = userRepository.findByEmail(email);
        if(user!=null){
            res.put("success", "0");
            res.put("message", "该邮箱已经被注册");
            return res;
        }
        if(type<1 || type> 3){
            res.put("success", "0");
            res.put("message", "用户类型不正确");
            return res;
        }

        user = new User();
        String captcha = DateFormatUtils.format(new Date(), "yyyyMMdd") + RandomStringUtils.random(22, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        user.setEmail(email);
        user.setPassword(DigestUtils.md5Hex(password));
        user.setNickName(nickName);
        user.setType(type);
        user.setCaptcha(captcha);
        user = userService.generateOptionalInfo(user, request);


        String message=userService.generateUserByType(user,type,request);


        if(!message.equals("")){
            res.put("success", "0");
            res.put("message", message);
            return res;
        }

//        commonEmail.sendTextMail("http://localhost:8080/user/",user.getId(), email, captcha);
        commonEmail.sendTextMail("http://121.40.143.120:8080/dream/user/",user.getId(), email, captcha);

        res.put("success", "1");
        return res;

    }

    /**
     * 登录
     */
    @RequestMapping("login")
    public Map<String, Object> login(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        Map<String, Object> res = new HashMap<>();

        User user;
        user = userRepository.findByEmailAndPassword(email, DigestUtils.md5Hex(password));
        if(user==null){
            res.put("success", "0");
            res.put("message", "用户名密码错误");
            return res;
        }
        model.addAttribute("currentUser", user);

        res = userService.User2Map(user);

        res.put("success", "1");

        return res;

    }


    /**
     * 激活
     */
    @RequestMapping("activateAccount")
    public Map<String, Object> activateAccount(
            @RequestParam long id,
            @RequestParam String captcha,
            Model model,HttpServletResponse response) {

        Map<String, Object> res = new HashMap<>();

        User user;
        user = userRepository.findOne(id);
        if(user==null){
            res.put("success", "0");
            res.put("message", "没有该用户");
            return res;
        }

        if(!user.getCaptcha().equals(captcha)){
            res.put("success", "0");
            res.put("message", "验证码不正确");
            return res;
        }

        user.setStatus(1);
        user.setCaptcha("");
        userRepository.save(user);
        model.addAttribute("currentUser", user);
        res.put("success", "1");
        try {
            response.sendRedirect("/dream/html/index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;

    }

}
