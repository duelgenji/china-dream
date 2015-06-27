package com.dream.service.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.user.OpenStatus;
import com.dream.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.dream.entity.user.OpenStatus.OPEN;

/**
 * Created by Knight on 2015/6/27 14:02.
 */
@Service
public class InquiryService {

    public void putPropertiesByName(Map<String, Object> res, User user , Inquiry inquiry,String name,boolean isAuthorize){
        String value="";
        if(inquiry.getProperties(name)!=null){
            value=inquiry.getProperties(name).toString();

        }
        OpenStatus openStatus = (OpenStatus) inquiry.getProperties( name + "Open");

        if(inquiry.getUser().getId().equals(user.getId()) || openStatus==null || openStatus== OPEN ){
            res.put(name, value);
        }else if (openStatus == OpenStatus.CLOSED || user.getId()==null)  {
            res.put(name, "不公开");
        }else if (openStatus == OpenStatus.AUTHORIZE){
            if(isAuthorize)
                res.put(name, value);
            else
                res.put(name,  "授权后公开");
        }

    }

    public void putPrivateInfo(Map<String, Object> res, User user , Inquiry inquiry){


        String names[] = {"remark","contactName","contactEmail","contactPhone","contactTel",
                "contactFax","contactWeiBo","contactWeiXin"};

        for ( String name: names){
            putPropertiesByName(res,user,inquiry,name,true);
        }


//        if(inquiry.getFilesOpen()== OPEN){
//            res.put("remark", inquiry.getRemark());
//        }
//        if(inquiry.getFilesOpen()== OPEN){
//        }


    }

}
