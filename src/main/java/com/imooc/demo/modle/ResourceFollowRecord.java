package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.*;

/**
 * @Author emperor
 * @Date 2019/11/26 13:22
 * @Version 1.0
 */

@Entity
@Data
@Table(name = "resourcefollowrecord")
public class ResourceFollowRecord {
    /** 跟进记录ID **/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer id;
    /** 员工ID **/
    @Column(name = "employeeId", nullable = false)
    public String employeeId;
    /** 员工姓名 **/
    @Column(name = "employeeName", nullable = false)
    public String employeeName;
    /** 人才ID **/
    @Column(name = "resourceId", nullable = false)
    private Integer resourceId;
    /** 跟进内容 **/
    @Column(name = "content")
    private String content;
    /** 创建日期 **/
    @Column(name = "createDate", nullable = false)
    private String createDate;
    /** 人才跟进状态 **/
    @Column(name = "status")
    private Integer status;

}
