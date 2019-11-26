package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/19 21:27
 * @Version 1.0
 */
@Entity
@Data
public class Resource {

    /** 人才ID **/
    @Id
    @Column(name = "resourceId")
    public String resourceId;

    /** 客户负责人ID **/
    public String employeeId;
    /** 录入客户时间 **/
    public Date createTime;
    /** 人才资源共享状态 **/
    public String shareStatus;
    /** 人才身份证号 **/
    public String identify;
    /** 客户姓名 **/
    public String resourceName;
    /** 客户手机号 **/
    public String phone;
    /** 证书 **/
    public String certificate;
    /** 到期时间 **/
    public Date endTime;
    /** 注册省份 **/
    public String province;
    /** QQ **/
    public String QQ;
    /** 性别 **/
    public String sex;
    /** 邮箱 **/
    public String email;
    /** 备注 **/
    public String info;





}
