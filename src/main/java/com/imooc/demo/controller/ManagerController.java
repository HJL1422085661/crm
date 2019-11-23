package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;

import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.exception.CrmException;
import com.imooc.demo.form.EmployeeForm;
import com.imooc.demo.modle.Business;
import com.imooc.demo.modle.Employee;
import com.imooc.demo.modle.Resource;
import com.imooc.demo.service.BusinessService;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.ManagerService;
import com.imooc.demo.service.ResourceService;
import com.imooc.demo.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public BusinessService businessService;
    @Autowired
    public ResourceService resourceService;

    /**
     * manager注册employer, 注册成功返回ticket
     * @param employeeForm
     * @param bindingResult
     * @return
     */
    @PostMapping ("/register")
    public ResultVO<Map<String, String>> register(@Valid EmployeeForm employeeForm, BindingResult bindingResult){

        Map<String, String> map = new HashMap<>();
        if(bindingResult.hasErrors()){
            log.error("【】参数不正确， employeeForm={}", employeeForm);
            throw new CrmException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        if(employeeForm.getPassWord().length() < 6){
            log.error("【注册用户】用户密码长度小于6位");
            return ResultVOUtil.error(ResultEnum.PASSWORD_LENGTH_SHORT);
        }
        Employee employee = managerService.getManagerByEmployeeId(employeeForm.getEmployeeId());
        if(employee != null){
            log.error("【注册用户】用户ID已存在");
            return ResultVOUtil.error(ResultEnum.USER_ID_EXIST);
        }

        try {
            managerService.register(employeeForm.getEmployeeId(), employeeForm.getPassWord(), employeeForm.getEmployRole());
        }catch (Exception e){
            log.error("【注册用户】注册发生异常");
            return ResultVOUtil.error(ResultEnum.REGISTER_EXCEPTION);
        }

        return ResultVOUtil.success();
    }

    /**
     * 修改员工权限
     * @param employeeId
     * @param employeeRole
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/updateEmployeeRole")
    public ResultVO<Map<String, String>> updateEmployeeRole(@RequestParam("employeeId") String employeeId,
                                                            @RequestParam("employeeRole") Integer employeeRole){

        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if(employee == null){
            log.error("【修改员工权限】该员工不存在");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        try {
            Boolean flag = employeeService.updateEmployeeRoleByEmployeeId(employeeRole, employeeId);
            if(!flag) {
                log.error("【修改员工权限】发生错误");
                return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_ROLE_ERROR);
            }
        }catch (Exception e){
            log.error("【修改员工权限】修改员工发生异常");
            return ResultVOUtil.error(ResultEnum.UPDATE_EMPLOYEE_EXCEPTION);
        }
        return ResultVOUtil.success();
    }

    /**
     * 删除员工
     * @param employeeId
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/deleteEmployee")
    public ResultVO<Map<String, String>> deleteEmployee(@RequestParam("employeeId") String employeeId) {

        //Todo
        /*先查询该用户是否存在进行的订单，如果该用户存在进行中的订单，则需要转让该订单给其他人
            该用户的客户资源需要变为公有
         */
        List<Business> businessList = businessService.getBusinessByEmployeeId(employeeId);
        boolean flag = false;
        //查询与该用户有关的订单是否存在ing状态的
        for(Business business : businessList){
            if(business.getBusinessStatus() == 0){
                flag = true;
                break;
            }
        }
        //如果该用户存在正在进行的订单,则不能删除
        if(flag){
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_DELETE);
        }
        //获取该用户的人才库，修改人才状态为公共public
        List<Resource> resourceList = resourceService.getResourceByEmployeeId(employeeId);
        for(Resource resource: resourceList){
            //公有人才的员工ID设为老板的ID（老板的ID固定为0）
            Boolean tag = resourceService.updateShareStatusAndEmployeeIdByResourceId("public", "0", resource.getResourceId());
           if(!tag){
               log.error("【设置人才到公有库】发生错误");
               return ResultVOUtil.error(ResultEnum.SET_RESOURCE_PUBLIC_ERROR);
           }
        }
        try{
            employeeService.deleteEmployee(employeeId);
        }catch (Exception e){
            log.error("【删除员工】发生异常" + e.getMessage());
            return ResultVOUtil.error(ResultEnum.DELETE_EMPLOYEE_EXCEPTION);
        }

        return ResultVOUtil.success();
    }


}
