package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.model.*;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.EnumUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Hash;
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
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.imooc.demo.utils.BeanCopyUtil.getNullPropertyNames;

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
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        resource.setShareStatus(2);
        //封装时间参数
        Date createDate = new Date();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        resource.setCreateDate(sim.format(createDate));

        String token = TokenUtil.parseToken(request);

        if (token.equals("")) {
            log.error("【创建人才资源】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才资源】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee dataBaseEmployee = employeeService.getEmployeeByEmployeeId(employeeId);
        resource.setEmployeeName(dataBaseEmployee.getEmployeeName());
        resource.setEmployeeId(employeeId);

        // 判断电话号码是否已存在
//            Boolean flag = resourceService.saveResource(resource);
        Boolean phoneExist = resourceService.existsByPhoneNumber(resource.getPhoneNumber());
        if (phoneExist) {
            log.error("【录入人才信息】电话号码已存在");
            return ResultVOUtil.fail(ResultEnum.DUPLICATE_PHONE, response);
        }

        Resource createResource = null;
        try {
            createResource = resourceService.createResource(resource);
            if (createResource == null) {
                log.error("【录入人才信息】发生错误");
                return ResultVOUtil.fail(ResultEnum.SAVE_RESOURCE_ERROR, response);
            }
        } catch (Exception e) {
            log.error("【录入人才信息】发生异常");
        }

        return ResultVOUtil.success(createResource);
    }

    /**
     * 修改人才状态（公有、私有）
     *
     * @return
     */
    @PostMapping("/updateResourceShareStatus")
    public ResultVO<Map<String, String>> updateResourceShareStatus(@RequestBody HashMap paramMap,
                                                                   HttpServletRequest request,
                                                                   HttpServletResponse response) {
        Integer resourceId = Integer.parseInt(paramMap.get("resourceId").toString());
        String shareStatus = paramMap.get("shareStatus").toString();

        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【更新人才状态】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【更新人才状态】 无权限");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);

        Resource resource = new Resource();
        Boolean flag = resourceService.updateShareStatusByResourceId(shareStatus, resourceId);

        // 如果变为公有，对应employeeId则设为大boss
        if (shareStatus.equals("1")) {
            resource = resourceService.getResourceByResourceId(resourceId);
            resource.setEmployeeId(EnumUtil.ROOT_ID);
            resource.setEmployeeName(EnumUtil.ROOT_NAME);
        } else {
            // 如果变为私有，对应employeeId则设为自己
            resource = resourceService.getResourceByResourceId(resourceId);
            resource.setEmployeeId(employeeId);
            resource.setEmployeeName(employee.getEmployeeName());
        }
        Boolean isSuccess = resourceService.saveResource(resource);
        if (!isSuccess) {
            log.error("【更新人才状态】保存失败");
            return ResultVOUtil.fail(ResultEnum.SAVE_RESOURCE_ERROR, response);
        }
        //TODO
        if (flag) {
            return ResultVOUtil.success(ResultEnum.UPDATE_RESOURCE_SUCCESS);
        } else {
            log.error("【更新人才状态】发生错误");
            return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_ERROR, response);
        }
    }


    /**
     * 分页显示人才客户信息
     *
     * @return
     */
    @PostMapping("/getResourceList")
    public ResultVO<Map<String, String>> getResourceList(@RequestBody HashMap map,
                                                         HttpServletRequest req,
                                                         HttpServletResponse response) {
//        String colName = map.get("dataIndex").toString();
        Integer shareStatus = Integer.parseInt(map.get("shareStatus").toString());
        Integer page = Integer.parseInt(map.get("page").toString()) - 1;
        Integer size = Integer.parseInt(map.get("pageSize").toString());
        String token = TokenUtil.parseToken(req);

        if (token.equals("")) {
            log.error("【获取人才列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<Resource> resourcePage = null;
        // 管理员能看到所有客户(所有公有、所有私有)
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() == 2) {
            resourcePage = resourceService.findResourceByShareStatusPageable(shareStatus, request);
        } else {
            // 普通员工：只能取自己私有的或者公共的
            // 1 公有,  2 私有
            if (shareStatus == 1) {
                resourcePage = resourceService.findResourceByShareStatusPageable(shareStatus, request);
            } else {
                resourcePage = resourceService.findResourceByEmployeeId(employeeId, request);
            }
        }
        if (resourcePage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        } else {
            System.out.println(resourcePage.getContent());
            return ResultVOUtil.success(resourcePage);
        }
    }


    /**
     * 获取人才姓名及ID
     *
     * @param req
     * @return
     */
    @GetMapping("/getResourceNames")
    public ResultVO<Map<String, String>> getResourceNames(HttpServletRequest req,
                                                          HttpServletResponse response) {

        String token = TokenUtil.parseToken(req);

        System.out.println("token is:" + token);
        if (token.equals("")) {
            log.error("【获取人才列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        System.out.println("id:" + employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        List<Resource> privateResourceList = new ArrayList<>();
        // 管理员能看到所有客户
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() == 2) {
            privateResourceList = resourceService.findAllResource();
        } else {
            privateResourceList = resourceService.getResourceByEmployeeId(employeeId);
            //1表示共有 2表示私有
            List<Resource> publicResourceList = resourceService.findResourceByShareStatus(1);
            for (Resource resource : publicResourceList)
                privateResourceList.add(resource);
        }
        //考虑下要不要排序

        //封装成只要ID ResourceName lambda表达式 java 11
        List<Object> resourceList = new ArrayList<>();
        for (Resource resource : privateResourceList) {
            Map<String, Object> map = new HashMap<>();
            map.put("resourceId", resource.getResourceId());
            map.put("resourceName", resource.getResourceName());
            resourceList.add(map);
        }
        return ResultVOUtil.success(resourceList);

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
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");
        Page<ResourceFollowRecord> resourceFollowRecords = resourceFollowRecordService.getFollowRecordsByResourceId(resourceId, request);
        if (resourceFollowRecords.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_FOLLOW_RECORD_EMPTY);
        } else {
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
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建人才跟进信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才跟进信息】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        resourceFollowRecord.setEmployeeId(employeeId);
        resourceFollowRecord.setEmployeeName(employee.getEmployeeName());
        // 更改对应人才的客户状态
        Resource resource = resourceService.getResourceByResourceId(resourceFollowRecord.getResourceId());
        if (resource == null) {
            log.error("【创建人才跟进信息】 找不到对应的人才信息");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        resource.setStatus(resourceFollowRecord.getStatus());
        Boolean flag = resourceService.saveResource(resource);
        if (!flag) {
            log.error("【创建人才跟进信息】保存人才信息失败");
            return ResultVOUtil.fail(ResultEnum.SAVE_RESOURCE_ERROR, response);
        }
        ResourceFollowRecord followRecord = resourceFollowRecordService.createResourceFollow(resourceFollowRecord);
        if (followRecord != null) {
            return ResultVOUtil.success(resourceFollowRecord);
        } else {
            log.error("【创建人才跟进信息】失败");
            return ResultVOUtil.fail(ResultEnum.CREATE_RESOURCE_FOLLOW_RECORD_ERROR, response);
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
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改人才信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【修改人才信息】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);

        // 判断电话号码是否已存在
        // 先根据电话号码找出resource，然后比较resourceId是否与当前传入的resourceId一致。一致才可以更新，否则电话号码重复。
        Resource searchResource = resourceService.findResourceByPhoneNumber(resource.getPhoneNumber());
        if (searchResource != null) {
            if (!searchResource.getResourceId().equals(resource.getResourceId())) {
                log.error("【修改人才信息】电话号码已存在");
                return ResultVOUtil.fail(ResultEnum.DUPLICATE_PHONE, response);
            }
        }

//        Boolean phoneExist = resourceService.existsByPhoneNumber(resource.getPhoneNumber());
//        if (phoneExist) {
//            log.error("【修改人才信息】电话号码已存在");
//            return ResultVOUtil.fail(ResultEnum.DUPLICATE_PHONE, response);
//        }

        ResourceTemp createResource = null;
        ResourceTemp resourceTemp = new ResourceTemp();
        // 前台只传了employeeId，要自己封装employeeName属性
        Employee employeeNew = employeeService.getEmployeeByEmployeeId(resource.getEmployeeId());
        resource.setEmployeeName(employeeNew.getEmployeeName());
        //如果是老板则直接操作，不需要审批,但是需要记录操作
        if (employee.getEmployeeRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(resource, resourceTemp, getNullPropertyNames(resource));
            resourceTemp.setRequestStatus(0);
            //老板的话直接设置同意
            resourceTemp.setCheckedStatus(1);
            Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
            if (!isSuccess) return ResultVOUtil.fail(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR, response);

            // 解决电话号码唯一性问题：先设置数据库的电话为一个特殊值，然后再修改
            Resource returnResource = new Resource();
            Resource tempResource = resourceService.getResourceByResourceId(resource.getResourceId());
            tempResource.setPhoneNumber("ceshiceshi");
            Boolean tempReturnResource = resourceService.saveResource(tempResource);
            if (!tempReturnResource) {
                log.error("【修改人才信息】临时修改异常");
                return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_ERROR, response);
            }
            returnResource = resourceService.createResource(resource);
            if (returnResource == null) {
                log.error("【修改人才信息】创建失败");
                return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_ERROR, response);
            }

            Map<String, Object> map = new HashMap<>();
            if (returnResource != null) {
                map.put("resource", returnResource);
                map.put("employeeRole", 2);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.fail(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR, response);
            }
        }

        BeanUtils.copyProperties(resource, resourceTemp, getNullPropertyNames(resource));
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示同意 2表示不同意
        resourceTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        resourceTemp.setRequestStatus(0);
        try {
            //去临时表中查找是否存在该resource未审批的记录，如果存在，覆盖
            ResourceTemp databaseResourceTemp = resourceTempService.findResourceTempByResourceIdAndCheckedStatus(resourceTemp.getResourceId(), 0);
            //如果上一条记录不为空，则需要更新
            if (databaseResourceTemp != null) {
                BeanUtils.copyProperties(resourceTemp, databaseResourceTemp, getNullPropertyNames(resourceTemp));
                createResource = resourceTempService.createResourceTemp(databaseResourceTemp);
            }
            //如果不存在对该resource尚未审批的记录，则新插入一条记录到ResourceTemp表
            else {
                createResource = resourceTempService.createResourceTemp(resourceTemp);
            }
            if (createResource == null) {
                log.error("【修改人才信息】创建临时表发生错误");
                return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_ERROR, response);
            }
        } catch (Exception e) {
            log.error("【修改人才信息】创建临时表发生异常");
            return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_EXCEPTION, response);
        }
        return ResultVOUtil.success(ResultEnum.UPDATE_RESOURCE_SUCCESS);
    }

    /**
     * 删除客户信息(需要经过老板审批)
     *
     * @param paramMap: resourceId
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/deleteResource")
    public ResultVO<Map<String, String>> deleteResource(@RequestBody HashMap paramMap,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        Integer resourceId = Integer.parseInt(paramMap.get("resourceId").toString());

        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除人才信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【删除人才信息】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        ResourceTemp createResource = new ResourceTemp();

        // 通过resourceId从数据库中取到对应的resource
        Resource resource = resourceService.getResourceByResourceId(resourceId);

        ResourceTemp resourceTemp = new ResourceTemp();
        //如果是老板则直接操作，不需要审批,但是需要记录操作
        if (employee.getEmployeeRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(resource, resourceTemp, getNullPropertyNames(resource));
            resourceTemp.setRequestStatus(1);
            resourceTemp.setCheckedStatus(1);
            Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
            if (!isSuccess) return ResultVOUtil.fail(ResultEnum.DELETE_RESOURCE_ERROR, response);

            Integer flag = resourceService.deleteResourceByResourceId(resource.resourceId);
            Map<String, Integer> map = new HashMap<>();
            if (flag != 0) {
                map.put("employeeRole", 2);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.fail(ResultEnum.DELETE_RESOURCE_ERROR, response);
            }
        }

        BeanUtils.copyProperties(resource, resourceTemp, getNullPropertyNames(resource));
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        resourceTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        resourceTemp.setRequestStatus(1);
        try {
            // 先在数据库中找是否有未审批记录
            ResourceTemp databaseResourceTemp = resourceTempService.findResourceTempByResourceIdAndCheckedStatus(resourceTemp.getResourceId(), 0);
            if (databaseResourceTemp != null) {
                // 如果有：直接覆盖
                BeanUtils.copyProperties(resourceTemp, databaseResourceTemp, getNullPropertyNames(resourceTemp));
                createResource = resourceTempService.createResourceTemp(databaseResourceTemp);
            } else {
                // 否则新建一条记录
                createResource = resourceTempService.createResourceTemp(resourceTemp);
            }
            if (createResource == null) {
                log.error("【删除人才信息】创建临时表发生错误");
                return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_ERROR, response);
            }
        } catch (Exception e) {
            log.error("【删除人才信息】创建临时表发生异常");
            return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_EXCEPTION, response);
        }
        return ResultVOUtil.success(ResultEnum.DELETE_RESOURCE_SUCCESS);
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
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改企业信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【修改企业信息】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 封装员工姓名参数
        Employee dbEmployee = employeeService.getEmployeeByEmployeeId(company.getEmployeeId());
        if (dbEmployee == null) {
            log.error("【修改企业信息】没有对应的员工");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        company.setEmployeeName(dbEmployee.getEmployeeName());
        CompanyTemp createCompany = new CompanyTemp();
        CompanyTemp companyTemp = new CompanyTemp();
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployeeRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(company, companyTemp, getNullPropertyNames(company));
            companyTemp.setRequestStatus(0);
            companyTemp.setCheckedStatus(1);

            Boolean isSuccess = companyTempService.saveCompanyTemp(companyTemp);
            if (!isSuccess) return ResultVOUtil.fail(ResultEnum.MANAGER_UPDATE_COMPANY_INFO_ERROR, response);

            Company returnCompany = companyService.createCompany(company);
            Map<String, Object> map = new HashMap<>();
            if (returnCompany != null) {
                map.put("employeeRole", 2);
                map.put("company", returnCompany);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.fail(ResultEnum.UPDATE_COMPANY_INFO_ERROR, response);
            }
        }

        BeanUtils.copyProperties(company, companyTemp, getNullPropertyNames(company));
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        companyTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        companyTemp.setRequestStatus(0);
        try {
            // 找到数据库中未批准的记录
            CompanyTemp databaseCompanyTemp = companyTempService.findCompanyTempByCompanyIdAndCheckedStatus(companyTemp.getCompanyId(), 0);
            if (databaseCompanyTemp != null) {
                // 如果数据库在有：覆盖
                BeanUtils.copyProperties(companyTemp, databaseCompanyTemp, getNullPropertyNames(companyTemp));
                createCompany = companyTempService.createCompanyTemp(databaseCompanyTemp);
            } else {
                // 在数据库中新建一个记录
                createCompany = companyTempService.createCompanyTemp(companyTemp);

            }
            if (createCompany == null) {
                log.error("【修改企业信息】创建临时表发生错误");
                return ResultVOUtil.fail(ResultEnum.UPDATE_COMPANY_INFO_ERROR, response);
            }
        } catch (Exception e) {
            log.error("【修改企业信息】创建临时表发生异常");
            return ResultVOUtil.fail(ResultEnum.UPDATE_COMPANY_INFO_EXCEPTION, response);
        }
        return ResultVOUtil.success(ResultEnum.UPDATE_COMPANY_SUCCESS);
    }

    /**
     * 删除企业信息(需要经过老板审批)
     *
     * @param paramMap: companyId
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/deleteCompany")
    public ResultVO<Map<String, String>> deleteCompany(@RequestBody HashMap paramMap,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        Integer companyId = Integer.parseInt(paramMap.get("companyId").toString());
        Company company = companyService.getCompanyByCompanyId(companyId);

        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除企业信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【删除企业信息】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        CompanyTemp createCompanyTmp = new CompanyTemp();
        CompanyTemp companyTemp = new CompanyTemp();
        //如果是老板则直接操作，不需要审批,但是需要记录操作?
        if (employee.getEmployeeRole() == 2) {
            //管理员直接同意修改，并写一条记录存到temp表中
            BeanUtils.copyProperties(company, companyTemp, getNullPropertyNames(company));
            companyTemp.setRequestStatus(1);
            companyTemp.setCheckedStatus(1);
            Boolean isSuccess = companyTempService.saveCompanyTemp(companyTemp);
            if (!isSuccess) return ResultVOUtil.fail(ResultEnum.MANAGER_DELETE_COMPANY_INFO_ERROR, response);

            Integer flag = companyService.deleteCompanyByCompanyId(company.companyId);
            Map<String, Integer> map = new HashMap<>();
            if (flag != 0) {
                map.put("employeeRole", 2);
                return ResultVOUtil.success(map);
            } else {
                return ResultVOUtil.fail(ResultEnum.DELETE_COMPANY_INFO_ERROR, response);
            }
        }

        BeanUtils.copyProperties(company, companyTemp, getNullPropertyNames(company));
        // 前端提交到数据库时，需要设置checkedStatus、 0表示未审批 1表示审批 2表示同意 3 表示不同意
        companyTemp.setCheckedStatus(0);
        // 请求内容 0: 改, 1:删
        companyTemp.setRequestStatus(1);
        try {
            // 先在数据库中找是否有未审批申请
            CompanyTemp databaseCompanyTemp = companyTempService.findCompanyTempByCompanyIdAndCheckedStatus(companyTemp.getCompanyId(), 0);
            if (databaseCompanyTemp != null) {
                // 如果有：直接覆盖
                BeanUtils.copyProperties(companyTemp, databaseCompanyTemp, getNullPropertyNames(companyTemp));
                createCompanyTmp = companyTempService.createCompanyTemp(databaseCompanyTemp);
            } else {
                // 否则新建一条记录
                createCompanyTmp = companyTempService.createCompanyTemp(companyTemp);
            }
            if (createCompanyTmp == null) {
                log.error("【删除企业信息】创建临时表发生错误");
                return ResultVOUtil.fail(ResultEnum.DELETE_COMPANY_INFO_ERROR, response);
            }
        } catch (Exception e) {
            log.error("【删除企业信息】创建临时表发生异常");
            return ResultVOUtil.fail(ResultEnum.DELETE_COMPANY_INFO_EXCEPTION, response);
        }
        return ResultVOUtil.success(ResultEnum.DELETE_COMPANY_SUCCESS);
    }

    /**
     * 创建企业客户
     *
     * @param company
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/createCompany")
    public ResultVO<Map<String, String>> createCompany(@RequestBody Company company,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公司资源信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公司资源信息】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee creatorEmployee = employeeService.getEmployeeByEmployeeId(company.getEmployeeId());
        if (creatorEmployee == null) {
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        company.setEmployeeName(creatorEmployee.getEmployeeName());
        // 员工创建的公司默认为私有(1 公有  2 私有)
        company.setShareStatus(2);
        Company company1 = companyService.createCompany(company);
        if (company1 == null) {
            return ResultVOUtil.fail(ResultEnum.CREATE_COMPANY_ERROR, response);
        }
        return ResultVOUtil.success(company1);
    }


    /**
     * 修改企业状态（公有、私有）
     *
     * @return
     */
    @PostMapping("/updateCompanyShareStatus")
    public ResultVO<Map<String, String>> updateCompanyShareStatus(@RequestBody HashMap paramMap,
                                                                  HttpServletRequest request,
                                                                  HttpServletResponse response) {
        Integer companyId = Integer.parseInt(paramMap.get("companyId").toString());
        String shareStatus = paramMap.get("shareStatus").toString();

        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【更新企业状态】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【更新企业状态】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        // 更新共享状态
        Boolean flag = companyService.updateShareStatusByCompanyId(shareStatus, companyId);

        Company company = new Company();
        // 如果变为公有，对应employeeId则设为大boss
        if (shareStatus.equals("1")) {
            company = companyService.getCompanyByCompanyId(companyId);
            company.setEmployeeId(EnumUtil.ROOT_ID);
            company.setEmployeeName(EnumUtil.ROOT_NAME);
        } else {
            // 如果变为私有，对应employeeId则设为自己
            company = companyService.getCompanyByCompanyId(companyId);
            company.setEmployeeId(employeeId);
            company.setEmployeeName(employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeName());
        }
        Boolean isSuccess = companyService.saveCompany(company);
        if (!isSuccess) {
            log.error("【更新企业状态】保存失败");
            return ResultVOUtil.fail(ResultEnum.SAVE_COMPANY_ERROR, response);
        }
        if (flag) {
            return ResultVOUtil.success(ResultEnum.UPDATE_COMPANY_SUCCESS);
        } else {
            log.error("【更新企业状态】发生错误");
            return ResultVOUtil.fail(ResultEnum.UPDATE_COMPANY_ERROR, response);
        }
    }


    /**
     * 分页显示企业客户信息
     *
     * @return
     */
    @PostMapping("/getCompanyList")
    public ResultVO<Map<String, String>> getCompanyList(@RequestBody HashMap map,
                                                        HttpServletRequest req,
                                                        HttpServletResponse response) {
        Integer shareStatus = Integer.parseInt(map.get("shareStatus").toString());
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("pageSize").toString());

        String token = TokenUtil.parseToken(req);

        if (token.equals("")) {
            log.error("【获取公司列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取公司列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");
        Page<Company> companyPage = null;

        // 管理员能看到所有客户(所有公有、所有私有)
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() == 2) {
            companyPage = companyService.findCompanyByShareStatusPageable(shareStatus, request);
        } else {
            // 普通员工：只能取自己私有的或者 公共的
            // 1 公有  2 私有
            if (shareStatus == 1) {
                companyPage = companyService.findCompanyByShareStatusPageable(shareStatus, request);
            } else {
                companyPage = companyService.findCompanyByEmployeeId(employeeId, request);
            }
        }

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
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("pageSize").toString());

        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");


        Page<CompanyFollowRecord> companyFollowRecordPage = companyFollowRecordService.getCompanyFollowRecordByCompanyId(companyId, request);
        if (companyFollowRecordPage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.COMPANY_FOLLOW_RECORD_EMPTY);
        } else {
            return ResultVOUtil.success(companyFollowRecordPage.getContent());
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
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公司跟进信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公司跟进信息】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.COMPANY_NOT_EXIST, response);
        }
        companyFollowRecord.setEmployeeId(employeeId);
        companyFollowRecord.setEmployeeName(employee.getEmployeeName());
        // 修改对应公司的客户状态
        Company company = companyService.getCompanyByCompanyId(companyFollowRecord.getCompanyId());
        if (company == null) {
            log.error("【创建公司跟进信息】找不到对应的公司");
            return ResultVOUtil.fail(ResultEnum.COMPANY_NOT_EXIST, response);
        }
        company.setStatus(companyFollowRecord.getStatus());
        // 写回数据库
        Boolean flag = companyService.saveCompany(company);
        if (!flag) {
            log.error("【创建公司跟进信息】保存公司信息失败");
            return ResultVOUtil.fail(ResultEnum.SAVE_COMPANY_ERROR, response);
        }
        CompanyFollowRecord createFollowRecord = companyFollowRecordService.createCompanyFollowRecord(companyFollowRecord);
        if (createFollowRecord != null) {
            return ResultVOUtil.success(createFollowRecord);
        } else {
            log.error("【创建公司跟进信息】失败");
            return ResultVOUtil.fail(ResultEnum.CREATE_COMPANY_FOLLOW_RECORD_ERROR, response);
        }
    }

    /**
     * 获取公司姓名及ID
     *
     * @param req
     * @return
     */
    @GetMapping("/getCompanyNames")
    public ResultVO<Map<String, String>> getCompanyNames(HttpServletRequest req,
                                                         HttpServletResponse response) {

        String token = TokenUtil.parseToken(req);

        System.out.println("token is:" + token);
        if (token.equals("")) {
            log.error("【获取公司列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        System.out.println("id:" + employeeId);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取公司列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        List<Company> privateCompanyList = new ArrayList<>();
        // 管理员能看到所有客户
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() == 2) {
            privateCompanyList = companyService.findAllCompany();
        } else {
            privateCompanyList = companyService.getCompanyByEmployeeId(employeeId);
            //1表示共有 2表示私有
            List<Company> publicCompanyList = companyService.findCompanyByShareStatus(1);
            for (Company company : publicCompanyList)
                privateCompanyList.add(company);
        }
        //封装成只要ID ResourceName lambda表达式 java 11
//        Map<String, Object> companyMap = new HashMap<>();
        List<Object> companyList = new ArrayList<>();
        for (Company company : privateCompanyList) {
            Map<String, Object> map = new HashMap<>();
            map.put("companyId", company.getCompanyId());
            map.put("companyName", company.getCompanyName());
            companyList.add(map);
        }
        return ResultVOUtil.success(companyList);
    }


    @PostMapping("/test")
    public String test() {
        return "hello world";
    }


}
