package com.imooc.demo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Author emperor
 * @Date 2019/10/20 16:35
 * @Version 1.0
 */

@Data
public class EmployeeSalary {

    /** 员工登录账号ID **/
    public String employeeId;
    /** 员工姓名 **/
    public String employeeName;

    /** 员工绩效 **/
    public BigDecimal performance = new BigDecimal("0");

    /** 员工罚款 **/
    public BigDecimal penalty = new BigDecimal("0");

    /** 员工底薪 **/
    public BigDecimal baseSalary = new BigDecimal("0");

    /** 员工应发工资 **/
    public BigDecimal salary = new BigDecimal("0");

    /** 备注 **/
    public String info = "";

    /** 人才订单列表 **/
    public List<Object> resourceBusinessList = new ArrayList<>();
//    public Set<Object> resourceBusinessList;
    /** 企业订单列表 **/
    public List<Object> comapnyBusinessList = new ArrayList<>();

}
