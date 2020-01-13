package com.imooc.demo.model;

import lombok.Data;

import javax.persistence.*;

@Data
public class ResourceDTO {

    /** 录入客户时间 **/
    public String createDate;

    /** 客户姓名 **/
    public String resourceName;
    /** 客户手机号 **/
    public String phoneNumber;
    /** 证书 **/
    public String certificate;
    /** 到期时间 **/
    public String endDate;
    /** 注册省份 **/
    public String province;
    /** qq **/
    public String qq;
    /** 性别 **/
    public Integer gender;
    /** 邮箱 **/
    public String email;
    /** 备注 **/
    public String info;


}
