package com.imooc.demo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ Author: yangfan
 * @ Date: 2019/12/1
 * @ Version: 1.0
 */

@Data
public class CompanyBusinessDTO {

    /** 业务ID **/
    public Integer id;

    /** 订单ID **/
    public String businessId;

    /** 业务负责人ID **/
    public String employeeId;
    /** 业务负责人ID **/
    public String employeeName;

    /** 人才 **/
    public List<Map<String, String>> resource;

    /** 公司ID **/
    public  Integer companyId;
    /** 公司名称 **/
    public  String companyName;

    /** 订单金额 **/
    public BigDecimal orderPaySum;

    /** 创建时间 **/
    public String createDate;

    /** 备注 **/
    public String info;


}
