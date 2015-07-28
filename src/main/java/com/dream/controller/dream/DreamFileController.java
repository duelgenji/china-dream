package com.dream.controller.dream;

import com.dream.entity.dream.DreamFile;
import com.dream.entity.user.Manager;
import com.dream.repository.dream.DreamFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件下载
 * Created by Knight on 2015/7/28 15:04.
 */
@RestController
@RequestMapping("dreamFile")
@SessionAttributes("currentManager")
public class DreamFileController {

    @Autowired
    DreamFileRepository dreamFileRepository;


    /**
     * 获取列表
     */
    @RequestMapping("retrieveDreamFileList")
    public Map<String, Object> retrieveDreamFileList(
            @PageableDefault(page = 0, size = 20,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Map<String, Object> res = new HashMap<>();

        Page<DreamFile> dreamFileList= dreamFileRepository.findAll(pageable);

        res.put("success",1);
        res.put("data",dreamFileList);
        return res;
    }


    /**
     * 新增
     */
    @RequestMapping("generateDreamFile")
    public Map<String, Object> generateDreamFile(
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


        DreamFile dreamFile = new DreamFile();
        dreamFile.setTitle(title);
        dreamFile.setUrl(url);
        dreamFileRepository.save(dreamFile);

        res.put("success",1);
        return res;
    }

    /**
     * 删除
     */
    @RequestMapping("removeDreamFile")
    public Map<String, Object> removeDreamFile(
            @RequestParam Long id,
            @ModelAttribute("currentManager") Manager manager

    ) {
        Map<String, Object> res = new HashMap<>();

        DreamFile dreamFile = dreamFileRepository.findOne(id);

        if(dreamFile==null){
            res.put("success",0);
            res.put("message","此记录不存在！");
            return res;
        }

        dreamFileRepository.delete(dreamFile);

        res.put("success",1);
        return res;
    }

}
