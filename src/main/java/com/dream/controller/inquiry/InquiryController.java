package com.dream.controller.inquiry;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyProvince;
import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.inquiry.InquiryMode;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import com.dream.repository.company.CompanyIndustryRepository;
import com.dream.repository.company.CompanyProvinceRepository;
import com.dream.repository.inquiry.InquiryFileRepository;
import com.dream.repository.inquiry.InquiryModeRepository;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.user.UserCompanyInfoRepository;
import com.dream.repository.user.UserGroupInfoRepository;
import com.dream.repository.user.UserPersonalInfoRepository;
import com.dream.repository.user.UserRepository;
import com.dream.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 标 接口
 * Created by Knight on 2015/6/25 15:56.
 */

@RestController
@RequestMapping("inquiry")
@SessionAttributes("currentUser")
public class InquiryController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPersonalInfoRepository userPersonalInfoRepository;

    @Autowired
    UserCompanyInfoRepository userCompanyInfoRepository;

    @Autowired
    UserGroupInfoRepository userGroupInfoRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    InquiryFileRepository inquiryFileRepository;

    @Autowired
    CompanyIndustryRepository companyIndustryRepository;

    @Autowired
    CompanyProvinceRepository companyProvinceRepository;

    @Autowired
    InquiryModeRepository inquiryModeRepository;


    @Value("${file_url}")
    private String file_url;

    @RequestMapping("generateInquiry")
    public Map<String, Object> generateInquiry(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) int round,
            @RequestParam(required = false) long provinceCode,
            @RequestParam(required = false) long industryCode,
            @RequestParam(required = false) String limitDate,
            @RequestParam(required = false) double totalPrice,
            @RequestParam(required = false) long inquiryModeCode,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) int remarkOpen,
            @RequestParam(required = false) int userLimit,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) int contactNameOpen,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) int contactEmailOpen,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) int contactPhoneOpen,
            @RequestParam(required = false) String contactTel,
            @RequestParam(required = false) int contactTelOpen,
            @RequestParam(required = false) String contactFax,
            @RequestParam(required = false) int contactFaxOpen,
            @RequestParam(required = false) String contactWeiXin,
            @RequestParam(required = false) int contactWeiXinOpen,
            @RequestParam(required = false) String contactWeiBo,
            @RequestParam(required = false) int contactWeiBoOpen,
            @RequestParam(required = false) int filesOpen,
            @RequestParam(required = false) MultipartFile file1,
            @RequestParam(required = false) MultipartFile file2,
            @RequestParam(required = false) MultipartFile file3,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setUser(user);
        inquiry.setTitle(title);
        if(round==0){
            inquiry.setRound(0);
        }else{
            inquiry.setRound(1);
        }

        CompanyProvince companyProvince = companyProvinceRepository.findOne(provinceCode);
        inquiry.setCompanyProvince(companyProvince);
        CompanyIndustry companyIndustry = companyIndustryRepository.findOne(industryCode);
        inquiry.setCompanyIndustry(companyIndustry);

        try {
            inquiry.setLimitDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(limitDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        inquiry.setTotalPrice(totalPrice);

        InquiryMode inquiryMode=inquiryModeRepository.findOne(inquiryModeCode);
        inquiry.setInquiryMode(inquiryMode);
        inquiry.setRemark(remark);
        inquiry.setRemarkOpen(OpenStatus.values()[remarkOpen]);
        inquiry.setUserLimit(userLimit);

        inquiry.setContactName(contactName);
        inquiry.setContactNameOpen(OpenStatus.values()[contactNameOpen]);
        inquiry.setContactEmail(contactEmail);
        inquiry.setContactEmailOpen(OpenStatus.values()[contactEmailOpen]);
        inquiry.setContactPhone(contactPhone);
        inquiry.setContactPhoneOpen(OpenStatus.values()[contactPhoneOpen]);
        inquiry.setContactTel(contactTel);
        inquiry.setContactTelOpen(OpenStatus.values()[contactTelOpen]);
        inquiry.setContactFax(contactFax);
        inquiry.setContactFaxOpen(OpenStatus.values()[contactFaxOpen]);
        inquiry.setContactWeiXin(contactWeiXin);
        inquiry.setContactWeiXinOpen(OpenStatus.values()[contactWeiXinOpen]);
        inquiry.setContactWeiBo(contactWeiBo);
        inquiry.setContactWeiBoOpen(OpenStatus.values()[contactWeiBoOpen]);
        inquiry.setFilesOpen(OpenStatus.values()[filesOpen]);

        inquiryRepository.save(inquiry);


        if (null != file1) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file1);
            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setFileUrl(fileUrl);
            inquiryFileRepository.save(inquiryFile);

        }

        if (null != file2) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file2);


            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setFileUrl(fileUrl);
            inquiryFileRepository.save(inquiryFile);

        }

        if (null != file3) {
            String uname;
            if (null == inquiry.getId()) {
                uname = file_url + "u" + user.getId();
            } else {
                uname = file_url + inquiry.getId() + "u" + user.getId();
            }

            String fileUrl;
            fileUrl = UploadUtils.uploadTo7niu(0, uname, file3);

            InquiryFile inquiryFile = new InquiryFile();
            inquiryFile.setInquiry(inquiry);
            inquiryFile.setFileUrl(fileUrl);
            inquiryFileRepository.save(inquiryFile);

        }

        res.put("success",1);
        return res;

    }

}
