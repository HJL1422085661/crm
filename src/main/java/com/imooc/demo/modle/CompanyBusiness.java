package com.imooc.demo.modle;

import com.imooc.demo.enums.BusinessStatusRoleEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ Author: yangfan
 * @ Date: 2019/12/1
 * @ Version: 1.0
 */

@Entity
@Data
@Table(name="companybusiness")
public class CompanyBusiness {

    /** 业务ID **/
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer id;

    /** 订单ID **/
    @Column(name = "businessId")
    public String businessId;

    /** 业务负责人ID **/
    @Column(name = "employeeId")
    public String employeeId;
    /** 业务负责人ID **/
    @Column(name = "employeeName")
    public String employeeName;

    /** 人才ID **/
    @Column(name = "resourceId")
    public String resourceId;
    /** 人才名称 **/
    @Column(name = "resourceName")
    public String resourceName;

    /** 公司ID **/
    @Column(name = "companyId")
    public  Integer companyId;
    /** 公司名称 **/
    @Column(name = "companyName")
    public  String companyName;

    /** 订单金额 **/
    @Column(name = "orderPaySum")
    public BigDecimal orderPaySum;

    /** 创建时间 **/
    @Column(name = "createDate")
    public String createDate;

    /** 备注 **/
    @Column(name = "info")
    public String info;


//    /** 订单状态 0 表示ing 1表示完成 **/
//    @Column(name = "businessStatus")
//    public Integer businessStatus =  BusinessStatusRoleEnum.PROCESSING.getCode();
//    /** 公司类型 **/
//    @Column(name = "companyCategory")
//    public Integer companyCategory;
//    /** 订单创建者 **/
//    @Column(name = "creatorId", nullable = false)
//    public String creatorId;

}
