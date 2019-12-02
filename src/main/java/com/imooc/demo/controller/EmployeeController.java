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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public ResourceFollowRecordService resourceFollowRecordService;
    @Autowired
    public ResourceTempService resourceTempService;
    @Autowired
    public CompanyTempService companyTempService;
    @Autowired
    public CompanyService companyService;
    @Autowired
    public CompanyFollowRecordService companyFollowRecordService;

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

        resource.setShareStatus(2);
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
    @PostMapping("/getResourceList")
    public ResultVO<Map<String, String>> getResourceList(@RequestBody HashMap map,
                                                         HttpServletRequest req) {

        Integer page = Integer.parseInt(map.get("page").toString()) - 1;
        Integer size = Integer.parseInt(map.get("pageSize").toString());
        String token = TokenUtil.parseToken(req);

        System.out.println(page + "," + size);
        System.out.println("token is:" + token);
        if (token.equals("")) {
            log.error("【获取人才列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        System.out.println("id:" + employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }

        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<Resource> resourcePage = resourceService.findResourceByEmployeeId(employeeId, request);
        if (resourcePage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        } else {
            System.out.println(resourcePage.getContent());
            return ResultVOUtil.success(resourcePage);
        }

    }

    /**
     * 获取人才跟进记录
     *
     * @param map
     * @return
     */
    @PostMapping("/getResourceFollows")
    public ResultVO<Map<String, String>> getResourceFollows(@RequestBody HashMap map) {

        Integer resourceId = Integer.parseInt(map.get("resourceId").toString());
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("pageSize").toString());

        System.out.println("resourceId:" + resourceId);
        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<ResourceFollowRecord> resourceFollowRecords = resourceFollowRecordService.getFollowRecordsByResourceId(resourceId, request);
        if (resourceFollowRecords.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_FOLLOW_RECORD_EMPTY);
        } else {
            return ResultVOUtil.success(resourceFollowRecords);
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
        ResourceFollowRecord followRecord = resourceFollowRecordService.createResourceFollow(resourceFollowRecord);
        if (followRecord != null) {
            return ResultVOUtil.success(resourceFollowRecord);
        } else {
            log.error("【创建人才跟进信息】失败");
            return ResultVOUtil.error(ResultEnum.CREATE_RESOURCE_FOLLOW_RECORD_ERROR);
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
        ResourceTemp createResource = null;
        ResourceTemp resourceTemp = new ResourceTemp();
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(resource, resourceTemp);
            resourceTemp.setRequestStatus(0);
            //老板的话直接设置同意
            resourceTemp.setCheckedStatus(1);
            Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
            if (!isSuccess) return ResultVOUtil.error(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR);

//            Boolean flag = resourceService.saveResource(resource);
            Resource returnResource = resourceService.createResource(resource);
            Map<String, Object> map = new HashMap<>();
            if (returnResource != null) {
                map.put("resource", returnResource);
                map.put("employeeRole", 2);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.error(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR);
            }
        }

        BeanUtils.copyProperties(resource, resourceTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        resourceTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        resourceTemp.setRequestStatus(0);
        try {
            //去临时表中查找是否存在该resource未审批的记录，如果存在，覆盖
            ResourceTemp databaseResourceTemp = resourceTempService.findResourceTempByResourceIdAndCheckedStatus(resourceTemp.getResourceId(), 0);
            //如果上一条记录不为空，则需要更新
            if (databaseResourceTemp != null) {
                BeanUtils.copyProperties(resourceTemp, databaseResourceTemp);
                createResource = resourceTempService.createResourceTemp(databaseResourceTemp);
            }
            //如果不存在对该resource尚未审批的记录，则新插入一条记录到ResourceTemp表
            else {
                createResource = resourceTempService.createResourceTemp(resourceTemp);
            }
            if (createResource == null) {
                log.error("【修改人才信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【修改人才信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_EXCEPTION);
        }
        return ResultVOUtil.success();
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
        ResourceTemp createResource = null;
        ResourceTemp resourceTemp = new ResourceTemp();
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(resource, resourceTemp);
            resourceTemp.setRequestStatus(1);
            resourceTemp.setCheckedStatus(1);
            Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
            if (!isSuccess) return ResultVOUtil.error(ResultEnum.DELETE_RESOURCE_ERROR);

            Boolean flag = resourceService.deleteResourceByResourceId(resource.resourceId);
            Map<String, Integer>map = new HashMap<>();
            if (flag) {
                map.put("employeeRole", 2);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.error(ResultEnum.DELETE_RESOURCE_ERROR);
            }
        }

        BeanUtils.copyProperties(resource, resourceTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        resourceTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        resourceTemp.setRequestStatus(1);
        try {
            // 先在数据库中找是否有未审批记录
            ResourceTemp databaseResourceTemp = resourceTempService.findResourceTempByResourceIdAndCheckedStatus(resourceTemp.getResourceId(), 0);
            if (databaseResourceTemp != null) {
                // 如果有：直接覆盖
                BeanUtils.copyProperties(resourceTemp, databaseResourceTemp);
                createResource = resourceTempService.createResourceTemp(databaseResourceTemp);
            } else {
                // 否则新建一条记录
                createResource = resourceTempService.createResourceTemp(resourceTemp);
            }
            if (createResource == null) {
                log.error("【删除人才信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【删除人才信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_EXCEPTION);
        }
        return ResultVOUtil.success();
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
        CompanyTemp createCompany = null;
        CompanyTemp companyTemp = new CompanyTemp();
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(company, companyTemp);
            companyTemp.setRequestStatus(0);
            companyTemp.setCheckedStatus(1);

            Boolean isSuccess = companyTempService.saveCompanyTemp(companyTemp);
            if (!isSuccess) return ResultVOUtil.error(ResultEnum.MANAGER_UPDATE_COMPANY_INFO_ERROR);

            Company returnCompany = companyService.createCompany(company);
            Map<String, Object> map = new HashMap<>();
            if (returnCompany != null) {
                map.put("employeeRole", 2);
                map.put("company", returnCompany);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.error(ResultEnum.UPDATE_COMPANY_INFO_ERROR);
            }
        }

        BeanUtils.copyProperties(company, companyTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        companyTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        companyTemp.setRequestStatus(0);
        try {
            // 找到数据库中未批准的记录
            CompanyTemp databaseCompanyTemp = companyTempService.findCompanyTempByCompanyIdAndCheckedStatus(companyTemp.getCompanyId(), 0);
            if (databaseCompanyTemp != null) {
                // 如果数据库在有：覆盖
                BeanUtils.copyProperties(companyTemp, databaseCompanyTemp);
                createCompany = companyTempService.createCompanyTemp(databaseCompanyTemp);
            } else {
                // 在数据库中新建一个记录
                createCompany = companyTempService.createCompanyTemp(companyTemp);

            }
            if (createCompany == null) {
                log.error("【修改企业信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_COMPANY_INFO_ERROR);
            }
        } catch (Exception e) {
            log.error("【修改企业信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_COMPANY_INFO_EXCEPTION);
        }
        return ResultVOUtil.success();
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
        CompanyTemp createCompany = null;
        CompanyTemp companyTemp = new CompanyTemp();
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(company, companyTemp);
            companyTemp.setRequestStatus(1);
            companyTemp.setCheckedStatus(1);
            Boolean isSuccess = companyTempService.saveCompanyTemp(companyTemp);
            if (!isSuccess) return ResultVOUtil.error(ResultEnum.MANAGER_DELETE_COMPANY_INFO_ERROR);

            Boolean flag = companyService.deleteCompanyByCompanyId(company.companyId);
            Map<String, Integer> map = new HashMap<>();
            if (flag) {
                map.put("employeeRole", 2);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_INFO_ERROR);
            }
        }

        BeanUtils.copyProperties(company, companyTemp);
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        companyTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        companyTemp.setRequestStatus(1);
        try {
            // 先在数据库中找是否有未审批申请
            CompanyTemp databaseCompanyTemp = companyTempService.findCompanyTempByCompanyIdAndCheckedStatus(companyTemp.getCompanyId(), 0);
            if (databaseCompanyTemp != null) {
                // 如果有：直接覆盖
                BeanUtils.copyProperties(companyTemp, databaseCompanyTemp);
                createCompany = companyTempService.createCompanyTemp(databaseCompanyTemp);
            } else {
                // 否则新建一条记录
                createCompany = companyTempService.createCompanyTemp(companyTemp);
            }
            if (createCompany == null) {
                log.error("【删除企业信息】创建临时表发生错误");
                return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_INFO_ERROR);
            }
        } catch (Exception e) {
            log.error("【删除企业信息】创建临时表发生异常");
            return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_INFO_EXCEPTION);
        }
        return ResultVOUtil.success();
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
        company.setEmployeeId(employeeId);
        Company company1 = companyService.createCompany(company);
        if (company1 == null) {
            return ResultVOUtil.error(ResultEnum.CREATE_COMPANY_ERROR);
        }
        return ResultVOUtil.error(company1);
    }

    @PostMapping("/getCompanyList")
    public ResultVO<Map<String, String>> getCompanyList(@RequestBody HashMap map, HttpServletRequest req) {
        Integer page = Integer.parseInt(map.get("page").toString()) - 1;
        Integer size = Integer.parseInt(map.get("pageSize").toString());

        String token = TokenUtil.parseToken(req);

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
            return ResultVOUtil.success(companyPage);
        }

    }

    /**
     * 获取人才跟进记录
     *
     * @param map
     * @return
     */
    @PostMapping("/getCompanyFollows")
    public ResultVO<Map<String, String>> getCompanyFollows(@RequestBody HashMap map) {


        Integer companyId = Integer.parseInt(map.get("companyId").toString());
        Integer page = Integer.parseInt(map.get("page").toString()) - 1;
        Integer size = Integer.parseInt(map.get("pageSize").toString());

        System.out.println("companyId:" + companyId);
        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");


        Page<CompanyFollowRecord> companyFollowRecordPage = companyFollowRecordService.getCompanyFollowRecordByCompanyId(companyId, request);
        if (companyFollowRecordPage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.COMPANY_FOLLOW_RECORD_EMPTY);
        } else {
            return ResultVOUtil.success(companyFollowRecordPage);
        }
    }

    /**
     * 创建公司跟进记录
     *
     * @param companyFollowRecord
     * @param request
     * @return
     */
    @PostMapping("/createCompanyFollow")
    public ResultVO<Map<String, String>> createCompanyFollow(@RequestBody CompanyFollowRecord companyFollowRecord,
                                                             HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公司跟进信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公司跟进信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        companyFollowRecord.setEmployeeId(employeeId);
        companyFollowRecord.setEmployeeName(employee.getEmployeeName());
        CompanyFollowRecord createFollowRecord = companyFollowRecordService.createCompanyFollowRecord(companyFollowRecord);
        if (createFollowRecord != null) {
            return ResultVOUtil.success(createFollowRecord);
        } else {
            log.error("【创建公司跟进信息】失败");
            return ResultVOUtil.error(ResultEnum.CREATE_COMPANY_FOLLOW_RECORD_ERROR);
        }

    }

    @PostMapping("/test")
    public String test() {
        return "hello world";
    }

}
