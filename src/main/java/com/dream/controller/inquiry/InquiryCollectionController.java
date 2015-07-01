package com.dream.controller.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryCollection;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryCollectionRepository;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.user.UserRepository;
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
public class InquiryCollectionController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    InquiryCollectionRepository inquiryCollectionRepository;



    /**
     * 获取 收藏列表
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
            map.put("collectionCreateTime", collection.getCreateTime());
            map.put("inquiryTitle", collection.getInquiry().getTitle());
            map.put("inquiryId", collection.getInquiry().getId());
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
     * 添加收藏
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

        List<InquiryCollection> collectionList= inquiryCollectionRepository.findByUserAndInquiry(user, inquiry);

        if(collectionList.size()==0){
            InquiryCollection collection = new InquiryCollection();
            collection.setUser(user);
            collection.setInquiry(inquiry);
            inquiryCollectionRepository.save(collection);

        }


        res.put("success",1);
        return res;
    }

    /**
     * 添加收藏
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

}