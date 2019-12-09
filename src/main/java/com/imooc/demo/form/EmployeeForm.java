package com.imooc.demo.form;

import com.imooc.demo.enums.EmployeeRoleEnum;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class EmployeeForm {


    /** 员工(登录账号)ID **/
    @NotEmpty(message = "用户ID必填")
    private String employeeId;
    /** 员工登录密码 **/
//    @NotNull(message = "密码必填")
    private String passWord = "123456";

    /** 员工名 **/
    @NotEmpty(message = "用户名必填")
    private String employeeName;
    /** 员工性別 **/
    private Integer gender;
    /** 员工名 **/
    @NotEmpty(message = "手机号必填")
    private String phoneNumber;

    /** 员工角色 **/
    @NotNull(message = "角色必填")
    private Integer employeeRole;

    /** 员工所属经理ID **/
    @NotNull(message = "所属经理ID必填")
    private String employeeManagerId;

    /** 员工email **/
    private String email;

}
