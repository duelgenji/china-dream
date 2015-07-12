package com.dream.repository.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryHistory;
import com.wonders.xlab.framework.repository.MyRepository;

import java.util.List;

/**
 * Created by Knight on 2015/7/12 18:05.
 */
public interface InquiryHistoryRepository extends MyRepository<InquiryHistory,Long> {

    List<InquiryHistory> findByInquiry(Inquiry inquiry);
}
