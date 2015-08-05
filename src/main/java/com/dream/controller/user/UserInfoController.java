package com.dream.controller.user;

import com.dream.entity.user.User;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.user.UserRepository;
import com.dream.service.user.UserService;
import com.dream.utils.UploadUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    MessageRepository messageRepository;

    @Value("${avatar_url}")
    private String avatar_url;

    @RequestMapping("retrieveInfo")
    public Map<String, Object> retrieveInfo(@ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();



        if(user.getId()!=null){
            res = userService.User2Map(userRepository.findOne(user.getId()));
            res.put("mailCount",messageRepository.countByUserAndChecked(user, false));
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
    public Map<String, Object> modifyInfo(
            @RequestParam(required = false) MultipartFile logoImage,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        Map<String, Object> res = new HashMap<>();

        if(user.getId()!=null){
            user = userRepository.findOne(user.getId());
            user = userService.generateOptionalInfo(user,request);

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
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) List<Long> industryCode,
            @RequestParam(required = false) List<Long> provinceCode,
            @RequestParam(required = false) List<Integer> userType,
            @RequestParam(required = false) Boolean removed ,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC)  Pageable pageable,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();



        if(type!=null){
            switch (type){
                case 1:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "userIndex.quotationDoneTime");
                    break;
                case 2:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "userIndex.quotationSuccessRate");
                    break;
                default:
                    break;
            }
        }

        Map<String, Object> filters = new HashMap<>();
        filters.put("status_equal", 1);

        if(industryCode!=null && industryCode.size()>0){
            filters.put("companyIndustry_in", industryCode);
        }
        if(provinceCode!=null && provinceCode.size()>0){
            filters.put("companyProvince_in", provinceCode);
        }
        if(userType!=null && userType.size()>0){
            filters.put("type_in", userType);
        }
        if(removed==null){
            filters.put("removed_equal", false);
        }
        Page<User> userPage;


        userPage = userRepository.findAll(filters,pageable);

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        String industry,province;
        String logoUrl;

        for (User u : userPage) {
            industry="";province="";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId",u.getId());
            map.put("nickname",u.getNickName());
            map.put("VIP", u.getVIP());
            map.put("test", u.isTest());
            map.put("removed", u.isRemoved());
            logoUrl=u.getLogoUrl();
            if(logoUrl!=null && !logoUrl.equals("")){
                //按百分比缩放
                logoUrl+="?imageView2/2/w/120&name=dl.jpg";
            }
            map.put("logoUrl",logoUrl);

            map.put("userType",u.getType());
            map.put("createDate", DateFormatUtils.format(u.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("quotationDoneTime",u.getUserIndex().getQuotationDoneTime());
            map.put("quotationSuccessRate",String.format("%.2f", u.getUserIndex().getQuotationSuccessRate()) + "%");
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
        res.put("count",userPage.getTotalElements());
        return res;
    }

    /**
     * 搜索 用户列表
     */
    @RequestMapping("searchUserList")
    public Map<String, Object> searchUserList(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) List<Long> industryCode,
            @RequestParam(required = false) List<Long> provinceCode,
            @RequestParam(required = false) List<Integer> userType,
            @RequestParam(required = false) String key,
            @RequestParam(required = false) Boolean removed ,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC)  Pageable pageable,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(type!=null){
            switch (type){
                case 1:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "userIndex.quotationDoneTime");
                    break;
                case 2:
                    pageable =  new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "userIndex.quotationSuccessRate");
                    break;
                default:
                    break;
            }
        }

        Map<String, Object> filters = new HashMap<>();
        filters.put("status_equal", 1);

        if(industryCode!=null && industryCode.size()>0){
            filters.put("companyIndustry_in", industryCode);
        }
        if(provinceCode!=null && provinceCode.size()>0){
            filters.put("companyProvince_in", provinceCode);
        }
        if(userType!=null && userType.size()>0){
            filters.put("type_in", userType);
        }
        if(removed==null){
            filters.put("removed_equal", false);
        }
        if(key!=null && !key.equals("")){
            filters.put("nickName_like", key);
        }
        Page<User> userPage ;


        userPage = userRepository.findAll(filters,pageable);


        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        String industry,province;
        String logoUrl;

        for (User u : userPage) {
            industry="";province="";

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId",u.getId());
            map.put("nickname",u.getNickName());
            map.put("VIP", u.getVIP());
            map.put("test", u.isTest());
            map.put("removed", u.isRemoved());

            logoUrl=u.getLogoUrl();
            if(logoUrl!=null && !logoUrl.equals("")){
                //按百分比缩放
                logoUrl+="?imageView2/2/w/120&name=dl.jpg";
            }
            map.put("logoUrl",logoUrl);
            map.put("userType",u.getType());
            map.put("createDate", DateFormatUtils.format(u.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("quotationDoneTime",u.getUserIndex().getQuotationDoneTime());
            map.put("quotationSuccessRate",String.format("%.2f", u.getUserIndex().getQuotationSuccessRate()) + "%");
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
        res.put("count",userPage.getTotalElements());
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
