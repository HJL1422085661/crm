package com.imooc.demo.enums;


import lombok.Getter;

/**
 * @Author emperor
 * @Date 2019/10/20 16:43
 * @Version 1.0
 */
@Getter
public enum BusinessStatusRoleEnum {
    PROCESSING(0,"订单进行中"),
    COMPLETED(1,"订单完成"),
    FAILURE(2,"订单交易失败")
    ;
    private Integer code;
    private String message;

    BusinessStatusRoleEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }


}
