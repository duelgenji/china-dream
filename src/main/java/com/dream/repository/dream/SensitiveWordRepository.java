package com.dream.repository.dream;

import com.dream.entity.dream.SensitiveWord;
import com.wonders.xlab.framework.repository.MyRepository;

/**
 * Created by Knight on 2015/7/28 15:02.
 */
public interface SensitiveWordRepository extends MyRepository<SensitiveWord,Long> {

    SensitiveWord findByContent(String content);
}
