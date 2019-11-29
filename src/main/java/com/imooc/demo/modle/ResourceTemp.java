package com.imooc.demo.modle;
/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-27 12:32
 **/

import lombok.Data;
import javax.persistence.*;



@Entity
@Data
@Table(name = "resourcetemp")
public class ResourceTemp {
    /*
     * 人才改删表
     * 用于存储员工提交改删申请后的内容
     * 在人才表的基础上，增加了表ID、修改状态、请求内容、审批操作
     * */

    /** 人才改删表ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer id;
    /** 请求内容 0: 改, 1:删 **/
    @Column(name = "requestStatus", nullable = false)
    public Integer requestStatus;
    /** 审批状态 0: 未审批,  1：同意 2: 不同意 **/
    @Column(name = "checkedStatus", nullable = false)
    public Integer checkedStatus;
    /** 人才ID **/
    @Column(name = "resourceId", nullable = false)
    public Integer resourceId;
    /** 客户负责人ID **/
    @Column(name = "employeeId", nullable = false)
    public String employeeId;
    /** 录入客户时间 **/
    @Column(name = "createDate", nullable = false)
    public String createDate;
    /** 到期时间 **/
    @Column(name = "endDate", nullable = false)
    public String endDate;
    /** 人才资源共享状态 **/
    @Column(name = "shareStatus", nullable = false)
    public String shareStatus;
    /** 人才身份证号 **/
    @Column(name = "identify")
    public String identify;
    /** 客户姓名 **/
    @Column(name = "resourceName", nullable = false)
    public String resourceName;
    /** 客户手机号 **/
    @Column(name = "phone", nullable = false)
    public String phone;
    /** 证书 **/
    @Column(name = "certificate", nullable = false)
    public String certificate;
    /** 注册省份 **/
    @Column(name = "province")
    public String province;
    /** qq **/
    @Column(name = "qq")
    public String qq;
    /** 性别 **/
    @Column(name = "gender")
    public Integer gender;
    /** 邮箱 **/
    @Column(name = "email")
    public String email;
    /** 备注 **/
    @Column(name = "info")
    public String info;
    /** 客户状态 **/
    @Column(name = "status")
    public Integer status;
    /** 员工姓名 **/
    @Column(name = "employeeName")
    public String employeeName;
    /** 所在城市 **/
    @Column(name = "city")
    public String city;

}
