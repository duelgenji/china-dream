package com.dream.service.inquiry;

import com.dream.dto.excel.ExportDto;
import com.dream.dto.excel.InquiryExportDto;
import com.dream.dto.excel.QuotationExportDto;
import com.dream.dto.excel.RoundExportDto;
import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.inquiry.InquiryHistory;
import com.dream.entity.message.Message;
import com.dream.entity.quotation.Quotation;
import com.dream.entity.quotation.QuotationFile;
import com.dream.entity.user.*;
import com.dream.interceptor.CommonException;
import com.dream.repository.inquiry.InquiryFileRepository;
import com.dream.repository.inquiry.InquiryHistoryRepository;
import com.dream.repository.inquiry.InquiryRepository;
import com.dream.repository.message.MessageRepository;
import com.dream.repository.quotation.QuotationFileRepository;
import com.dream.repository.quotation.QuotationRepository;
import com.dream.repository.user.UserAccountLogRepository;
import com.dream.repository.user.UserExportLogRepository;
import com.dream.repository.user.UserIndexRepository;
import com.dream.repository.user.UserRepository;
import com.dream.utils.CommonEmail;
import com.dream.utils.ExcelUtils;
import com.dream.utils.UploadUtils;
import com.qiniu.processing.OperationStatus;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;

import static com.dream.entity.user.OpenStatus.OPEN;

/**
 * Created by Knight on 2015/6/27 14:02.
 */
@Service
public class InquiryService {

    Logger logger = LoggerFactory.getLogger(InquiryService.class);

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    InquiryFileRepository inquiryFileRepository;


    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    QuotationFileRepository quotationFileRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserIndexRepository userIndexRepository;

    @Autowired
    InquiryHistoryRepository inquiryHistoryRepository;

    @Autowired
    UserAccountLogRepository userAccountLogRepository;

    @Autowired
    UserExportLogRepository userExportLogRepository;

    @Autowired
    CommonEmail commonEmail;

    private void putPropertiesByName(Map<String, Object> res, User user , Inquiry inquiry,String name,boolean isAuthorize){
        String value="";

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties( name + "Open");

        if(openStatus== OPEN || inquiry.getUser().getId().equals(user.getId()) || (openStatus == OpenStatus.AUTHORIZE && isAuthorize) || openStatus==null){
            if(inquiry.getProperties(name)!=null){
                value=inquiry.getProperties(name).toString();
            }
        }
        assert openStatus != null;
        res.put(name, value + "(" + openStatus.getName() +")");

    }

    public void putPrivateInfo(Map<String, Object> res, User user , Inquiry inquiry){

        boolean isAuthorize= user.getId() != null && messageRepository.findAllUserAndInquiryAndStatus(user, inquiry, 1).size() != 0;


        String names[] = {"remark","contactName","contactEmail","contactPhone","contactTel",
                "contactFax","contactWeiBo","contactWeiXin"};

        for ( String name: names){
            putPropertiesByName(res,user,inquiry,name,isAuthorize);
        }

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties("filesOpen");
        List<InquiryFile> fileList = new ArrayList<InquiryFile>();

        if(openStatus== OPEN || inquiry.getUser().getId().equals(user.getId()) || (openStatus == OpenStatus.AUTHORIZE && isAuthorize) || openStatus==null){
            fileList = inquiryFileRepository.findByInquiryAndRound(inquiry,inquiry.getRound());
        }
        assert openStatus != null;
        res.put("files",  "(" + openStatus.getName() +")");

        res.put("fileList", fileList);

    }

    /**
     * 他人出价 我的出价
     */
    public void putQuotationList(Map<String, Object> res, User user , Inquiry inquiry){

        List<Quotation> quotationList= new ArrayList<>();

        /*甲方标记*/
        boolean isOwner=user.getId()!=null && user.getId().equals(inquiry.getUser().getId());

        /*授权标记*/
        boolean isAuthorize= user.getId()!=null &&  messageRepository.findAllUserAndInquiryAndStatus(user, inquiry, 1).size() != 0;

        /*排名标记*/
        boolean isRank = false;
        //全明询价 只有注册用户才能看到他人出价
        if(user.getId()!=null){
            //除了全明询价 都至少授权才能看到
            if(inquiry.getInquiryMode().getId()<2 || isAuthorize || isOwner){
                quotationList  =quotationRepository.findByInquiryAndRoundOrderByCreateTimeDesc(inquiry, inquiry.getRound());
            }
        }
        List<QuotationFile> quotationFileList = new ArrayList<>();
        List<Map<String, Object>> myList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> histList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> techFileList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> businessFileList = new ArrayList<Map<String,Object>>();

        for( Quotation quotation : quotationList ){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("quotationId",quotation.getId());
            map.put("createTime", DateFormatUtils.format(quotation.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("round",quotation.getRound());


            map.put("totalPrice",quotation.getTotalPrice());
            map.put("userId",quotation.getUser().getId());
            map.put("userNickname",quotation.getUser().getNickName());

            if(quotation.getUser().getCompanyProvince()!=null){
                map.put("userProvince", quotation.getUser().getCompanyProvince().getName());
            }else{
                map.put("userProvince","");
            }
            map.put("userNickName",quotation.getUser().getNickName());

            businessFileList = new ArrayList<Map<String,Object>>();
            techFileList = new ArrayList<Map<String,Object>>();
            quotationFileList = new ArrayList<>();
            if(quotation.getUser().getId().equals(user.getId())){
                //我的出价
                quotationFileList=quotationFileRepository.findByQuotation(quotation);
                for(QuotationFile quotationFile : quotationFileList){
                    Map<String, Object> fileMap = new HashMap<String, Object>();
                    fileMap.put("id",quotationFile.getId());
                    fileMap.put("type",quotationFile.getType());
                    fileMap.put("fileUrl",quotationFile.getFileUrl());
                    fileMap.put("remark",quotationFile.getRemark());
                    if(quotationFile.getType()==0){
                        businessFileList.add(fileMap);
                    }else if(quotationFile.getType()==1){
                        techFileList.add(fileMap);
                    }
                }

                //竞价排名 或者 限时竞价 获取我的出价排名
                if(!isRank && (inquiry.getInquiryMode().getId()==1 || inquiry.getInquiryMode().getId()==7 )){
                    map.put("rank",getRank(inquiry,quotation));
                    isRank = true;
                }

                map.put("businessFileList",businessFileList);
                map.put("techFileList",techFileList);
                myList.add(map);
            }else{
                //他人出价

                //全明询价 改为无法看到他人出价
                if(inquiry.getInquiryMode().getId().equals(1L) && !isOwner){
                    continue;
                }

                if(!inquiry.getInquiryMode().getId().equals(3l) || isOwner){
                    /*半明询价 乙方不能看到文件*/
                    quotationFileList=quotationFileRepository.findByQuotation(quotation);
                }
                for(QuotationFile quotationFile : quotationFileList){
                    Map<String, Object> fileMap = new HashMap<String, Object>();
                    fileMap.put("id",quotationFile.getId());
                    fileMap.put("type",quotationFile.getType());
                    fileMap.put("fileUrl",quotationFile.getFileUrl());
                    fileMap.put("remark",quotationFile.getRemark());
                    if(quotationFile.getType()==0){
                        businessFileList.add(fileMap);
                    }else if(quotationFile.getType()==1){
                        techFileList.add(fileMap);
                    }
                }
                map.put("businessFileList",businessFileList);
                map.put("techFileList",techFileList);

                if(inquiry.getInquiryMode().getId()<=3 ){
                    /*明询价 或者是甲方 才能看到他人*/
                    histList.add(map);
                }else if(isOwner){
                    if(inquiry.getInquiryMode().getId().equals(4l)){
                        /* 半暗询价 甲方可以看到*/
                        histList.add(map);
                    }else if(inquiry.getInquiryMode().getId().equals(5l) ){
                        /* 暗询价 截止时间到 甲方可以看到 */
                        if(new Date().compareTo(inquiry.getLimitDate())<0){
                            map.put("techFileList",new ArrayList<>());
                        }
                        if((new Date().getTime()-inquiry.getLimitDate().getTime())<inquiry.getIntervalHour()*3600*1000){
                            /* 暗询价 截止时间到 n小时 （2-240小时）之前  甲方是不能看到 商务文件和价格 */
                            map.put("totalPrice", "***");
                            map.put("businessFileList",new ArrayList<>());
                        }
                        histList.add(map);
                    }else if(inquiry.getInquiryMode().getId().equals(6l)){
                        if((new Date().getTime()-inquiry.getLimitDate().getTime())<2*3600*1000){
                            map.put("techFileList",new ArrayList<>());
                            map.put("totalPrice", "***");
                            map.put("businessFileList",new ArrayList<>());
                        }
                        /* 全暗询价 截止时间 2小时后 甲方可以看到 */
                        histList.add(map);
                    }
                }
            }
        }

        res.put("myList",myList);
        res.put("hisList",histList);
    }


    /**
     * 计算 个人指数
     */
    public void calcUserIndex(User user){

        UserIndex userIndex = userIndexRepository.findOne(user.getId());

        if(userIndex==null){
            userIndex = new UserIndex();
            userIndex.setId(user.getId());
        }

        //int quotationDoneTime= quotationRepository.countByInquiryAndUser(user.getId());
        int quotationDoneTime= quotationRepository.countByDoneTimes(user.getId());
        int quotationSuccessTime= inquiryRepository.countByWinner(user);
        //int quotationDoingTime= messageRepository.countByInquiryAndUser(user);
        int quotationDoingTime = quotationRepository.countByDoingTimes(user.getId());

        int inquirySuccessTime = inquiryRepository.countByUserAndStatus(user, 1);
        long inquiryDoingTime = inquiryRepository.countByUserAndStatus(user, 0);
        long inquiryFailTime = inquiryRepository.countByUserAndStatus(user, 2);
        int inquiryDoneTime = (int) (inquirySuccessTime + inquiryFailTime);


        userIndex.setQuotationDoneTime(quotationDoneTime);
        userIndex.setQuotationSuccessTime(quotationSuccessTime);
        userIndex.setInquiryDoneTime(inquiryDoneTime);
        userIndex.setInquirySuccessTime(inquirySuccessTime);


        if(quotationDoneTime == 0 || quotationSuccessTime == 0 ){
            userIndex.setQuotationSuccessRate(0);
        }else{
            userIndex.setQuotationSuccessRate((double)quotationSuccessTime/(double)quotationDoneTime * 100);
        }

        if(inquiryDoneTime == 0l || inquirySuccessTime == 0l ){
            userIndex.setInquirySuccessRate(0);
        }else{
            userIndex.setInquirySuccessRate((double)inquirySuccessTime/(double)inquiryDoneTime * 100);
        }

        userIndexRepository.save(userIndex);
    }


    public void saveInquiryHistory(Inquiry inquiry){
        InquiryHistory inquiryHistory = new InquiryHistory();
        inquiryHistory.setInquiry(inquiry);
        inquiryHistory.setInquiryNo(inquiry.getInquiryNo());
        inquiryHistory.setStatus(inquiry.getStatus());
        inquiryHistory.setRound(inquiry.getRound());
        inquiryHistory.setCompanyIndustry(inquiry.getCompanyIndustry());
        inquiryHistory.setCompanyProvince(inquiry.getCompanyProvince());
        inquiryHistory.setInquiryMode(inquiry.getInquiryMode());

        inquiryHistory.setRemark(inquiry.getRemark());
        inquiryHistory.setContactEmail(inquiry.getContactEmail());
        inquiryHistory.setContactWeiBo(inquiry.getContactWeiXin());
        inquiryHistory.setContactWeiXin(inquiry.getContactWeiXin());
        inquiryHistory.setContactFax(inquiry.getContactFax());
        inquiryHistory.setContactName(inquiry.getContactName());
        inquiryHistory.setContactPhone(inquiry.getContactPhone());
        inquiryHistory.setContactTel(inquiry.getContactTel());

        inquiryHistory.setRemarkOpen(inquiry.getRemarkOpen());
        inquiryHistory.setContactEmailOpen(inquiry.getContactEmailOpen());
        inquiryHistory.setContactWeiBoOpen(inquiry.getContactWeiBoOpen());
        inquiryHistory.setContactWeiXinOpen(inquiry.getContactWeiXinOpen());
        inquiryHistory.setContactFaxOpen(inquiry.getContactFaxOpen());
        inquiryHistory.setContactNameOpen(inquiry.getContactNameOpen());
        inquiryHistory.setContactPhoneOpen(inquiry.getContactPhoneOpen());
        inquiryHistory.setContactTelOpen(inquiry.getContactTelOpen());
        inquiryHistory.setFilesOpen(inquiry.getFilesOpen());

        inquiryHistory.setPurchaseCloseDate(inquiry.getPurchaseCloseDate());
        inquiryHistory.setLimitDate(inquiry.getLimitDate());
        inquiryHistory.setModifyDate(inquiry.getModifyDate());
        inquiryHistory.setCreateDate(inquiry.getCreateDate());
        inquiryHistory.setHotLevel(inquiry.getHotLevel());
        inquiryHistory.setTotalPrice(inquiry.getTotalPrice());
        inquiryHistory.setTitle(inquiry.getTitle());
        inquiryHistory.setUserLimit(inquiry.getUserLimit());
        inquiryHistory.setUser(inquiry.getUser());
        inquiryHistory.setLogoUrl(inquiry.getLogoUrl());

        inquiryHistoryRepository.save(inquiryHistory);

    }

    /**
     * 计算前两轮数据
     */
    public void putExRoundInfo(Map<String, Object> res, Inquiry inquiry){

        List<InquiryHistory> inquiryHistoryList = inquiryHistoryRepository.findByInquiry(inquiry);
        List<Map<String, Object>> historyList = new ArrayList<Map<String,Object>>();

        for (InquiryHistory inquiryHistory : inquiryHistoryList){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("round",inquiryHistory.getRound());
            map.put("userName", inquiryHistory.getUser().getNickName());
            map.put("title", inquiryHistory.getTitle());
            map.put("inquiryNo", inquiryHistory.getInquiryNo());
            map.put("status", inquiryHistory.getStatus());
            map.put("totalPrice", inquiryHistory.getTotalPrice());
            map.put("round", inquiryHistory.getRound());
            map.put("limitDate", DateFormatUtils.format(inquiryHistory.getLimitDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("inquiryMode", inquiryHistory.getInquiryMode().getName());
            map.put("industryCode", inquiryHistory.getCompanyIndustry().getName());
            map.put("provinceCode", inquiryHistory.getCompanyProvince().getName());
            map.put("userLimit", inquiryHistory.getUserLimit());
            map.put("logoUrl", inquiryHistory.getLogoUrl());
            map.put("test", inquiryHistory.getTest());


            putPrivateInfo(map,inquiryHistory);
            putQuotationList(map, inquiryHistory);
            historyList.add(map);
        }

        res.put("historyList",historyList);

    }


    /**
     * 用于计算询价历史信息
     */
    private void putPropertiesByName(Map<String, Object> res ,InquiryHistory inquiry,String name){
        String value="";

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties( name + "Open");

        if(inquiry.getProperties(name)!=null){
            value=inquiry.getProperties(name).toString();
        }

        assert openStatus != null;
        res.put(name, value + "(" + openStatus.getName() +")");

    }

    /**
     * 用于计算询价历史信息
     */
    public void putPrivateInfo(Map<String, Object> res, InquiryHistory inquiry){


        String names[] = {"remark","contactName","contactEmail","contactPhone","contactTel",
                "contactFax","contactWeiBo","contactWeiXin"};

        for ( String name: names){
            putPropertiesByName(res,inquiry,name);
        }

        OpenStatus openStatus = (OpenStatus) inquiry.getProperties("filesOpen");
        List<InquiryFile> fileList = inquiryFileRepository.findByInquiryAndRound(inquiry.getInquiry(),inquiry.getRound());

        assert openStatus != null;
        res.put("files",  "(" + openStatus.getName() +")");

        res.put("fileList", fileList);

    }

    /**
     *  询价历史 他人出价
     */
    public void putQuotationList(Map<String, Object> res,  InquiryHistory ih){

        List<Quotation> quotationList=quotationRepository.findByInquiryAndRoundOrderByCreateTimeDesc(ih.getInquiry(),ih.getRound());
        List<QuotationFile> quotationFileList;

        List<Map<String, Object>> histList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> techFileList = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> businessFileList = new ArrayList<Map<String,Object>>();

        for( Quotation quotation : quotationList ){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("quotationId",quotation.getId());
            map.put("createTime", DateFormatUtils.format(quotation.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("round",quotation.getRound());
            map.put("totalPrice",quotation.getTotalPrice());
            map.put("userId",quotation.getUser().getId());
            map.put("userNickname",quotation.getUser().getNickName());
            if(quotation.getUser().getCompanyProvince()!=null){
                map.put("userProvince",quotation.getUser().getCompanyProvince().getName());
            }else{
                map.put("userProvince","");
            }
            map.put("userNickName",quotation.getUser().getNickName());


            histList.add(map);

            quotationFileList=quotationFileRepository.findByQuotation(quotation);
            businessFileList = new ArrayList<Map<String,Object>>();
            techFileList = new ArrayList<Map<String,Object>>();
            for(QuotationFile quotationFile : quotationFileList){
                Map<String, Object> fileMap = new HashMap<String, Object>();
                fileMap.put("id",quotationFile.getId());
                fileMap.put("type",quotationFile.getType());
                fileMap.put("fileUrl",quotationFile.getFileUrl());
                fileMap.put("remark",quotationFile.getRemark());
                if(quotationFile.getType()==0){
                    businessFileList.add(fileMap);
                }else if(quotationFile.getType()==1){
                    techFileList.add(fileMap);
                }
            }
            map.put("businessFileList",businessFileList);
            map.put("techFileList",techFileList);
        }
        res.put("hisList",histList);
    }


    @Async
    public void pushEmail2User(Inquiry inquiry){

        int id = inquiry.getCompanyIndustry().getId().intValue();
        String sd = id+"";
        if(0<id && id<10){
            sd = "0"+sd;
        }
        List<Object[]> userList = userRepository.findByRemovedIndustry("%" + sd + "%");

        for (Object[] objects : userList) {
            commonEmail.pushEmail(objects[0].toString(),objects[1].toString(),inquiry);
        }

    }

    //竞价排名 获取我的出价排名
    private String getRank(Inquiry inquiry,Quotation mine){
        List<Quotation> list ;
        if(inquiry.getInquiryMode().getId()==1){
            //竞价排名
            list = quotationRepository.findLastQuotationOrderByPrice(inquiry.getId(),inquiry.getRound());
        }else{
            //限时竞价
            //获取时间
            Date limitDate = inquiry.getLimitDate();
            Date now = new Date();

            //当前时间在截止时间前20-30分钟的
            if(limitDate.getTime()-now.getTime()<30*60*1000 && limitDate.getTime()-now.getTime()>=20*60*1000){
                //半小时前的出价
                list = quotationRepository.findLastQuotationByTimeOrderByPrice(inquiry.getId(),inquiry.getRound(),new Date(limitDate.getTime()-30*60*1000));
            }else if(limitDate.getTime()-now.getTime()<20*60*1000 && limitDate.getTime()-now.getTime()>=10*60*1000){
                //20分钟前的
                list = quotationRepository.findLastQuotationByTimeOrderByPrice(inquiry.getId(),inquiry.getRound(),new Date(limitDate.getTime()-20*60*1000));
            }else if(limitDate.getTime()-now.getTime()<10*60*1000){
                list = quotationRepository.findLastQuotationOrderByPrice(inquiry.getId(),inquiry.getRound());
            }else{
                return "暂无排名";
            }

        }

        int index = 1;
        for (Quotation q : list) {
            if(q.getId().equals(mine.getId())){
                break;
            }
            index++;
        }
        return index+"";
    }

    //甲方同意后自动结束流程
    public void chooseAndFinish(User user_b, Inquiry inquiry, Long price){

        Message message = new Message();
        message.setUser(user_b);
        message.setType(1);
        message.setContent(price.toString());
        message.setRound(inquiry.getRound());
        message.setInquiry(inquiry);
        message.setInquiryUser(inquiry.getUser());
        message.setStatus(1);
        messageRepository.save(message);


        inquiry.setStatus(1);
        inquiry.setWinner(user_b);
        inquiry.setWinnerPrice(Long.parseLong(message.getContent()));
        inquiry.setPurchaseCloseDate(new Date());
        inquiryRepository.save(inquiry);

        double calcAmount = inquiry.getTotalPrice() * inquiry.getDefaultAmountRate() * inquiry.getAdjustAmountRate();

        UserIndex userIndex = user_b.getUserIndex();
        DecimalFormat df=new DecimalFormat("0.00");
        double currentAmount = userIndex.getAmount();
        currentAmount = new Double(df.format(currentAmount - calcAmount));

        userIndex.setAmount(currentAmount);
        userIndexRepository.save(userIndex);

        UserAccountLog userAccountLog = new UserAccountLog();
        userAccountLog.setAuto(true);
        userAccountLog.setInquiry(inquiry);
        userAccountLog.setAmountChange(-calcAmount);
        userAccountLog.setCurrentAmount(currentAmount);
        userAccountLog.setUser(user_b);
        userAccountLogRepository.save(userAccountLog);


        // 发送成功邮件
        List<Message> messages = messageRepository.findByInquiryAndStatus(inquiry,1);
        for(Message m : messages){
            commonEmail.sendEmail(m.getUser(),commonEmail.getContent(CommonEmail.TYPE.SUCCESS_B,inquiry,m.getUser()));
        }

    }


    //限时竞价 判断是否能出价
    public String verifyQuotationLimit(User user, long inquiryId){
        Inquiry inquiry = inquiryRepository.findOne(inquiryId);

        Date limitDate = inquiry.getLimitDate();
        Date now = new Date();

        List<Quotation> list;

        //当前时间在截止时间30分钟的
        if(limitDate.getTime()-now.getTime() >= 30*60*1000){
            //判断三十分钟前有没有出价
            list = quotationRepository.findByInquiryAndRoundAndCreateTimeBetween(user,inquiry,inquiry.getRound(),new Date(0),new Date(limitDate.getTime()-30*60*1000));
            if(list.size()>0){
                return "请在"+new DateTime(new Date(limitDate.getTime()-30*60*1000)).toString("hh:mm:ss")+"之后再次出价";
            }

        }else if(limitDate.getTime()-now.getTime()<30*60*1000 && limitDate.getTime()-now.getTime()>=20*60*1000){

            list = quotationRepository.findByInquiryAndRoundAndCreateTimeBetween(user,inquiry,inquiry.getRound(),new Date(limitDate.getTime()-30*60*1000),new Date(limitDate.getTime()-20*60*1000));
            if(list.size()>0){
                return "请在"+new DateTime(new Date(limitDate.getTime()-20*60*1000)).toString("hh:mm:ss")+"之后再次出价";
            }

        }else if(limitDate.getTime()-now.getTime()<20*60*1000 && limitDate.getTime()-now.getTime()>=10*60*1000){

            list = quotationRepository.findByInquiryAndRoundAndCreateTimeBetween(user,inquiry,inquiry.getRound(),new Date(limitDate.getTime()-20*60*1000),new Date(limitDate.getTime()-10*60*1000));
            if(list.size()>0){
                return "不能再次出价";
            }

        }else if(limitDate.getTime()-now.getTime()<10*60*1000){
            return "最后出价时间已过";
        }

        return "";
    }

    //导出excel 返回zip url
    @Transactional
    public String exportToExcel(User user, String os){

        //判断近日的导出日志
        UserExportLog record = userExportLogRepository.findFirstByUserOrderByCreateTimeDesc(user);
        if(record!=null){

            long mill = new Date().getTime() - record.getCreateTime().getTime();

            //一天内成功过的 直接返回原来的url
            if(mill <  24 * 60 * 60 * 1000 && record.getStatus()==0){
                return record.getZipUrl();
            }

            // 检查有没有完成 更改状态 返回url
            if(record.getStatus()==1 || record.getStatus()==2){
                OperationStatus status = new UploadUtils().checkZipStatus(record.getPersistentId());

                if(status.code != 1 && status.code != 2 ){
                    record.setStatus(status.code);
                    userExportLogRepository.save(record);
                    return record.getZipUrl();
                }
            }

        }

        ExportDto exportDto = new ExportDto();
        exportDto.setName("用户"+user.getNickName()+"询价导出" + new DateTime().toString("yyMMdd"));
        List<InquiryExportDto> inquiryExportDtoList = new ArrayList<>();

        List<Inquiry> inquiryList = inquiryRepository.findByUser(user);

        if(inquiryList.size()<1){
            throw new CommonException("没有可以导出的询价");
        }

        logger.info("有"+inquiryList.size() +"条询价");

        for(Inquiry inquiry : inquiryList){
            if(inquiry.getStatus()==0 || inquiry.getAuditStatus()!=2){
                continue;
            }

            InquiryExportDto inquiryExportDto = new InquiryExportDto();
            inquiryExportDto.inquiry2Dto(inquiry);

            List<InquiryFile> inquiryFile = inquiryFileRepository.findByInquiryAndRound(inquiry, inquiry.getRound());
            inquiryExportDto.setInquiryFileList(inquiryFile);

            List<RoundExportDto> roundExportDtoList = new ArrayList<>();
            RoundExportDto roundExportDto;
            for (int i = 1; i <= inquiry.getRound(); i++) {

                roundExportDto = new RoundExportDto();
                List<Message> messageList = messageRepository.findByInquiryAndRound(inquiry, i);
                List<Quotation> quotationList = quotationRepository.findByInquiryAndRoundOrderByCreateTimeDesc(inquiry , i);
                List<QuotationExportDto> quotationExportDtoList = new ArrayList<>();

                roundExportDto.setMessageList(messageList);
                QuotationExportDto quotationExportDto;
                for(Quotation quotation : quotationList){
                    quotationExportDto = new QuotationExportDto();
                    quotationExportDto.setUserUrl("http://www.mychinadreams.com/test/html/userDetail.html?key="+quotation.getUser().getId());
                    quotationExportDto.setName(quotation.getUser().getNickName());
                    quotationExportDto.setProvince(quotation.getUser().getCompanyProvince()!=null?quotation.getUser().getCompanyProvince().getName():"");
                    quotationExportDto.setPrice(quotation.getTotalPrice().toString());
                    quotationExportDto.setDateTime(new DateTime(quotation.getCreateTime()).toString("yyyy-MM-dd hh:mm:ss"));

                    List<QuotationFile> quotationFileList = quotationFileRepository.findByQuotation(quotation);
                    List<QuotationFile> businessFileList = new ArrayList<>();
                    List<QuotationFile> techFileList = new ArrayList<>();
                    for(QuotationFile quotationFile : quotationFileList){
                        if(quotationFile.getType()==0){
                            businessFileList.add(quotationFile);
                        }else if(quotationFile.getType()==1){
                            techFileList.add(quotationFile);
                        }
                    }
                    quotationExportDto.setBusinessFileList(businessFileList);
                    quotationExportDto.setTechFileList(techFileList);
                    quotationExportDtoList.add(quotationExportDto);

                }

                logger.info("第"+i +"轮" + messageList.size() + "," + quotationExportDtoList.size());

                roundExportDto.setQuotationExportDtoList(quotationExportDtoList);
                roundExportDtoList.add(roundExportDto);

            }

            inquiryExportDto.setRoundExportDtoList(roundExportDtoList);


            inquiryExportDtoList.add(inquiryExportDto);
        }
        exportDto.setInquiryExportDtoList(inquiryExportDtoList);

        ExcelUtils excelUtils= new ExcelUtils();

        String excelUrl = UploadUtils.uploadFile("excel/"+exportDto.getName()+".xlsx",excelUtils.exportToExcel(exportDto));

        UserExportLog userExportLog = new UserExportLog();
        userExportLog.setUser(user);
        userExportLog.setExcelUrl(excelUrl);
        userExportLog.setZipUrl("http://cdn.mychinadreams.com/zip/"+exportDto.getName()+".zip");

        //创建压缩
        String perId = UploadUtils.mkzip(excelUrl, excelUtils.fileLinkList, os);
        userExportLog.setPersistentId(perId);


        for (int i = 1; i <=10; i++) {
            logger.info("check zip status time" + i);
            OperationStatus status = new UploadUtils().checkZipStatus(perId);
            if(status.code != 1 && status.code != 2 ){
                userExportLog.setStatus(status.code);
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        userExportLogRepository.save(userExportLog);

        return userExportLog.getZipUrl();
    }





}
