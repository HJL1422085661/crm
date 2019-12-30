package com.imooc.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @ Author: yangfan
 * @ Date: 2019-12-2
 * @ Version: 1.0
 */

@Entity
@Data
@Table(name="resourcebusiness")
public class ResourceBusiness {
    /** ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer id;

    /** 人才订单ID **/
    @Column(name = "businessId")
    public String businessId;

    /** 人才ID **/
    @Column(name = "resourceId", nullable = false)
    public Integer resourceId;
    /** 人才名称 **/
    @Column(name = "resourceName")
    public String resourceName;

    /** 公司ID **/
    @Column(name = "companyId", nullable = false)
    public Integer companyId;
    /** 公司名称 **/
    @Column(name = "companyName")
    public String companyName;
    /** 员工ID **/
    @Column(name = "employeeId")
    public String employeeId;
    /** 员工ID **/
    @Column(name = "employeeName")
    public String employeeName;

    /** 订单金额 **/
    @Column(name = "orderPaySum")
    public BigDecimal orderPaySum;

    /** 创建时间 **/
    @Column(name = "createDate")
    public String createDate;

    /** 订单状态 0 表示ing 1表示完成 **/
    @Column(name = "isCompleted")
    public Integer isCompleted = 0;

    /** 备注 **/
    @Column(name = "info")
    public String info;


//    /** 合同起始时间 **/
//    @Column(name = "startDate")
//    public String startDate;
//    /** 合同截止时间 */
//    @Column(name = "expireDate")
//    public String expireDate;
//    /** 职位 **/
//    @Column(name = "occupation")
//    public String occupation;
//    /** 公司联系人姓名 **/
//    @Column(name = "contactorName")
//    public String contactorName;
//    /** 联系人性别 **/
//    @Column(name = "gender")
//    public Integer gender;
//    /**联系人手机号 **/
//    @Column(name = "phoneNumber")
//    public String phoneNumber;
//    /** 公司类型 **/
//    @Column(name = "companyCategory")
//    public String companyCategory;
}
