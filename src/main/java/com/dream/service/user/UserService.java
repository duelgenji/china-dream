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
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;

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
        if(request.getParameter("logoUrl")!=null){
            user.setLogoUrl(request.getParameter("logoUrl"));
        }
        if(request.getParameter("headImage")!=null){
            user.setHeadImage(request.getParameter("headImage"));
        }


        user.setUpdateTime(new Date());

        return user;

    }

    /**
     * 根据 注册类型 来添加相应的info表
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
                if(request.getParameter("birthday")!=null){
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

                userRepository.save(user);
                userPersonalInfo.setId(user.getId());
                userPersonalInfoRepository.save(userPersonalInfo);
                break;
            case 2:

                UserCompanyInfo userCompanyInfo = user.getId()==null?null:userCompanyInfoRepository.findOne(user.getId());

                if(userCompanyInfo==null){
                    userCompanyInfo= new UserCompanyInfo();
                }


                if(request.getParameter("website")!=null && !request.getParameter("website").equals("")){
                    userCompanyInfo.setWebsite(request.getParameter("website"));
                }else if (userCompanyInfo.getWebsite()==null){
                    return "网址不能为空";
                }

                if(request.getParameter("companyName")!=null){
                    if(userCompanyInfoRepository.findByCompanyName(request.getParameter("companyName")).size()==0){
                        userCompanyInfo.setCompanyName(request.getParameter("companyName"));
                    }else{
                        return "公司名称已经被使用";
                    }
                }else if (userCompanyInfo.getCompanyName()==null ){
                    return "公司名称不能为空";
                }
                if(request.getParameter("companyProvince")!=null){
                    Long pid=Long.parseLong(request.getParameter("companyProvince"));
                    CompanyProvince companyProvince= companyProvinceRepository.findOne(pid);
                    if(companyProvince!=null){
                        userCompanyInfo.setCompanyProvince(companyProvince);
                    }else{
                        return "地区编号错误";
                    }
                }else if (userCompanyInfo.getCompanyProvince()==null ){
                    return "企业所在地不能为空";
                }
                if(request.getParameter("companyIndustry")!=null){
                    Long iid=Long.parseLong(request.getParameter("companyIndustry"));
                    CompanyIndustry companyIndustry= companyIndustryRepository.findOne(iid);
                    if(companyIndustry!=null){
                        userCompanyInfo.setCompanyIndustry(companyIndustry);
                    }else{
                        return "行业编号错误";
                    }
                }else if (userCompanyInfo.getCompanyIndustry()==null ){
                    return "企业所属行业不能为空";
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
                    return "企业性质不能为空";
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


                if(request.getParameter("groupSize")!=null){
                    userGroupInfo.setGroupSize(Integer.parseInt(request.getParameter("groupSize")));
                }
                if(request.getParameter("groupNumber")!=null){
                    userGroupInfo.setGroupNumber(request.getParameter("groupNumber"));
                }
                if(request.getParameter("description") != null) {
                    userGroupInfo.setDescription(request.getParameter("description"));
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
}
