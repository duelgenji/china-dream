package com.dream.controller.dream;

import com.dream.entity.dream.DreamWord;
import com.dream.repository.dream.DreamWordRepository;
import com.wonders.xlab.framework.controller.AbstractBaseController;
import com.wonders.xlab.framework.repository.MyRepository;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
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


    private static final char last2byte = (char) Integer.parseInt("00000011", 2);
    private static final char last4byte = (char) Integer.parseInt("00001111", 2);
    private static final char last6byte = (char) Integer.parseInt("00111111", 2);
    private static final char lead6byte = (char) Integer.parseInt("11111100", 2);
    private static final char lead4byte = (char) Integer.parseInt("11110000", 2);
    private static final char lead2byte = (char) Integer.parseInt("11000000", 2);
    private static final char[] encodeTable = new char[] { 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static String standardURLEncoder(String data, String key) {
        byte[] byteHMAC = null;
        String urlEncoder = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
            if (byteHMAC != null) {
                String oauth = encode(byteHMAC);
                if (oauth != null) {
                    urlEncoder = URLEncoder.encode(oauth, "utf8");
                }
            }
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return urlEncoder;
    }

    public static String encode(byte[] from) {
        StringBuffer to = new StringBuffer((int) (from.length * 1.34) + 3);
        int num = 0;
        char currentByte = 0;
        for (int i = 0; i < from.length; i++) {
            num = num % 8;
            while (num < 8) {
                switch (num) {
                    case 0:
                        currentByte = (char) (from[i] & lead6byte);
                        currentByte = (char) (currentByte >>> 2);
                        break;
                    case 2:
                        currentByte = (char) (from[i] & last6byte);
                        break;
                    case 4:
                        currentByte = (char) (from[i] & last4byte);
                        currentByte = (char) (currentByte << 2);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead2byte) >>> 6;
                        }
                        break;
                    case 6:
                        currentByte = (char) (from[i] & last2byte);
                        currentByte = (char) (currentByte << 4);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead4byte) >>> 4;
                        }
                        break;
                }
                to.append(encodeTable[currentByte]);
                num += 6;
            }
        }
        if (to.length() % 4 != 0) {
            for (int i = 4 - to.length() % 4; i > 0; i--) {
                to.append("=");
            }
        }
        return to.toString();
    }


    /**
     * 获取语录列表
     */
    @RequestMapping("tq")
    public Map<String, Object> tq() {
        Map<String, Object> res = new HashMap<>();
        String data = "http://open.weather.com.cn/data/?areaid=101010100&type=index_f&date=201506241030&appid=0e6637183715ff1f";
        //密钥
        String key = "464ab5_SmartWeatherAPI_acfc61c";

        String str = standardURLEncoder(data, key);

        HttpClient client = new HttpClient();
        String url = "http://open.weather.com.cn/data/?areaid=101010100&type=index_f&date=201506241030&appid=0e6637&key="+str;
        //https://oauth.api.189.cn/emp/oauth2/v3/access_token?grant_type=authorization_code&code=91000975&app_id=178348250000035560&app_secret=abd2d041cb8613e0fee922ac7d7dbdff
//https://oauth.api.189.cn:443/emp/oauth2/v3/udblogin/26012302

        // PutMethod put = new
        // PutMethod("https://oauth.api.189.cn/emp/oauth2/v3/authorize?app_id=178348250000035560&app_secret=abd2d041cb8613e0fee922ac7d7dbdff&redirect_uri=http://115.28.176.74/189sms/auth.htm&response_type=token");
        PostMethod post = new PostMethod(url);
        post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");

        try {
            NameValuePair[] da2 = { new NameValuePair("userName", "18021012486"), new NameValuePair("password", "sk19880824") };
            //post.setRequestBody();
			//post.setRequestEntity(new StringRequestEntity("", "application/json", "UTF-8"));
            int responseCode = client.executeMethod(post);
            System.out.println(responseCode);

            String response = post.getResponseBodyAsString();
            URI purl = post.getURI();

            //Header locationHeader = post.getResponseHeader("location");

            res.put("p",new String(post.getResponseBodyAsString().getBytes("utf-8")));
            res.put("p2",post.getResponseBodyAsString());


            JSONObject jsonObject = new JSONObject(post.getResponseBodyAsString());

            res.put("jsonObject",jsonObject.getJSONArray("i").toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        res.put("success",1);
        return res;
    }

}
