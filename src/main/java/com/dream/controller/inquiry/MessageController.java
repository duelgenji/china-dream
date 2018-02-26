package com.dream.controller.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.message.Message;
import com.dream.entity.user.User;
import com.dream.entity.user.UserAccountLog;
import com.dream.entity.user.UserIndex;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.user.UserAccountLogRepository;
import com.dream.repository.user.UserIndexRepository;
import com.dream.repository.user.UserRepository;
import com.dream.utils.CommonEmail;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Knight on 2015/7/1 0:12.
 */
@RestController
@RequestMapping("message")
@SessionAttributes("currentUser")
public class MessageController{

    @Autowired
    UserRepository userRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CommonEmail commonEmail;



    @Autowired
    UserIndexRepository userIndexRepository;

    @Autowired
    UserAccountLogRepository userAccountLogRepository;

    /**
     * 获取 站内信列表
     * @param type 0自己发的   1别人发给我的标的  2 出价成功确认
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
            filters.put("type_equal", 0);
        }else if(type==1){
            filters.put("inquiryUser_equal", user);
            filters.put("type_equal", 0);
        }else if(type==2){
            filters.put("user_equal", user);
            filters.put("type_equal", 1);
        }

        Page<Message> messagePage= messageRepository.findAll(filters,pageable);


        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        for (Message message : messagePage) {

            message.setChecked(true);
            messageRepository.save(message);
            //被删除不显示
            if(message.getInquiry().isRemoved() || message.getInquiry().getUser().isRemoved())
                continue;

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("messageId", message.getId());
            map.put("messageStatus", message.getStatus());
            /* 0自己发的 用户显示询价方  1别人发我 显示出价方  */
            if(type==0){
                map.put("userNickname", message.getInquiryUser().getNickName());
                map.put("userId", message.getInquiryUser().getId());
            }else if(type==1){
                map.put("userNickname", message.getUser().getNickName());
                map.put("userId", message.getUser().getId());
            }else if(type ==2){
                map.put("userNickname", message.getInquiryUser().getNickName());
                map.put("userId", message.getInquiryUser().getId());
                map.put("winnerPrice", message.getInquiry().getWinnerPrice());
            }
            map.put("content", message.getContent());
            map.put("createTime", DateFormatUtils.format(message.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryNo", message.getInquiry().getInquiryNo());
            map.put("round", message.getRound());
            map.put("inquiryStatus", message.getInquiry().getStatus());
            map.put("province", message.getInquiry().getCompanyProvince().getName());
            map.put("totalPrice", message.getInquiry().getTotalPrice());
            map.put("limitDate", DateFormatUtils.format(message.getInquiry().getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryTitle", message.getInquiry().getTitle());
            map.put("inquiryId", message.getInquiry().getId());
            map.put("inquiryMode", message.getInquiry().getInquiryMode().getName());
            map.put("inquiryRound", message.getInquiry().getRound());
            map.put("type", message.getType());
            map.put("reason", message.getReason());
            map.put("reasonTime", message.getReasonTime());
            list.add(map);
        }



        res.put("success",1);
        res.put("data",list);
        return res;
    }

    /**
     * 修改 站内信状态 （同意 拒绝）
     */
    @Transactional
    @RequestMapping("modifyMessageStatus")
    public Map<String, Object> modifyMessageStatus(
            @RequestParam(required = false) long messageId,
            @RequestParam(required = false) int status,
            @RequestParam(required = false) String reason,
            @ModelAttribute("currentUser") User user) {

        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }

        Message message = messageRepository.findOne(messageId);
        if(message == null || (message.getType()==0  && !message.getInquiryUser().getId().equals(user.getId()))
                ||  (message.getType()==1  && !message.getUser().getId().equals(user.getId()))){
            res.put("success",0);
            res.put("message","没有数据");
            return res;
        }

        if(message.getStatus()!=0 || message.getRound()!=message.getInquiry().getRound()){
            res.put("success",0);
            res.put("message","该条不能修改状态！");
            return res;
        }

        /* type 0 询价申请站内信 1 出价成功站内信
        *  status 1 成功 2拒绝
        * */

        message.setStatus(status);
         if(status==1 && message.getType()==1){
             //乙方确认 甲方的选择合同 = 出价成功
             Inquiry inquiry =  message.getInquiry();
             inquiry.setStatus(1);
             inquiry.setWinner(user);
             inquiry.setWinnerPrice(Long.parseLong(message.getContent()));
             inquiry.setPurchaseCloseDate(new Date());
             inquiryRepository.save(inquiry);


             double calcAmount = inquiry.getTotalPrice() * inquiry.getDefaultAmountRate() * inquiry.getAdjustAmountRate();

             UserIndex userIndex = user.getUserIndex();
             DecimalFormat df=new DecimalFormat("0.00");
             double currentAmount = userIndex.getAmount();
             currentAmount = new Double(df.format(currentAmount - calcAmount));

             userIndex.setAmount(currentAmount);
             userIndexRepository.save(userIndex);

             UserAccountLog userAccountLog = new UserAccountLog();
             userAccountLog.setAuto(true);
             userAccountLog.setInquiry(inquiry);
             userAccountLog.setAmountChange(-calcAmount);
             userAccountLog.setCurrentAmount(currentAmount);
             userAccountLog.setUser(user);
             userAccountLogRepository.save(userAccountLog);


             // 发送成功邮件
             List<Message> messages = messageRepository.findByInquiryAndStatus(inquiry,1);
             for(Message m : messages){
                 commonEmail.sendEmail(m.getUser(),commonEmail.getContent(CommonEmail.TYPE.SUCCESS_B,inquiry,m.getUser()));
             }

        } else if(status==2 && message.getType()==1){
             //拒绝 邮件
             commonEmail.sendEmail(message.getInquiryUser(),commonEmail.getContent(CommonEmail.TYPE.REJECT_A,message.getInquiry(),message.getUser()));
        } else if(status==1 && message.getType()==0){
             //同意 授权 邮件
             commonEmail.sendEmail(message.getUser(),commonEmail.getContent(CommonEmail.TYPE.AGREE_B,message.getInquiry(),message.getUser()));
             message.setReasonTime(new Date());
             message.setReason(reason);
         }else if(status==2 && message.getType()==0){
             //拒绝 授权
             message.setReasonTime(new Date());
             message.setReason(reason);
         }
        messageRepository.save(message);

        res.put("success",1);
        return res;
    }
}
