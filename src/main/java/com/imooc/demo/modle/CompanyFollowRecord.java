package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
    @Column(name = "id", nullable = false)
    public String id;
    /** 订单ID **/
    @Column(name = "businessId", nullable = false)
    public String businessId;
    /** 员工ID **/
    @Column(name = "employeeId", nullable = false)
    private String employeeId;
    /** 跟进内容 **/
    @Column(name = "content")
    private String content;

    /** 创建日期 **/
    @Column(name = "createTime", nullable = false)
    private Date createTime;


}
