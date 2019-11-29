package com.imooc.demo.modle;
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

    /** 公司ID **/
    @Column(name = "companyId")
    public Integer companyId;
    /** 公司名称 **/
    @Column(name = "companyName")
    public String companyName;
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
    /** 公司类型 **/
    @Column(name = "companyCategory")
    public String companyCategory;
}
