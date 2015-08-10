package com.dream.repository.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryGoods;
import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;

import java.util.List;

/**
 * Created by Knight on 2015/7/17 11:16.
 */
public interface InquiryGoodsRepository extends MyRepository<InquiryGoods,Long> {

    List<InquiryGoods> findByInquiryAndUser(Inquiry inquiry,User user);

    int countByInquiry(Inquiry inquiry);

}
