package com.dream.repository.dream;

import com.dream.entity.dream.DreamWord;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Knight on 2015/6/18 15:02.
 */
public interface DreamWordRepository extends MyRepository<DreamWord,Long> {

    DreamWord findByContent(String content);

    @Query("from DreamWord order by rand()")
    List<DreamWord> orderByRand();
}
