package com.dream.controller.inquiry;

import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.message.Message;
import com.dream.entity.quotation.Quotation;
import com.dream.entity.quotation.QuotationFile;
import com.dream.entity.user.User;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.quotation.QuotationFileRepository;
import com.dream.repository.quotation.QuotationRepository;
import com.dream.repository.user.UserRepository;
import com.dream.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Knight on 2015/7/6 11:30.
 */
@RestController
@RequestMapping("quotation")
@SessionAttributes("currentUser")
public class QuotationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    QuotationFileRepository quotationFileRepository;

    @Value("${file_url}")
    private String file_url;

    /**
     * 正式出价
     */
    @RequestMapping("generateQuotation")
    public Map<String, Object> generateQuotation(
            @RequestParam(required = false) long inquiryId,
            @RequestParam double totalPrice,
            MultipartHttpServletRequest request,
            @ModelAttribute("currentUser") User user) {
        Map<String, Object> res = new HashMap<>();

        if(user.getId()==null){
            res.put("success",0);
            res.put("message","请先登录");
            return res;
        }


        Inquiry inquiry = inquiryRepository.findOne(inquiryId);
        if(inquiry==null){
            res.put("success",0);
            res.put("message","数据未查到");
            return res;
        }

        List<Message> list=messageRepository.findAllUserAndInquiryAndStatus(user,inquiry,1);
        if(list.size()==0){
            res.put("success",0);
            res.put("message","你没有获得授权");
            return res;
        }

        Quotation quotation = new Quotation();
        quotation.setUser(user);
        quotation.setInquiry(inquiry);
        quotation.setRound(inquiry.getRound());
        quotation.setTotalPrice(totalPrice);

        quotationRepository.save(quotation);


        Map<String,MultipartFile> fileMap = request.getFileMap();

        for (Map.Entry<String,MultipartFile> file :fileMap.entrySet()){


            String uname = file_url + inquiry.getId() + "q" + quotation.getId();

            String fileUrl  = UploadUtils.uploadTo7niu(0, uname, file.getValue());

            QuotationFile quotationFile = new QuotationFile();
            quotationFile.setQuotation(quotation);
            quotationFile.setFileUrl(fileUrl);
            quotationFile.setRemark(file.getValue().getOriginalFilename());

            if(file.getKey().contains("business")){
                quotationFile.setType(0);
                quotationFileRepository.save(quotationFile);

            }else if (file.getKey().contains("tech")){
                quotationFile.setType(1);
                quotationFileRepository.save(quotationFile);
            }
        }

        res.put("success",1);
        return res;

    }
}
