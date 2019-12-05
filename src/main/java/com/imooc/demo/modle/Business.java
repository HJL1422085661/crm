package com.imooc.demo.modle;

import com.imooc.demo.enums.BusinessStatusRoleEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/20 13:12
 * @Version 1.0
 */

@Entity
@Data
public class Business {

    /** 业务ID **/
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer id;

    /** 创建者ID **/
    @Column(name = "creatorId")
    public String creatorId;

    /** 人才ID **/
    @Column(name = "resourceId")
    public Integer resourceId;
    /** 人才名称 **/
    @Column(name = "resourceName")
    public String resourceName;
    /** 人才提供者ID（算提成） **/
    @Column(name = "resourceEmployeeId")
    public  String resourceEmployeeId;

    /** 公司ID **/
    @Column(name = "companyId")
    public  Integer companyId;
    /** 公司名称 **/
    @Column(name = "companyName")
    public  String companyName;
    /** 公司提供者ID（算提成） **/
    @Column(name = "companyEmployeeId")
    public  String companyEmployeeId;
    /** 公司类型 **/
    @Column(name = "companyCategory")
    public Integer companyCategory;

    /** 成交时间 **/
    @Column(name = "createDate")
    public String createDate;

    /** 订单状态 0 表示ing 1表示完成 **/
    @Column(name = "businessStatus")
    public Integer businessStatus =  BusinessStatusRoleEnum.PROCESSING.getCode();

    /** 订单金额 **/
    @Column(name = "orderPaySum")
    public BigDecimal orderPaySum;

    /** 创建时间 **/
    @Column(name = "saveDate")
    public Date saveDate;

    /** 备注 **/
    @Column(name = "info")
    public String info;

}
