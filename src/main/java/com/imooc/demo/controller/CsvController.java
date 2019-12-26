package com.imooc.demo.controller;


import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.model.*;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.LoginTicketService;
import com.imooc.demo.utils.ExcelUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            return ExcelUtil.parseResourceExcel(multipartFile);

        } catch (Exception e) {
            log.error("【导入数据发生异常】" + e.getMessage());
            return ResultVOUtil.fail(ResultEnum.IMPORT_FILE_EXCEPTION, response);
        }

    }

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
            return ExcelUtil.parseCompanyExcel(multipartFile);

        } catch (Exception e) {
            log.error("【导入数据如发生异常】" + e.getMessage());
            return ResultVOUtil.fail(ResultEnum.IMPORT_FILE_EXCEPTION, response);
        }

    }
}
