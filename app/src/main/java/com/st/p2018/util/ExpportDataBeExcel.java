package com.st.p2018.util;

import com.st.p2018.dao.ProductDao;
import com.st.p2018.entity.ProductBar;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



/**
 * Created by Administrator on 2018/11/27.
 */

public  class ExpportDataBeExcel {

    private static Logger logger = Logger.getLogger(ExpportDataBeExcel.class);

    public static  boolean ImportExcelData(File file) {
        ProductDao pd = new ProductDao();
        //先将库中信息全部清空
        pd.clearAll_AllProduct();;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(file);
            HSSFWorkbook book = new HSSFWorkbook(inFile);
            HSSFSheet sheet = book.getSheetAt(0);
            int row = sheet.getLastRowNum();
            if (row <= 1) {
                return false;
            }
            List<ProductBar> list = new ArrayList<ProductBar>();
            for (int i = 1; i <= row; i++) {
                HSSFRow oneRow = null;
                try {
                    oneRow = sheet.getRow(i);
                } catch (Exception e) {
                    continue;
                }
                if (oneRow == null) {
                    continue;
                }
                ProductBar pb=new ProductBar();
                pb.setId(UUID.randomUUID().toString());
                pb.setPp(oneRow.getCell((short)0)==null?"":oneRow.getCell((short)0).toString().trim());
                pb.setType(oneRow.getCell((short)1)==null?"":oneRow.getCell((short)1).toString().trim());
                pb.setGg(oneRow.getCell((short)2)==null?"":oneRow.getCell((short)2).toString().trim());
                String yxq=oneRow.getCell((short)3)==null?"":oneRow.getCell((short)3).toString().trim();
                if(yxq.equals("")){
                    pb.setYxq(System.currentTimeMillis());
                }else{
                    pb.setYxq(sdf.parse(yxq).getTime()+1000*60*60*24-1);
                }
                pb.setCard(oneRow.getCell((short)4)==null?"":oneRow.getCell((short)4).toString().trim());
                list.add(pb);
            }

            pd.addMutil_AllProduct(list);
            return true;

        } catch (Exception ex) {
        logger.error("解析excel出错",ex);

        } finally {
            if (inFile != null) {
                try {
                    inFile.close();
                } catch (IOException ex) {

                }
            }
            return true;
        }

    }


    public static boolean saveExcel( File file) {
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            ProductDao pd = new ProductDao();
            List<HashMap<String,String>> list=pd.getAll_AllProduct();


            //-------------------
            // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
            HSSFWorkbook wb=null;
            if(wb == null){
                wb = new HSSFWorkbook();
            }

            // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
            HSSFSheet sheet = wb.createSheet("sheet1");

            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
            HSSFRow row = sheet.createRow(0);

            // 第四步，创建单元格，并设置值表头 设置表头居中
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFDataFormat format = wb.createDataFormat();
            style.setDataFormat(format.getFormat("@"));
            sheet.setDefaultColumnStyle((short)3,style);
            //标题
            for(int i=0;i<5;i++){
                HSSFCell cell = row.getCell((short) i);
                if (cell == null) {
                    cell = row.createCell((short) i);

                }
                if(i==0){
                    cell.setCellValue(new HSSFRichTextString("品牌"));
                }else if(i==1){
                    cell.setCellValue(new HSSFRichTextString("种类"));
                }else if(i==2){
                    cell.setCellValue(new HSSFRichTextString("规格"));
                }else if(i==3){
                    cell.setCellValue(new HSSFRichTextString("有效期(格式：2018-01-01)"));
                }else if(i==4){
                    cell.setCellValue(new HSSFRichTextString("标签ID"));
                }
            }
            //内容
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int rownum=1;
            for(HashMap map : list){
                row = sheet.createRow(rownum);
                rownum=rownum+1;
                for(int i=0;i<5;i++){
                    HSSFCell cell = row.getCell((short) i);
                    if (cell == null) {
                        cell = row.createCell((short) i);
                    }
                    if(i==0){
                        cell.setCellValue(new HSSFRichTextString(map.get("pp").toString()));
                    }else if(i==1){
                        cell.setCellValue(new HSSFRichTextString(map.get("zl").toString()));
                    }else if(i==2){
                        cell.setCellValue(new HSSFRichTextString(map.get("gg").toString()));
                    }else if(i==3){
                        cell.setCellValue(new HSSFRichTextString(sdf.format(new Date(Long.valueOf(map.get("yxq").toString())))));
                    }else if(i==4){
                        cell.setCellValue(new HSSFRichTextString(map.get("card").toString()));
                    }
                }
            }
            try{
                FileOutputStream outFile = new FileOutputStream(file);
                wb.write(outFile);
                outFile.close();
            }catch(Exception e){
                logger.error(e);
            }
            return true;
        }catch(Exception e){
            logger.error("创建down.xls出错",e);
        }
        return true;


        //-----------------------------------
//        FileOutputStream outFile = null;
//        FileInputStream inFile = null;
//        try {
//            inFile = new FileInputStream(file);
//            HSSFWorkbook book = new HSSFWorkbook(inFile);
//            HSSFSheet sheet = book.getSheetAt(0);
//           for(int i=0;i<5;i++){
//               HSSFRow row = sheet.getRow(0);
//               HSSFCell cell = row.getCell((short) i);
//               if (cell == null) {
//                   cell = row.createCell((short) i);
//               }
//               HSSFCellStyle style = cell.getCellStyle();
//               style.setFillBackgroundColor((short) 0x00ff);
//               cell.setCellStyle(style);
//               if(i==0){
//                   cell.setCellValue(new HSSFRichTextString("品牌"));
//               }else if(i==1){
//                   cell.setCellValue(new HSSFRichTextString("种类"));
//               }else if(i==2){
//                   cell.setCellValue(new HSSFRichTextString("规格"));
//               }else if(i==3){
//                   cell.setCellValue(new HSSFRichTextString("有效期"));
//               }else if(i==4){
//                   cell.setCellValue(new HSSFRichTextString("标签ID"));
//               }
//
//           }


            //int firstBlockColumn = 0;
//            for (int i = 0; i < 50; i++) {
//                String cell = "";
//                try {
//                    cell = rown.getCell((short) i).toString().trim();
//                } catch (Exception e) {
//                    firstBlockColumn = i;
//                    break;
//                }
//                if (cell.equals("")) {
//                    firstBlockColumn = i;
//                    break;
//                }
//            }
//            int rownum=1;
//            for(HashMap map : list){
//                HSSFRow row = sheet.getRow(rownum);
//                rownum=rownum+1;
//                HSSFCell cell = row.getCell((short) firstBlockColumn);
//                if (cell == null) {
//                    cell = row.createCell((short) firstBlockColumn);
//                }
//                HSSFCellStyle style = cell.getCellStyle();
//                style.setFillBackgroundColor((short) 0x00ff);
//                cell.setCellStyle(style);
//                cell.setCellValue(new HSSFRichTextString(pir.getDescription()));
//            }
//            for (int i = 0; i < report.size(); i++) {
//                //PersonInReport pir = report.get(i);
//                HSSFRow row = sheet.getRow(i + 3);
//                HSSFCell cell = row.getCell((short) firstBlockColumn);
//                if (cell == null) {
//                    cell = row.createCell((short) firstBlockColumn);
//                }
//                HSSFCellStyle style = cell.getCellStyle();
//                style.setFillBackgroundColor((short) 0x00ff);
//                cell.setCellStyle(style);
//                cell.setCellValue(new HSSFRichTextString(pir.getDescription()));
//            }
//            if (inFile != null) {
//                inFile.close();
//            }
//            outFile = new FileOutputStream(file);
//            book.write(outFile);
//
//            try {
//                if (outFile != null) {
//                    outFile.close();
//                }
//                return true;
//            } catch (IOException ex) {
//                logger.error(ex);
//                return false;
//            }
//        } catch (Exception ex) {
//            logger.error(ex);
//            return false;
//        }
    }
}
