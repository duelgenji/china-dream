package com.dream.controller.user;

import com.dream.entity.user.User;
import com.dream.repository.user.UserCompanyInfoRepository;
import com.dream.repository.user.UserGroupInfoRepository;
import com.dream.repository.user.UserPersonalInfoRepository;
import com.dream.repository.user.UserRepository;
import com.dream.service.user.UserService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    UserPersonalInfoRepository userPersonalInfoRepository;

    @Autowired
    UserCompanyInfoRepository userCompanyInfoRepository;

    @Autowired
    UserGroupInfoRepository userGroupInfoRepository;

    @RequestMapping("getInfo")
    public Map<String, Object> getInfo(@ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()!=null){
            res.put("id",user.getId());
            res.put("email",user.getEmail());
            res.put("nickName",user.getNickName());
            res.put("IDCardNumber",user.getIDCardNumber());
            res.put("logoUrl",user.getLogoUrl());
            res.put("headImage",user.getHeadImage());
            int type = user.getType();
            res.put("type",user.getType());
            res.put("status",user.getStatus());

            res.put("createTime", DateFormatUtils.format(user.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            res.put("inquiryTimes",0);
            res.put("inquirySuccessTimes",0);
            res.put("keywords",user.getKeywords());

            res.put("bank",user.getBank());
            res.put("bankAccountOpen",user.getBankAccountOpen().ordinal());
            res.put("bankAccount",user.getBankAccount());
            res.put("zhifubaoAccountOpen",user.getZhifubaoAccountOpen().ordinal());
            res.put("zhifubaoAccount",user.getZhifubaoAccount());
            res.put("telOpen",user.getTelOpen().ordinal());
            res.put("tel",user.getTel());
            res.put("telephoneOpen",user.getTelephoneOpen().ordinal());
            res.put("telephone",user.getTelephone());


            Map<String, Object> info = new HashMap<>();
            switch (type){
                case 1 :
                    info.put("birthday",user.getUserPersonalInfo().getBirthday());
                    info.put("sex",user.getUserPersonalInfo().getSex());
                    info.put("weiboUrl",user.getUserPersonalInfo().getWeiboUrl());
                    info.put("weixin",user.getUserPersonalInfo().getWeixin());
                    info.put("realName",user.getUserPersonalInfo().getRealName());
                    info.put("realNameOpen",user.getUserPersonalInfo().getRealNameOpen().ordinal());
                    info.put("degree",user.getUserPersonalInfo().getDegree());
                    info.put("degreeOpen",user.getUserPersonalInfo().getDegreeOpen().ordinal());
                    info.put("major",user.getUserPersonalInfo().getMajor());
                    info.put("majorOpen",user.getUserPersonalInfo().getMajorOpen().ordinal());
                    info.put("school",user.getUserPersonalInfo().getSchool());
                    info.put("schoolOpen",user.getUserPersonalInfo().getSchoolOpen().ordinal());
                    break;
                case 2:
                    info.put("website",user.getUserCompanyInfo().getWebsite());
                    info.put("weiboUrl",user.getUserCompanyInfo().getWeiboUrl());
                    info.put("weixin",user.getUserCompanyInfo().getWeixin());
                    info.put("companyName",user.getUserCompanyInfo().getCompanyName());
                    info.put("companyIndustry",user.getUserCompanyInfo().getCompanyIndustry().getId());
                    info.put("companyOwnership",user.getUserCompanyInfo().getCompanyOwnership().getId());
                    info.put("companyProvince",user.getUserCompanyInfo().getCompanyProvince().getId());
                    info.put("companyEmail",user.getUserCompanyInfo().getCompanyEmail());
                    info.put("companyEmailOpen",user.getUserCompanyInfo().getCompanyEmailOpen().ordinal());
                    info.put("organizationsCode",user.getUserCompanyInfo().getOrganizationsCode());
                    info.put("organizationsCodeOpen", user.getUserCompanyInfo().getOrganizationsCodeOpen().ordinal());
                    info.put("taxNumber",user.getUserCompanyInfo().getTaxNumber());
                    info.put("taxNumberOpen",user.getUserCompanyInfo().getTaxNumberOpen().ordinal());

                    break;
                case 3:
                    info.put("description",user.getUserGroupInfo().getDescription());
                    info.put("groupSize",user.getUserGroupInfo().getGroupSize());
                    info.put("groupNumber",user.getUserGroupInfo().getGroupNumber());

                    break;
                default:
                    break;
            }
            res.put("info",info);

        }else{
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        res.put("success",1);
        return res;

    }

    @RequestMapping("modifyInfo")
    public Map<String, Object> modifyInfo(@ModelAttribute("currentUser") User user,
                                          HttpServletRequest request) {

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

}
