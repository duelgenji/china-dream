package com.dream.controller.collection;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryCollection;
import com.dream.entity.user.User;
import com.dream.entity.user.UserCollection;
import com.dream.repository.inquiry.InquiryCollectionRepository;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.user.UserCollectionRepository;
import com.dream.repository.user.UserRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Knight on 2015/7/1 1:03.
 */
@RestController
@RequestMapping("collection")
@SessionAttributes("currentUser")
public class CollectionController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    InquiryCollectionRepository inquiryCollectionRepository;

    @Autowired
    UserCollectionRepository userCollectionRepository;



    /**
     * 获取 收藏列表 询价
     */
    @RequestMapping("retrieveCollectionList")
    public Map<String, Object> retrieveCollectionList(
            @ModelAttribute("currentUser") User user,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }



        Map<String, Object> filters = new HashMap<>();

        filters.put("user_equal", user);

        Page<InquiryCollection> collectionPage= inquiryCollectionRepository.findAll(filters,pageable);


        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (InquiryCollection collection : collectionPage) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("collectionId", collection.getId());
            map.put("collectionCreateTime",  DateFormatUtils.format(collection.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryTitle", collection.getInquiry().getTitle());
            map.put("inquiryId", collection.getInquiry().getId());
            map.put("round", collection.getInquiry().getRound());
            map.put("inquiryMode", collection.getInquiry().getInquiryMode().getName());
            map.put("inquiryNo", collection.getInquiry().getInquiryNo());
            map.put("province", collection.getInquiry().getCompanyProvince().getName());
            map.put("industry", collection.getInquiry().getCompanyIndustry().getName());
            map.put("totalPrice", collection.getInquiry().getTotalPrice());


            list.add(map);
        }



        res.put("success",1);
        res.put("data",list);
        return res;
    }


    /**
     * 添加收藏  询价
     */
    @RequestMapping("generateCollection")
    public Map<String, Object> generateCollection(
            @RequestParam long inquiryId,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Inquiry inquiry = inquiryRepository.findOne(inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","数据未查到");
            return res;
        }
        if(inquiry.getUser().getId().equals(user.getId())){
            res.put("success",0);
            res.put("message","不要收藏自己的询价");
            return res;
        }

        InquiryCollection inquiryCollection= inquiryCollectionRepository.findByUserAndInquiry(user, inquiry);

        if(inquiryCollection==null){
            InquiryCollection collection = new InquiryCollection();
            collection.setUser(user);
            collection.setInquiry(inquiry);
            inquiryCollectionRepository.save(collection);
            res.put("collectionId", collection.getId());
        }

        res.put("success",1);
        return res;
    }

    /**
     * 添加收藏  询价
     */
    @RequestMapping("cancelCollection")
    public Map<String, Object> cancelCollection(
            @RequestParam long collectionId,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        InquiryCollection inquiryCollection = inquiryCollectionRepository.findByUserAndId(user, collectionId);
        if(inquiryCollection==null){
            res.put("success",0);
            res.put("message","数据未查到");
            return res;
        }

        inquiryCollectionRepository.delete(inquiryCollection);



        res.put("success",1);
        return res;
    }


    /**
     * 获取 收藏列表 用户
     */
    @RequestMapping("retrieveCollectionListU")
    public Map<String, Object> retrieveCollectionListU(
            @ModelAttribute("currentUser") User user,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }



        Map<String, Object> filters = new HashMap<>();

        filters.put("user_equal", user);

        Page<UserCollection> collectionPage= userCollectionRepository.findAll(filters,pageable);


        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        String industry="",province="";
        for (UserCollection collection : collectionPage) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("collectionId", collection.getId());
            map.put("collectionCreateTime",  DateFormatUtils.format(collection.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));

            map.put("userId", collection.getTargetUser().getId());
            map.put("userNickname", collection.getTargetUser().getNickName());
            map.put("userType", collection.getTargetUser().getType());
            map.put("quotationDoneTime", collection.getTargetUser().getUserIndex().getQuotationDoneTime());
            map.put("quotationSuccessRate", collection.getTargetUser().getUserIndex().getQuotationSuccessRate());

            if(collection.getTargetUser().getType()==1){
                if( collection.getTargetUser().getUserPersonalInfo().getCompanyIndustry()!=null)
                    industry = collection.getTargetUser().getUserPersonalInfo().getCompanyIndustry().getName();
            }else if(collection.getTargetUser().getType()==2){
                if( collection.getTargetUser().getUserCompanyInfo().getCompanyIndustry()!=null)
                    industry = collection.getTargetUser().getUserCompanyInfo().getCompanyIndustry().getName();
                if( collection.getTargetUser().getUserCompanyInfo().getCompanyProvince() !=null)
                    province = collection.getTargetUser().getUserCompanyInfo().getCompanyProvince().getName();
            }

            map.put("industry", industry);
            map.put("province", province);
            list.add(map);
        }



        res.put("success",1);
        res.put("data",list);
        return res;
    }


    /**
     * 添加收藏  用户
     */
    @RequestMapping("generateCollectionU")
    public Map<String, Object> generateCollectionU(
            @RequestParam long userId,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        User targetUser = userRepository.findOne(userId);
        if(targetUser==null){
            res.put("success",0);
            res.put("message","数据未查到");
            return res;
        }
        if(targetUser.getId().equals(user.getId())){
            res.put("success",0);
            res.put("message","不要收藏自己");
            return res;
        }

        List<UserCollection> collectionList= userCollectionRepository.findByUserAndTargetUser(user, targetUser);

        if(collectionList.size()==0){
            UserCollection collection = new UserCollection();
            collection.setUser(user);
            collection.setTargetUser(targetUser);
            userCollectionRepository.save(collection);
            res.put("collectionId", collection.getId());
        }
        res.put("success",1);
        return res;
    }

    /**
     * 删除收藏  用户
     */
    @RequestMapping("cancelCollectionU")
    public Map<String, Object> cancelCollectionU(
            @RequestParam long collectionId,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        UserCollection collection = userCollectionRepository.findByUserAndId(user, collectionId);
        if(collection==null){
            res.put("success",0);
            res.put("message","数据未查到");
            return res;
        }

        userCollectionRepository.delete(collection);

        res.put("success",1);
        return res;
    }
}
