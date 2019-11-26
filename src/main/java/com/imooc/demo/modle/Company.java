package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/22 12:16
 * @Version 1.0
 */
@Entity
@Data
public class Company {

    /** 公司ID **/
    @Id
    @Column(name = "companyId")
    public String companyId;
    /** 公司名称 **/
    @Column(name = "companyName")
    public String companyName;
    /** 合同起始时间 **/
    @Column(name = "createTime")
    public Date createTime;
    /** 合同截止时间 */
    @Column(name = "endTime")
    public Date endTime;
    /** 职位 **/
    @Column(name = "occupation")
    public String occupation;
    /** 公司联系人姓名 **/
    @Column(name = "contactorName")
    public String contactorName;
    /** 联系人性别 **/
    @Column(name = "gender")
    public Integer gender;
    /**联系人手机号 **/
    @Column(name = "iphoneNumber")
    public String iphoneNumber;
    /** 公司类型 **/
    @Column(name = "companyCategory")
    public String companyCategory;
}
