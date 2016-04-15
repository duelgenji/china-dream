package com.dream.utils;

import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * 上传图片
 * Created by knight on 15/6/25.
 */
@Service
public class UploadUtils {

    public static String ACCESS_KEY = "f0c0xY3B9RvWVN0M0sgP7bQBCj6UGVXS8qhz3XAV";
    public static String SECRET_KEY = "K9cXNHbZKPYWTiBcunh8fA2i0PkpZcglsXi8bNU7";

    private static UploadManager uploadManager = new UploadManager();
    private static Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);


    /**
     * 上传图片至7牛
     * type 1 公有图片，2 私有图片，3 默认
     * @return
     */
    public static String uploadTo7niu(Integer type, String name, MultipartFile file) {

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

        String token = auth.uploadToken(bucketName, key);

        try {
            Response res = uploadManager.put(file.getBytes(), key, token);
        } catch (IOException e) {
            return "{\"ret_code\" : -1, \"message\" : \"上传失败\"}";
        }

        return url;
    }

    //判断是否图片   (application  audio  image video text multipart message)
    public static boolean isImage(MultipartFile file){
        String type = file.getContentType();
        return type.contains("image");

    }


}
