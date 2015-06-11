package com.dream.repository.user;

import com.dream.entity.user.UserCompanyInfo;
import com.wonders.xlab.framework.repository.MyRepository;

import java.util.List;

/**
 * Created by Knight on 2015/6/10 17:42.
 */
public interface UserCompanyInfoRepository extends MyRepository<UserCompanyInfo,Long> {
    List<UserCompanyInfo> findByCompanyName(String companyName);
}
