package com.imooc.demo.model;

import lombok.Data;
import javax.persistence.*;

/**
 * @Author emperor
 * @Date 2019/11/19 21:27
 * @Version 1.0
 */
@Entity
@Data
//@Table(name = "resource", uniqueConstraints=@UniqueConstraint(columnNames="phoneNumber"))
@Table(name = "resource")
public class Resource {

    /** 人才ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "resourceId", nullable = false)
    public Integer resourceId;

    /** 客户负责人ID **/
    @Column(name = "employeeId")
    public String employeeId;
    /** 录入客户时间 **/
    @Column(name = "createDate")
    public String createDate;
    /** 人才资源共享状态  1 公有  2 私有**/
    @Column(name = "shareStatus")
    public Integer shareStatus;
    /** 人才身份证号 **/
    @Column(name = "identify")
    public String identify;
    /** 客户姓名 **/
    @Column(name = "resourceName")
    public String resourceName;
    /** 客户手机号 **/
    @Column(name = "phoneNumber")
    public String phoneNumber;
    /** 证书 **/
    @Column(name = "certificate")
    public String certificate;
    /** 到期时间 **/
    @Column(name = "endDate")
    public String endDate;
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
    /** 客户状态（潜在客户、流失客户等） **/
    @Column(name = "status")
    public Integer status;
    /** 员工姓名 **/
    @Column(name = "employeeName")
    public String employeeName;
    /** 所在城市 **/
    @Column(name = "city")
    public String city;

}
