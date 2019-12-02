package com.imooc.demo.modle;

import com.imooc.demo.enums.BusinessStatusRoleEnum;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "businesstemp")
public class BusinessTemp {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Integer id;

    /** 业务ID **/
    @Column(name = "businessId")
    public Integer businessId;

    /** 请求内容 0: 改, 1:删 **/
    @Column(name = "requestStatus", nullable = false)
    public Integer requestStatus;

    /** 审批状态 0: 未审批, 1:已审批 2：同意 3:不同意 **/
    @Column(name = "checkedStatus", nullable = false)
    public Integer checkedStatus;

    /** 业务负责人ID **/
    @Column(name = "employeeId")
    public String employeeId;
    /** 人才ID **/
    @Column(name = "resourceId")
    public Integer resourceId;
    /** 成交时间 **/
    @Column(name = "createDate")
    public String createDate;
    /** 公司名称 **/
    @Column(name = "companyName")
    public  String companyName;
    /** 订单状态 0 表示ing 1表示完成 **/
    @Column(name = "businessStatus")
    public Integer businessStatus =  BusinessStatusRoleEnum.PROCESSING.getCode();
    /** 公司类型 **/
    @Column(name = "companyCategory")
    public Integer companyCategory;
    /** 订单创建者 **/
    @Column(name = "creatorId", nullable = false)
    public String creatorId;


}
