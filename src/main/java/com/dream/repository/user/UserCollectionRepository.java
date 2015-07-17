package com.dream.repository.user;

import com.dream.entity.user.User;
import com.dream.entity.user.UserCollection;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Knight on 2015/7/6 10:37.
 */
public interface UserCollectionRepository extends MyRepository<UserCollection,Long> {

    @Query("from UserCollection uc where uc.user = :user and uc.targetUser = :targetUser ")
    List<UserCollection> findByUserAndTargetUser(@Param("user") User user, @Param("targetUser") User targetUser);

    @Query("from UserCollection uc where uc.user = :user and uc.id = :id ")
    UserCollection findByUserAndId(@Param("user") User user, @Param("id") long id);
}
