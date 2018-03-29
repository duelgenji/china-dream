package com.dream.repository.quotation;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.quotation.Quotation;
import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Knight on 2015/7/6 11:29.
 */
public interface QuotationRepository extends MyRepository<Quotation,Long> {

    List<Quotation> findByInquiryAndRoundOrderByCreateTimeDesc(Inquiry inquiry,int round);

    @Query("from Quotation q where q.user = :user ")
    List<Quotation> findByUser(@Param("user") User user);


    @Query(value = "select count(*) from ( select * from Quotation q where q.user_id =:user group by q.inquiry_id ) as  ta", nativeQuery = true)
    int countByInquiryAndUser(@Param("user") long user);

    @Query(value = "select count(*) from ( select * from Quotation q where q.user_id =:user and q.status =:status group by q.inquiry_id ) as  ta " , nativeQuery = true)
    int countByInquiryAndUserAndStatus(@Param("user") long user,@Param("status") int status);

    Quotation findByInquiryIdAndId(long inquiryId,long id);

    @Query(value = "select count(*) from  (select * from Quotation q left join (select id as aid,round as around from Inquiry i where i.status=0 and i.audit_status=2 )\n" +
            " as a on a.aid=q.inquiry_id  where q.user_id =:user and q.round = a.around group by q.inquiry_id  ) as ta " , nativeQuery = true)
    int countByDoingTimes(@Param("user") long user);

    @Query(value = "select count(*) from  (select * from Quotation q left join (select id as aid,round as around from Inquiry i where i.status<>0 )\n" +
            " as a on a.aid=q.inquiry_id  where q.user_id =:user and q.round = a.around  group by q.inquiry_id  ) as ta " , nativeQuery = true)
    int countByDoneTimes(@Param("user") long user);

    @Query(value = "select * from quotation where id in ( select max(id) as id from quotation where round =:round and inquiry_id =:inquiryId group by user_id) order by total_price asc", nativeQuery = true)
    List<Quotation> findLastQuotationOrderByPrice(@Param("inquiryId")long inquiryId,@Param("round")int round);

    @Query(value = "select * from quotation where id in ( select max(id) as id from quotation where round =:round and inquiry_id =:inquiryId and create_time>:createTime group by user_id) order by total_price asc", nativeQuery = true)
    List<Quotation> findLastQuotationByTimeOrderByPrice(@Param("inquiryId")long inquiryId,@Param("round")int round,@Param("createTime")Date createTime);

    @Query("from Quotation q where q.user = :user and q.inquiry= :inquiry and q.round=:round and q.createTime between :startTime and :endTime")
    List<Quotation> findByInquiryAndRoundAndCreateTimeBetween(@Param("user") User user,@Param("inquiry")Inquiry inquiry,@Param("round")int round,@Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
