package com.dream.controller.system;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.message.Message;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.user.UserRepository;
import com.dream.utils.CommonEmail;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Created by knight on 15/3/24.
 */
@EnableScheduling
@RestController
@RequestMapping("systemSchedule")
public class SystemScheduleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CommonEmail commonEmail;

    private static int times=0;

    //todo 定时任务开关
    @Transactional
    @Scheduled(cron = "0 0/30 * * * ?")
    public void execute() {

        System.out.println("Mission running..."+ ++times);
        removeUser();
        inquiryOverLimitTime();
    }


    /**
     * 如该邮箱已经注册， 但未激活， 几天后会失效  120小时
     */
    public void removeUser(){

        List<User> userList = userRepository.findByStatusAndCreateTimeLessThan(0, DateUtils.addHours(new Date(), -120));
        //System.out.println("即将删除用户"+userList.size()+"条");
        for ( User user : userList) {
            userRepository.delete(user);
        }

    }

    public void inquiryOverLimitTime(){
//        //发送60天即将流标邮件
//        List<Inquiry> inquiryList = inquiryRepository.findByStatusAndModifyDateLessThanAndSendFailEmail(0, DateUtils.addDays(new Date(), -60), false);
//        for ( Inquiry inquiry : inquiryList) {
//            commonEmail.sendEmail(inquiry.getUser(),commonEmail.getContent(CommonEmail.TYPE.AUTO60,inquiry,null));
//            inquiry.setSendFailEmail(true);
//            inquiryRepository.save(inquiry);
//        }
//
//        //67天自动流标
//        //发送流标邮件
//        List<Inquiry> inquiryList2  = inquiryRepository.findByStatusAndModifyDateLessThan(0, DateUtils.addDays(new Date(), -67));
//        for ( Inquiry inquiry : inquiryList2) {
//            commonEmail.sendEmail(inquiry.getUser(),commonEmail.getContent(CommonEmail.TYPE.AUTO67,inquiry,null));
//            inquiry.setStatus(2);
//            inquiryRepository.save(inquiry);
//        }

        //发送 成功标 2天未响应邮件
        List<Message> messageList = messageRepository.findByStatusAndCreateTimeLessThanAndSendFailEmailAndType(0, DateUtils.addDays(new Date(), -2), false, 1);
        for ( Message message : messageList) {
            commonEmail.sendEmail(message.getInquiryUser(),commonEmail.getContent(CommonEmail.TYPE.NO_RESPONSE_A,message.getInquiry(),message.getUser()));
            message.setSendFailEmail(true);
            messageRepository.save(message);
        }

        //成功标 5天未响应邮件 自动流标
        List<Message> messageList2  = messageRepository.findByStatusAndCreateTimeLessThanAndSendFailEmailAndType(0, DateUtils.addDays(new Date(), -5), true, 1);

        for ( Message message : messageList2) {
            Inquiry i = message.getInquiry();
            i.setStatus(2);
            inquiryRepository.save(i);
        }

    }



}
