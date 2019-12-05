package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.*;
import com.imooc.demo.repository.ResourceRepository;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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


    @PostMapping("/getBusinessList")
    public ResultVO<Map<Integer, String>> getBusinessList(@RequestBody HashMap paramMap,
                                                          HttpServletRequest req) {
        //orderType 1表示人才订单 2表示公司订单
        Integer orderType = Integer.parseInt(paramMap.get("orderType").toString());
        Integer page = Integer.parseInt(paramMap.get("page").toString());
        Integer size = Integer.parseInt(paramMap.get("pageSize").toString());
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取订单列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取订单列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if (orderType != 1 && orderType != 2) {
            log.error("【获取订单列表】参数错误");
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");
        Map<String, Object> map = new HashMap<>();
        // 个人订单，根据employeeId取个人所有的
        if (orderType == 1) {
            Page<ResourceBusiness> resourceBusinessPage = null;
            if (employee.getEmployRole() == 2) {
                // 管理员看到所有人的订单
                resourceBusinessPage = resourceBusinessService.findAllResourceBusinessPageable(request);
            } else {
                // 普通员工看到自己的订单
                resourceBusinessPage = resourceBusinessService.findResourceBusinessByEmployeeId(employeeId, request);
            }
            map.put("businessList", resourceBusinessPage);
            map.put("orderType", ResultEnum.GET_RESOURCE_BUSINESS_SUCCESS);
            return ResultVOUtil.success(map);
        } else {
            // 公司订单
            Page<CompanyBusiness> companyBusinessPage = null;
            if (employee.getEmployRole() == 2) {
                // 管理员看到所有人的订单
                companyBusinessPage = companyBusinessService.findAllCompanyBusinessPageable(request);
            } else {
                // 普通员工看到自己的订单
                companyBusinessPage = companyBusinessService.findCompanyBusinessByEmployeeId(employeeId, request);
            }


            List<CompanyBusinessDTO> companyBusinessDTOList = new ArrayList<>();
            // 遍历数据库取出来的CompanyBusiness，将其resourceId和resourceName字段（字符串）
            // 转换为resource列表:[(resourceId resourceName), (resourceId resourceName), ... ]
            for (CompanyBusiness companyBusinessTemp : companyBusinessPage.getContent()) {
                CompanyBusinessDTO companyBusinessDTO = new CompanyBusinessDTO();
                BeanUtils.copyProperties(companyBusinessTemp, companyBusinessDTO, getNullPropertyNames(companyBusinessTemp));
                String[] resourceIdList = companyBusinessTemp.getResourceId().split(",");
                String[] resourceNameList = companyBusinessTemp.getResourceName().split(",");
                List<Map<String, String>> resourceListTemp = new ArrayList<>();
                for (int i = 0; i < resourceIdList.length; i++) {
                    Map<String, String> resourceTemp = new HashMap<>();

                    resourceTemp.put("resourceId", resourceIdList[i]);
                    resourceTemp.put("resourceName", resourceNameList[i]);
                    resourceListTemp.add(resourceTemp);
                }
                companyBusinessDTO.setResource(resourceListTemp);
                companyBusinessDTOList.add(companyBusinessDTO);
            }
            map.put("businessList", companyBusinessDTOList);
            map.put("orderType", ResultEnum.GET_COMPANY_BUSINESS_SUCCESS);
            return ResultVOUtil.success(map);
        }
    }


    @PostMapping("/createResourceBusiness")
    public ResultVO<Map<Integer, String>> createResourceBusiness(@RequestBody ResourceBusiness resourceBusiness,
                                                                 HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建人才订单】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才订单】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }

        // 封装Name属性
        Resource resource = resourceService.getResourceByResourceId(resourceBusiness.getResourceId());
        if (resource == null) {
            log.error("【创建人才订单】该人才不存在");
            return ResultVOUtil.error(ResultEnum.RESOURCE_NOT_EXIST);
        }
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
                log.error("【创建人才订单】发生错误");
                return ResultVOUtil.error(ResultEnum.CREATE_RESOURCE_BUSINESS_ERROR);
            }
        } catch (Exception e) {
            log.error("【创建人才订单】发生异常");
        }
        return ResultVOUtil.success();
    }

    @PostMapping("/deleteResourceBusiness")
    public ResultVO<Map<Integer, String>> deleteResourceBusiness(@RequestBody HashMap paramMap,
                                                                 HttpServletRequest request) {

        Integer resourceBusinessId = Integer.parseInt(paramMap.get("id").toString());
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除人才订单】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【删除人才订单】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        ResourceBusiness dataBaseResourceBusiness = resourceBusinessService.getResourceBusinessById(resourceBusinessId);
        if (dataBaseResourceBusiness == null) {
            log.error("【删除人才订单】该订单不存在");
            return ResultVOUtil.error(ResultEnum.RESOURCE_BUSINESS_NOT_EXIST);
        }
        Integer flag = resourceBusinessService.deleteResourceBusinessById(resourceBusinessId);
        if (flag == 0) {
            log.error("【删除人才订单】发生错误");
            return ResultVOUtil.error(ResultEnum.DELETE_RESOURCE_BUSINESS_ERROR);
        }
        return ResultVOUtil.success();
    }

    @PostMapping("/createCompanyBusiness")
    public ResultVO<Map<Integer, String>> createCompanyBusiness(@RequestBody HashMap paramMap,
                                                                HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公司订单】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公司订单】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }

        // 生成订单ID
        String businessId = UUID.randomUUID().toString().replaceAll("-", "");
        // 提取属性
        String info = paramMap.get("info").toString();
        String createDate = paramMap.get("createDate").toString();
        employeeId = paramMap.get("employeeId").toString();
        Integer companyId = Integer.parseInt(paramMap.get("companyId").toString());
        BigDecimal orderPaySum = BigDecimal.valueOf(Double.parseDouble(paramMap.get("orderPaySum").toString()));
        String resourceIdList = paramMap.get("resourceId").toString();
        resourceIdList = resourceIdList.replace(" ", "");

        String resourceIdString = resourceIdList.substring(1, resourceIdList.length() - 1);
        String[] resourceIdTemp = resourceIdString.split(",");
        String resourceNameString = "";
        // 设置属性
        CompanyBusiness createBusiness = new CompanyBusiness();
        createBusiness.setBusinessId(businessId);
        createBusiness.setCompanyId(companyId);
        createBusiness.setInfo(info);
        createBusiness.setCreateDate(createDate);
        createBusiness.setOrderPaySum(orderPaySum);
        createBusiness.setEmployeeId(employeeId);
        Company company = companyService.getCompanyByCompanyId(companyId);
        if (company == null) {
            log.error("【创建公司订单】该公司不存在");
            return ResultVOUtil.error(ResultEnum.COMPANY_NOT_EXIST);
        }
        createBusiness.setEmployeeName(company.getEmployeeName());
        createBusiness.setCompanyName(company.getCompanyName());
        // 封装resourceName属性（由多个Name拼接而成）
        for (String s : resourceIdTemp) {
            Integer t = Integer.parseInt(s);
            Resource resource = resourceService.getResourceByResourceId(t);
            if (resource == null) {
                log.error("【创建公司订单】该人才不存在");
                return ResultVOUtil.error(ResultEnum.RESOURCE_NOT_EXIST);
            }
            resourceNameString += resource.getResourceName() + ",";
        }
        createBusiness.setResourceId(resourceIdString);
        createBusiness.setResourceName(resourceNameString.substring(0, resourceNameString.length() - 1));

        CompanyBusiness returnCompanyBusiness = companyBusinessService.createCompanyBusiness(createBusiness);
        if (returnCompanyBusiness == null) {
            log.error("【创建公司订单】失败");
            return ResultVOUtil.error(ResultEnum.CREATE_COMPANY_BUSINESS_ERROR);
        }
        return ResultVOUtil.success();
    }

    @PostMapping("/deleteCompanyBusiness")
    public ResultVO<Map<Integer, String>> deleteCompanyBusiness(@RequestBody HashMap paramMap,
                                                                HttpServletRequest request) {

        Integer companyBusinessId = Integer.parseInt(paramMap.get("id").toString());
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除公司订单】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【删除公司订单】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        CompanyBusiness dataBaseCompanyBusiness = companyBusinessService.getCompanyBusinessById(companyBusinessId);
        if (dataBaseCompanyBusiness == null) {
            log.error("【删除公司订单】该订单不存在");
            return ResultVOUtil.error(ResultEnum.RESOURCE_BUSINESS_NOT_EXIST);
        }
        Integer flag = companyBusinessService.deleteCompanyBusinessById(companyBusinessId);
        if (flag == 0) {
            log.error("【删除公司订单】发生错误");
            return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_BUSINESS_ERROR);
        }
        return ResultVOUtil.success();
    }

}
