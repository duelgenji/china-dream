package com.dream.repository.user;

import com.dream.entity.user.Manager;
import com.wonders.xlab.framework.repository.MyRepository;

/**
 * Created by Knight on 2015/7/27 9:34.
 */
public interface ManagerRepository extends MyRepository<Manager,Long>{

    Manager findByNameAndPassword(String email, String password);

}
