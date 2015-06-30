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

    @Query("from Message m where m.user = :user and m.inquiry = :inquiry and m.status = :status")
    List<Inquiry> findAllUserAndInquiryAndStatus(@Param("user") User user, @Param("inquiry") Inquiry inquiry, @Param("status") int status );

}
