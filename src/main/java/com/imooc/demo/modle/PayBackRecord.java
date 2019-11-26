package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @Column(name = "recordId")
    public String recordId;
    /** 业务ID **/
    @Column(name = "businessId")
    public String businessId;
    /** 回款金额 **/
    @Column(name = "payBackAmount")
    public BigDecimal payBackAmount;
    /** 回款记录创建时间 **/
    @Column(name = "createTime")
    public Date createTime;
    /** 回款到账时间 **/
    @Column(name = "endTime")
    public Date endTime;
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
