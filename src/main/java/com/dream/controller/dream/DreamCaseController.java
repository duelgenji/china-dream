package com.dream.controller.dream;

import com.dream.entity.dream.DreamCase;
import com.dream.entity.user.Manager;
import com.dream.repository.dream.DreamCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 案例分析
 * Created by Knight on 2015/7/28 15:04.
 */
@RestController
@RequestMapping("dreamCase")
@SessionAttributes("currentManager")
public class DreamCaseController{

    @Autowired
    DreamCaseRepository dreamCaseRepository;


    /**
     * 获取列表
     */
    @RequestMapping("retrieveDreamCaseList")
    public Map<String, Object> retrieveDreamCaseList(
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Map<String, Object> res = new HashMap<>();

        Page<DreamCase> dreamCaseList= dreamCaseRepository.findAll(pageable);

        res.put("success",1);
        res.put("data",dreamCaseList);
        return res;
    }


    /**
     * 新增
     */
    @RequestMapping("generateDreamCase")
    public Map<String, Object> generateDreamCase(
            @RequestParam String title,
            @RequestParam String url,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        if(title==null || title.equals("")){
            res.put("success",0);
            res.put("message","输入不能为空");
            return res;
        }
        if(url==null || url.equals("")){
            res.put("success",0);
            res.put("message","输入不能为空");
            return res;
        }


        DreamCase dreamCase = new DreamCase();
        dreamCase.setTitle(title);
        dreamCase.setUrl(url);
        dreamCaseRepository.save(dreamCase);

        res.put("success",1);
        return res;
    }

    /**
     * 删除
     */
    @RequestMapping("removeDreamCase")
    public Map<String, Object> removeDreamCase(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        DreamCase dreamCase = dreamCaseRepository.findOne(id);

        if(dreamCase==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        dreamCaseRepository.delete(dreamCase);

        res.put("success",1);
        return res;
    }

}
