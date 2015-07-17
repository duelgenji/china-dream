package com.dream.controller.system;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.user.UserRepository;
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

    @Transactional
    @Scheduled(cron = "0 0/10 * * * ?")
    public void execute() {
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
            //userRepository.delete(user);
        }

    }

    public void inquiryOverLimitTime(){

        List<Inquiry> inquiryList = inquiryRepository.findByStatusAndCreateDateLessThan(0, DateUtils.addDays(new Date(), -60));
        //System.out.println("即将流标"+inquiryList.size()+"条");
        for ( Inquiry inquiry : inquiryList) {
            inquiry.setStatus(2);
            inquiryRepository.save(inquiry);
            //TODO 发送邮件
        }

    }

}
