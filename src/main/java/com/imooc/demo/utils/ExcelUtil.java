package com.imooc.demo.utils;


import com.imooc.demo.model.Resource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by tofuchen on 2018/10/26.
 */

@Component
public class ExcelUtil {


    public static ExcelUtil excelUtil;

    @PostConstruct
    public void init(){
        excelUtil = this;
//        excelUtil.interfaceService= this.interfaceService;
//        excelUtil.reportService = this.reportService;
    }


    /**
     * 解析混合接口Excel数据
     * @param file
     * @return
     */
    public static List<Resource> parseResourceExcel(MultipartFile file) {
        List<Resource> res = new ArrayList<>();
        try {
            //1、获取文件输入流
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            //2、获取Excel工作簿对象
            Sheet sheet;
            Workbook workbook;
            //2、获取Excel工作簿对象
            if (fileName.indexOf(".xlsx") != -1) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }
            //3、得到Excel工作表对象
            sheet = workbook.getSheetAt(0);


            //4、循环读取表格数据
            int row = 0;
            Row nameRow = sheet.getRow(row);
            Cell cell = nameRow.getCell(0);
            cell.setCellType(CellType.STRING);
            String name = cell.getStringCellValue();

            HashMap<String, String> stringHashMap = new HashMap<>();


            while (name != null && !"".equals(name.trim())) {
                Resource resource = new Resource();

                row = importResourceData(sheet, resource, row, stringHashMap);
                if(row != 0) res.add(resource);
                else return null;
                System.out.println(sheet.getLastRowNum());
                if (row >= sheet.getLastRowNum()) break;
                nameRow = sheet.getRow(row);
                if(nameRow == null) break;
                cell = nameRow.getCell(0);
                cell.setCellType(CellType.STRING);
                name = cell.getStringCellValue();
            }
            //5、关闭流
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private static int importResourceData(Sheet sheet, Resource resource, int row, HashMap<String, String> stringHashMap) {
        Row nameRow = sheet.getRow(row);
        Cell cell = nameRow.getCell(0);
        cell.setCellType(CellType.STRING);
        String name = cell.getStringCellValue();

        //读取ratio数据
        row ++;
        Row ratioRow = sheet.getRow(row);
        cell = ratioRow.getCell(0);
        cell.setCellType(CellType.STRING);
        String interfaceName = cell.getStringCellValue();


        return row;
    }


}
