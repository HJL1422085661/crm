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
    public String companyName;
    /** 合同起始时间 **/
    public Date createTime;
    /** 合同截止时间 */
    public Date endTime;
    /** 职位 **/
    public String occupation;
    /** 公司联系人姓名 **/
    public String contactorName;
    /** 联系人性别 **/
    public String sex;
    /**联系人手机号 **/
    public String iphoneNumber;
}
