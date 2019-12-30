package com.imooc.demo.model;
/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-27 12:39
 **/

import lombok.Data;
import javax.persistence.*;



@Entity
@Data
@Table(name = "companytemp")
public class CompanyTemp {
    /*
     * 企业改删表
     * 用于存储员工提交改删申请后的内容
     * 在企业表的基础上，增加了表ID、修改状态、请求内容、审批操作
     * */
    /** 企业改删表ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer id;

     /** 请求内容 0: 改, 1:删 **/
    @Column(name = "requestStatus", nullable = false)
    public Integer requestStatus;
    /** 审批状态 0: 未审批,  1：同意 2: 不同意 **/
    @Column(name = "checkedStatus", nullable = false)
    public Integer checkedStatus;

    /** 创建者 **/
    @Column(name = "employeeId")
    public String employeeId;
    /** 员工姓名 **/
    @Column(name = "employeeName")
    public String employeeName;
    /** 公司ID **/
    @Column(name = "companyId")
    public Integer companyId;
    /** 公司名称 **/
    @Column(name = "companyName")
    public String companyName;
    /** 获得客户时间 **/
    @Column(name = "createDate")
    public String createDate;
    /** 证书截止时间 **/
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
    /** 公司类型 **/
    @Column(name = "companyCategory")
    public Integer companyCategory;
    /** 公司资源共享状态  1 公有  2 私有**/
    @Column(name = "shareStatus")
    public Integer shareStatus;
    /** 客户状态（潜在客户、流失客户等） **/
    @Column(name = "status")
    public Integer status;
}
