package com.dream.controller.user;

import com.dream.entity.user.User;
import com.dream.entity.user.UserIndex;
import com.dream.repository.user.UserIndexRepository;
import com.dream.repository.user.UserRepository;
import com.dream.service.inquiry.InquiryService;
import com.dream.service.user.UserService;
import com.dream.utils.CommonEmail;
import com.dream.utils.UploadUtils;
import com.wonders.xlab.framework.controller.AbstractBaseController;
import com.wonders.xlab.framework.repository.MyRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
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
    InquiryService inquiryService;

    @Autowired
    CommonEmail commonEmail;

    @Autowired
    UserIndexRepository userIndexRepository;

    @Override
    protected MyRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Value("${avatar_url}")
    private String avatar_url;

    /**
     * 注册
     */
    @RequestMapping("register")
    public Map<String, Object> register(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password,
            @RequestParam(required = true) String nickName,
            @RequestParam(required = false) MultipartFile logoImage,
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
        if(!email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")){
            res.put("success", "0");
            res.put("message", "邮箱不符合格式");
            return res;
        }
        if(type==2 && Arrays.asList(new String[]{"126.com", "163.com", "139.com", "gmail.com", "hotmail.com",
                "sohu.com", "sina.com", "sina.cn", "yeah.net", "qq.com", "189.cn", "263.net", "outlook.com",
                "21cn.com", "188.com", "wo.cn", "sogou.com"}).contains(email.split("@")[1])){
            res.put("success", "0");
            res.put("message", "请不要使用个人邮箱");
            return res;
        }
        user.setEmail(email);
        user.setPassword(DigestUtils.md5Hex(password));
        user.setNickName(nickName);
        user.setType(type);
        user.setCaptcha(captcha);



        user = userService.generateOptionalInfo(user, request);

        if (null != logoImage) {
            String uname;
            if (null == user.getId()) {
                uname = avatar_url + "u" + user.getId();
            } else {
                uname = avatar_url + user.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, logoImage);

            user.setLogoUrl(fileUrl);
        }

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

        //TODO 增加是否激活判断
        if(user.getStatus()==1){
            if(userIndexRepository.findOne(user.getId())==null){
                UserIndex userIndex = new UserIndex();
                userIndex.setId(user.getId());
                userIndexRepository.save(userIndex);
            }
        }else{
            res.put("success", "0");
            res.put("message", "用户没有激活");
            return res;
        }


        model.addAttribute("currentUser", user);


        inquiryService.calcUserIndex(user);
        res = userService.User2Map(user);

        res.put("success", "1");

        return res;

    }

    /**
     * 登出
     */
    @RequestMapping("logout")
    public Map<String, Object> logout(
            @ModelAttribute("currentUser") User user,
            Model model) {

        Map<String, Object> res = new HashMap<>();


        if(user.getId()!=null){
            model.addAttribute("currentUser", new User());
        }

        res.put("success", "1");

        return res;

    }


    /**
     * 邮箱激活
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

        UserIndex userIndex = new UserIndex();
        userIndex.setId(user.getId());
        userIndexRepository.save(userIndex);

        model.addAttribute("currentUser", user);
        res.put("success", "1");
        try {
            response.sendRedirect("/dream/html/index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;

    }

    /**
     * 忘记密码
     */
    @RequestMapping("forgetPassword")
    public Map<String, Object> forgetPassword(
            @RequestParam String email) {

        Map<String, Object> res = new HashMap<>();

        User user;
        user = userRepository.findByEmail(email);
        if(user==null){
            res.put("success", "0");
            res.put("message", "没有该用户");
            return res;
        }
        if(user.getStatus()==0){
            res.put("success", "0");
            res.put("message", "请先去邮箱激活该用户！");
            return res;
        }
        String captcha;

        if(user.getCaptcha()==null || user.getCaptcha().equals("")){
            captcha=DateFormatUtils.format(new Date(), "yyyyMMdd") + RandomStringUtils.random(22, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            user.setCaptcha(captcha);
            userRepository.save(user);
        }else{
            captcha=user.getCaptcha();
        }
        commonEmail.sendForgetPwdMail("http://121.40.143.120:8080/dream/user/", user, email, captcha);
        res.put("success", "1");
        return res;

    }

    /**
     * 重置密码  激活接口
     */
    @RequestMapping("activateForget")
    public Map<String, Object> activateForget(
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

        user.setCaptcha("");
        userRepository.save(user);
        model.addAttribute("currentUser", user);
        res.put("success", "1");
        try {
            response.sendRedirect("/dream/html/modifyPassword.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

}
