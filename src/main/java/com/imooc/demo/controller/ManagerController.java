package com.imooc.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.imooc.demo.VO.ResultVO;

import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.exception.CrmException;
import com.imooc.demo.form.EmployeeForm;
import com.imooc.demo.model.*;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.BeanCopyUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.imooc.demo.utils.BeanCopyUtil.getNullPropertyNames;


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
    @Autowired
    public CompanyBusinessService companyBusinessService;
    @Autowired
    public ResourceBusinessService resourceBusinessService;
    @Autowired
    public EmployeeSalaryRegulationService employeeSalaryRegulationService;

    /**
     * manager注册employer, 注册成功返回ticket
     * 创建新员工（管理员才可以操作）
     *
     * @param employeeForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/createEmployee")
    public ResultVO<Map<String, String>> createEmployee(@Valid @RequestBody EmployeeForm employeeForm, BindingResult bindingResult,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        // 验证信息
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建新员工】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String databaseEmployeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(databaseEmployeeId)) {
            log.error("【创建新员工】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee databaseEmployee = employeeService.getEmployeeByEmployeeId(databaseEmployeeId);
        if (databaseEmployee.getEmployeeRole() != 2) {
            log.error("【创建新员工】普通员工无权创建新员工");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }

        if (bindingResult.hasErrors()) {
            log.error("【参数不正确】 employeeForm={}", employeeForm);
            throw new CrmException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        if (employeeForm.getPassWord().length() < 6) {
            log.error("【注册用户】用户密码长度小于6位");
            return ResultVOUtil.fail(ResultEnum.PASSWORD_LENGTH_SHORT, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeForm.getEmployeeId());
        if (employee != null) {
            log.error("【注册用户】用户ID已存在");
            return ResultVOUtil.fail(ResultEnum.USER_ID_EXIST, response);
        }
        Boolean phoneExist = employeeService.existsByPhoneNumber(employeeForm.getPhoneNumber());
        if (phoneExist) {
            log.error("【注册用户】电话号码已存在");
            return ResultVOUtil.fail(ResultEnum.USER_PHONE_EXIST, response);
        }

        Employee createEmployee = new Employee();
        BeanUtils.copyProperties(employeeForm, createEmployee, getNullPropertyNames(employeeForm));
        // 创建的是管理员，则不封装所属经理名称参数
        if (employeeForm.getEmployeeRole() == 1) {
            // 封装所属经理名称
            Employee manager = employeeService.getEmployeeByEmployeeId(employeeForm.getEmployeeManagerId());
            if (manager == null) {
                log.error("【创建新员工】manager不存在");
                return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
            }
            createEmployee.setEmployeeManagerName(manager.getEmployeeName());
        } else {
            createEmployee.setEmployeeManagerId("");
            createEmployee.setEmployeeManagerName("");
        }
        try {
            employee = managerService.register(createEmployee);
        } catch (Exception e) {
            log.error("【注册用户】注册发生异常");
            return ResultVOUtil.fail(ResultEnum.REGISTER_EXCEPTION, response);
        }

        return ResultVOUtil.success(employee);
    }


    /**
     * 修改员工信息（重要信息，管理员才可以操作）
     *
     * @param paramMap
     * @return
     */
    @PostMapping("/updateEmployee")
    public ResultVO<Map<String, String>> updateEmployee(@RequestBody Employee paramMap,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        // 验证信息
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改员工权限】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String databaseEmployeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(databaseEmployeeId)) {
            log.error("【修改员工权限】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee databaseEmployee = employeeService.getEmployeeByEmployeeId(databaseEmployeeId);
        if (databaseEmployee.getEmployeeRole() != 2) {
            log.error("【修改员工权限】普通员工无权限访问所有员工");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }

        // 解析参数
        String employeeId = paramMap.getEmployeeId();
        String employeeManagerId = paramMap.getEmployeeManagerId();

        // 从数据库取出待修改员工
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (employee == null) {
            log.error("【修改员工权限】该员工不存在");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        try {
            // 更新员工角色和所属经理
            BeanUtils.copyProperties(paramMap, employee, BeanCopyUtil.getNullPropertyNames(paramMap));
            Employee manager = employeeService.getEmployeeByEmployeeId(employeeManagerId);
            if (manager == null) {
                log.error("【修改员工权限】该员工不存在");
                return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
            }
            employee.setEmployeeManagerName(manager.getEmployeeName());
            // 改为经理后，所属经理置空
            if (employee.getEmployeeRole() == 2) {
                employee.setEmployeeManagerId("");
                employee.setEmployeeManagerName("");
            }
            // 写回数据库
            Boolean flag = employeeService.saveEmployee(employee);
            if (!flag) {
                log.error("【修改员工权限】发生错误");

                return ResultVOUtil.fail(ResultEnum.UPDATE_EMPLOYEE_ROLE_ERROR, response);
            }
        } catch (Exception e) {
            log.error("【修改员工权限】修改员工发生异常");
            return ResultVOUtil.fail(ResultEnum.UPDATE_EMPLOYEE_EXCEPTION, response);
        }
        return ResultVOUtil.success();
    }

    /**
     * 获取个人信息(外部接口API)
     *
     * @param request
     * @return
     */
    @PostMapping("/getEmployeeDetail")
    public ResultVO<Map<String, String>> getEmployeeDetail(@RequestBody HashMap paramMap,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {
        String queryEmployeeId = paramMap.get("employeeId").toString();

        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【获取个人信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取个人信息】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        Employee employee = employeeService.getEmployeeByEmployeeId(queryEmployeeId);
        if (employee == null) {
            log.error("【获取个人信息】该员工不存在");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Map<String, String> map = new HashMap<>();
        map.put("employeeId", employee.getEmployeeId());
        map.put("employeeRole", employee.getEmployeeRole().toString());
        map.put("employeeName", employee.getEmployeeName());
        map.put("employeePhone", employee.getPhoneNumber());
        map.put("employeeEmail", employee.getEmail());
        map.put("supEmployeeName", employee.getEmployeeManagerName());
        map.put("supEmployeeId", employee.getEmployeeManagerId());

        return ResultVOUtil.success(map);
    }
//
//    /**
//     * 修改员工信息（一般信息，自己可以修改）
//     *
//     * @param employee
//     * @return
//     */
//    @PostMapping("/updateEmployee")
//    public ResultVO<Map<String, String>> updateEmployee(@RequestBody Employee employee,
//                                                        HttpServletRequest request) {
//        // 验证信息
//        String token = TokenUtil.parseToken(request);
//        if (token.equals("")) {
//            log.error("【修改员工信息】Token为空");
//            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
//        }
//        String databaseEmployeeId = loginTicketService.getEmployeeIdByTicket(token);
//        if (StringUtils.isEmpty(databaseEmployeeId)) {
//            log.error("【修改员工信息】employeeId为空");
//            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
//        }
//        // 从数据库取出待修改员工
//        Employee databaseEmployee = employeeService.getEmployeeByEmployeeId(databaseEmployeeId);
//        if (databaseEmployee == null) {
//            log.error("【修改员工信息】该员工不存在");
//            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
//        }
//
//        try {
//            // 更新员工信息
//            BeanUtils.copyProperties(employee, databaseEmployee, BeanCopyUtil.getNullPropertyNames(employee));
//            // 写回数据库
//            Boolean flag = employeeService.saveEmployee(employee);
//            if (!flag) {
//                log.error("【修改员工信息】发生错误");
//                return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_ROLE_ERROR);
//            }
//        } catch (Exception e) {
//            log.error("【修改员工信息】修改员工发生异常");
//            return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_EXCEPTION);
//        }
//        return ResultVOUtil.success();
//    }

    /**
     * 删除员工（管理员才可以操作）
     *
     * @param
     * @return
     */
    @PostMapping("/deleteEmployee")
    public ResultVO<Map<String, String>> deleteEmployee(@RequestBody HashMap paramMap,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        // 验证信息
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除员工】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String databaseEmployeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(databaseEmployeeId)) {
            log.error("【删除员工】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        // 从数据库取出待修改员工
        Employee databaseEmployee = employeeService.getEmployeeByEmployeeId(databaseEmployeeId);
        if (databaseEmployee == null) {
            log.error("【删除员工】该员工不存在");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        // 解析参数
        String employeeId = paramMap.get("employeeId").toString();

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
//            Integer deleltNum = employeeService.deleteEmployeeByEmployeeId(employeeId);
//        } catch (Exception e) {
//            log.error("【删除员工】发生异常" + e.getMessage());
//            return ResultVOUtil.error(ResultEnum.DELETE_EMPLOYEE_EXCEPTION);
//        }

        return ResultVOUtil.success();
    }

    // 创建共享人才库
    @PostMapping("/createPublicEmployee")
    public ResultVO<Map<String, String>> createPublicResource(@RequestBody Resource resource,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建公有人才信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建公有人才信息】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 只有管理员才可以创建公有人才
        if (employee.getEmployeeRole() != 2) {
            return ResultVOUtil.fail(ResultEnum.USER_IDENTIFY_ERROR, response);
        }

        //设置人才状态 1:私有，2：公有
        resource.setShareStatus(2);
        Resource returnResource;
        try {
            returnResource = resourceService.createResource(resource);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【创建公有人才信息】电话号码重复");
            return ResultVOUtil.fail(ResultEnum.DUPICATE_PHONE, response);
        }
        Map<String, Object> map = new HashMap<>();
        if (returnResource != null) {
            map.put("resource", returnResource);
            map.put("employeeRole", 2);
            return ResultVOUtil.success(map);
        } else {
            response.setStatus(400);
            log.error("【创建公有人才信息】发生错误");
            return ResultVOUtil.fail(ResultEnum.CREATE_PUBLIC_RESOURCE_ERROR, response);
        }
    }

    /**
     * 获得管理员下属员工关系树
     *
     * @param
     * @return
     */
    @GetMapping("/getEmployeeTree")
    public ResultVO<Map<String, String>> getEmployeeTree(HttpServletRequest request,
                                                         HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【获取员工Tree】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取员工Tree】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (employee == null) {
            log.error("【获取员工Tree】 employee为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employee.getEmployeeRole() != 2) {
            log.error("【获取员工Tree】普通员工无权限访问所有员工");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        // 取得所有管理员
        List<Employee> managerList = employeeService.findEmployeeByEmployeeRole(2);
        List<Map<String, Object>> trees = new ArrayList<>();
        // 遍历所有manager
        for (Employee manager : managerList) {
            Map<String, Object> managerBranch = new HashMap<>();
            managerBranch.put("employeeId", manager.getEmployeeId());
            managerBranch.put("employeeName", manager.getEmployeeName());
            // 遍历当前manager所有下属员工
            List<Map<String, String>> team = new ArrayList<>();
            List<Employee> myEmployee = employeeService.findEmployeeByManagerId(manager.getEmployeeId());
            for (Employee employeeTemp : myEmployee) {
                Map<String, String> myEmployeeTemp = new HashMap<>();
                myEmployeeTemp.put("employeeId", employeeTemp.getEmployeeId());
                myEmployeeTemp.put("employeeName", employeeTemp.getEmployeeName());
                team.add(myEmployeeTemp);
            }
            managerBranch.put("team", team);
            trees.add(managerBranch);
        }
        return ResultVOUtil.success(trees);
    }

    /**
     * 获得所有管理员列表
     *
     * @param
     * @return
     */
    @GetMapping("/getManagerEmployeeList")
    public ResultVO<Map<String, String>> getManagerEmployeeList(HttpServletRequest request,
                                                                HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【获取员工列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取员工列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (employee.getEmployeeRole() != 2) {
            log.error("【获取员工列表】普通员工无权限访问所有员工");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        // 取得所有管理员
        List<Employee> employeeList = employeeService.findEmployeeByEmployeeRole(2);
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
     * 获得所有员工列表
     *
     * @param
     * @return
     */
    @GetMapping("/getEmployeeList")
    public ResultVO<Map<String, String>> getEmployeeList(HttpServletRequest request,
                                                         HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【获取员工列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取员工列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (employee.getEmployeeRole() != 2) {
            log.error("【获取员工列表】普通员工无权限访问所有员工");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
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
     * 将某个员工人才资源、公司资源变为公有资源(管理员才可以操作)
     *
     * @param request
     * @return
     */
    @GetMapping("/change2Public")
    public ResultVO<Map<String, String>> change2Public(HttpServletRequest request,
                                                       HttpServletResponse response) {
        // 验证信息
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【变为公有资源】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String databaseEmployeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(databaseEmployeeId)) {
            log.error("【变为公有资源】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee databaseEmployee = employeeService.getEmployeeByEmployeeId(databaseEmployeeId);
        // 管理员才可以操作公有人才
        if (databaseEmployee.getEmployeeRole() != 2) {
            log.error("【变为公有资源】普通员工无权限");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }

        // 通过employeeId取得该员工所有资源
        // 变为公有
        return null;
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
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改公有人才信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【修改公有人才信息】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 管理员才可以修改公有人才
        if (employee.getEmployeeRole() != 2) {
            return ResultVOUtil.fail(ResultEnum.USER_IDENTIFY_ERROR, response);
        }
        //首先获取数据库中的人才对象
        ResourceTemp resourceTemp = new ResourceTemp();
        //管理员直接同意修改，并写一条记录存到temp表中
        BeanUtils.copyProperties(resource, resourceTemp, getNullPropertyNames(resource));
        resourceTemp.setRequestStatus(0);
        //老板的话直接设置同意
        resourceTemp.setCheckedStatus(1);
        Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
        if (!isSuccess) return ResultVOUtil.fail(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR, response);

        // 判断电话号码是否已存在
//            Boolean flag = resourceService.saveResource(resource);
        Boolean phoneExist = resourceService.existsByPhoneNumber(resource.getPhoneNumber());
        if (phoneExist) {
            log.error("【修改人才信息】电话号码已存在");
            return ResultVOUtil.fail(ResultEnum.DUPICATE_PHONE, response);
        }

        // 将修改后的人才信息存入数据库
        Resource returnResource = resourceService.createResource(resource);
        Map<String, Object> map = new HashMap<>();
        if (returnResource != null) {
            map.put("resource", returnResource);
            map.put("employeeRole", 2);
            return ResultVOUtil.success(map);
        } else {
            log.error("【修改公有人才信息】发生错误");
            return ResultVOUtil.fail(ResultEnum.MANAGER_UPDATE_RESOURCE_INFO_ERROR, response);
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
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        Integer publicResourceId = Integer.parseInt(paramMap.get("resourceId").toString());

        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【删除公有人才信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【删除公有人才信息】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 管理员才可以删除公有人才
        if (employee.getEmployeeRole() != 2) {
            return ResultVOUtil.fail(ResultEnum.USER_IDENTIFY_ERROR, response);
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
        if (!isSuccess) return ResultVOUtil.fail(ResultEnum.MANAGER_DELETE_COMPANY_INFO_ERROR, response);

        // 删除公有人才
        Integer flag = resourceService.deleteResourceByResourceId(publicResourceId);
        Map<String, Object> map = new HashMap<>();
        if (flag != 0) {
            map.put("employeeRole", 2);
            return ResultVOUtil.success(map);
        } else {
            log.error("【删除公有人才信息】发生错误");
            return ResultVOUtil.fail(ResultEnum.MANAGER_DELETE_COMPANY_INFO_ERROR, response);
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
                                                              HttpServletRequest req,
                                                              HttpServletResponse response) {
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());
        Integer requestStatus = Integer.parseInt(map.get("requestStatus").toString());
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("pageSize").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取人才资源审批|未审批列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【获取人才资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");
        Page<ResourceTemp> resourceTempPage = null;
        if (checkedStatus == 0) {
            // 未审核
            resourceTempPage = resourceTempService.findResourceTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);
        } else {
            // 已审核（同意、拒绝）
            resourceTempPage = resourceTempService.findResourceTempByCheckedStatusIsNotAndRequestStatus(0, requestStatus, request);
        }

        if (resourceTempPage.getContent().isEmpty()) {
            if (page > 1) {
                request = PageRequest.of(page - 2, size, Sort.Direction.DESC, "createDate");
                resourceTempPage = resourceTempService.findResourceTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);
                return ResultVOUtil.success(resourceTempPage);
            } else return ResultVOUtil.success(ResultEnum.RESOURCE_TEMP_LIST_EMPTY);
        } else {
//            System.out.println(resourceTempPage.getContent());
            return ResultVOUtil.success(resourceTempPage);
        }

    }


    /**
     * 管理员获取公司修改|删除代办事项
     *
     * @param map 审批状态 0: 未审批,1：同意 2:不同意
     * @param req
     * @return
     */
    @PostMapping("/getCompanyCheckList")
    public ResultVO<Map<String, String>> getCompanyCheckList(@RequestBody HashMap map,
                                                             HttpServletRequest req,
                                                             HttpServletResponse response) {
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());
        Integer requestStatus = Integer.parseInt(map.get("requestStatus").toString());
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer size = Integer.parseInt(map.get("pageSize").toString());
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取人才资源审批|未审批列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【获取人才资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "startDate");
        Page<CompanyTemp> companyTempPage = null;
        if (checkedStatus == 0) {
            // 未审核
            companyTempPage = companyTempService.findCompanyTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);
        } else {
            // 已审核（同意、拒绝）
            companyTempPage = companyTempService.findCompanyTempByCheckedStatusIsNotAndRequestStatus(0, requestStatus, request);
        }

        if (companyTempPage.getContent().isEmpty()) {
            if (page > 1) {
                request = PageRequest.of(page - 2, size, Sort.Direction.DESC, "startDate");
                companyTempPage = companyTempService.findCompanyTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, request);
                return ResultVOUtil.success(companyTempPage);
            } else
                return ResultVOUtil.success(ResultEnum.RESOURCE_TEMP_LIST_EMPTY);
        } else {
//            System.out.println(companyTempPage.getContent());
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
    public ResultVO<Map<String, String>> checkResourceCheckList(@RequestBody HashMap map, HttpServletRequest req,
                                                                HttpServletResponse response) {
        Integer id = Integer.parseInt(map.get("id").toString());
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员审批人才资源审批|未审批列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员审批人才资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【管理员审批人才资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        ResourceTemp resourceTemp = resourceTempService.findResourceTempById(id);
        // 更新相应操作状态
        resourceTemp.setCheckedStatus(checkedStatus);

        // 审批完成后，将临时表写回数据库
        Boolean isSuccess = resourceTempService.saveResourceTemp(resourceTemp);
        if (!isSuccess) return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_TEMP_ERROR, response);

        Resource resource = new Resource();
        BeanUtils.copyProperties(resourceTemp, resource);
        // 审批状态 0: 未审批,  1：同意 2: 不同意
        if (checkedStatus == 1) {
            // 同意
            if (resourceTemp.requestStatus == 0) {
                // 改
                Boolean flag = resourceService.saveResource(resource);
                if (flag) return ResultVOUtil.success(ResultEnum.UPDATE_RESOURCE_SUCCESS);
                else return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_ERROR, response);
            } else if (resourceTemp.requestStatus == 1) {
                // 删
                Integer flag = resourceService.deleteResourceByResourceId(resource.getResourceId());
                if (flag != 0) return ResultVOUtil.success(ResultEnum.DELETE_RESOURCE_SUCCESS);
                else return ResultVOUtil.fail(ResultEnum.DELETE_RESOURCE_ERROR, response);
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
                                                               HttpServletRequest req,
                                                               HttpServletResponse response) {
        Integer id = Integer.parseInt(map.get("id").toString());
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员审批公司资源审批|未审批列表】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员审批公司资源审批|未审批列表】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【管理员审批公司资源审批|未审批列表】普通员工无权查看所有回款记录");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        CompanyTemp companyTemp = companyTempService.findCompanyTempById(id);
        // 更新相应操作状态
        companyTemp.setCheckedStatus(checkedStatus);

        // 审批完成后，将临时表写回数据库
        Boolean isSuccess = companyTempService.saveCompanyTemp(companyTemp);
        if (!isSuccess) {
            return ResultVOUtil.fail(ResultEnum.UPDATE_RESOURCE_TEMP_ERROR, response);
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
                    return ResultVOUtil.fail(ResultEnum.UPDATE_COMPANY_ERROR, response);
                }
            } else if (companyTemp.requestStatus == 1) {
                // 删
                Integer flag = companyService.deleteCompanyByCompanyId(company.getCompanyId());
                if (flag != 0) {
                    return ResultVOUtil.success(ResultEnum.DELETE_COMPANY_SUCCESS);
                } else {
                    return ResultVOUtil.fail(ResultEnum.DELETE_COMPANY_ERROR, response);
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


    /**
     * 管理员审核工资结算
     *
     * @param paramMap
     * @param req
     * @return
     */
    @PostMapping("/getEmployeeSalaryList")
    public ResultVO<Map<String, String>> getEmployeeSalaryList(@RequestBody HashMap paramMap,
                                                               HttpServletRequest req,
                                                               HttpServletResponse response) {
        // 解析参数：工资结算起始日期
        String searchMonth = paramMap.get("searchMonth").toString();
        Integer page = Integer.parseInt(paramMap.get("page").toString());
        Integer size = Integer.parseInt(paramMap.get("pageSize").toString());

        String startDate = searchMonth + "-01";
        String endDate = searchMonth + "-31";

        // 解析token
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员审核工资结算】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员审核工资结算】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        PageRequest request = PageRequest.of(page - 1, size);
        Page<Employee> employeePage = null;
        Integer totalPages = 0;
        Integer curPage = 0;
        List<Object> employeeSalaryList = new ArrayList<>();
        EmployeeSalaryRegulation createEmployeeSalaryRegulation = new EmployeeSalaryRegulation();
        // 管理员能看到所有员工的订单(所有公有、所有私有)
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() == 2) {
            employeePage = employeeService.findAllEmployeePageable(request);
            totalPages = employeePage.getTotalPages();
            curPage = page;
            List<Employee> employeeList = employeePage.getContent();

            // 遍历员工，取得某个员工的所有公司订单+人才订单
            for (Employee employee : employeeList) {
                String employeeIdIdx = employee.getEmployeeId();
                // 应该找到当月所有存在回款记录的订单，而不是当月创建的订单！
                List<PayBackRecord> payBackRecordList = payBackRecordService.findPayBackRecordByEmployeeIdAndDate(employeeIdIdx, startDate, endDate);
                // 按照人才、企业订单分组
                Map<Integer, List<PayBackRecord>> groupByType = payBackRecordList.stream().collect(Collectors.groupingBy(PayBackRecord::getBusinessType));
                List<PayBackRecord> companyBusinessPayBackRecordList = groupByType.get(2);
                List<PayBackRecord> resourceBusinessPayBackRecordList = groupByType.get(1);
                // TODO 判断空
                // 得到人才、企业订单的BusinessId列表
                List<String> companyBusinessIdList = null;
                List<String> resourceBusinessIdList = null;
                if (companyBusinessPayBackRecordList != null) {
                    // 当月所有存在回款记录的订单，并去重
                    companyBusinessIdList = companyBusinessPayBackRecordList.stream()
                            .map(PayBackRecord::getBusinessId).distinct()
                            .collect(Collectors.toList());
                    //
                }
                if (resourceBusinessPayBackRecordList != null) {
                    resourceBusinessIdList = resourceBusinessPayBackRecordList.stream()
                            .map(PayBackRecord::getBusinessId).distinct()
                            .collect(Collectors.toList());
                }
                EmployeeSalary employeeSalary = new EmployeeSalary();
                employeeSalary.setEmployeeId(employeeIdIdx);
                employeeSalary.setEmployeeName(employee.getEmployeeName());

                // 用于计算人才、企业当月回款总额
                BigDecimal resourcePaySum = new BigDecimal("0");
                BigDecimal companyPaySum = new BigDecimal("0");

                // 封装公司订单
                List<Object> companyBusinessList1 = new ArrayList<>();
//                List<CompanyBusiness> companyBusinessList = companyBusinessService.findCompanyBusinessByEmployeeIdAndDate(employeeIdIdx, startDate, endDate);
                List<CompanyBusiness> companyBusinessList = new ArrayList<>();
                if (companyBusinessIdList != null) {
                    companyBusinessList = companyBusinessService.findCompanyBusinessByBusinessIdList(companyBusinessIdList);
                }
                for (CompanyBusiness companyBusiness : companyBusinessList) {
                    Map<String, Object> businessMap = new HashMap<>();
                    businessMap.put("clientName", companyBusiness.getCompanyName());
                    businessMap.put("businessId", companyBusiness.getBusinessId());
                    businessMap.put("createDate", companyBusiness.getCreateDate());
                    // 通过当前订单该月的所有回款记录，计算回款总额
                    BigDecimal paybackSum = calPayBackSumByBusinessIsAndDate(companyBusiness.getBusinessId(), startDate, endDate);
                    businessMap.put("paybackSum", paybackSum);
                    // 欠款
                    List<PayBackRecord> payBackRecordListIdx = payBackRecordService.findAllPayBackRecordByBusinessId(companyBusiness.getBusinessId());
                    Optional<PayBackRecord> p = payBackRecordListIdx.stream().max(Comparator.comparingInt(PayBackRecord::getBackTimes));
                    businessMap.put("oweSum", p.get().getOwePay());

                    businessMap.put("orderPaySum", companyBusiness.getOrderPaySum());
                    companyBusinessList1.add(businessMap);

                    companyPaySum = companyPaySum.add(paybackSum);
                }

                employeeSalary.setComapnyBusinessList(companyBusinessList1);

                // 封装人才订单
                List<Object> resourceBusinessList1 = new ArrayList<>();
//                List<ResourceBusiness> resourceBusinessList = resourceBusinessService.findResourceBusinessByEmployeeIdAndDate(employeeIdIdx, startDate, endDate);
                List<ResourceBusiness> resourceBusinessList = new ArrayList<>();
                if (resourceBusinessIdList != null) {
                    resourceBusinessList = resourceBusinessService.findResourceBusinessByBusinessIdList(resourceBusinessIdList);
                }
                for (ResourceBusiness resourceBusiness : resourceBusinessList) {
                    Map<String, Object> businessMap = new HashMap<>();
                    businessMap.put("clientName", resourceBusiness.getResourceName());
                    businessMap.put("businessId", resourceBusiness.getBusinessId());
                    businessMap.put("createDate", resourceBusiness.getCreateDate());
                    // 通过当前订单该月的所有回款记录，计算回款总额
                    BigDecimal paybackSum = calPayBackSumByBusinessIsAndDate(resourceBusiness.getBusinessId(), startDate, endDate);
                    businessMap.put("paybackSum", paybackSum);
                    // 欠款
                    List<PayBackRecord> payBackRecordListIdx = payBackRecordService.findAllPayBackRecordByBusinessId(resourceBusiness.getBusinessId());
                    Optional<PayBackRecord> p = payBackRecordListIdx.stream().max(Comparator.comparingInt(PayBackRecord::getBackTimes));
                    businessMap.put("oweSum", p.get().getOwePay());
                    businessMap.put("orderPaySum", resourceBusiness.getOrderPaySum());
                    resourceBusinessList1.add(businessMap);

                    resourcePaySum = resourcePaySum.add(paybackSum);
                }
                employeeSalary.setResourceBusinessList(resourceBusinessList1);


                // 创建EmployeeSalaryRegulation表
                EmployeeSalaryRegulation employeeSalaryRegulation = new EmployeeSalaryRegulation();
                BeanUtils.copyProperties(employeeSalary, employeeSalaryRegulation, BeanCopyUtil.getNullPropertyNames(employeeSalary));
                employeeSalaryRegulation.setCompanyPaySum(companyPaySum);
                employeeSalaryRegulation.setResourcePaySum(resourcePaySum);
                employeeSalaryRegulation.setMonth(searchMonth);

                // 先去数据库查找是否存在
                // 1、存在，则取出来更新
                EmployeeSalaryRegulation dbEmployeeSalaryRegulation = employeeSalaryRegulationService.
                        findEmployeeSalaryRegulationByEmployeeIdAndMonth(employeeSalaryRegulation.getEmployeeId(), employeeSalaryRegulation.getMonth());
                if (dbEmployeeSalaryRegulation != null) {
                    // 更新
//                    BeanUtils.copyProperties(employeeSalaryRegulation, dbEmployeeSalaryRegulation, BeanCopyUtil.getNullPropertyNames(employeeSalaryRegulation));
                    dbEmployeeSalaryRegulation.setResourcePaySum(employeeSalaryRegulation.getResourcePaySum());
                    dbEmployeeSalaryRegulation.setCompanyPaySum(employeeSalaryRegulation.getCompanyPaySum());
                    createEmployeeSalaryRegulation = employeeSalaryRegulationService.createEmployeeSalaryRegulation(dbEmployeeSalaryRegulation);
                } else {
                    // 2、不存在，则新建
                    createEmployeeSalaryRegulation = employeeSalaryRegulationService.createEmployeeSalaryRegulation(employeeSalaryRegulation);
                }
                // 保存到数据库
                if (createEmployeeSalaryRegulation == null) {
                    log.error("{【创建工资结算规则】失败");
                    return ResultVOUtil.fail(ResultEnum.CREATE_SALARY_REGULATION_ERROR, response);
                }
                if (employeeSalary != null) {
                    BeanUtils.copyProperties(dbEmployeeSalaryRegulation, employeeSalary, BeanCopyUtil.getNullPropertyNames(dbEmployeeSalaryRegulation));
                }

                employeeSalaryList.add(employeeSalary);

            }
        } else {
            // 普通员工：只能取自己的订单
            totalPages = 1;
            curPage = 1;
            // 应该找到当月所有存在回款记录的订单，而不是当月创建的订单！
            List<PayBackRecord> payBackRecordList = payBackRecordService.findPayBackRecordByEmployeeIdAndDate(employeeId, startDate, endDate);
            // 按照人才、企业订单分组
            Map<Integer, List<PayBackRecord>> groupByType = payBackRecordList.stream().collect(Collectors.groupingBy(PayBackRecord::getBusinessType));
            List<PayBackRecord> companyBusinessPayBackRecordList = groupByType.get(2);
            List<PayBackRecord> resourceBusinessPayBackRecordList = groupByType.get(1);
            // TODO 判断空
            // 得到人才、企业订单的BusinessId列表
            List<String> companyBusinessIdList = null;
            List<String> resourceBusinessIdList = null;
            if (companyBusinessPayBackRecordList != null) {
                companyBusinessIdList = companyBusinessPayBackRecordList.stream()
                        .map(PayBackRecord::getBusinessId).distinct()
                        .collect(Collectors.toList());
            }
            if (resourceBusinessPayBackRecordList != null) {
                resourceBusinessIdList = resourceBusinessPayBackRecordList.stream()
                        .map(PayBackRecord::getBusinessId).distinct()
                        .collect(Collectors.toList());
            }

            // 用于计算人才、企业当月回款总额
            BigDecimal resourcePaySum = new BigDecimal("0");
            BigDecimal companyPaySum = new BigDecimal("0");

            EmployeeSalary employeeSalary = new EmployeeSalary();

            employeeSalary.setEmployeeId(employeeId);
            Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
            if (employee == null) {
                log.error("【获取工资列表】employee不存在");
                return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
            }
            employeeSalary.setEmployeeName(employee.getEmployeeName());


            List<Object> companyBusinessList1 = new ArrayList<>();
//            List<CompanyBusiness> companyBusinessList = companyBusinessService.findCompanyBusinessByEmployeeIdAndDate(employeeId, startDate, endDate);
            List<CompanyBusiness> companyBusinessList = companyBusinessService.findCompanyBusinessByBusinessIdList(companyBusinessIdList);

            for (CompanyBusiness companyBusiness : companyBusinessList) {
                Map<String, Object> businessMap = new HashMap<>();
                businessMap.put("clientName", companyBusiness.getCompanyName());
                businessMap.put("businessId", companyBusiness.getBusinessId());
                businessMap.put("createDate", companyBusiness.getCreateDate());
                // 通过当前订单该月的所有回款记录，计算回款总额
                BigDecimal paybackSum = calPayBackSumByBusinessIsAndDate(companyBusiness.getBusinessId(), startDate, endDate);
                businessMap.put("paybackSum", paybackSum);
                // 欠款
                List<PayBackRecord> payBackRecordListIdx = payBackRecordService.findAllPayBackRecordByBusinessId(companyBusiness.getBusinessId());
                Optional<PayBackRecord> p = payBackRecordListIdx.stream().max(Comparator.comparingInt(PayBackRecord::getBackTimes));
                businessMap.put("orderPaySum", companyBusiness.getOrderPaySum());

                businessMap.put("oweSum", p.get().getOwePay());

                companyBusinessList1.add(businessMap);

                companyPaySum = companyPaySum.add(paybackSum);
            }
            employeeSalary.setComapnyBusinessList(companyBusinessList1);

            List<Object> resourceBusinessList1 = new ArrayList<>();
            // TODO 应该找到当月所有存在回款记录的订单，而不是当月创建的订单！
//            List<ResourceBusiness> resourceBusinessList = resourceBusinessService.findResourceBusinessByEmployeeIdAndDate(employeeId, startDate, endDate);
            List<ResourceBusiness> resourceBusinessList = resourceBusinessService.findResourceBusinessByBusinessIdList(resourceBusinessIdList);
            for (ResourceBusiness resourceBusiness : resourceBusinessList) {
                Map<String, Object> businessMap = new HashMap<>();
                businessMap.put("clientName", resourceBusiness.getResourceName());
                businessMap.put("businessId", resourceBusiness.getBusinessId());
                businessMap.put("createDate", resourceBusiness.getCreateDate());
                // 通过当前订单该月的所有回款记录，计算回款总额
                BigDecimal paybackSum = calPayBackSumByBusinessIsAndDate(resourceBusiness.getBusinessId(), startDate, endDate);
                businessMap.put("paybackSum", paybackSum);
                // 欠款
                List<PayBackRecord> payBackRecordListIdx = payBackRecordService.findAllPayBackRecordByBusinessId(resourceBusiness.getBusinessId());
                Optional<PayBackRecord> p = payBackRecordListIdx.stream().max(Comparator.comparingInt(PayBackRecord::getBackTimes));
                businessMap.put("oweSum", p.get().getOwePay());
                businessMap.put("orderPaySum", resourceBusiness.getOrderPaySum());

                resourceBusinessList1.add(businessMap);

                resourcePaySum = resourcePaySum.add(paybackSum);
            }
            employeeSalary.setResourceBusinessList(resourceBusinessList1);

            // 创建EmployeeSalaryRegulation表
            EmployeeSalaryRegulation employeeSalaryRegulation = new EmployeeSalaryRegulation();
            BeanUtils.copyProperties(employeeSalary, employeeSalaryRegulation, BeanCopyUtil.getNullPropertyNames(employeeSalary));
            employeeSalaryRegulation.setCompanyPaySum(companyPaySum);
            employeeSalaryRegulation.setResourcePaySum(resourcePaySum);
            employeeSalaryRegulation.setMonth(searchMonth);

            // 先去数据库查找是否存在
            // 1、存在，则取出来更新
            EmployeeSalaryRegulation dbEmployeeSalaryRegulation = employeeSalaryRegulationService.
                    findEmployeeSalaryRegulationByEmployeeIdAndMonth(employeeSalaryRegulation.getEmployeeId(), employeeSalaryRegulation.getMonth());
            if (dbEmployeeSalaryRegulation != null) {
                // 更新
//                BeanUtils.copyProperties(employeeSalaryRegulation, dbEmployeeSalaryRegulation, BeanCopyUtil.getNullPropertyNames(employeeSalaryRegulation));
                dbEmployeeSalaryRegulation.setResourcePaySum(employeeSalaryRegulation.getResourcePaySum());
                dbEmployeeSalaryRegulation.setCompanyPaySum(employeeSalaryRegulation.getCompanyPaySum());
                createEmployeeSalaryRegulation = employeeSalaryRegulationService.createEmployeeSalaryRegulation(dbEmployeeSalaryRegulation);
            } else {
                // 2、不存在，则新建
                createEmployeeSalaryRegulation = employeeSalaryRegulationService.createEmployeeSalaryRegulation(employeeSalaryRegulation);
            }
            // 保存到数据库
            if (createEmployeeSalaryRegulation == null) {
                log.error("{【创建工资结算规则】失败");
                return ResultVOUtil.fail(ResultEnum.CREATE_SALARY_REGULATION_ERROR, response);
            }

            if (employeeSalary != null) {
                BeanUtils.copyProperties(dbEmployeeSalaryRegulation, employeeSalary, BeanCopyUtil.getNullPropertyNames(dbEmployeeSalaryRegulation));
            }

            employeeSalaryList.add(employeeSalary);

        }

        if (employeeSalaryList.size() == 0) {
            return ResultVOUtil.success(ResultEnum.EMPLOYEE_SALARY_EMPTY);
        } else {
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("totalPages", totalPages);
            returnMap.put("curPage", curPage);
            returnMap.put("employeeSalaryList", employeeSalaryList);
            return ResultVOUtil.success(returnMap);
        }

    }


    /**
     * 计算订单当月已回款总额
     *
     * @param businessId
     * @param startDate
     * @param endDate
     * @return
     */
    public BigDecimal calPayBackSumByBusinessIsAndDate(String businessId, String startDate, String endDate) {
        // 通过businessId取当月所有回款记录
        List<PayBackRecord> payBackRecordList = payBackRecordService.findPayBackRecordByBusinessIdAndDate(businessId, startDate, endDate);
        BigDecimal paybackSum = new BigDecimal("0");
        // 计算回款总额
        for (PayBackRecord payBackRecord : payBackRecordList) {
            paybackSum = paybackSum.add(payBackRecord.getBackPay());
        }
        return paybackSum;
    }


    /**
     * 创建某个员工的工资结算规则
     *
     * @param req
     * @param response
     * @return
     */
    @PostMapping("/createEmployeeSalaryRegulation")
    public ResultVO<Map<String, String>> createEmployeeSalaryRegulation(@RequestBody EmployeeSalaryRegulation
                                                                                employeeSalaryRegulation,
                                                                        HttpServletRequest req,
                                                                        HttpServletResponse response) {
        // 解析token
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员创建工资结算规则】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员创建工资结算规则】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        // 计算员工本月绩效和应发工资
        BigDecimal salary = employeeSalaryRegulation.getTotalSalary();

        EmployeeSalaryRegulation createEmployeeSalaryRegulation = new EmployeeSalaryRegulation();
        // 先去数据库查找是否存在
        // 1、存在，则取出来更新
        EmployeeSalaryRegulation dbEmployeeSalaryRegulation = employeeSalaryRegulationService.
                findEmployeeSalaryRegulationByEmployeeIdAndMonth(employeeSalaryRegulation.getEmployeeId(), employeeSalaryRegulation.getMonth());
        if (dbEmployeeSalaryRegulation != null) {
            // 更新
            BeanUtils.copyProperties(employeeSalaryRegulation, dbEmployeeSalaryRegulation, BeanCopyUtil.getNullPropertyNames(employeeSalaryRegulation));
            createEmployeeSalaryRegulation = employeeSalaryRegulationService.createEmployeeSalaryRegulation(dbEmployeeSalaryRegulation);
        } else {
            // 2、不存在，则新建
            createEmployeeSalaryRegulation = employeeSalaryRegulationService.createEmployeeSalaryRegulation(employeeSalaryRegulation);
        }

        if (createEmployeeSalaryRegulation != null) {
            return ResultVOUtil.success(ResultEnum.CREATE_SALARY_REGULATION_SUCCESS);
        } else {
            log.error("{【创建工资结算规则】失败");
            return ResultVOUtil.fail(ResultEnum.CREATE_SALARY_REGULATION_ERROR, response);
        }
    }

    /**
     * 查看某个员工的工资结算规则
     *
     * @param paramMap
     * @param req
     * @param response
     * @return
     */
    @PostMapping("/getEmployeeSalaryRegulation")
    public ResultVO<Map<String, String>> getEmployeeSalaryRegulation(@RequestBody HashMap paramMap,
                                                                     HttpServletRequest req,
                                                                     HttpServletResponse response) {
        String searchEmployeeId = paramMap.get("employeeId").toString();
        String month = paramMap.get("searchMonth").toString();

        // 解析token
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员审核工资结算规则】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员审核工资结算规则】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        // 先去数据库查找是否存在
        // 1、存在，则取出来返回
        EmployeeSalaryRegulation dbEmployeeSalaryRegulation = employeeSalaryRegulationService.findEmployeeSalaryRegulationByEmployeeIdAndMonth(searchEmployeeId, month);
        if (dbEmployeeSalaryRegulation != null) {
            return ResultVOUtil.success(dbEmployeeSalaryRegulation);
        } else {
            // 2、不存在，则新建一个空对象返回
            EmployeeSalaryRegulation createEmployeeSalaryRegulation = new EmployeeSalaryRegulation();
            Employee employee = employeeService.getEmployeeByEmployeeId(searchEmployeeId);
            createEmployeeSalaryRegulation.setEmployeeId(searchEmployeeId);
            createEmployeeSalaryRegulation.setEmployeeName(employee.getEmployeeName());
            return ResultVOUtil.success(createEmployeeSalaryRegulation);
        }
    }

}
