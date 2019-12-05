package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/20 13:19
 * @Version 1.0
 */
@Entity
@Data
@Table(name="paybackrecord")
public class PayBackRecord {
    /** 回款记录ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    /** 提成给谁 **/
    @Column(name = "employeeId")
    public String employeeId;

     /** 回款相应订单ID（哪个订单的回款） **/
    @Column(name = "businessId")
    public String businessId;
    /** 订单类型：1：人才订单 2：企业订单 **/
    @Column(name = "businessType")
    public Integer businessType;

    /** 成交总额 **/
    @Column(name = "orderPaySum")
    public BigDecimal orderPaySum;
    /** 欠款金额 **/
    @Column(name = "owePay")
    public BigDecimal owePay;
    /** 回款金额 **/
    @Column(name = "backPay")
    public BigDecimal backPay;

    /** 回款次数 **/
    @Column(name = "backTimes")
    public Integer backTimes;
    /** 录入时间 **/
    @Column(name = "recordDate")
    public String recordDate;

    /** 最后成交时间 **/
    @Column(name = "createDate")
    public String createDate;

    /** 备注 **/
    @Column(name = "info")
    public String info;



}
