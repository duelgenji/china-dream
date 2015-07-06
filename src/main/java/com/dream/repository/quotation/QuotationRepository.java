package com.dream.repository.quotation;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.quotation.Quotation;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Knight on 2015/7/6 11:29.
 */
public interface QuotationRepository extends MyRepository<Quotation,Long> {

    @Query("from Quotation q where q.inquiry = :inquiry ")
    List<Quotation> findByInquiry(@Param("inquiry") Inquiry inquiry);
}
