package com.imooc.demo.modle;

import com.imooc.demo.enums.EmployeeRoleEnum;
import lombok.Data;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public String id;

    /** 员工登录账号 **/
    @Column(name = "employeeId", nullable = false)
    public String employeeId;
    /** 员工姓名 **/
    @Column(name = "employeeName",  nullable = false)
    public String employeeName;

    /** 员工角色 **/
    @Column(name = "employeeRole", nullable = false)
    private Integer employeeRole;
    /** 员工手机号 **/
    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;
    /** 员工密码 **/
    @Column(name = "passWord", nullable = false)
    private String passWord;
    /** 员工性别 **/
    @Column(name = "gender")
    private Integer gender;
    /** 密码加密需要 **/
    @Column(name = "salt", nullable = false)
    private String salt;
    /** 员工邮箱号 **/
    @Column(name = "email")
    private String email;

    /** 员工所属经理ID **/
    @Column(name = "employeeManagerId", nullable = false)
    private String employeeManagerId;

    /** 员工所属经理姓名 **/
    @Column(name = "employeeManagerName", nullable = false)
    private String employeeManagerName;

    /** 验证码 **/
    @Column(name = "verifyCode")
    private String verifyCode;

    /** 验证码失效日期 **/
    @Column(name = "verifyCodeExpireTime")
    private Date verifyCodeExpireTime;

}
