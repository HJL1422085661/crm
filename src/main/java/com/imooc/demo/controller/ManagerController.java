package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;

import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.exception.CrmException;
import com.imooc.demo.form.EmployeeForm;
import com.imooc.demo.modle.*;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.BeanCopyUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imooc.demo.utils.BeanCopyUtil.getNullPropertyNames;

/**
 * @Author emperor
 * @Date 2019/11/18 14:29
 * @Version 1.0
 */

@RestController
@RequestMapping("/manager")
@Slf4j
public class ManagerController {

    @Autowired
    public ManagerService managerService;
    @Autowired
    public EmployeeService employeeService;
    @Autowired
    public ResourceService resourceService;
    @Autowired
    public ResourceTempService resourceTempService;
    @Autowired
    public CompanyService companyService;
    @Autowired
    public CompanyTempService companyTempService;
    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    public PayBackRecordService payBackRecordService;

    /**
     * manager注册employer, 注册成功返回ticket
     *
     * @param employeeForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/register")
    public ResultVO<Map<String, String>> register(@Valid EmployeeForm employeeForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("【】参数不正确， employeeForm={}", employeeForm);
            throw new CrmException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        if (employeeForm.getPassWord().length() < 6) {
            log.error("【注册用户】用户密码长度小于6位");
            return ResultVOUtil.error(ResultEnum.PASSWORD_LENGTH_SHORT);
        }
        Employee employee = managerService.getManagerByEmployeeId(employeeForm.getEmployeeId());
        if (employee != null) {
            log.error("【注册用户】用户ID已存在");
            return ResultVOUtil.error(ResultEnum.USER_ID_EXIST);
        }
        Employee createEmployee = new Employee();
        BeanUtils.copyProperties(employeeForm, createEmployee, getNullPropertyNames(employeeForm));
        try {
            employee = managerService.register(createEmployee);
        } catch (Exception e) {
            log.error("【注册用户】注册发生异常");
            return ResultVOUtil.error(ResultEnum.REGISTER_EXCEPTION);
        }

        return ResultVOUtil.success(employee);
    }


    /**
     * 修改员工权限和所属经理（重要信息，管理员才可以操作）
     *
     * @param paramMap
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/updateEmployeeRoleAndManager")
    public ResultVO<Map<String, String>> updateEmployeeRole(@RequestBody HashMap paramMap,
                                                            HttpServletRequest request) {
        // 验证信息
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改员工权限】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String databseEmployeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(databseEmployeeId)) {
            log.error("【修改员工权限】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee databaseEmployee = employeeService.getEmployeeByEmployeeId(databseEmployeeId);
        if (databaseEmployee.getEmployeeRole() != 2) {
            log.error("【修改员工权限】普通员工无权限访问所有员工");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }

        // 解析参数
        String employeeId = paramMap.get("employeeId").toString();
        Integer employeeRole = Integer.parseInt(paramMap.get("employeeRole").toString());
        String employeeManagerId = paramMap.get("employeeManagerId").toString();
        String employeeManagerName = paramMap.get("employeeManagerName").toString();

        // 从数据库取出待修改员工
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (employee == null) {
            log.error("【修改员工权限】该员工不存在");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        try {
            // 更新员工角色和所属经理
            employee.setEmployeeRole(employeeRole);
            employee.setEmployeeManagerId(employeeManagerId);
            employee.setEmployeeManagerName(employeeManagerName);
            // 写回数据库
            Boolean flag = employeeService.saveEmployee(employee);
            if (!flag) {
                log.error("【修改员工权限】发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_ROLE_ERROR);
            }
        } catch (Exception e) {
            log.error("【修改员工权限】修改员工发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_EXCEPTION);
        }
        return ResultVOUtil.success();
    }

    /**
     * 修改员工信息（一般信息，自己可以修改）
     *
     * @param employee
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/updateEmployee")
    public ResultVO<Map<String, String>> updateEmployee(@RequestBody Employee employee,
                                                        HttpServletRequest request) {
        // 验证信息
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改员工信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String databseEmployeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(databseEmployeeId)) {
            log.error("【修改员工信息】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        // 从数据库取出待修改员工
        Employee databaseEmployee = employeeService.getEmployeeByEmployeeId(databseEmployeeId);
        if (databaseEmployee == null) {
            log.error("【修改员工信息】该员工不存在");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }

        try {
            // 更新员工信息
            BeanUtils.copyProperties(employee, databaseEmployee, BeanCopyUtil.getNullPropertyNames(employee));
            // 写回数据库
            Boolean flag = employeeService.saveEmployee(employee);
            if (!flag) {
                log.error("【修改员工信息】发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_ROLE_ERROR);
            }
        } catch (Exception e) {
            log.error("【修改员工信息】修改员工发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_EXCEPTION);
        }
        return ResultVOUtil.success();
    }

    /**
     * 删除员工
     *
     * @param
     * @return
     */
//    @Modifying
//    @Transactional
//    @PostMapping("/deleteEmployee")
//    public ResultVO<Map<String, String>> deleteEmployee(@RequestParam("employeeId") String employeeId) {
//
//        //Todo
//        /*先查询该用户是否存在进行的订单，如果该用户存在进行中的订单，则需要转让该订单给其他人
//            该用户的客户资源需要变为公有
//         */
//        List<Business> businessList = businessService.getBusinessByEmployeeId(employeeId);
//        boolean flag = false;
//        //查询与该用户有关的订单是否存在ing状态的
//        for (Business business : businessList) {
//            if (business.getBusinessStatus() == 0) {
//                flag = true;
//                break;
//            }
//        }
//        //如果该用户存在正在进行的订单,则不能删除
//        if (flag) {
//            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_DELETE);
//        }
//        //获取该用户的人才库，修改人才状态为公共public
//        List<Resource> resourceList = resourceService.getResourceByEmployeeId(employeeId);
//        for (Resource resource : resourceList) {
//            //公有人才的员工ID设为老板的ID（老板的ID固定为0）
//            Boolean tag = resourceService.updateShareStatusAndEmployeeIdByResourceId("public", "0", resource.getResourceId());
//            if (!tag) {
//                log.error("【设置人才到公有库】发生错误");
//                return ResultVOUtil.error(ResultEnum.SET_RESOURCE_PUBLIC_ERROR);
//            }
//        }
//        try {
//            employeeService.deleteEmployee(employeeId);
//        } catch (Exception e) {
//            log.error("【删除员工】发生异常" + e.getMessage());
//            return ResultVOUtil.error(ResultEnum.DELETE_EMPLOYEE_EXCEPTION);
//        }
//
//        return ResultVOUtil.success();
//    }

    // 创建共享人才库
    @PostMapping("/createPublicEmployee")
    public ResultVO<Map<String, String>> createPublicResource(@RequestBody Resource resource,
                                                              HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公有人才信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公有人才信息】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 只有管理员才可以创建公有人才
        if (employee.getEmployeeRole() != 2) {
            return ResultVOUtil.error(ResultEnum.USER_IDENTIFY_ERROR);
        }

        //设置人才状态 1:私有，2：公有
        resource.setShareStatus(2);
        Resource returnResource = resourceService.createResource(resource);
        Map<String, Object> map = new HashMap<>();
        if (returnResource != null) {
            map.put("resource", returnResource);
            map.put("employeeRole", 2);
            return ResultVOUtil.success(map);
        } else {
            log.error("【创建公有人才信息】发生错误");
            return ResultVOUtil.error(ResultEnum.CREATE_PUBLIC_RESOURCE_ERROR);
        }
    }

    @GetMapping("/getEmployeeList")
    public ResultVO<Map<String, String>> getEmployeeList(HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【获取员工列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取员工列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (employee.getEmployeeRole() != 2) {
            log.error("【获取员工列表】普通员工无权限访问所有员工");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        // 取得所有员工
        List<Employee> employeeList = employeeService.findAllEmployee();
        List<Map<String, String>> employeeListTemp = new ArrayList<>();
        for (Employee employeeIdx : employeeList) {
            Map<String, String> employeeTemp = new HashMap<>();
            employeeTemp.put("employeeId", employeeIdx.getEmployeeId());
            employeeTemp.put("employeeName", employeeIdx.getEmployeeName());
            employeeListTemp.add(employeeTemp);
        }
        return ResultVOUtil.success(employeeListTemp);
    }


    /**
     * 修改共享人才库
     *
     * @param resource
     * @param request
     * @return
     */
    @PostMapping("/updatePublicEmployee")
    public ResultVO<Map<String, String>> updatePublicResource(@RequestBody Resource resource,
                                                              HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改公有人才信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【修改公有人才信息】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 管理员才可以修改公有人才
        if (employee.getEmployeeRole() != 2) {
            return ResultVOUtil.error(ResultEnum.USER_IDENTIFY_ERROR);
        }
        //首先获取数据库中的人才对象
        ResourceTemp resourceTemp = new ResourceTemp();
        //管理员直接同意修改，并写一条记录存到temp表中
        BeanUtils.copyProperties(resource, resourceTemp, getNullPropertyNames(resource));
        resourceTemp.setRequestStatus(0);
        //老板的话直接设置同意
        resourceTemp.setCheckedStatus(1);
        Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
        if (!isSuccess) return ResultVOUtil.error(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR);

        // 将修改后的人才信息存入数据库
        Resource returnResource = resourceService.createResource(resource);
        Map<String, Object> map = new HashMap<>();
        if (returnResource != null) {
            map.put("resource", returnResource);
            map.put("employeeRole", 2);
            return ResultVOUtil.success(map);
        } else {
            log.error("【修改公有人才信息】发生错误");
            return ResultVOUtil.error(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR);
        }

    }

    /**
     * 删除共享人才库
     *
     * @param paramMap
     * @param request
     * @return
     */
    @PostMapping("/deletePublicResource")
    public ResultVO<Map<String, String>> deletePublicResource(@RequestBody HashMap paramMap,
                                                              HttpServletRequest request) {
        Integer publicResourceId = Integer.parseInt(paramMap.get("resourceId").toString());

        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除公有人才信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【删除公有人才信息】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 管理员才可以删除公有人才
        if (employee.getEmployeeRole() != 2) {
            return ResultVOUtil.error(ResultEnum.USER_IDENTIFY_ERROR);
        }

        //首先获取数据库中的人才对象
        Resource publicResource = resourceService.getResourceByResourceId(publicResourceId);
        ResourceTemp resourceTemp = new ResourceTemp();
        //管理员直接同意修改，并写一条记录存到temp表中
        BeanUtils.copyProperties(publicResource, resourceTemp, getNullPropertyNames(publicResource));
        resourceTemp.setRequestStatus(1);
        //老板的话直接设置同意
        resourceTemp.setCheckedStatus(1);
        Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
        if (!isSuccess) return ResultVOUtil.error(ResultEnum.MANAGER_DELETE_COMPANY_INFO_ERROR);

        // 删除公有人才
        Integer flag = resourceService.deleteResourceByResourceId(publicResourceId);
        Map<String, Object> map = new HashMap<>();
        if (flag != 0) {
            map.put("employeeRole", 2);
            return ResultVOUtil.success(map);
        } else {
            log.error("【删除公有人才信息】发生错误");
            return ResultVOUtil.error(ResultEnum.MANAGER_DELETE_COMPANY_INFO_ERROR);
        }

    }

    /**
     * 管理员获取人才修改|删除代办事项
     *
     * @param map 审批状态 0: 未审批,2：同意 3:不同意
     * @param req
     * @return
     */
    @PostMapping("/getResourceCheckList")
    public ResultVO<Map<String, String>> getResourceCheckList(@RequestBody HashMap map,
                                                              HttpServletRequest req) {
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());
        Integer requestStatus = Integer.parseInt(map.get("requestStatus").toString());
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("pageSize").toString());
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取人才资源审批|未审批列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【获取人才资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");
        Page<ResourceTemp> resourceTempPage = resourceTempService.findResourceTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);

        if (resourceTempPage.getContent().isEmpty()) {
            if (page > 1) {
                request = PageRequest.of(page - 2, size, Sort.Direction.DESC, "createDate");
                resourceTempPage = resourceTempService.findResourceTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);
                return ResultVOUtil.success(resourceTempPage);
            } else return ResultVOUtil.success(ResultEnum.RESOURCE_TEMP_LIST_EMPTY);
        } else {
            System.out.println(resourceTempPage.getContent());
            return ResultVOUtil.success(resourceTempPage);
        }

    }


    /**
     * 管理员获取人才修改|删除代办事项
     *
     * @param map 审批状态 0: 未审批,1：同意 2:不同意
     * @param req
     * @return
     */
    @PostMapping("/getCompanyCheckList")
    public ResultVO<Map<String, String>> getCompanyCheckList(@RequestBody HashMap map,
                                                             HttpServletRequest req) {
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());
        Integer requestStatus = Integer.parseInt(map.get("requestStatus").toString());
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("pageSize").toString());
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取人才资源审批|未审批列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【获取人才资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "startDate");
        Page<CompanyTemp> companyTempPage = companyTempService.findCompanyTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);

        if (companyTempPage.getContent().isEmpty()) {
            if (page > 1) {
                request = PageRequest.of(page - 2, size, Sort.Direction.DESC, "startDate");
                companyTempPage = companyTempService.findCompanyTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);
                return ResultVOUtil.success(companyTempPage);
            } else
                return ResultVOUtil.success(ResultEnum.RESOURCE_TEMP_LIST_EMPTY);
        } else {
            System.out.println(companyTempPage.getContent());
            return ResultVOUtil.success(companyTempPage);
        }

    }


    /** 待办事项 ·审批·部分程序 **/

    /**
     * 管理员审批人才修改|删除代办事项
     *
     * @param map 审批状态 0: 未审批, 1：同意 2:不同意; 人才改删临时表ID
     * @param req
     * @return
     */
    @PostMapping("/checkResourceCheckList")
    public ResultVO<Map<String, String>> checkResourceCheckList(@RequestBody HashMap map, HttpServletRequest req) {
        Integer id = Integer.parseInt(map.get("id").toString());
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员审批人才资源审批|未审批列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员审批人才资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【管理员审批人才资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        ResourceTemp resourceTemp = resourceTempService.findResourceTempById(id);
        // 更新相应操作状态
        resourceTemp.setCheckedStatus(checkedStatus);

        // 审批完成后，将临时表写回数据库
        Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
        if (!isSuccess) return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_TEMP_ERROR);

        Resource resource = new Resource();
        BeanUtils.copyProperties(resourceTemp, resource);
        // 审批状态 0: 未审批,  1：同意 2: 不同意
        if (checkedStatus == 1) {
            // 同意
            if (resourceTemp.requestStatus == 0) {
                // 改
                Boolean flag = resourceService.saveResource(resource);
                if (flag) return ResultVOUtil.success(ResultEnum.UPDATE_RESOURCE_SUCCESS);
                else return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
            } else if (resourceTemp.requestStatus == 1) {
                // 删
                Integer flag = resourceService.deleteResourceByResourceId(resource.getResourceId());
                if (flag != 0) return ResultVOUtil.success(ResultEnum.DELETE_RESOURCE_SUCCESS);
                else return ResultVOUtil.error(ResultEnum.DELETE_RESOURCE_ERROR);
            }
            return ResultVOUtil.success(ResultEnum.PARAM_ERROR);
        } else if (checkedStatus == 2) {
            // 不同意
            if (resourceTemp.requestStatus == 0) return ResultVOUtil.success(ResultEnum.REJECT_UPDATE_SUCCESS);
            if (resourceTemp.requestStatus == 1) return ResultVOUtil.success(ResultEnum.REJECT_DELETE_SUCCESS);
            return ResultVOUtil.success(ResultEnum.PARAM_ERROR);
        } else {
            return ResultVOUtil.success(ResultEnum.PARAM_ERROR);
        }
    }

    /**
     * 管理员审批公司修改|删除代办事项
     *
     * @param map 审批状态 0: 未审批,1：同意 2:不同意; 公司改删临时表ID
     * @param req
     * @return
     */
    @PostMapping("/checkCompanyCheckList")
    public ResultVO<Map<String, String>> checkCompanyCheckList(@RequestBody HashMap map,
                                                               HttpServletRequest req) {
        Integer id = Integer.parseInt(map.get("id").toString());
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员审批公司资源审批|未审批列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员审批公司资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【管理员审批公司资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        CompanyTemp companyTemp = companyTempService.findCompanyTempById(id);
        // 更新相应操作状态
        companyTemp.setCheckedStatus(checkedStatus);

        // 审批完成后，将临时表写回数据库
        Boolean isSuccess = companyTempService.saveCompanyTemp(companyTemp);
        if (!isSuccess) {
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_TEMP_ERROR);
        }

        Company company = new Company();
        BeanUtils.copyProperties(companyTemp, company);
        // 审批状态 0: 未审批,  1：同意 2: 不同意
        if (checkedStatus == 1) {
            // 同意
            if (companyTemp.requestStatus == 0) {
                // 改
                Boolean flag = companyService.saveCompany(company);
                if (flag) {
                    return ResultVOUtil.success(ResultEnum.UPDATE_COMPANY_SUCCESS);
                } else {
                    return ResultVOUtil.error(ResultEnum.UPDATE_COMPANY_ERROR);
                }
            } else if (companyTemp.requestStatus == 1) {
                // 删
                Integer flag = companyService.deleteCompanyByCompanyId(company.getCompanyId());
                if (flag != 0) {
                    return ResultVOUtil.success(ResultEnum.DELETE_COMPANY_SUCCESS);
                } else {
                    return ResultVOUtil.error(ResultEnum.DELETE_COMPANY_ERROR);
                }

            }
            return ResultVOUtil.success(ResultEnum.PARAM_ERROR);
        } else if (checkedStatus == 2) {
            // 不同意
            if (companyTemp.requestStatus == 0) return ResultVOUtil.success(ResultEnum.REJECT_UPDATE_SUCCESS);
            if (companyTemp.requestStatus == 1) return ResultVOUtil.success(ResultEnum.REJECT_DELETE_SUCCESS);
            return ResultVOUtil.success(ResultEnum.PARAM_ERROR);
        } else {
            return ResultVOUtil.success(ResultEnum.PARAM_ERROR);
        }
    }

//
//    /**
//     * 管理员审批回款代办事项
//     * 修改表中对应项：isChecked 是否已确认（ 0: 未确认, 1:已确认 ）
//     * TODO: isChecked是否还有其他状态？？？
//     *
//     * @param recordId 回款记录表ID
//     * @param req
//     * @return
//     */
//    @PostMapping("/checkPayBackList")
//    public ResultVO<Map<String, String>> checkPayBackList(@RequestBody int recordId,
//                                                          @RequestBody int checkedStatus,
//                                                          HttpServletRequest req) {
//        String token = TokenUtil.parseToken(req);
//        if (token.equals("")) {
//            log.error("【管理员审批回款代办事项】Token为空");
//            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
//        }
//        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
//        if (StringUtils.isEmpty(employeeId)) {
//            log.error("【管理员审批回款代办事项】 employeeId为空");
//            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
//        }
//        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployRole() != 2) {
//            log.error("【管理员审批回款代办事项】普通员工无权限");
//            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
//        }
//        PayBackRecord payBackRecord = payBackRecordService.getPayBackRecordByRecordId(recordId);
//        // 更新回款操作状态
//        payBackRecord.setIsChecked(checkedStatus);
//        // 写回数据库
//        Boolean flag = payBackRecordService.savePayBackRecord(payBackRecord);
//        if (flag) {
//            return ResultVOUtil.success();
//        } else {
//            return ResultVOUtil.error(ResultEnum.UPDATE_PAY_BACK_RECORD_ERROR);
//        }
//    }


}
