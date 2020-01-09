package com.imooc.demo.model;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author emperor
 * @Date 2019/10/20 16:35
 * @Version 1.0
 */

@Data
public class EmployeeSalary {

    /**
     * 员工登录账号ID
     **/
    public String employeeId;
    /**
     * 员工姓名
     **/
    public String employeeName;

    /**
     * 底薪
     **/
    @Column(name = "baseSalary")
    public BigDecimal baseSalary = new BigDecimal("0");
    /**
     * 员工绩效
     **/
    public BigDecimal performance = new BigDecimal("0");
    /**
     * 岗位工资
     **/
    @Column(name = "positionWage")
    public BigDecimal positionWage = new BigDecimal("0");

    /**
     * 工龄工资
     **/
    @Column(name = "positionAge")
    public BigDecimal positionAge = new BigDecimal("0");

    /**
     * 请假扣除
     **/
    @Column(name = "employeeLeave")
    public BigDecimal employeeLeave = new BigDecimal("0");
    /**
     * 迟到扣除
     **/
    @Column(name = "employeeLate")
    public BigDecimal employeeLate = new BigDecimal("0");

    /**
     * 罚款
     **/
    @Column(name = "penalty")
    public BigDecimal penalty = new BigDecimal("0");

    /**
     * 奖金
     **/
    @Column(name = "bonus")
    public BigDecimal bonus = new BigDecimal("0");

    /**
     * 社保个人费用
     **/
    @Column(name = "insurance")
    public BigDecimal insurance = new BigDecimal("0");

    /**
     * 其他
     **/
    @Column(name = "other")
    public BigDecimal other = new BigDecimal("0");

    /**
     * 员工应发工资
     **/
    public BigDecimal salary = new BigDecimal("0");

    /**
     * 备注
     **/
    public String info = "";

    /**
     * 人才订单列表
     **/
    public List<Object> resourceBusinessList = new ArrayList<>();
//    public Set<Object> resourceBusinessList;
    /**
     * 企业订单列表
     **/
    public List<Object> companyBusinessList = new ArrayList<>();
    /**
     * 个人客户总额
     **/
    @Column(name = "resourcePaySum")
    public BigDecimal resourcePaySum = new BigDecimal("0");
    /**
     * 个人客户提成比例
     **/
    @Column(name = "resourceRatio")
    public BigDecimal resourceRatio = new BigDecimal("0");

    /**
     * 企业客户总额
     **/
    @Column(name = "companyPaySum")
    public BigDecimal companyPaySum = new BigDecimal("0");
    /**
     * 企业客户提成比例
     **/
    @Column(name = "companyRatio")
    public BigDecimal companyRatio = new BigDecimal("0");

}
