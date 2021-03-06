package com.dream.repository.message;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.message.Message;
import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Knight on 2015/6/29 13:41.
 */
public interface MessageRepository  extends MyRepository<Message,Long> {

    @Query("from Message m where m.user = :user and m.inquiry = :inquiry and m.status = :status and m.inquiry.round=m.round and m.type=0 order by m.id desc ")
    List<Message> findAllUserAndInquiryAndStatus(@Param("user") User user, @Param("inquiry") Inquiry inquiry, @Param("status") int status );

    @Query("from Message m where m.user = :user and m.inquiry = :inquiry and m.inquiry.round=m.round and m.type=0 order by id desc ")
    List<Message> findAllUserAndInquiry(@Param("user") User user, @Param("inquiry") Inquiry inquiry);

    @Query("from Message m where m.user = :user and m.type=:type and  m.status=1 and  m.inquiry.round=m.round and m.inquiry.status=0 and m.bade=0 ")
    List<Message> findByUser(@Param("user") User user,@Param("type") int type);

    @Query("select count(*) from Message m where m.user =:user and m.status =1 and m.inquiry.status=0 and m.type=0")
    int countByInquiryAndUser(@Param("user") User user);

    int countByInquiryAndUserAndRoundAndStatusAndType(Inquiry inquiry,User user,int round,int status,int type);

    @Query("from Message m where m.inquiry = :inquiry and m.status = :status and m.inquiry.round=m.round and m.type=0 order by m.id desc ")
    List<Message> findByInquiryAndStatus(@Param("inquiry") Inquiry inquiry, @Param("status") int status );

    @Query("from Message m where m.inquiry = :inquiry and m.status = :status and m.round=:round and m.type=0 order by m.id desc ")
    List<Message> findByInquiryAndRoundAndStatus(@Param("inquiry") Inquiry inquiry,@Param("round") int round, @Param("status") int status );

    int countByUserAndChecked(User user,boolean checked);

    List<Message> findByStatusAndCreateTimeLessThanAndSendFailEmailAndType(int status,Date date,boolean sendFailEmail,int type);

    @Query("from Message m where m.inquiry = :inquiry and m.round=:round and m.type=0 order by id desc ")
    List<Message> findByInquiryAndRound( @Param("inquiry") Inquiry inquiry, @Param("round") int round);

}
