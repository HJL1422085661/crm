package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.*;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author emperor
 * @Date 2019/10/21 10:15
 * @Version 1.0
 */

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    public ResourceService resourceService;
    @Autowired
    public EmployeeService employeeService;
    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    public FollowRecordService followRecordService;
    @Autowired
    public ResourceTempService resourceTempService;
    @Autowired
    public CompanyTempService companyTempService;
    @Autowired
    public CompanyService companyService;

    /**
     * 创建人才资源
     *
     * @param resource
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/createResource")
    public ResultVO<Map<String, String>> createResource(@RequestBody Resource resource,
                                                        HttpServletRequest request) {

        resource.setShareStatus("private");
        //封装时间参数
        Date createDate = new Date();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        resource.setCreateDate(sim.format(createDate));

        String token = TokenUtil.parseToken(request);

        if (token.equals("")) {
            log.error("【创建人才资源】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才资源】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        resource.setEmployeeId(employeeId);

        Resource createResource = null;
        try {
            createResource = resourceService.createResource(resource);
            if (createResource == null) {
                log.error("【录入人才信息】发生错误");
                return ResultVOUtil.error(ResultEnum.SAVE_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【录入人才信息】发生异常");
        }
        return ResultVOUtil.success(createResource);
    }

    /**
     * 修改人才状态
     *
     * @param resourceId
     * @param shareStatus
     * @return
     */
    @PostMapping("/updateResourceShareStatus")
    public ResultVO<Map<String, String>> updateResourceShareStatus(@RequestParam("resourceId") Integer resourceId,
                                                                   @RequestParam("shareStatus") String shareStatus) {
        Boolean flag = resourceService.updateShareStatusByResourceId(shareStatus, resourceId);
        //TODO
        if (flag) {
            return ResultVOUtil.success();
        } else {
            log.error("【更新人才状态】发生错误");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
        }
    }


    //分页显示私有客户信息
    @GetMapping("/getResourceList")
    public ResultVO<Map<String, String>> getResourceList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         HttpServletRequest req) {
        String token = TokenUtil.parseToken(req);

        System.out.println("token is:" + token);
        if (token.equals("")) {
            log.error("【获取人才列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        PageRequest request = PageRequest.of(page, size);
        Page<Resource> resourcePage = resourceService.findResourceByEmployeeId(employeeId, request);
        if (resourcePage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        } else {
            System.out.println(resourcePage.getContent());
            return ResultVOUtil.success(resourcePage.getContent());
        }

    }

    /**
     * 获取人才跟进记录
     *
     * @param map
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/getResourceFollows")
    public ResultVO<Map<String, String>> getResourceFollows(@RequestBody HashMap map,
                                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Integer resourceId = Integer.parseInt(map.get("resourceId").toString());

        System.out.println("resourceId:" + resourceId);
        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<ResourceFollowRecord> resourceFollowRecords = followRecordService.getFollowRecordsByResourceId(resourceId, request);
        if (resourceFollowRecords.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_FOLLOW_RECORD_EMPTY);
        } else {
            System.out.println(resourceFollowRecords.getContent());
            return ResultVOUtil.success(resourceFollowRecords.getContent());
        }
    }

    /**
     * 创建人才跟进记录
     *
     * @param resourceFollowRecord
     * @param request
     * @return
     */
    @PostMapping("/createResourceFollow")
    public ResultVO<Map<String, String>> createResourceFollow(@RequestBody ResourceFollowRecord resourceFollowRecord,
                                                              HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建人才跟进信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才跟进信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        resourceFollowRecord.setEmployeeId(employeeId);
        resourceFollowRecord.setEmployeeName(employee.getEmployeeName());
        ResourceFollowRecord followRecord = followRecordService.createResourceFollow(resourceFollowRecord);
        if (followRecord != null) {
            return ResultVOUtil.success(resourceFollowRecord);
        } else {
            log.error("【创建人才跟进信息】失败");
            return ResultVOUtil.error(ResultEnum.CREATE_FOLLOW_RECORD_ERROR);
        }

    }


    /**
     * 修改客户信息(需要经过老板审批)
     *
     * @param resource
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/updateResource")
    public ResultVO<Map<String, String>> updateResource(@RequestBody Resource resource,
                                                        HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改人才信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【修改人才信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            Boolean flag = resourceService.saveResource(resource);
            if (flag) {
                return ResultVOUtil.success();
            } else {
                return ResultVOUtil.error(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR);
            }
        }
        ResourceTemp createResource = null;
        ResourceTemp resourceTemp = null;
        BeanUtils.copyProperties(resource, resourceTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        resourceTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        resourceTemp.setRequestStatus(0);
        try {
            createResource = resourceTempService.createResourceTemp(resourceTemp);
            if (createResource == null) {
                log.error("【修改人才信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【修改人才信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_EXCEPTION);
        }
        return ResultVOUtil.success(createResource);
    }

    /**
     * 删除客户信息(需要经过老板审批)
     *
     * @param resource
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/deleteResource")
    public ResultVO<Map<String, String>> deleteResource(@RequestBody Resource resource,
                                                        HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除人才信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【删除人才信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            Boolean flag = resourceService.deleteResourceByResourceId(resource.resourceId);
            if (flag) {
                return ResultVOUtil.success();
            } else {
                return ResultVOUtil.error(ResultEnum.DELETE_RESOURCE_ERROR);
            }
        }
        ResourceTemp createResource = null;
        ResourceTemp resourceTemp = null;
        BeanUtils.copyProperties(resource, resourceTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        resourceTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        resourceTemp.setRequestStatus(1);
        try {
            createResource = resourceTempService.createResourceTemp(resourceTemp);
            if (createResource == null) {
                log.error("【删除人才信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【删除人才信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_EXCEPTION);
        }
        return ResultVOUtil.success(createResource);
    }

    /**
     * 修改企业信息(需要经过老板审批)
     *
     * @param company
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/updateCompany")
    public ResultVO<Map<String, String>> updateCompany(@RequestBody Company company,
                                                       HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改企业信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【修改企业信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            Boolean flag = companyService.saveCompany(company);
            if (flag) {
                return ResultVOUtil.success();
            } else {
                return ResultVOUtil.error(ResultEnum.UPDATE_COMPANY_INFO_ERROR);
            }
        }
        CompanyTemp createCompany = null;
        CompanyTemp companyTemp = null;
        BeanUtils.copyProperties(company, companyTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        companyTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        companyTemp.setRequestStatus(0);
        try {
            createCompany = companyTempService.createCompanyTemp(companyTemp);
            if (createCompany == null) {
                log.error("【修改企业信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_COMPANY_INFO_ERROR);
            }
        } catch (Exception e) {
            log.error("【修改企业信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_COMPANY_INFO_EXCEPTION);
        }
        return ResultVOUtil.success(createCompany);
    }

    /**
     * 删除企业信息(需要经过老板审批)
     *
     * @param company
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/deleteCompany")
    public ResultVO<Map<String, String>> deleteCompany(@RequestBody Company company,
                                                       HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除企业信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【删除企业信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            Boolean flag = companyService.deleteCompanyByCompanyId(company.companyId);
            if (flag) {
                return ResultVOUtil.success();
            } else {
                return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_INFO_ERROR);
            }
        }
        CompanyTemp createCompany = null;
        CompanyTemp companyTemp = null;
        BeanUtils.copyProperties(company, companyTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        companyTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        companyTemp.setRequestStatus(1);
        try {
            createCompany = companyTempService.createCompanyTemp(companyTemp);
            if (createCompany == null) {
                log.error("【删除企业信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_INFO_ERROR);
            }
        } catch (Exception e) {
            log.error("【删除企业信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_INFO_EXCEPTION);
        }
        return ResultVOUtil.success(createCompany);
    }

    @PostMapping("/createCompany")
    public ResultVO<Map<String, String>> createCompany(@RequestBody Company company,
                                                       HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公司资源信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公司资源信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Company company1 = companyService.createCompany(company);
        if(company1 == null){
            return ResultVOUtil.error(ResultEnum.CREATE_COMPANY_ERROR);
        }
        return ResultVOUtil.error(company1);
    }
    @GetMapping("/getCompanyList")
    public ResultVO<Map<String, String>> getCompanyList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         HttpServletRequest req) {
        String token = TokenUtil.parseToken(req);

        System.out.println("token is:" + token);
        if (token.equals("")) {
            log.error("【获取公司列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取公司列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "startDate");
        Page<Company> companyPage = companyService.findCompanyByEmployeeId(employeeId, request);
        if (companyPage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.COMPANY_LIST_EMPTY);
        } else {
            System.out.println(companyPage.getContent());
            return ResultVOUtil.success(companyPage.getContent());
        }

    }
    @PostMapping("/test")
    public String test() {
        return "hello world";
    }

}
