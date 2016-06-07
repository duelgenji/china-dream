package com.dream.service.user;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyOwnership;
import com.dream.entity.company.CompanyProvince;
import com.dream.entity.user.*;
import com.dream.repository.company.CompanyIndustryRepository;
import com.dream.repository.company.CompanyOwnershipRepository;
import com.dream.repository.company.CompanyProvinceRepository;
import com.dream.repository.user.UserCompanyInfoRepository;
import com.dream.repository.user.UserGroupInfoRepository;
import com.dream.repository.user.UserPersonalInfoRepository;
import com.dream.repository.user.UserRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Knight on 2015/6/10 15:04.
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPersonalInfoRepository userPersonalInfoRepository;

    @Autowired
    UserCompanyInfoRepository userCompanyInfoRepository;

    @Autowired
    UserGroupInfoRepository userGroupInfoRepository;

    @Autowired
    CompanyOwnershipRepository companyOwnershipRepository;

    @Autowired
    CompanyProvinceRepository companyProvinceRepository;

    @Autowired
    CompanyIndustryRepository companyIndustryRepository;

    /**
     * 修改用户 可选字段
     */
    @Transactional
    public User generateOptionalInfo(User user, HttpServletRequest request) {

        if(request.getParameter("IDCardNumber")!=null){
            user.setIDCardNumber(request.getParameter("IDCardNumber"));
        }
        if(request.getParameter("bankAccount")!=null){
            user.setBankAccount(request.getParameter("bankAccount"));
        }
        if(request.getParameter("bankAccountOpen")!=null){
            user.setBankAccountOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("bankAccountOpen"))]);
        }
        if(request.getParameter("zhifubaoAccount")!=null){
            user.setZhifubaoAccount(request.getParameter("zhifubaoAccount"));
        }
        if(request.getParameter("zhifubaoAccountOpen")!=null){
            user.setZhifubaoAccountOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("zhifubaoAccountOpen"))]);
        }
        if(request.getParameter("tel")!=null){
            user.setTel(request.getParameter("tel"));
        }
        if(request.getParameter("telOpen")!=null){
            user.setTelOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("telOpen"))]);
        }
        if(request.getParameter("telephone")!=null){
            user.setTelephone(request.getParameter("telephone"));
        }
        if(request.getParameter("telephoneOpen")!=null){
            user.setTelephoneOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("telephoneOpen"))]);
        }
        if(request.getParameter("keywords")!=null){
            user.setKeywords(request.getParameter("keywords"));
        }
        if(request.getParameter("description")!=null){
            user.setDescription(request.getParameter("description"));
        }
        if(request.getParameter("removedIndustry")!=null){
            user.setRemovedIndustry(request.getParameter("removedIndustry"));
        }


        user.setUpdateTime(new Date());

        return user;

    }

    /**
     * 根据 用户类型 来修改相应的info表
     */
    @Transactional
    public String generateUserByType(User user,int type,HttpServletRequest request) {

        /* 1 个人  2 企业  3 群 */
        switch (type){
            case 1:
                UserPersonalInfo userPersonalInfo = user.getId()==null?null:userPersonalInfoRepository.findOne(user.getId());

                if(userPersonalInfo==null){
                    userPersonalInfo= new UserPersonalInfo();
                }

                if(request.getParameter("sex")!=null){
                    userPersonalInfo.setSex(Integer.parseInt(request.getParameter("sex")));
                }
                if(request.getParameter("birthday")!=null && !request.getParameter("birthday").equals("")){
                    try {
                        userPersonalInfo.setBirthday(DateUtils.parseDate(request.getParameter("birthday"), "yyyy-MM-dd"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(request.getParameter("realName")!=null){
                    userPersonalInfo.setRealName(request.getParameter("realName"));
                }
                if(request.getParameter("realNameOpen")!=null){
                    userPersonalInfo.setRealNameOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("realNameOpen"))]);
                }
                if(request.getParameter("degree")!=null){
                    userPersonalInfo.setDegree(request.getParameter("degree"));
                }
                if(request.getParameter("degreeOpen")!=null){
                    userPersonalInfo.setDegreeOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("degreeOpen"))]);
                }
                if(request.getParameter("school")!=null){
                    userPersonalInfo.setSchool(request.getParameter("school"));
                }
                if(request.getParameter("schoolOpen")!=null){
                    userPersonalInfo.setSchoolOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("schoolOpen"))]);
                }
                if(request.getParameter("major")!=null){
                    userPersonalInfo.setMajor(request.getParameter("major"));
                }
                if(request.getParameter("majorOpen")!=null){
                    userPersonalInfo.setMajorOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("majorOpen"))]);
                }
                if(request.getParameter("weiboUrl")!=null){
                    userPersonalInfo.setWeiboUrl(request.getParameter("weiboUrl"));
                }
                if(request.getParameter("weixin")!=null){
                    userPersonalInfo.setWeixin(request.getParameter("weixin"));
                }
                if(request.getParameter("companyIndustry")!=null){
                    Long iid=Long.parseLong(request.getParameter("companyIndustry"));
                    CompanyIndustry companyIndustry= companyIndustryRepository.findOne(iid);
                    if(companyIndustry!=null){
                        user.setCompanyIndustry(companyIndustry);
                    }else{
                        return "行业编号错误";
                    }
                }else if (user.getCompanyIndustry()==null ){
//                    return "行业不能为空";
                }
                if(request.getParameter("companyProvince")!=null){
                    Long pid=Long.parseLong(request.getParameter("companyProvince"));
                    CompanyProvince companyProvince= companyProvinceRepository.findOne(pid);
                    if(companyProvince!=null){
                        user.setCompanyProvince(companyProvince);
                    }else{
                        return "地区编号错误";
                    }
                }else if (user.getCompanyProvince()==null ){
//                    return "所在地区不能为空";
                }

                userRepository.save(user);
                userPersonalInfo.setId(user.getId());
                userPersonalInfoRepository.save(userPersonalInfo);
                break;
            case 2:

                UserCompanyInfo userCompanyInfo = user.getId()==null?null:userCompanyInfoRepository.findOne(user.getId());

                if(userCompanyInfo==null){
                    userCompanyInfo= new UserCompanyInfo();
                }


                if(request.getParameter("website")!=null){
                    if(request.getParameter("website").equals(""))
                        return "网址不能为空" ;
                    userCompanyInfo.setWebsite(request.getParameter("website"));
                }else if (userCompanyInfo.getWebsite()==null){
                    return "网址不能为空";
                }

                if(request.getParameter("companyName")!=null && !request.getParameter("website").equals("")){
                    if(userCompanyInfo.getCompanyName()!=null)
                        return "公司名称不能修改";
//                    if(userCompanyInfoRepository.findByCompanyName(request.getParameter("companyName")).size()==0){
//                        userCompanyInfo.setCompanyName(request.getParameter("companyName"));
//                    }else{
//                        return "公司名称已经被使用";
//                    }//2015 12 24 去除公司名称 唯一性
                    userCompanyInfo.setCompanyName(request.getParameter("companyName"));
                }else if (userCompanyInfo.getCompanyName()==null ){
//                    return "公司名称不能为空";
                }

                if(request.getParameter("companyProvince")!=null){
                    Long pid=Long.parseLong(request.getParameter("companyProvince"));
                    CompanyProvince companyProvince= companyProvinceRepository.findOne(pid);
                    if(companyProvince!=null){
                        user.setCompanyProvince(companyProvince);
                    }else{
                        return "地区编号错误";
                    }
                }else if (user.getCompanyProvince()==null ){
//                    return "企业所在地不能为空";
                }
                if(request.getParameter("companyIndustry")!=null){
                    Long iid=Long.parseLong(request.getParameter("companyIndustry"));
                    CompanyIndustry companyIndustry= companyIndustryRepository.findOne(iid);
                    if(companyIndustry!=null){
                        user.setCompanyIndustry(companyIndustry);
                    }else{
                        return "行业编号错误";
                    }
                }else if (user.getCompanyIndustry()==null ){
//                    return "企业所属行业不能为空";
                }
                if(request.getParameter("companyOwnership")!=null){
                    Long oid=Long.parseLong(request.getParameter("companyOwnership"));
                    CompanyOwnership companyOwnership= companyOwnershipRepository.findOne(oid);
                    if(companyOwnership!=null){
                        userCompanyInfo.setCompanyOwnership(companyOwnership);
                    }else{
                        return "行业性质编号错误";
                    }
                }else if (userCompanyInfo.getCompanyOwnership()==null ){
//                    return "企业性质不能为空";
                }

                if(request.getParameter("companyEmail")!=null){
                    userCompanyInfo.setCompanyEmail(request.getParameter("companyEmail"));
                }
                if(request.getParameter("companyEmailOpen")!=null){
                    userCompanyInfo.setCompanyEmailOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("companyEmailOpen"))]);
                }
                if(request.getParameter("taxNumber")!=null){
                    userCompanyInfo.setTaxNumber(request.getParameter("taxNumber"));
                }
                if(request.getParameter("taxNumberOpen")!=null){
                    userCompanyInfo.setTaxNumberOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("taxNumberOpen"))]);
                }
                if(request.getParameter("organizationsCode")!=null){
                    userCompanyInfo.setOrganizationsCode(request.getParameter("organizationsCode"));
                }
                if(request.getParameter("organizationsCodeOpen")!=null){
                    userCompanyInfo.setOrganizationsCodeOpen(OpenStatus.values()[Integer.parseInt(request.getParameter("organizationsCodeOpen"))]);
                }
                if(request.getParameter("weiboUrl")!=null){
                    userCompanyInfo.setWeiboUrl(request.getParameter("weiboUrl"));
                }
                if(request.getParameter("weixin")!=null){
                    userCompanyInfo.setWeixin(request.getParameter("weixin"));
                }

                userRepository.save(user);
                userCompanyInfo.setId(user.getId());
                userCompanyInfoRepository.save(userCompanyInfo);
                break;

            case 3:
                UserGroupInfo userGroupInfo = user.getId()==null?null:userGroupInfoRepository.findOne(user.getId());

                if(userGroupInfo==null){
                    userGroupInfo= new UserGroupInfo();
                }


                if(request.getParameter("groupSize")!=null && !request.getParameter("groupSize").equals("")){
                    userGroupInfo.setGroupSize(Integer.parseInt(request.getParameter("groupSize")));
                }
                if(request.getParameter("groupNumber")!=null){
                    userGroupInfo.setGroupNumber(request.getParameter("groupNumber"));
                }
                if(request.getParameter("description") != null) {
                    userGroupInfo.setDescription(request.getParameter("description"));
                }
                if(request.getParameter("companyProvince")!=null){
                    Long pid=Long.parseLong(request.getParameter("companyProvince"));
                    CompanyProvince companyProvince= companyProvinceRepository.findOne(pid);
                    if(companyProvince!=null){
                        user.setCompanyProvince(companyProvince);
                    }else{
                        return "地区编号错误";
                    }
                }else if (user.getCompanyProvince()==null ){
                    return "所在地区不能为空";
                }

                userRepository.save(user);

                userGroupInfo.setId(user.getId());
                userGroupInfoRepository.save(userGroupInfo);

                break;
            default:
                break;

        }

        return "";
    }

    public Map<String, Object> User2Map(User user){

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

            res.put("createTime", DateFormatUtils.format(user.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
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
            res.put("VIP",user.getVIP());
            res.put("description",user.getDescription()!=null?user.getDescription():"");
            res.put("removedIndustry",user.getRemovedIndustry());
            if(user.getCompanyIndustry()!=null){
                res.put("companyIndustry",user.getCompanyIndustry().getId());
            }else{
                res.put("companyIndustry","");
            }
            if(user.getCompanyProvince()!=null){
                res.put("companyProvince", user.getCompanyProvince().getId());

            }else{
                res.put("companyProvince","");
            }

            //Map<String, Object> info = new HashMap<>();
            switch (type){
                case 1 :
                    res.put("birthday",user.getUserPersonalInfo().getBirthday());
                    res.put("sex",user.getUserPersonalInfo().getSex());
                    res.put("weiboUrl",user.getUserPersonalInfo().getWeiboUrl());
                    res.put("weixin",user.getUserPersonalInfo().getWeixin());
                    res.put("realName",user.getUserPersonalInfo().getRealName());
                    res.put("realNameOpen",user.getUserPersonalInfo().getRealNameOpen().ordinal());
                    res.put("degree",user.getUserPersonalInfo().getDegree());
                    res.put("degreeOpen",user.getUserPersonalInfo().getDegreeOpen().ordinal());
                    res.put("major",user.getUserPersonalInfo().getMajor());
                    res.put("majorOpen",user.getUserPersonalInfo().getMajorOpen().ordinal());
                    res.put("school",user.getUserPersonalInfo().getSchool());
                    res.put("schoolOpen",user.getUserPersonalInfo().getSchoolOpen().ordinal());
                    break;
                case 2:
                    res.put("website",user.getUserCompanyInfo().getWebsite());
                    res.put("weiboUrl",user.getUserCompanyInfo().getWeiboUrl());
                    res.put("weixin",user.getUserCompanyInfo().getWeixin());
                    res.put("companyName",user.getUserCompanyInfo().getCompanyName()!=null?user.getUserCompanyInfo().getCompanyName():user.getNickName());
                    res.put("companyOwnership",user.getUserCompanyInfo().getCompanyOwnership()!=null ? user.getUserCompanyInfo().getCompanyOwnership().getId():"");
                    res.put("companyEmail",user.getUserCompanyInfo().getCompanyEmail());
                    res.put("companyEmailOpen",user.getUserCompanyInfo().getCompanyEmailOpen().ordinal());
                    res.put("organizationsCode",user.getUserCompanyInfo().getOrganizationsCode());
                    res.put("organizationsCodeOpen", user.getUserCompanyInfo().getOrganizationsCodeOpen().ordinal());
                    res.put("taxNumber",user.getUserCompanyInfo().getTaxNumber());
                    res.put("taxNumberOpen",user.getUserCompanyInfo().getTaxNumberOpen().ordinal());

                    break;
                case 3:
                    res.put("description",user.getUserGroupInfo().getDescription());
                    res.put("groupSize", user.getUserGroupInfo().getGroupSize());
                    res.put("groupNumber",user.getUserGroupInfo().getGroupNumber());

                    break;
                default:
                    break;
            }
            //res.put("info",info);

        }
        return res;
    }


    public void putUserDetailInfo(Map<String, Object> res, User user ){

        int type = user.getType();
        switch (type) {
            case 1:
                if(user.getCompanyIndustry()!=null){
                    res.put("personalIndustry",user.getCompanyIndustry().getName());
                }else{
                    res.put("personalIndustry","");
                }
                res.put("personalWeiBo",user.getUserPersonalInfo().getWeiboUrl());
                res.put("personalWeiXin",user.getUserPersonalInfo().getWeixin());
                break;
            case 2:
                res.put("companyName",user.getUserCompanyInfo().getCompanyName()!=null?user.getUserCompanyInfo().getCompanyName():user.getNickName());

                if(user.getUserCompanyInfo().getCompanyOwnership()!=null){
                    res.put("companyOwnership",user.getUserCompanyInfo().getCompanyOwnership().getName());
                }else{
                    res.put("companyOwnership","");
                }

                if(user.getCompanyIndustry()!=null){
                    res.put("companyIndustry",user.getCompanyIndustry().getName());
                }else{
                    res.put("companyIndustry","");
                }
                res.put("companyWebsite", user.getUserCompanyInfo().getWebsite());

                break;
            case 3:
                res.put("groupDescription",user.getUserGroupInfo().getDescription());
                res.put("groupSize", user.getUserGroupInfo().getGroupSize());
                res.put("groupNumber",user.getUserGroupInfo().getGroupNumber());

                break;
            default:
                break;
        }
    }

}
