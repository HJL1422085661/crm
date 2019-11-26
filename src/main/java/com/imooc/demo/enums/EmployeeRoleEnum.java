package com.imooc.demo.enums;


import lombok.Getter;

/**
 * @Author emperor
 * @Date 2019/10/20 16:43
 * @Version 1.0
 */
@Getter
public enum EmployeeRoleEnum {
    EMPLOYEE(1,"普通员工"),
    MANAGER(2,"经理")
    ;
    private Integer code;
    private String message;

    EmployeeRoleEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }


}
