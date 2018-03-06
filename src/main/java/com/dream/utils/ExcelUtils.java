package com.dream.utils;

import com.dream.dto.excel.ExportDto;
import com.dream.dto.excel.InquiryExportDto;
import com.dream.dto.excel.QuotationExportDto;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.hssf.usermodel.HSSFCell.*;

public class ExcelUtils {



    public void exportToExcel(ExportDto dto){

        if(dto==null){
            return;
        }

        InquiryExportDto inquiry;
        QuotationExportDto quotation;
        HSSFSheet sheet;
        HSSFRow row;
        HSSFCell cell;
        HSSFComment comment;

        String fileName = dto.getName();
        HSSFWorkbook wb = new HSSFWorkbook();

        for(int i=0; i<dto.getInquiryExportDtoList().size();i++){
            inquiry = dto.getInquiryExportDtoList().get(i);
            sheet = wb.createSheet(inquiry.getName());

            //批注


            //样式
            HSSFCellStyle style = wb.createCellStyle();
            HSSFFont font = wb.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 16);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);

            //表头
            row = sheet.createRow(0);
            row.setRowStyle(style);
            cell = row.createCell(0);
            cell.setCellValue("姓名");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("价格");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("状态");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("时间");
            cell.setCellStyle(style);
            sheet.setColumnWidth(0,5000);
            sheet.setColumnWidth(1,5000);
            sheet.setColumnWidth(2,5000);
            sheet.setColumnWidth(3,5000);


            for(int j=0; j<inquiry.getQuotationExportDtoList().size();j++){

                row = sheet.createRow(j+1);
                quotation = inquiry.getQuotationExportDtoList().get(j);
                cell = row.createCell(0);
                cell.setCellValue(quotation.getName());
                row.createCell(1).setCellValue(quotation.getPrice());
                row.createCell(2).setCellValue(quotation.getStatus());
                row.createCell(3).setCellValue(quotation.getDateTime());
                cell = row.createCell(4);
                cell.setCellValue("备注");
                cell.setCellComment(createComment("",sheet));

            }

        }

        //文件导出
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("/Users/knight/"+fileName+".xls");
            wb.write(fileOut);
            fileOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public HSSFComment createComment(String content, HSSFSheet sheet){
        HSSFPatriarch patr = sheet.createDrawingPatriarch();
        HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        comment.setString(new HSSFRichTextString("这里可以添加批注"));
        comment.setAuthor("作者");
        return comment;
    }

    public static void main(String[] args) {
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet("new sheet");
//        sheet.protectSheet("password");


        wb.setSheetName(0, "第一张工作表");
        HSSFRow row = sheet.createRow((short)0); //创建Excel工作表的行
        sheet.setColumnWidth(0,5000);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("单元格内容");
        row.createCell(0).setCellValue("内容0");
        row.createCell(1).setCellValue("内容1");

        CellStyle unlockedCellStyle = wb.createCellStyle();
        unlockedCellStyle.setLocked(false);
        cell.setCellStyle(unlockedCellStyle);




        //测试数据
        ExportDto dto = new ExportDto();
        dto.setName("用户a");
        List<InquiryExportDto> inquiryExportDtoList = new ArrayList<>();
        for(int i=0; i<5; i++){
            InquiryExportDto inquiryExportDto = new InquiryExportDto();
            inquiryExportDto.setName("标的名称_"+i);
            inquiryExportDto.setDateTime(new DateTime().toString("yyyy-MM-dd"));
            inquiryExportDto.setStatus("成功");

            List<QuotationExportDto> quotationExportDtoList = new ArrayList<>();
            for(int j=0;j<5;j++){
                QuotationExportDto quotationExportDto = new QuotationExportDto();
                quotationExportDto.setName("参与者"+j);
                quotationExportDto.setPrice("5000"+j);
                quotationExportDto.setDateTime(new DateTime().toString("yyyy-MM-dd"));
                quotationExportDto.setStatus("成功");
                quotationExportDtoList.add(quotationExportDto);
            }

            inquiryExportDto.setQuotationExportDtoList(quotationExportDtoList);
            inquiryExportDtoList.add(inquiryExportDto);
        }

        dto.setInquiryExportDtoList(inquiryExportDtoList);

        new ExcelUtils().exportToExcel(dto);

    }
}
