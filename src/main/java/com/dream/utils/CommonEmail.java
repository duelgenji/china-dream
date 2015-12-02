package com.dream.utils;

import com.dream.entity.inquiry.Inquiry;
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

    public enum TYPE{
        AUTO60,AUTO67,FAIL_B,ROUND_B,SUCCESS_B,CHOSEN_B,REJECT_A,NO_RESPONSE_A,INVITE_B,AGREE_B,REQUEST_A
    }

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
            buffer.append("<img src='http://121.40.143.120/dream/image/logo.png'/>");
            buffer.append("<div style='font-weight:bolder;font-size:20px;'>你好， 中国梦网用户0000000"+userId+"，欢迎成为中国梦网用户。</div>");
            buffer.append("<div style='font-size:18px;'>请点击下面的链接对你的邮箱进行验证：</div>");
            buffer.append("<br>");
            buffer.append("<div><font size =\"3\" face=\"arial\" >" + url + "activateAccount?id=" + userId +"&captcha=" + captcha + "</font></div>");
            buffer.append("<div style='font-size:18px;color:#999;'>(此链接1天内有效，超时需要重新获取验证邮件)</div>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>如果该链接无法点击，请将其复制粘贴到你的浏览器地址栏中访问。</div>");
            buffer.append("<div style='font-size:18px;'>验证邮箱会增加帐号的安全性，也会享受更多的中梦国网服务。</div>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>祝使用愉快！</div>");
            buffer.append("<div style='font-size:18px;'>中梦国网 "+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"</div>");
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
            buffer.append("<img src='http://121.40.143.120/dream/image/logo.png'/>");
            buffer.append("<div style='font-weight:bolder;font-size:20px;'>你好， "+user.getNickName()+" </div>");
            buffer.append("<div style='font-size:18px;'>请点击下面的链接重置密码：：</div>");
            buffer.append("<br>");
            buffer.append("<div><font size =\"3\" face=\"arial\" >" + url + "activateForget?id=" + user.getId() +"&captcha=" + captcha + "</font></div>");
            buffer.append("<div style='font-size:18px;color:#999;'>(此链接1天内有效，超时需要重新获取验证邮件)</div>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>如果该链接无法点击，请将其复制粘贴到你的浏览器地址栏中访问。</div>");
            buffer.append("<div style='font-size:18px;'>祝使用愉快！</div>");
            buffer.append("<div style='font-size:18px;'>中梦国网 "+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"</div>");
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

    public String sendEmail(String email,String content){

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
            sEmail.setSubject("中梦国网 系统提示");
            //标题
            StringBuffer buffer = new StringBuffer();
            buffer.append("<img src='http://121.40.143.120/dream/image/logo.png'/>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>"+content+"</div>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:18px;'>更多精彩梦想/询价， 敬请登录中梦国网（http://www.mychinadreams.com）</div>");
            buffer.append("<div style='font-size:18px;'>祝使用愉快！</div>");
            buffer.append("<div style='font-size:18px;'>中梦国网 "+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"</div>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("<div style='font-size:14px;color:#999;'>如果你没有注册过中梦国网，请忽略此邮件。</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>这是中梦国网的帐号服务邮件，请不要回复。</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>如需了解中梦国网或遇到注册问题，请访问我们的网站（http://www.mychinadreams.com）</div>");
            buffer.append("<div style='font-size:14px;color:#999;'>或关注我们的微信公众号 ： 中梦国网   微信号：zmguowang </div>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("<img width='160' height='160' src='http://121.40.143.120/dream/image/weixinLogo.jpg'/>");
            buffer.append("<br>");
            buffer.append("<br>");
            buffer.append("中梦国网");
//            sEmail.setMsg(buffer.toString());
            sEmail.setHtmlMsg(buffer.toString());
            //发送
            sEmail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return "1";
    }

    public String getContent(TYPE type,Inquiry inquiry,User user){
        switch (type){
            case AUTO60:
                return "你好,"+inquiry.getUser().getNickName()+"。\n" +
                        "您在中梦国网发布的标号为 "+inquiry.getInquiryNo()+" ， 即将到达该轮截止时间， 请尽快登录并按实际情况操作， 成功， 下一轮， 流标， 如5天内未做任何响应， 系统会自动流标， 并将记录在您的中梦国网的流标指数。\n";
            case AUTO67:
                return "你好,"+inquiry.getUser().getNickName()+"。\n" +
                        "您在中梦国网发布的标号为 "+inquiry.getInquiryNo()+" ， 已经到达该轮截止时间， 由于您未进行如何后续操作， 该询价自动流标， 本次流标将计入中梦指数。\n";
            case FAIL_B:
                return "你好,"+user.getNickName()+"。\n" +
                        "您在中梦国网参与的标号为 "+inquiry.getInquiryNo()+" ， 未寻找到合适的出价方， 已经流标。\n";
            case ROUND_B:
                return "你好,"+user.getNickName()+"。\n" +
                        "您在中梦国网参与的标号为 "+inquiry.getInquiryNo()+" ， 进入下一轮， 是否继续接受您的出价取决于询价发布方。\n";
            case SUCCESS_B:
                return "你好,"+user.getNickName()+"。\n" +
                        "您在中梦国网参与的标号为 "+inquiry.getInquiryNo()+" ， 已经成功寻找到合适的出价方/圆梦者， 详情登录中梦国网。\n";
            case CHOSEN_B:
                return "你好,"+user.getNickName()+"。\n" +
                        "您在中梦国网参与的标号为 "+inquiry.getInquiryNo()+" ， 询价方"+inquiry.getUser().getNickName()+"已经确认您为最合适人选， 如您已经与该询价方线下合同，请登录中梦国网（http://www.mychinadreams.com） 并在  “我的主页”/“站内信息”中“出价成功确认”下拉框中选择响应的标号点击“确认”， 系统才能认定该询价真正成功（见下图示列）：\n"+
                        "<img src='http://121.40.143.120/dream/image/chosenExample.png'/>\n" +
                        "如询价方"+inquiry.getUser().getNickName()+"并未与您线下合同，请选择拒绝；\n" +
                        "如您未做任何操作，系统将在4日之后自动流标，  详情登录中梦国网。\n" ;
            case REJECT_A:
                return "你好,"+inquiry.getUser().getNickName()+"。\n" +
                        "您在中梦国网发布的标号为 "+inquiry.getInquiryNo()+" ，  并勾选的成功出价方"+user.getNickName()+"拒绝了您的成功确认， 请重新选择出价方， （如其误操作， 请电话告知其小心谨慎）。\n";
            case NO_RESPONSE_A:
                return "你好,"+inquiry.getUser().getNickName()+"。\n" +
                        "您在中梦国网发布的标号为 "+inquiry.getInquiryNo()+" ， 并勾选的成功出价方"+user.getNickName()+"没有确认， 请线下通知， 否则， 系统将在2天后自动流标。\n";
            case INVITE_B:
                return "你好,"+user.getNickName()+"。\n" +
                        inquiry.getUser().getNickName()+"在中梦国网发布的标号为 "+inquiry.getInquiryNo()+" ， 邀请您参与并已经全部授权， 请登录中梦国网在“我的主页”中“我的出价”中“未报价”下拉框中找到该询价， 参见下图：\n"+
                        "<img src='http://121.40.143.120/dream/image/inviteExample.png'/>\n" ;
            case AGREE_B:
                return "你好,"+user.getNickName()+"。\n" +
                        "您在中梦国网申请的的标号为 "+inquiry.getInquiryNo()+" ， 询价方"+inquiry.getUser().getNickName()+"已经同意了您的申请 ，并已经全部授权， 请登录中梦国网在“我的主页”中“我的出价”中“未报价”下拉框中找到该询价， 参见下图：\n"+
                        "<img src='http://121.40.143.120/dream/image/agreeExample.png'/>\n" ;
            case REQUEST_A:
                return "你好,"+inquiry.getUser().getNickName()+"。\n" +
                        "您在中梦国网发布的标号为 "+inquiry.getInquiryNo()+" ，  出价方"+user.getNickName()+"（乙方）向您提出了申请 ，并发送站内信， 请登录中梦国网在“我的主页”中“站内信息”中“需我回复的”下拉框中找到该询价， 参见下图， 点击查看，可见申请方留言； 点击其ID， 可浏览其主页，并可链接至其网站，判断是否同意。\n"+
                        "<img src='http://121.40.143.120/dream/image/requestExample.png'/>\n";
            default:
                break;
        }
        return null;
    }

//    public static void testEmail(String email) {
//        HtmlEmail sEmail = new HtmlEmail ();
//        sEmail.setHostName("smtp.mxhichina.com");
//        sEmail.setAuthentication("info@mychinadreams.com", "Macbook010");
//        //接收人
//        try {
//            System.out.println(email);
//            sEmail.addTo(email);
//            sEmail.setFrom("info@mychinadreams.com", "中梦国网");
//            sEmail.setHtmlMsg("中梦国网测试邮件");
//            sEmail.send();
//        } catch (EmailException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        testEmail("asdasdas@asdas11dasd.com");
//    }
}
