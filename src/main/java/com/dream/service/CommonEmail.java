package com.dream.service;

import com.dream.entity.user.User;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        sEmail.setAuthentication("info@mychinadreams.com", "Macbook010");
        //接收人
        try {
            sEmail.addTo(email);
            //发送人
            sEmail.setFrom("info@mychinadreams.com", "中梦国网");
            //标题
            StringBuffer buffer = new StringBuffer();
            buffer.append("<img src='http://121.40.143.120:8080/ChinaDream/image/logo.png'/>");
            buffer.append("<div style='font-weight:bolder;font-size:20px;'>你好， 中国梦网用户0000000"+userId+"，欢迎成为中国梦网用户。</div>");
            buffer.append("<div style='font-size:18px;'>请点击下面的链接对你的邮箱进行验证：</div>");
            buffer.append("<br>");
            buffer.append("<div><font size =\"3\" face=\"arial\" >" + url + "activateAccount?id=" + userId +"&captcha=" + captcha + "</font></div>");
            buffer.append("<div style='font-size:18px;color:#999;'>(此链接1天内有效，超时需要重新获取验证邮件)</div>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>如果该链接无法点击，请将其复制粘贴到你的浏览器地址栏中访问。</div>");
            buffer.append("<div style='font-size:18px;'>验证邮箱会增加帐号的安全性，也会享受更多的优酷服务。</div>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>祝使用愉快！</div>");
            buffer.append("<div style='font-size:18px;'>中梦国网 "+ DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss")+"</div>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:14px;color:#999;'>如果你没有注册过中梦国网，请忽略此邮件。</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>这是中梦国网的帐号服务邮件，请不要回复。</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>如需了解中梦国网或遇到注册问题，请访问我们的网站（http://www.mychinadreams.com）</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>或关注我们的微信公众号 ： 中梦国网   微信号：zmguowang </div>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("中梦国网");
            sEmail.setSubject("中梦国网欢迎您 请立即激活您的账户");
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


    public String sendForgetPwdMail(String url, User user, String email, String captcha) {


        HtmlEmail sEmail = new HtmlEmail ();
        //smtp host
        sEmail.setHostName("smtp.mxhichina.com");
        //登陆邮件服务器的用户名和密码
        sEmail.setAuthentication("info@mychinadreams.com", "Macbook010");
        //接收人
        try {
            sEmail.addTo(email);
            //发送人
            sEmail.setFrom("info@mychinadreams.com", "中梦国网");
            //标题
            StringBuffer buffer = new StringBuffer();
            buffer.append("<img src='http://121.40.143.120:8080/ChinaDream/image/logo.png'/>");
            buffer.append("<div style='font-weight:bolder;font-size:20px;'>你好， "+user.getNickName()+" </div>");
            buffer.append("<div style='font-size:18px;'>请点击下面的链接重置密码：：</div>");
            buffer.append("<br>");
            buffer.append("<div><font size =\"3\" face=\"arial\" >" + url + "activateForget?id=" + user.getId() +"&captcha=" + captcha + "</font></div>");
            buffer.append("<div style='font-size:18px;color:#999;'>(此链接1天内有效，超时需要重新获取验证邮件)</div>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>如果该链接无法点击，请将其复制粘贴到你的浏览器地址栏中访问。</div>");
            buffer.append("<div style='font-size:18px;'>祝使用愉快！</div>");
            buffer.append("<div style='font-size:18px;'>中梦国网 "+ DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss")+"</div>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:14px;color:#999;'>如果你没有注册过中梦国网，请忽略此邮件。</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>这是中梦国网的帐号服务邮件，请不要回复。</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>如需了解中梦国网或遇到注册问题，请访问我们的网站（http://www.mychinadreams.com）</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>或关注我们的微信公众号 ： 中梦国网   微信号：zmguowang </div>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("中梦国网");
            sEmail.setSubject("中梦国网欢迎您 请重置您的密码");
//            sEmail.setMsg(buffer.toString());
            sEmail.setHtmlMsg(buffer.toString());
            //发送
            sEmail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return "1";
    }

}
