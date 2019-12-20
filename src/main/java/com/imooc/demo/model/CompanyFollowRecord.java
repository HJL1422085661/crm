package com.imooc.demo.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author emperor
 * @Date 2019/11/26 13:22
 * @Version 1.0
 */

@Entity
@Data
@Table(name = "companyfollowrecord")
public class CompanyFollowRecord {
    /** 跟进记录ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer id;
    /** 公司ID **/
    @Column(name = "companyId", nullable = false)
    public Integer companyId;

    /** 员工姓名 **/
    @Column(name = "employeeName", nullable = false)
    public String employeeName;
    /** 员工ID **/
    @Column(name = "employeeId", nullable = false)
    private String employeeId;

    /** 跟进内容 **/
    @Column(name = "content")
    private String content;

    /** 创建日期 **/
    @Column(name = "createDate", nullable = false)
    private String createDate;

    /** 公司跟进状态 **/
    @Column(name = "status")
    private Integer status;


}
