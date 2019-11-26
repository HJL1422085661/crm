package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/20 13:19
 * @Version 1.0
 */
@Entity
@Data
public class PayBackRecod {
    /** 记录ID **/
    @Id
    @Column(name = "recordId")
    public String recordId;
    /** 业务ID **/
    public String businessId;
    /** 回款金额 **/
    public BigDecimal payBackAmount;
    /** 回款时间 **/
    public Date payBackTime;
}