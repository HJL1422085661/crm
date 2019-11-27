package com.imooc.demo.modle;

import com.imooc.demo.enums.EmployeeRoleEnum;
import lombok.Data;

import javax.persistence.*;

/**
 * @Author emperor
 * @Date 2019/10/20 16:35
 * @Version 1.0
 */

@Entity
@Data
@Table(name = "employee")
public class Employee {

    /** 员工ID **/
    @Id
    @Column(name = "employeeId", nullable = false)
    public String employeeId;

    /** 员工姓名 **/
    @Column(name = "employeeName")
    public String employeeName;
    /** 员工角色 **/
    @Column(name = "employeeRole", nullable = false)
    private Integer employRole = EmployeeRoleEnum.EMPLOYEE.getCode();
    /** 员工手机号 **/
    @Column(name = "iphoneNumber")
    private String iphoneNumber;
    /** 员工密码 **/
    @Column(name = "passWord", nullable = false)
    private String passWord;
    /** 员工性别 **/
    @Column(name = "gender")
    private Integer gender;
    /** 密码加密需要 **/
    @Column(name = "salt" )
    private String salt;



}
