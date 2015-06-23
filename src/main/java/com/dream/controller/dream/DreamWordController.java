package com.dream.controller.dream;

import com.dream.entity.dream.DreamWord;
import com.dream.repository.dream.DreamWordRepository;
import com.wonders.xlab.framework.controller.AbstractBaseController;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 梦想语录
 * Created by Knight on 2015/6/18 15:01.
 */
@RestController
@RequestMapping("dreamWord")
public class DreamWordController extends AbstractBaseController<DreamWord, Long> {

    @Autowired
    DreamWordRepository dreamWordRepository;

    @Override
    protected MyRepository<DreamWord, Long> getRepository() {
        return dreamWordRepository;
    }

    /**
     * 获取语录列表
     */
    @RequestMapping("retrieveDreamWordList")
    public Map<String, Object> retrieveDreamWordList() {
        Map<String, Object> res = new HashMap<>();

        List<DreamWord> wordList= dreamWordRepository.findAll();

        res.put("success",1);
        res.put("data",wordList);
        return res;
    }


}
