package com.dream.controller.company;

import com.dream.entity.company.CompanyIndustry;
import com.dream.entity.company.CompanyOwnership;
import com.dream.entity.company.CompanyProvince;
import com.dream.repository.company.CompanyIndustryRepository;
import com.dream.repository.company.CompanyOwnershipRepository;
import com.dream.repository.company.CompanyProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业用户 相关接口
 * Created by Knight on 2015/6/11 11:03.
 */
@RestController
@RequestMapping("company")
public class CompanyController{
    @Autowired
    CompanyIndustryRepository companyIndustryRepository;

    @Autowired
    CompanyOwnershipRepository companyOwnershipRepository;

    @Autowired
    CompanyProvinceRepository companyProvinceRepository;

    /**
     * 添加 行业 公司所在地 企业性质
     */
    @Transactional
    @RequestMapping("generateData")
    public Map<String, Object> login(@RequestParam List<String> industryList,
                                     @RequestParam List<String> provinceList,
                                     @RequestParam List<String> ownershipList) {

        Map<String, Object> res = new HashMap<>();

        CompanyIndustry companyIndustry;
        CompanyProvince companyProvince;
        CompanyOwnership companyOwnership;
        for(String name : industryList){
            companyIndustry = new CompanyIndustry();
            companyIndustry.setName(name);
            companyIndustryRepository.save(companyIndustry);
        }

        for(String name : provinceList){
            companyProvince = new CompanyProvince();
            companyProvince.setName(name);
            companyProvinceRepository.save(companyProvince);
        }

        for(String name : ownershipList){
            companyOwnership = new CompanyOwnership();
            companyOwnership.setName(name);
            companyOwnershipRepository.save(companyOwnership);
        }

        res.put("success", "1");

        return res;

    }
}
