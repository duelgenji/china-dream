package com.dream.utils;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.URLUtils;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 上传图片
 * Created by knight on 15/6/25.
 */
@Service
public class UploadUtils {

    private static Mac mac;
    static {
        Config.ACCESS_KEY = "f0c0xY3B9RvWVN0M0sgP7bQBCj6UGVXS8qhz3XAV";
        Config.SECRET_KEY = "K9cXNHbZKPYWTiBcunh8fA2i0PkpZcglsXi8bNU7";
        mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
    }


    public static String retrievePriavteUrl(String url) {

        if (null == url || !url.startsWith("http://")) {
            return "";
        }

        String[] str = url.split("/");


        List<String> list = new ArrayList<>();
        for (int i = 3; i < str.length ; i++) {
            list.add(str[i]);
        }

        String downloadUrl = null;
        try {
            if (null != str[2] && list.size() > 0) {
                String baseUrl = URLUtils.makeBaseUrl(str[2], StringUtils.join(list, "/"));
                GetPolicy getPolicy = new GetPolicy();
                downloadUrl = getPolicy.makeRequest(baseUrl, mac);
            }
        } catch (EncoderException e) {
            e.printStackTrace();
        } catch (AuthException e) {
            e.printStackTrace();
        }
        return downloadUrl;
    }

    /**
     * 上传图片至7牛
     * type 1 公有图片，2 私有图片，3 默认
     * @return
     */
    public static String uploadTo7niu(Integer type, String name, MultipartFile file) {

        // 请确保该bucket已经存在
        InputStream io = null;
        try {
            io = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String bucketName;
//        String key = name + file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")) +  "_" +  DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")+"."+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String suffix = file.getOriginalFilename().contains(".")?"." +file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1):"";

        String key = name + "_" +  DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")+ suffix;

        String url;
        switch (type) {
            case 0 :
                bucketName = "chinadream";
                url = "http://7xk71v.dl1.z0.glb.clouddn.com/" + key ;
            break;
            default:
                bucketName = "chinadream";
                url = "http://7xk71v.dl1.z0.glb.clouddn.com/" + key ;
                break;
        }

        PutPolicy putPolicy = new PutPolicy(bucketName);

        try {
            String uptoken = putPolicy.token(mac);
            PutExtra extra = new PutExtra();
            IoApi.Put(uptoken, key, io, extra);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    //判断是否图片   (application  audio  image video text multipart message)
    public static boolean isImage(MultipartFile file){
        String type = file.getContentType();
        return type.contains("image");

    }


}
