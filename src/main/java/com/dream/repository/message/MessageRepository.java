package com.dream.repository.message;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.message.Message;
import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Knight on 2015/6/29 13:41.
 */
public interface MessageRepository  extends MyRepository<Message,Long> {

    @Query("from Message m where m.user = :user and m.inquiry = :inquiry and m.status = :status and m.inquiry.round=m.round order by m.id desc ")
    List<Message> findAllUserAndInquiryAndStatus(@Param("user") User user, @Param("inquiry") Inquiry inquiry, @Param("status") int status );

    @Query("from Message m where m.user = :user and m.inquiry = :inquiry and m.inquiry.round=m.round order by id desc ")
    List<Message> findAllUserAndInquiry(@Param("user") User user, @Param("inquiry") Inquiry inquiry);

    @Query("from Message m where m.user = :user  ")
    List<Message> findByUser(@Param("user") User user);

    @Query("select count(*) from Message m where m.user =:user and m.status =1 and m.inquiry.status=0 ")
    int countByInquiryAndUser(@Param("user") User user);

    int countByInquiryAndUserAndRoundAndStatus(Inquiry inquiry,User user,int round,int status);
}
