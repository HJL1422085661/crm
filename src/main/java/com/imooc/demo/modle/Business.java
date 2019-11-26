package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    @Column(name = "businessId")
    public String businessId;

    /** 业务负责人ID **/
    public String employeeId;
    /** 人才ID **/
    public String resourceId;
    /** 成交时间 **/
    public Date createTime;
    /** 公司名称 **/
    public  String companyName;
    /** 订单状态 0 表示ing 1表示完成 **/
    public Integer businessStatus;
}
