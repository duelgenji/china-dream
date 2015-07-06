package com.dream.controller.inquiry;

import com.dream.entity.message.Message;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.user.UserRepository;
import com.wonders.xlab.framework.controller.AbstractBaseController;
import com.wonders.xlab.framework.repository.MyRepository;
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
 * Created by Knight on 2015/7/1 0:12.
 */
@RestController
@RequestMapping("message")
@SessionAttributes("currentUser")
public class MessageController extends AbstractBaseController<Message, Long> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    MessageRepository messageRepository;

    @Override
    protected MyRepository<Message, Long> getRepository() {
        return messageRepository;
    }


    /**
     * 获取 站内信列表
     * @param type 0自己发的   1别人发给我的标的
     */
    @RequestMapping("retrieveMessageList")
    public Map<String, Object> retrieveMessageList(
            @RequestParam(required = false) int type,
            @ModelAttribute("currentUser") User user,
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }


        Map<String, Object> filters = new HashMap<>();

        if(type==0){
            filters.put("user_equal", user);
        }else if(type==1){
            filters.put("inquiryUser_equal", user);

        }

        Page<Message> messagePage= messageRepository.findAll(filters,pageable);


        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (Message message : messagePage) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("messageId", message.getId());
            map.put("messageStatus", message.getStatus());
            map.put("content", message.getContent());
            map.put("inquiryNo", message.getInquiry().getInquiryNo());
            map.put("round", message.getInquiry().getRound());
            map.put("inquiryStatus", message.getInquiry().getStatus());
            map.put("province", message.getInquiry().getCompanyProvince().getName());
            map.put("totalPrice", message.getInquiry().getTotalPrice());
            map.put("limitDate",  DateFormatUtils.format(message.getInquiry().getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryTitle", message.getInquiry().getTitle());
            map.put("inquiryId", message.getInquiry().getId());
            map.put("inquiryMode", message.getInquiry().getInquiryMode().getName());

            list.add(map);
        }



        res.put("success",1);
        res.put("data",list);
        return res;
    }

    /**
     * 修改 站内信状态 （同意 拒绝）
     */
    @RequestMapping("modifyMessageStatus")
    public Map<String, Object> modifyMessageStatus(
            @RequestParam(required = false) long messageId,
            @RequestParam(required = false) int status,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Message message = messageRepository.findOne(messageId);
        if(message == null || !message.getInquiryUser().getId().equals(user.getId())){
            res.put("success",0);
            res.put("message","没有数据");
            return res;
        }

        if(message.getStatus()!=0){
            res.put("success",0);
            res.put("message","该条不能修改状态！");
            return res;
        }

        message.setStatus(status);
        messageRepository.save(message);

        res.put("success",1);
        return res;
    }
}
