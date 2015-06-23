package com.dream.service;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Service;

/**
 * Created by mars on 15/3/23.
 */
@Service
public class CommonEmail {

    public String sendTextMail(String url, long userId, String email, String captcha) {

//        SimpleEmail sEmail = new SimpleEmail();

        HtmlEmail sEmail = new HtmlEmail ();
        //smtp host
        sEmail.setHostName("smtp.mxhichina.com");
        //登陆邮件服务器的用户名和密码
        sEmail.setAuthentication("postmaster@qudiaoyan.cn", "Qubaopen2013");
        //接收人
        try {
            sEmail.addTo(email);
            //发送人
            sEmail.setFrom("postmaster@qudiaoyan.cn", "中国梦");
            //标题
            StringBuffer buffer = new StringBuffer();
            buffer.append("感谢您中国梦！");
            buffer.append("<br>");
            buffer.append("请在15分钟内点击以下链接激活您的帐户、完成注册。");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("<div><font size =\"3\" face=\"arial\" >" + url + "activateAccount?id=" + userId +"&captcha=" + captcha + "</font></div>");
            buffer.append("<br>");
            buffer.append("[中国梦]");
            sEmail.setSubject("中国梦欢迎您 请立即激活您的账户");
            //邮件内容
            sEmail.setHtmlMsg(buffer.toString());
//            sEmail.setMsg(buffer.toString());
            //发送
            sEmail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return "1";
    }


}
