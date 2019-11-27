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
    @Column(name = "employeeId")
    public String employeeId;
    /** 人才ID **/
    @Column(name = "resourceId")
    public String resourceId;
    /** 成交时间 **/
    @Column(name = "createTime")
    public Date createTime;
    /** 公司名称 **/
    @Column(name = "companyName")
    public  String companyName;
    /** 订单状态 0 表示ing 1表示完成 **/
    @Column(name = "businessStatus")
    public Integer businessStatus;
    /** 公司类型 **/
    @Column(name = "companyCategory")
    public String companyCategory;
    /** 订单创建者 **/
    @Column(name = "creatorId", nullable = false)
    public String creatorId;

}
