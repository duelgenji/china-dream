package com.dream.repository.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Knight on 2015/6/25 15:55.
 */
public interface InquiryRepository extends MyRepository<Inquiry,Long> {

    @Query("from Inquiry i where i.user = :user ")
    List<Inquiry> findByUser(@Param("user") User user);

    int countByUser(User user);

    @Query("select count(*) from Inquiry i where i.user =:user and i.status =:status")
    int countByUserAndStatus(@Param("user") User user,@Param("status") int status);

    Page<Inquiry> findByInquiryNoLikeOrTitleLike(String key,String key2,Pageable pageable);

    Inquiry findByUserAndId(User user ,long id);

    int countByWinner(User winner);

    List<Inquiry> findByStatusAndCreateDateLessThan(int status,Date date);

    List<Inquiry> findByStatusAndCreateDateLessThanAndSendFailEmail(int status,Date date,boolean sendFailEmail);
}
