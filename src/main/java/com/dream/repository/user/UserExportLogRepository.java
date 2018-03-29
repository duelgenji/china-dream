package com.dream.repository.user;

import com.dream.entity.user.User;
import com.dream.entity.user.UserAccountLog;
import com.dream.entity.user.UserExportLog;
import com.wonders.xlab.framework.repository.MyRepository;

/**
 * Created by Knight on 2016/4/24 11:58.
 */
public interface UserExportLogRepository extends MyRepository<UserExportLog,Long> {

    UserExportLog findFirstByUserOrderByCreateTimeDesc(User user);

}