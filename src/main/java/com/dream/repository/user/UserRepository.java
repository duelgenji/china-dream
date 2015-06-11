package com.dream.repository.user;

import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;

/**
 * Created by Knight on 2015/6/10 11:16.
 */
public interface UserRepository extends MyRepository<User,Long>{

    User findByEmail(String email);

    User findByEmailAndPassword(String email,String password);
}
