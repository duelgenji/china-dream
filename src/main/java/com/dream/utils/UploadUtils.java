package com.dream.utils;

import com.dream.dto.excel.FileLink;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.processing.OperationStatus;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 上传图片
 * Created by knight on 15/6/25.
 */
@Service
public class UploadUtils {

    public static String ACCESS_KEY = "f0c0xY3B9RvWVN0M0sgP7bQBCj6UGVXS8qhz3XAV";
    public static String SECRET_KEY = "K9cXNHbZKPYWTiBcunh8fA2i0PkpZcglsXi8bNU7";

    private static String BUCKET = "chinadream";

    private static Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    Zone z = Zone.zone0();
    Configuration c = new Configuration(z);


    /**
     * 上传图片至7牛
     * type 1 公有图片，2 私有图片，3 默认
     * @return
     */
    public static String uploadTo7niu(Integer type, String name, MultipartFile file) {
        Zone z = Zone.autoZone();
        Configuration c = new Configuration(z);
         UploadManager uploadManager = new UploadManager(c);

        String bucketName;
//        String key = name + file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf(".")) +  "_" +  DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")+"."+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String suffix = file.getOriginalFilename().contains(".")?"." +file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1):"";

        String key = name + "_" +  DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")+ suffix;

        String url;
        switch (type) {
            case 0 :
                bucketName = "chinadream";
                url = "http://cdn.mychinadreams.com/" + key ;
            break;
            default:
                bucketName = "chinadream";
                url = "http://cdn.mychinadreams.com/" + key ;
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

    public static String uploadFile(String fileName, byte[] fileBytes) {
        Zone z = Zone.autoZone();
        Configuration c = new Configuration(z);
        UploadManager uploadManager = new UploadManager(c);

        String url = "http://cdn.mychinadreams.com/" + fileName ;
        String token = auth.uploadToken(BUCKET, fileName);

        try {
            Response res = uploadManager.put(fileBytes, fileName, token);
        } catch (IOException e) {
            return "{\"ret_code\" : -1, \"message\" : \"上传失败\"}";
        }

        return url;
    }


    public static String mkzip(String excelUrl, List<FileLink> fileLinkList, String os) {

        String excelName = Arrays.asList(excelUrl.split("/")).get(Arrays.asList(excelUrl.split("/")).size()-1);

        Zone z = Zone.zone0();
        Configuration c = new Configuration(z);

        //实例化一个BucketManager对象
        String BUCKET = "chinadream";

        try {
            //索引文件路径
            String txtKey = "zip/index.txt";
            //默认utf-8，但是中文显示乱码，修改为gbk
            OperationManager operater = new OperationManager(auth, c);


            StringBuffer fps = new StringBuffer();
            if(os!=null && os.equals("Windows")){
                fps.append("mkzip/2/encoding/"+UrlSafeBase64.encodeToString("gbk"));
            }else{
                fps.append("mkzip/2");
            }
            //等待压缩的文件
            //excel文件
            fps.append("/url/"+UrlSafeBase64.encodeToString(excelUrl)+"/alias/"+UrlSafeBase64.encodeToString(excelName));
            for(FileLink fileLink : fileLinkList){
                fps.append("/url/"+UrlSafeBase64.encodeToString(fileLink.getUrl())+"/alias/"+UrlSafeBase64.encodeToString("file/"+fileLink.getName()));
            }

            fps.append("|saveas/"+UrlSafeBase64.encodeToString("chinadream:zip/"+excelName.split("\\.")[0]+".zip"));
            //压缩完成后，七牛回调URL
            //params.put("notifyURL", NOTIFY_URL);

            String id = operater.pfop(BUCKET, txtKey, fps.toString(), "","");
            String purl = "http://api.qiniu.com/status/get/prefop?id="+ id;
            return id;
        } catch (QiniuException e) {
            Response res = e.response;
            System.out.println(res);
            try {
                System.out.println(res.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
            }
            return "";
        }

    }


    public OperationStatus checkZipStatus(String id){
        OperationManager operater = new OperationManager(auth, c);

        try {
            return operater.prefop(id);

        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        new UploadUtils().checkZipStatus("z0.5abc79b3b9465312103f9d7a");

    }




}
