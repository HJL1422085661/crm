package com.imooc.demo.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.model.Employee;
import com.imooc.demo.model.Resource;
import com.imooc.demo.model.ResourceDTO;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.LoginTicketService;
import com.imooc.demo.service.ResourceService;
import com.imooc.demo.utils.MyExcelUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@Slf4j
public class CsvController {

    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    public EmployeeService employeeService;
    @Autowired
    public ResourceService resourceService;

    /**
     * 上传人才资源文件
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadResourceFile")
    public ResultVO<Map<String, String>> uploadResourceFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【导入文件】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【导入文件】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【导入文件】普通员工无权导入文件");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        try {
            if (multipartFile.isEmpty()) {
                log.error("【导入文件】为空");
                return ResultVOUtil.fail(ResultEnum.FILE_IS_EMPTY, response);
            }
            //解析excel
            return MyExcelUtil.parseResourceExcel(multipartFile);

        } catch (Exception e) {
            log.error("【导入数据发生异常】" + e.getMessage());
            return ResultVOUtil.fail(ResultEnum.IMPORT_FILE_EXCEPTION, response);
        }

    }

    /**
     * 导出人才excel文件
     *
     * @param request
     * @param response
     * @throws IOException
     */
//    @GetMapping("/downloadResourceFile")
//    public void downloadResourceFile(HttpServletRequest request, HttpServletResponse response) {
//        String token = TokenUtil.parseToken(request);
//        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
//        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
//
//        XSSFWorkbook  workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet("人才信息表");
//        OutputStream output = null;
//
//        // 管理员导出所有人才信息
//        List<Resource> resourceList = new ArrayList<>();
//        if (employee.getEmployeeRole() == 2) {
//            resourceList = resourceService.findAllResource();
//        } else {
//            // 普通员工导出自己的人才信息
//            resourceList = resourceService.getResourceByEmployeeId(employeeId);
//        }
//
//
//        String fileName = "response" + ".xls";//设置要导出的文件的名字
//
//
//        //新增数据行，并且设置单元格数据
//        int rowNum = 1;
//
//        //headers表示excel表中第一行的表头
//        String[] headers = {"姓名", "证书专业", "备注", "省份", "到期日", "性别", "QQ号码", "电话", "邮箱", "日期"};
//
//        XSSFRow row = sheet.createRow(0);
//
//
//        for (int i = 0; i < headers.length; i++) {
//            XSSFCell cell = row.createCell(i);
//            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
//            cell.setCellValue(text);
//        }
//
//        //在表中存放查询到的数据放入对应的列
//        for (Resource resource : resourceList) {
//            Row row1 = sheet.createRow(rowNum);
////            Row row1 = sheet.createRow(rowNum);
//            row1.createCell(0).setCellValue(resource.getResourceName());
//            row1.createCell(1).setCellValue(resource.getCertificate());
//            row1.createCell(2).setCellValue(resource.getInfo());
//            row1.createCell(3).setCellValue(resource.getProvince());
//            row1.createCell(4).setCellValue(resource.getEndDate());
//            String gender = null;
//            if (resource.getGender() != null) {
//                gender = (resource.getGender().equals(1) ? "男" : "女");
//            }
//            row1.createCell(5).setCellValue(gender);
//            row1.createCell(6).setCellValue(resource.getQq());
//            row1.createCell(7).setCellValue(resource.getPhoneNumber());
//            row1.createCell(8).setCellValue(resource.getEmail());
//            row1.createCell(9).setCellValue(resource.getCreateDate());
//            rowNum++;
//        }
//
//        try{
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//
//            // response.reset();
//            //response.addHeader("Content-Disposition", "attachment;filename=" + new String(( "response").getBytes("gb2312"), "ISO-8859-1") + ".xls");
//
////        response.setContentType("application/octet-stream");
////            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//
////        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
//
//            output = response.getOutputStream();
//           // response.setHeader("Content-disposition", "attachment; filename="+fileName);
//           // response.setContentType("application/msexcel");
//            ServletOutputStream t = (ServletOutputStream)output;
//            workbook.write(t);
//            output.flush();
//            output.close();
//        }catch (IOException e) {
//            try {
//                throw new Exception(e.getMessage());
//            } catch (Exception e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//        }
//    }
    @GetMapping("/downloadResourceFile")
    public void downloadResourceFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = TokenUtil.parseToken(request);
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);

        // 管理员导出所有人才信息
        List<Resource> resourceList = new ArrayList<>();
        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        if (employee.getEmployeeRole() == 2) {
            resourceList = resourceService.findAllResource();
        } else {
            // 普通员工导出自己的人才信息
            resourceList = resourceService.getResourceByEmployeeId(employeeId);
        }
        for (Resource resource : resourceList) {
            ResourceDTO resourceDTO = new ResourceDTO();
            BeanUtils.copyProperties(resource, resourceDTO);
            resourceDTOList.add(resourceDTO);
        }

        ExcelWriter writer = ExcelUtil.getWriter();

        writer.addHeaderAlias("resourceName", "姓名");
        writer.addHeaderAlias("certificate", "证书专业");
        writer.addHeaderAlias("info", "备注");
        writer.addHeaderAlias("province", "省份");
        writer.addHeaderAlias("endDate", "到期日");
        writer.addHeaderAlias("gender", "性别");
        writer.addHeaderAlias("qq", "QQ号码");
        writer.addHeaderAlias("phoneNumber", "电话");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("createDate", "日期");

        writer.write(resourceDTOList, true);

        response.setContentType("application/vnd.ms-excel");
//        response.setContentType("application/x-msdownload; charset=utf-8");
//        response.setContentType("multipart/form-data; charset=utf-8");
//        response.setContentType("application/octet-stream; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=customer.xls");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out);
        // 关闭writer，释放内存
        writer.close();

//        ServletOutputStream out = null;
//        try {
//            out = response.getOutputStream();
//            writer.flush(out, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            // 关闭writer，释放内存
//            writer.close();
//        }
//        //此处记得关闭输出Servlet流
//        IoUtil.close(out);

    }


//    @GetMapping("/downloadResourceFile")
//    public void downloadResourceFile(HttpServletRequest request, HttpServletResponse response) {
//        String token = TokenUtil.parseToken(request);
//        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
//        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
//
//        ExcelWriter writer = ExcelUtil.getWriter();
//
//
//        // 管理员导出所有人才信息
//        List<Resource> resourceList = new ArrayList<>();
//        if (employee.getEmployeeRole() == 2) {
//            resourceList = resourceService.findAllResource();
//        } else {
//            // 普通员工导出自己的人才信息
//            resourceList = resourceService.getResourceByEmployeeId(employeeId);
//        }
//
//       String fileName = "response" + ".xls";//设置要导出的文件的名字
//
//
//        //新增数据行，并且设置单元格数据
//        int rowNum = 1;
//
//        //headers表示excel表中第一行的表头
//        String[] headers = {"姓名", "证书专业", "备注", "省份", "到期日", "性别", "QQ号码", "电话", "邮箱", "日期"};
//
//        HSSFRow row = sheet.createRow(0);
//
//
//        for (int i = 0; i < headers.length; i++) {
//            HSSFCell cell = row.createCell(i);
//            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
//            cell.setCellValue(text);
//        }
//
//        //在表中存放查询到的数据放入对应的列
//        for (Resource resource : resourceList) {
//            HSSFRow row1 = sheet.createRow(rowNum);
////            Row row1 = sheet.createRow(rowNum);
//            row1.createCell(0).setCellValue(resource.getResourceName());
//            row1.createCell(1).setCellValue(resource.getCertificate());
//            row1.createCell(2).setCellValue(resource.getInfo());
//            row1.createCell(3).setCellValue(resource.getProvince());
//            row1.createCell(4).setCellValue(resource.getEndDate());
//            String gender = null;
//            if (resource.getGender() != null) {
//                gender = (resource.getGender().equals(1) ? "男" : "女");
//            }
//            row1.createCell(5).setCellValue(gender);
//            row1.createCell(6).setCellValue(resource.getQq());
//            row1.createCell(7).setCellValue(resource.getPhoneNumber());
//            row1.createCell(8).setCellValue(resource.getEmail());
//            row1.createCell(9).setCellValue(resource.getCreateDate());
//            rowNum++;
//        }
//
//        try(OutputStream out = response.getOutputStream()){
//            // response.reset();
//            // response.addHeader("Content-Disposition", "attachment;filename=" + new String(( "response").getBytes("gb2312"), "ISO-8859-1") + ".xls");
//
////        response.setContentType("application/octet-stream");
//            response.setContentType("application/vnd.ms-excel;charset=gb2312");
////        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
//
//
//            ByteArrayOutputStream  os =  new ByteArrayOutputStream();
//            workbook.write(os);
//            response.setHeader("Content-Length", String.valueOf(os.size()));
//            out.write( os.toByteArray() );
//            os.flush();
//            os.close();
//        }catch (IOException e){
//            try {
//                throw new Exception(e.getMessage());
//            } catch (Exception e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//        }
//
//    }

    /**
     * 上传公司资源文件
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadCompanyFile")
    public ResultVO<Map<String, String>> uploadCompanyFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【导入文件】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【导入文件】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【导入文件】普通员工无权导入文件");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        try {
            if (multipartFile.isEmpty()) {
                log.error("【导入文件】为空");
                return ResultVOUtil.fail(ResultEnum.FILE_IS_EMPTY, response);
            }
            //解析excel
            return MyExcelUtil.parseCompanyExcel(multipartFile);

        } catch (Exception e) {
            log.error("【导入数据如发生异常】" + e.getMessage());
            return ResultVOUtil.fail(ResultEnum.IMPORT_FILE_EXCEPTION, response);
        }

    }
}
