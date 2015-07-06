package com.dream.repository.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryCollection;
import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * Created by Knight on 2015/7/1 1:00.
 */
public interface InquiryCollectionRepository extends MyRepository<InquiryCollection,Long> {

    @Query("from InquiryCollection ic where ic.user = :user and ic.inquiry = :inquiry ")
    InquiryCollection findByUserAndInquiry(@Param("user") User user, @Param("inquiry") Inquiry inquiry);


    @Query("from InquiryCollection ic where ic.user = :user and ic.id = :id ")
    InquiryCollection findByUserAndId(@Param("user") User user, @Param("id") long id);

}
