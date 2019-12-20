package com.imooc.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Data
@Table(name = "employeesalaryregulation")
public class EmployeeSalaryRegulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    /**
     * 员工登录账号
     **/
    @Column(name = "employeeId")
    public String employeeId;
    /**
     * 员工姓名
     **/
    @Column(name = "employeeName")
    public String employeeName;

    /**
     * 发放月份
     **/
    @Column(name = "month")
    public String month;

    /**
     * 底薪
     **/
    @Column(name = "baseSalary")
    public BigDecimal baseSalary = new BigDecimal("0");

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
    @Column(name = "salary")
    public BigDecimal salary = new BigDecimal("0");

    /**
     * 备注
     **/
    @Column(name = "info")
    public String info;

    public BigDecimal getTotalSalary() {
        this.salary = this.baseSalary.add(this.resourcePaySum.multiply(this.resourceRatio)).add(this.companyPaySum.multiply(this.companyRatio))
                .add(this.positionAge).add(this.positionWage)
                .subtract(this.employeeLate).subtract(this.employeeLeave).subtract(this.penalty)
                .add(this.bonus)
                .subtract(this.insurance)
                .add(this.other);
        return this.salary;
    }

//    /**实际收入**/
//    @Column(name = "performance")
//    public BigDecimal performance;
//
//    /** 录入时间 **/
//    @Column(name = "recordDate")
//    public String recordDate;
//

}
