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
    /** 记录ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "recordId")
    public Integer recordId;
    /** 业务ID **/
    @Column(name = "businessId")
    public Integer businessId;
    /** 回款金额 **/
    @Column(name = "payBackAmount")
    public BigDecimal payBackAmount;
    /** 回款记录创建时间 **/
    @Column(name = "createDate")
    public String createDate;
    /** 回款到账时间 **/
    @Column(name = "payBackDate")
    public String payBackDate;
    /** 是否已确认 **/
    @Column(name = "isChecked")
    public Integer isChecked;

    /** 回款记录创建者 **/
    @Column(name = "creatorId")
    public String creatorId;
    /** 领工资的人 **/
    @Column(name = "employeeId")
    public String employeeId;
}
