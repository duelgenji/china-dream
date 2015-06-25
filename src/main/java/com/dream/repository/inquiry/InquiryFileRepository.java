package com.dream.repository.inquiry;

import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.inquiry.Inquiry;
import com.wonders.xlab.framework.repository.MyRepository;

import java.util.List;

/**
 *
 * Created by Knight on 2015/6/25 15:55.
 */
public interface InquiryFileRepository extends MyRepository<InquiryFile,Long> {

    List<InquiryFile> findByInquiry(Inquiry inquiry);

}
