package com.dream.repository.user;

import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Knight on 2015/6/10 11:16.
 */
public interface UserRepository extends MyRepository<User,Long>{

    User findByEmail(String email);

    User findByEmailAndPassword(String email,String password);

    List<User> findByStatus(int status,Pageable pageable);

    @Query("from User u where u.status=1 ")
    Page<User> findAllUser(Pageable pageable);

    Page<User> findByNickNameLike(String key,Pageable pageable);

}
