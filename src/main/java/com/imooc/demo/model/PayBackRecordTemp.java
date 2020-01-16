package com.imooc.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Data
@Table(name="paybackrecordtemp")
public class PayBackRecordTemp {
    /** 回款记录ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    /** 提成给谁 **/
    @Column(name = "employeeId")
    public String employeeId;
    /** 提成给谁 **/
    @Column(name = "employeeName")
    public String employeeName;

    /** 回款相应订单ID（哪个订单的回款） **/
    @Column(name = "businessId")
    public String businessId;
    /** 订单类型：1：人才订单 2：企业订单 **/
    @Column(name = "businessType")
    public Integer businessType;

    /** 回款审核状态：0：未审核 1：同意 2：拒绝 **/
    @Column(name = "checkedStatus")
    public Integer checkedStatus;

    /** 成交总额 **/
    @Column(name = "orderPaySum")
    public BigDecimal orderPaySum;
    /** 欠款金额 **/
    @Column(name = "owePay")
    public BigDecimal owePay;
    /** 已回款金额 **/
    @Column(name = "backPay")
    public BigDecimal backPay;

    /** 最后回款金额 **/
    @Column(name = "laterBackPay")
    public BigDecimal laterBackPay;
    /** 最后回款时间 **/
    @Column(name = "laterBackDate")
    public String laterBackDate;

    /** 回款次数 **/
    @Column(name = "backTimes")
    public Integer backTimes;
    /** 录入时间 **/
    @Column(name = "recordDate")
    public String recordDate;

    /** 最后成交时间 **/
    @Column(name = "createDate")
    public String createDate;

    /** 是否已完成（是否有欠款）0 表示ing 1表示完成 **/
    @Column(name = "isCompleted")
    public Integer isCompleted = 0;

    /** 备注 **/
    @Column(name = "info")
    public String info;

    /** 人才ID **/
    @Column(name = "resourceId")
    public Integer resourceId;

    /** 人才姓名 **/
    @Column(name = "resourceName")
    public String resourceName;

    /** 公司ID **/
    @Column(name = "companyId")
    public  Integer companyId;

    /**
     * 成交企业名称
     **/
    @Column(name = "companyName")
    public String companyName;




}
