package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.model.*;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.KeyUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

import static com.imooc.demo.utils.BeanCopyUtil.getNullPropertyNames;


/**
 * @ Author: yangfan
 * @ Date: 2019-12-2
 * @ Version: 1.0
 */
@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {
    @Autowired
    public EmployeeService employeeService;
    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    public CompanyBusinessService companyBusinessService;
    @Autowired
    public ResourceBusinessService resourceBusinessService;
    @Autowired
    public ResourceService resourceService;
    @Autowired
    public CompanyService companyService;
    @Autowired
    public PayBackRecordService payBackRecordService;

    /**
     * 根据人才Id取人才订单
     *
     * @param paramMap
     * @param req
     * @param response
     * @return
     */
    @PostMapping("/getResourceBusinessListByResourceId")
    public ResultVO<Map<Integer, String>> getResourceBusinessListByResourceId(@RequestBody HashMap paramMap,
                                                                              HttpServletRequest req,
                                                                              HttpServletResponse response) {
        Integer resourceId = Integer.parseInt(paramMap.get("resourceId").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取订单列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取订单列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        List<ResourceBusiness> resourceBusinessList = resourceBusinessService.findResourceBusinessByResourceId(resourceId);
        return ResultVOUtil.success(resourceBusinessList);
    }

    /**
     * 根据企业Id取人才订单
     *
     * @param paramMap
     * @param req
     * @param response
     * @return
     */
    @PostMapping("/getCompanyBusinessListByCompanyId")
    public ResultVO<Map<Integer, String>> getCompanyBusinessListByCompanyId(@RequestBody HashMap paramMap,
                                                                            HttpServletRequest req,
                                                                            HttpServletResponse response) {
        Integer companyId = Integer.parseInt(paramMap.get("companyId").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取订单列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取订单列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        List<CompanyBusiness> CompanyBusinessList = companyBusinessService.findCompanyBusinessByCompanyId(companyId);
        return ResultVOUtil.success(CompanyBusinessList);
    }

    @PostMapping("/getBusinessList")
    public ResultVO<Map<Integer, String>> getBusinessList(@RequestBody HashMap paramMap,
                                                          HttpServletRequest req,
                                                          HttpServletResponse response) {
        // orderType 1表示人才订单 2表示公司订单
        Integer orderType = Integer.parseInt(paramMap.get("orderType").toString());
        Integer isCompleted = Integer.parseInt(paramMap.get("isCompleted").toString());
        Integer page = Integer.parseInt(paramMap.get("page").toString());
        Integer size = Integer.parseInt(paramMap.get("pageSize").toString());
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取订单列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取订单列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (orderType != 1 && orderType != 2) {
            log.error("【获取订单列表】参数错误");
            return ResultVOUtil.fail(ResultEnum.PARAM_ERROR, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");
        Map<String, Object> map = new HashMap<>();
        // 个人订单，根据employeeId取个人所有的
        if (orderType == 1) {
            Page<ResourceBusiness> resourceBusinessPage = null;
            if (employee.getEmployeeRole() == 2) {
                // 管理员看到所有人的订单
//                resourceBusinessPage = resourceBusinessService.findAllResourceBusinessPageable(request);
                resourceBusinessPage = resourceBusinessService.findAllResourceBusinessByIsCompletedPageable(isCompleted, request);
            } else {
                // 普通员工看到自己的订单
//                resourceBusinessPage = resourceBusinessService.findResourceBusinessByEmployeeId(employeeId, request);
                resourceBusinessPage = resourceBusinessService.findResourceBusinessByEmployeeIdAndIsCompleted(employeeId, isCompleted, request);
            }
            map.put("businessList", resourceBusinessPage);
            map.put("orderType", ResultEnum.GET_RESOURCE_BUSINESS_SUCCESS);
            return ResultVOUtil.success(map);
        } else {
            // 公司订单
            Page<CompanyBusiness> companyBusinessPage = null;
            if (employee.getEmployeeRole() == 2) {
                // 管理员看到所有人的订单
//                companyBusinessPage = companyBusinessService.findAllCompanyBusinessPageable(request);
                companyBusinessPage = companyBusinessService.findAllCompanyBusinessByIsCompletedPageable(isCompleted, request);
            } else {
                // 普通员工看到自己的订单
//                companyBusinessPage = companyBusinessService.findCompanyBusinessByEmployeeId(employeeId, request);
                companyBusinessPage = companyBusinessService.findCompanyBusinessByEmployeeIdAndIsCompleted(employeeId, isCompleted, request);
            }

            List<CompanyBusinessDTO> companyBusinessDTOList = new ArrayList<>();
            // 遍历数据库取出来的CompanyBusiness，将其resourceId和resourceName字段（字符串）
            // 转换为resource列表:[(resourceId resourceName), (resourceId resourceName), ... ]
            for (CompanyBusiness companyBusinessTemp : companyBusinessPage.getContent()) {
                CompanyBusinessDTO companyBusinessDTO = new CompanyBusinessDTO();
                BeanUtils.copyProperties(companyBusinessTemp, companyBusinessDTO, getNullPropertyNames(companyBusinessTemp));
                //companyBusinessDTO.setBusinessId(KeyUtil.createID());
//                String[] resourceIdList = companyBusinessTemp.getResourceId().split(",");
//                String[] resourceNameList = companyBusinessTemp.getResourceName().split(",");
//                List<Map<String, String>> resourceListTemp = new ArrayList<>();
//                for (int i = 0; i < resourceIdList.length; i++) {
//                    Map<String, String> resourceTemp = new HashMap<>();
//                    resourceTemp.put("resourceId", resourceIdList[i]);
//                    resourceTemp.put("resourceName", resourceNameList[i]);
//                    resourceListTemp.add(resourceTemp);
//                }
//                companyBusinessDTO.setResource(resourceListTemp);
                companyBusinessDTOList.add(companyBusinessDTO);
            }
            map.put("businessList", companyBusinessDTOList);
            map.put("orderType", ResultEnum.GET_COMPANY_BUSINESS_SUCCESS);
            map.put("totalPages", companyBusinessPage.getTotalPages());
            map.put("page", companyBusinessPage.getPageable().getPageNumber());
            return ResultVOUtil.success(map);
        }
    }


    @PostMapping("/createResourceBusiness")
    public ResultVO<Map<Integer, String>> createResourceBusiness(@RequestBody ResourceBusiness resourceBusiness,
                                                                 HttpServletRequest request,
                                                                 HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建人才订单】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才订单】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        // 封装Name属性
        Resource resource = resourceService.getResourceByResourceId(resourceBusiness.getResourceId());
        if (resource == null) {
            response.setStatus(400);
            log.error("【创建人才订单】该人才不存在");
            return ResultVOUtil.fail(ResultEnum.RESOURCE_NOT_EXIST, response);
        }
        // 生成订单ID
        resourceBusiness.setBusinessId(KeyUtil.createID());
        //因为人才是和employeeId绑定的
        resourceBusiness.setEmployeeId(resource.getEmployeeId());
        resourceBusiness.setEmployeeName(resource.getEmployeeName());
        resourceBusiness.setResourceName(resource.getResourceName());
        Company company = companyService.getCompanyByCompanyId(resourceBusiness.getCompanyId());
        resourceBusiness.setCompanyName(company.getCompanyName());
        // 存到数据库
        try {
            ResourceBusiness createBusiness = resourceBusinessService.createResourceBusiness(resourceBusiness);
            if (createBusiness == null) {
                response.setStatus(400);
                log.error("【创建人才订单】发生错误");
                return ResultVOUtil.fail(ResultEnum.CREATE_RESOURCE_BUSINESS_ERROR, response);
            }
        } catch (Exception e) {
            response.setStatus(400);
            log.error("【创建人才订单】发生异常");
        }
        return ResultVOUtil.success(ResultEnum.CREATE_COMPANY_BUSINESS_SUCCESS);
    }


    @PostMapping("/createCompanyBusiness")
    public ResultVO<Map<Integer, String>> createCompanyBusiness(@RequestBody HashMap paramMap,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公司订单】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公司订单】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        // 提取属性
        String info = paramMap.get("info").toString();
        String createDate = paramMap.get("createDate").toString();
        employeeId = paramMap.get("employeeId").toString();
        Integer companyId = Integer.parseInt(paramMap.get("companyId").toString());
        BigDecimal orderPaySum = BigDecimal.valueOf(Double.parseDouble(paramMap.get("orderPaySum").toString()));
//        String resourceIdList = paramMap.get("resourceId").toString();
//
//        resourceIdList = resourceIdList.replace(" ", "");
//
//        String resourceIdString = resourceIdList.substring(1, resourceIdList.length() - 1);
//        String[] resourceIdTemp = resourceIdString.split(",");
//        String resourceNameString = "";


        // 设置属性
        CompanyBusiness createBusiness = new CompanyBusiness();
        createBusiness.setCompanyId(companyId);
        createBusiness.setInfo(info);
        createBusiness.setCreateDate(createDate);
        createBusiness.setOrderPaySum(orderPaySum);
        createBusiness.setEmployeeId(employeeId);
        // 生成订单ID
        createBusiness.setBusinessId(KeyUtil.createID());
        Company company = companyService.getCompanyByCompanyId(companyId);
        if (company == null) {
            response.setStatus(400);
            log.error("【创建公司订单】该公司不存在");
            return ResultVOUtil.fail(ResultEnum.COMPANY_NOT_EXIST, response);
        }
        createBusiness.setEmployeeName(company.getEmployeeName());
        createBusiness.setCompanyName(company.getCompanyName());
//        // 封装resourceName属性（由多个Name拼接而成）
//        for (String s : resourceIdTemp) {
//            Integer t = Integer.parseInt(s);
//            Resource resource = resourceService.getResourceByResourceId(t);
//            if (resource == null) {
//                response.setStatus(400);
//                log.error("【创建公司订单】该人才不存在");
//                return ResultVOUtil.fail(ResultEnum.RESOURCE_NOT_EXIST, response);
//            }
//            resourceNameString += resource.getResourceName() + ",";
//        }
//        createBusiness.setResourceId(resourceIdString);
//        createBusiness.setResourceName(resourceNameString.substring(0, resourceNameString.length() - 1));

        CompanyBusiness returnCompanyBusiness = companyBusinessService.createCompanyBusiness(createBusiness);
        if (returnCompanyBusiness == null) {
            response.setStatus(400);
            log.error("【创建公司订单】失败");
            return ResultVOUtil.fail(ResultEnum.CREATE_COMPANY_BUSINESS_ERROR, response);
        }
        return ResultVOUtil.success();
    }

    @PostMapping("/deleteBusiness")
    public ResultVO<Map<Integer, String>> deleteBusiness(@RequestBody HashMap paramMap,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        String businessId = paramMap.get("businessId").toString();
        Integer businessType = Integer.parseInt(paramMap.get("orderType").toString());
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除公司订单】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【删除公司订单】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);

        Boolean flag = false;
        String info = "";
        // 找到该订单最新回款记录
        List<PayBackRecord> payBackRecordListTemp = payBackRecordService.findAllPayBackRecordByBusinessId(businessId);
        PayBackRecord p = null;
        if (payBackRecordListTemp.size() != 0) {
            // 最新一条回款记录
            p = Collections.min(payBackRecordListTemp);
        }
        // 管理员：已完成订单和回款为0订单才可以删除
        if (employee.getEmployeeRole() == 2) {
            // 如果回款等于成交金额或者回款为0或者没有回款记录，则可以删除
            if (p == null || p.getOwePay().equals(p.getOrderPaySum()) || p.getOwePay().compareTo(new BigDecimal("0.00")) == 0) {
                flag = true;
            } else {
                info += "删除订单错误，订单在进行中！";
            }
        } else {
            // 普通员工：回款为0订单才可以删除
            // 如果回款为成交总额或者没有回款记录，则可以删除
            if (p == null || p.getOwePay().equals(p.getOrderPaySum())) {
                flag = true;
            } else {
                info += "删除订单错误，您没有权限！";
            }
        }

        if (flag) {
            if (businessType == 1) {
                // 删除人才订单
                return deleteResourceBusiness(businessId, response);
            } else if (businessType == 2) {
                // 删除公司订单
                return deleteCompanyBusiness(businessId, response);
            } else {
                log.error("【删除订单】发生错误");
                return ResultVOUtil.fail(ResultEnum.DELETE_BUSINESS_ERROR, response);
            }
        } else {
            log.error("【删除订单】不能删除订单");
            return ResultVOUtil.fail(info, response);
        }

    }

    public ResultVO<Map<Integer, String>> deleteCompanyBusiness(String companyBusinessId,
                                                                HttpServletResponse response) {
        CompanyBusiness dataBaseCompanyBusiness = companyBusinessService.getCompanyBusinessByBusinessId(companyBusinessId);
        if (dataBaseCompanyBusiness == null) {
            log.error("【删除公司订单】该订单不存在");
            return ResultVOUtil.fail(ResultEnum.RESOURCE_BUSINESS_NOT_EXIST, response);
        }
        Integer flag = companyBusinessService.deleteCompanyBusinessByBusinessId(companyBusinessId);
        if (flag == 0) {
            log.error("【删除公司订单】发生错误");
            return ResultVOUtil.fail(ResultEnum.DELETE_COMPANY_BUSINESS_ERROR, response);
        }
        return ResultVOUtil.success();
    }

    public ResultVO<Map<Integer, String>> deleteResourceBusiness(String resourceBusinessId,
                                                                 HttpServletResponse response) {
        Integer flag = resourceBusinessService.deleteResourceBusinessByBusinessId(resourceBusinessId);
        if (flag == 0) {
            log.error("【删除人才订单】发生错误");
            return ResultVOUtil.fail(ResultEnum.DELETE_RESOURCE_BUSINESS_ERROR, response);
        }
        return ResultVOUtil.success(ResultEnum.DELETE_RESOURCE_BUSINESS_SUCCESS);
    }

}
