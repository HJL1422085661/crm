package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/22 12:16
 * @Version 1.0
 */
@Entity
@Data@Table(name = "company")
public class Company {

    /** 公司ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "companyId", nullable = false)
    public Integer companyId;
    /** 公司名称 **/
    @Column(name = "companyName")
    public String companyName;

    /** 创建者 **/
    @Column(name = "employeeId")
    public String employeeId;
    /** 员工姓名 **/
    @Column(name = "employeeName")
    public String employeeName;

    /** 客户状态（潜在客户、流失客户等） **/
    @Column(name = "status")
    public Integer status;
    /** 合同起始时间 **/
    @Column(name = "startDate")
    public String startDate;
    /** 合同截止时间 */
    @Column(name = "expireDate")
    public String expireDate;
    /** 职位 **/
    @Column(name = "occupation")
    public String occupation;
    /** 公司联系人姓名 **/
    @Column(name = "contactorName")
    public String contactorName;
    /** 联系人性别 **/
    @Column(name = "gender")
    public Integer gender;
    /**联系人手机号 **/
    @Column(name = "phoneNumber")
    public String phoneNumber;
    /** 联系人邮箱 **/
    @Column(name = "email")
    public String email;
    /** 公司类型 **/
    @Column(name = "companyCategory")
    public Integer companyCategory;
       /** 备注 **/
    @Column(name = "info")
    public String info;
    /** 注册省份 **/
    @Column(name = "province")
    public String province;

}
