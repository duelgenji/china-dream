package com.dream.repository.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Knight on 2015/6/25 15:55.
 */
public interface InquiryRepository extends MyRepository<Inquiry,Long> {

    @Query("select count(*) from Inquiry")
    int countAll();

}
