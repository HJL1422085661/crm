package com.imooc.demo.enums;

import lombok.Getter;

/**
 * @author emperor
 * @date 2019/7/21 14:07
 */
@Getter
public enum ResultEnum {

    PARAM_ERROR(1, "参数不正确"),
    USER_ID_ISNULL(2,"用户ID不能为空"),
    PASSWORD_ISNULL(3, "密码不能为空"),
    MANAGER_NOT_EXIST(4,"管理员不存在"),
    USER_ID_EXIST(5,"用户ID已存在"),
    EMPLOYEE_NOT_EXIST(6,"用户不存在"),
    USER_IDENTIFY_ERROR(7,"用户身份错误"),
    MANAGER_IDENTIFY_ERROR(8, "管理员身份错误"),
    REGISTER_EXCEPTION(9, "注册异常"),
    PASSWORD_LENGTH_SHORT(10, "密码长度小于6位"),
    USER_ID_NOT_EXIST(11,"用户ID不存在"),
    PASSWORD_IS_WRONG(12,"用户密码错误"),
    LOGIN_ERROR(13, "登陆出现异常"),
    SAVE_PERSONAL_INFO_EXCEPTION(14, "保存用户信息发生异常"),
    UPDATE_EMPLOYEE_EXCEPTION(15, "修改员工权限发生异常"),
    DELETE_EMPLOYEE_EXCEPTION(16, "删除员工发生异常"),
    SET_RESOURCE_PUBLIC_ERROR(17, "设置员工为公有状态发生错误"),
    EMPLOYEE_NOT_DELETE(18, "该员工存在正在进行的订单，当前不能删除"),
    SAVE_RESOURCE_ERROR(19,"录入人才信息发生错误"),
    UPDATE_RESOURCE_ERROR(20,"更新人才信息发生错误"),
    DELETE_RESOURCE_ERROR(21,"删除人才信息发生错误"),
    UPDATE_EMPLOYEE_ROLE_ERROR(22, "修改员工权限发生错误"),
    CREATE_PUBLIC_RESOURCE_ERROR(23, "创建共享人才发生错误"),
    CREATE_PUBLIC_BUSINESS_ERROR(24, "创建共享企业发生错误");
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
