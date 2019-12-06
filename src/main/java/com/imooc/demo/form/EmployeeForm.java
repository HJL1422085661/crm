package com.imooc.demo.form;

import com.imooc.demo.enums.EmployeeRoleEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class EmployeeForm {


    /** 员工用户ID **/
    @NotEmpty(message = "用户ID必填")
    private String employeeId;

    /** 员工密码 **/
    @NotEmpty(message = "密码必填")
    private String passWord;

    /** 员工性別 **/
    @NotNull(message = "性別必填")
    private Integer gender;

    /** 员工角色 **/
    @NotNull(message = "角色必填")
    private Integer employeeRole = EmployeeRoleEnum.EMPLOYEE.getCode();

}
