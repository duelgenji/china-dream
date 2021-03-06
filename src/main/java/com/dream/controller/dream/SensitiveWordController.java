package com.dream.controller.dream;

import com.dream.entity.dream.DreamWord;
import com.dream.entity.dream.SensitiveWord;
import com.dream.entity.user.Manager;
import com.dream.repository.dream.SensitiveWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感词
 * Created by Knight on 2015/7/28 10:43.
 */
@RestController
@RequestMapping("sensitive")
@SessionAttributes("currentManager")
public class SensitiveWordController{

    @Autowired
    SensitiveWordRepository sensitiveWordRepository;

    /**
     * 获取 敏感词列表
     */
    @RequestMapping("retrieveSensitiveWordList")
    public Map<String, Object> retrieveSensitiveWordList(
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Map<String, Object> res = new HashMap<>();

        Page<SensitiveWord> sensitiveWordList= sensitiveWordRepository.findAll(pageable);

        res.put("success",1);
        res.put("data",sensitiveWordList);
        return res;
    }


    /**
     * 新增 敏感词
     */
    @RequestMapping("generateSensitiveWord")
    public Map<String, Object> generateSensitiveWord(
            @RequestParam String word,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        if(word==null || word.equals("")){
            res.put("success",0);
            res.put("message","输入不能为空");
            return res;
        }
        if(sensitiveWordRepository.findByContent(word)!=null){
            res.put("success",0);
            res.put("message","已经存在该敏感词,不用重复添加");
            return res;
        }


        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setContent(word);
        sensitiveWordRepository.save(sensitiveWord);

        res.put("success",1);
        return res;
    }

    /**
     * 新增 敏感词
     */
    @RequestMapping("generateSensitiveWords")
    public Map<String, Object> generateSensitiveWords(
            @RequestParam(required = false) List<String> words,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        int total = 0;
        int same = 0;

        if(words==null || words.size()==0){
            res.put("success",0);
            res.put("message","数据为空");
            return res;
        }

        SensitiveWord sensitiveWord ;
        for(String word : words){
            if(sensitiveWordRepository.findByContent(word)!=null){
                same++;
                continue;
            }

            sensitiveWord = new SensitiveWord();
            sensitiveWord.setContent(word);
            sensitiveWordRepository.save(sensitiveWord);
            total++;
        }



        res.put("success", 1);
        res.put("same", same);
        res.put("total",total);
        return res;
    }

    /**
     * 删除 敏感词
     */
    @RequestMapping("removeSensitiveWord")
    public Map<String, Object> removeSensitiveWord(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        SensitiveWord sensitiveWord = sensitiveWordRepository.findOne(id);

        if(sensitiveWord==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        sensitiveWordRepository.delete(sensitiveWord);

        res.put("success",1);
        return res;
    }

}
