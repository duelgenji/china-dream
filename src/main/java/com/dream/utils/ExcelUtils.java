package com.dream.utils;

import com.dream.dto.excel.*;
import com.dream.entity.inquiry.Inquiry;
import com.dream.entity.inquiry.InquiryFile;
import com.dream.entity.message.Message;
import com.dream.entity.quotation.QuotationFile;
import com.qiniu.util.UrlSafeBase64;
import org.apache.catalina.util.URLEncoder;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import static org.apache.poi.hssf.usermodel.HSSFCell.*;

public class ExcelUtils {

    private Logger logger = LoggerFactory.getLogger(ExcelUtils.class);


    XSSFWorkbook wb = new XSSFWorkbook();
    CreationHelper createHelper = wb.getCreationHelper();
    Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);

    XSSFCellStyle hyperLinkStyle = wb.createCellStyle();
    XSSFCellStyle centerStyle = wb.createCellStyle();
    XSSFCellStyle titleBoldStyle = wb.createCellStyle();
    XSSFCellStyle stringStyle = wb.createCellStyle();
    XSSFCellStyle greenTextStyle = wb.createCellStyle();
    XSSFCellStyle redTextStyle = wb.createCellStyle();

    String[] messageStatus = {"未确认","已同意","已拒绝"};

    String fullPath = "file/";
    XSSFCellStyle[] messageStatusStyle = {null,greenTextStyle,redTextStyle};

    public List<FileLink> fileLinkList = new ArrayList<>();
    private int fileSize = 1;

    public ExcelUtils() {
        Font font = wb.createFont();


        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        hyperLinkStyle.setFont(font);

        font = wb.createFont();
        font.setBold(true);
        font.setFontName("Microsoft YaHei");
        titleBoldStyle.setFont(font);

        font = wb.createFont();
        font.setColor(IndexedColors.GREEN.getIndex());
        greenTextStyle.setFont(font);

        font = wb.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        redTextStyle.setFont(font);

        stringStyle.setDataFormat(BuiltinFormats.getBuiltinFormat("text"));

    }

    public byte[] exportToExcel(ExportDto dto){

        if(dto==null){
            return null;
        }

        XSSFSheet sheet;
        XSSFComment comment;

        int round;

        for(InquiryExportDto inquiryExportDto : dto.getInquiryExportDtoList()){

            sheet = wb.createSheet(inquiryExportDto.getName());

            //批注


            createBasicFrame(sheet,inquiryExportDto);

            round = 1;
            for (RoundExportDto roundExportDto : inquiryExportDto.getRoundExportDtoList()){
                createApplyFrame(sheet,roundExportDto.getMessageList() ,round);
                createQuotationFrame(sheet, roundExportDto.getQuotationExportDtoList() , round);
                round++;
            }
        }

//                cell.setCellComment(createComment("这里可以添加批注",sheet));


        //文件导出
//        FileOutputStream fileOut = null;
//        try {
//            fileOut = new FileOutputStream("/Users/knight/"+dto.getName()+".xlsx");
//            wb.write(fileOut);
//            fileOut.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            wb.write(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();

    }


    //添加批注
    public XSSFComment createComment(String content, XSSFSheet sheet){
        XSSFDrawing patr = sheet.createDrawingPatriarch();
        XSSFComment comment = patr.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        comment.setString(new HSSFRichTextString(content));
        comment.setAuthor("作者");
        return comment;
    }


    //创建基础信息模块
    public void createBasicFrame(XSSFSheet sheet, InquiryExportDto inquiryExportDto){

        List<BasePropMap> list = new ArrayList<>();
        list.add(new BasePropMap("询价编号","inquiryNo"));
        list.add(new BasePropMap("询价方","user"));
        list.add(new BasePropMap("标的","price"));
        list.add(new BasePropMap("询价标题","title"));
        list.add(new BasePropMap("询价方式","mode"));
        list.add(new BasePropMap("本轮发布时间","dateTime"));
        list.add(new BasePropMap("所处行业", "industry"));
        list.add(new BasePropMap("实施地点", "province"));
        list.add(new BasePropMap("传真", "fax"));
        list.add(new BasePropMap("轮数", "round"));
        list.add(new BasePropMap("截止日期", "limitTime"));
        list.add(new BasePropMap("简要说明", "remark"));
        list.add(new BasePropMap("联系人姓名", "contactName"));
        list.add(new BasePropMap("联系人电话", "contactTel"));
        list.add(new BasePropMap("附件说明" , "inquiryFileList"));
        list.add(new BasePropMap("联系人手机","contactPhone"));
        list.add(new BasePropMap("联系人邮箱","contactMail"));
        list.add(new BasePropMap("当前状态","status"));
        list.add(new BasePropMap("微信","wechat"));
        list.add(new BasePropMap("微博","weibo"));


        XSSFRow row;
        XSSFCell cell;
        int baseRow = sheet.getLastRowNum();

        row = sheet.createRow(baseRow);
        sheet.setColumnWidth(0,16000);
//        sheet.setColumnWidth(1,8000);

        sheet.addMergedRegion(new CellRangeAddress(0,14,0,0));
        sheet.addMergedRegion(new CellRangeAddress(0,1,1,10));

        cell = row.createCell(0);
        cell.setCellValue("标的基本信息");
        cell.setCellStyle(centerStyle);

        logger.info("标的"+inquiryExportDto.getName());

        cell = row.createCell(1);
        cell.setCellValue(inquiryExportDto.getName());
        cell.setCellStyle(centerStyle);
        baseRow+=2;

        int baseMapCount = 0;
        for(BasePropMap map : list){
            if(baseMapCount%3==0){
                row = sheet.createRow(baseRow);
                baseRow+=2;
            }
            cell = row.createCell(1 + baseMapCount%3 * 4);
            cell.setCellValue(map.getName());
            cell.setCellStyle(stringStyle);
            try {
                Field field = inquiryExportDto.getClass().getDeclaredField(map.getProp());
                field.setAccessible(true);
                if(map.getProp().equals("inquiryFileList")){
                    List<InquiryFile> inquiryFileList = (List<InquiryFile>) field.get(inquiryExportDto);
                    int i = 0;
                    for (InquiryFile file :inquiryFileList) {
                        cell = row.createCell(2 + baseMapCount%3 * 4 + i++);
                        setLinkCell(cell,  file.getRemark());
                        fileLinkList.add(new FileLink(file.getFileUrl(), file.getRemark()));
//                        cell.setCellFormula("hyperlink(\"./file/"+file.getRemark()+"\",\""+file.getRemark()+"\")");
                    }

                }else{
                    cell = row.createCell(2 + baseMapCount%3 * 4);
                    cell.setCellValue((String) field.get(inquiryExportDto));
                    cell.setCellStyle(stringStyle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            baseMapCount++;
        }


    }

    public void createApplyFrame(XSSFSheet sheet , List<Message> list , int round){

        XSSFRow row;
        XSSFCell cell;
        int baseRow = sheet.getLastRowNum() + 2;

        sheet.addMergedRegion(new CellRangeAddress(baseRow,baseRow + list.size(),0,0));

        row = sheet.createRow(baseRow);

        cell = row.createCell(0);
        cell.setCellValue("乙方申请信息 第"+round+"轮");
        cell.setCellStyle(centerStyle);
        cell = row.createCell(1);
        cell.setCellValue("投标方名称");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(2);
        cell.setCellValue("状态");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(3);
        cell.setCellValue("备注");
        cell.setCellStyle(titleBoldStyle);



        for (Message s : list) {

            logger.info("申请人"+s.getUser().getNickName());

            row = sheet.createRow(++baseRow);
            cell = row.createCell(1);
            cell.setCellValue(s.getUser().getNickName());
            cell = row.createCell(2);
            cell.setCellValue(messageStatus[s.getStatus()]);
            cell.setCellStyle(messageStatusStyle[s.getStatus()]);
            cell = row.createCell(3);
            cell.setCellValue(s.getReason());
        }


    }

    public void createQuotationFrame(XSSFSheet sheet, List<QuotationExportDto> list, int round){
        XSSFRow row;
        XSSFCell cell;
        int baseRow = sheet.getLastRowNum() + 2;
        int pointerRow = baseRow;


        row = sheet.createRow(pointerRow++);
        cell = row.createCell(1);
        cell.setCellValue("投标方名称");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(2);
        cell.setCellValue("所在地");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(3);
        cell.setCellValue("报价日期");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(4);
        cell.setCellValue("报价金额");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(5);
        cell.setCellValue("商务附件");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(6);
        cell.setCellValue("技术附件");
        cell.setCellStyle(titleBoldStyle);
        cell = row.createCell(7);
        cell.setCellValue("原因");
        cell.setCellStyle(titleBoldStyle);


        int fileSize;
        int totalSize = 0;
        for (QuotationExportDto q : list) {

            pointerRow = sheet.getLastRowNum() + 1;

            fileSize = q.getBusinessFileList().size();
            if(fileSize < q.getTechFileList().size()){
                fileSize = q.getTechFileList().size();
            }
            logger.info("投标信息 size"+fileSize);


            if(fileSize>1){
                logger.info("投标信息 B"+pointerRow + "-" + (pointerRow + fileSize - 1));

                sheet.addMergedRegion(new CellRangeAddress(pointerRow,pointerRow + fileSize - 1,1,1));
                sheet.addMergedRegion(new CellRangeAddress(pointerRow,pointerRow + fileSize - 1,2,2));
                sheet.addMergedRegion(new CellRangeAddress(pointerRow,pointerRow + fileSize - 1,3,3));
                sheet.addMergedRegion(new CellRangeAddress(pointerRow,pointerRow + fileSize - 1,4,4));

                totalSize+= fileSize;
            }else{
                totalSize++;
            }

            //商务文件
            int i = 0;
            for(QuotationFile file : q.getBusinessFileList()){

                if(sheet.getRow(pointerRow+i)==null){
                    row = sheet.createRow(pointerRow+i);
                }else{
                    row = sheet.getRow(pointerRow+i);
                }
                logger.info("投标信息 商务 B" + (pointerRow + i));

                cell = row.createCell(5);
                cell.setCellValue(file.getRemark());
                setLinkCell(cell, file.getRemark());
//                cell.setCellFormula("hyperlink(\"./file/"+URLEncoder.DEFAULT.encode(file.getRemark())+"\",\""+file.getRemark()+"\")");
//                cell.setCellFormula("hyperlink(\"./file/"+file.getRemark()+"\",\""+file.getRemark()+"\")");
                fileLinkList.add(new FileLink(file.getFileUrl(), file.getRemark()));
//                System.out.println(cell.getRowIndex() + ", " + cell.getColumnIndex()+ " :" + cell.getStringCellValue());
                i++;
            }


            //技术文件
            i = 0;
            for(QuotationFile file : q.getTechFileList()){
                if(sheet.getRow(pointerRow+i)==null){
                    row = sheet.createRow(pointerRow+i);
                }else{
                    row = sheet.getRow(pointerRow+i);
                }
                logger.info("投标信息 技术 B" + (pointerRow + i));

                cell = row.createCell(6);
                cell.setCellValue(file.getRemark());
                setLinkCell(cell,  file.getRemark());
//                cell.setCellFormula("hyperlink(\"./file/"+URLEncoder.DEFAULT.encode(file.getRemark())+"\",\""+file.getRemark()+"\")");
//                cell.setCellFormula("hyperlink(\"./file/"+file.getRemark()+"\",\""+file.getRemark()+"\")");

                fileLinkList.add(new FileLink(file.getFileUrl(), file.getRemark()));
//                System.out.println(cell.getRowIndex() + ", " + cell.getColumnIndex()+ " :" + cell.getStringCellValue());
                i++;

            }

            if(sheet.getRow(pointerRow)==null){
                row = sheet.createRow(pointerRow);
            }else{
                row = sheet.getRow(pointerRow);
            }
            cell = row.createCell(1);
            cell.setCellValue(q.getName());
            setLinkCell(cell, q.getUserUrl());
            cell = row.createCell(2);
            cell.setCellValue(q.getProvince());
            cell = row.createCell(3);
            cell.setCellValue(q.getDateTime());
            cell = row.createCell(4);
            cell.setCellValue(q.getPrice());

            cell = row.createCell(7);
            cell.setCellValue("");
        }


        sheet.addMergedRegion(new CellRangeAddress(baseRow,baseRow + totalSize,0,0));
        row = sheet.getRow(baseRow);
        cell = row.createCell(0);
        cell.setCellValue("乙方投标信息 第"+round+"轮");
        cell.setCellStyle(centerStyle);
    }

    private void setLinkCell(XSSFCell cell, String url){
        link = createHelper.createHyperlink(HyperlinkType.FILE);
        link.setAddress("file/file"+fileSize+"."+url.split("\\.")[url.split("\\.").length-1]);
        cell.setHyperlink(link);
        cell.setCellStyle(hyperLinkStyle);
        fileSize++;
    }

//    public static void main(String[] args) {
//        HSSFWorkbook wb = new HSSFWorkbook();
//
//        HSSFSheet sheet = wb.createSheet("new sheet");
////        sheet.protectSheet("password");
//
//        sheet.setColumnWidth(0,5000);
//
//        CellStyle unlockedCellStyle = wb.createCellStyle();
//        unlockedCellStyle.setLocked(false);
//        cell.setCellStyle(unlockedCellStyle);
//    }

    public static void main(String[] args) {

    }


}
