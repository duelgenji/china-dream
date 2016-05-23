package com.dream.repository.user;

import com.dream.entity.user.User;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Knight on 2015/6/10 11:16.
 */
public interface UserRepository extends MyRepository<User,Long>{

    User findByEmail(String email);

    User findByNickName(String nickName);

    User findByEmailAndPassword(String email,String password);

    List<User> findByStatus(int status,Pageable pageable);

    @Query("from User u where u.status=1")
    Page<User> findAllUser(Pageable pageable);

    @Query("from User u where u.status=1 and u.removed=:removed")
    Page<User> findAllUserAndRemoved(@Param("removed")boolean removed,Pageable pageable);

    Page<User> findByNickNameLikeAndStatus(String key,int status,Pageable pageable);

    Page<User> findByNickNameLikeAndStatusAndRemoved(String key,int status,boolean removed,Pageable pageable);

    List<User> findByStatusAndCreateTimeLessThan(int status,Date date);

    @Query(value = "select u.email,u.nick_name from user u where u.status = 1 and u.removed = 0 and u.removed_industry not like ?1 ",nativeQuery = true)
    List<Object[]> findByRemovedIndustry(String removedIndustry);

}
