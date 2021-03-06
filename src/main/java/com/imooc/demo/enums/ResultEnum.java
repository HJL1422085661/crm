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
    CREATE_PUBLIC_BUSINESS_ERROR(24, "创建共享企业发生错误"),
    TOKEN_IS_EMPTY(25, "token不能为空"),
    RESOURCE_LIST_EMPTY(26, "人才资源为空"),
    CREATE_PAY_BACK_RECORD_ERROR(27, "创建回款记录发生错误"),
    UPDATE_PAY_BACK_RECORD_ERROR(28, "修改回款记录发生错误"),
    COMMON_EMPLOYEE_NO_RIGHT(29, "普通员工无权访问"),
    SELECT_PAY_BACK_RECORD_PARAM_ERROR(30, "查看回款记录参数错误"),
    PARSE_TIME_EXCEPTION(31, "日期格式转化发生异常"),
    RESOURCE_FOLLOW_RECORD_EMPTY(32, "人才跟进记录为空"),
    CREATE_RESOURCE_FOLLOW_RECORD_ERROR(33, "创建人才跟进记录错误"),
    UPDATE_RESOURCE_EXCEPTION(34, "更新人才资源信息发生异常"),
    MODIFY_DEL_COMPANY_ERROR(35, "改删企业资源息发生错误"),
    MODIFY_DEL_COMPANY_EXCEPTION(36, "改删企业资源息发生异常"),
    MANAGER_UPDATE_RESOURCE_INFO_ERROR(37, "管理员更新人才资源信息发生错误"),
    CREATE_COMPANY_ERROR(38, "创建公司信息发生错误"),
    COMPANY_LIST_EMPTY(39, "公司资源列表为空"),
    UPDATE_COMPANY_INFO_ERROR(40, "更新企业信息发生错误"),
    UPDATE_COMPANY_INFO_EXCEPTION(41, "更新企业信息发生异常"),
    DELETE_COMPANY_INFO_ERROR(42, "删除企业信息发生错误"),
    DELETE_COMPANY_INFO_EXCEPTION(43, "删除企业信息发生异常"),
    RESOURCE_TEMP_LIST_EMPTY(44, "人才管理代办事项为空"),
    UPDATE_RESOURCE_TEMP_ERROR(45, "更新人才临时表错误"),
    CHECK_SUCCESS(46, "审批成功"),
    COMPANY_FOLLOW_RECORD_EMPTY(47, "公司跟进记录为空"),
    CREATE_COMPANY_FOLLOW_RECORD_ERROR(48, "创建企业跟进记录错误"),
    UPDATE_RESOURCE_SUCCESS(49, "更新人才成功"),
    DELETE_RESOURCE_SUCCESS(50, "删除人才成功"),
    UPDATE_COMPANY_SUCCESS(51, "更新公司成功"),
    UPDATE_COMPANY_ERROR(52, "更新公司失败"),
    DELETE_COMPANY_SUCCESS(53, "删除公司成功"),
    DELETE_COMPANY_ERROR(54, "删除公司失败"),
    MANAGER_DELETE_COMPANY_INFO_ERROR(55, "管理员删除公司失败"),
    MANAGER_UPDATE_COMPANY_INFO_ERROR(56, "管理员修改公司失败"),
    REJECT_UPDATE_SUCCESS(57, "管理员审批（拒绝修改）成功"),
    REJECT_DELETE_SUCCESS(58, "管理员审批（拒绝删除）成功"),
    CREATE_RESOURCE_BUSINESS_ERROR(59, "创建人才订单失败"),
    RESOURCE_NOT_EXIST(60, "人才不存在"),
    CREATE_COMPANY_BUSINESS_ERROR(61, "创建公司订单失败"),
    DELETE_RESOURCE_BUSINESS_ERROR(62, "删除人才订单失败"),
    DELETE_COMPANY_BUSINESS_ERROR(63, "删除公司订单失败"),
    RESOURCE_BUSINESS_NOT_EXIST(64, "人才订单不存在"),
    BUSINESS_BUSINESS_NOT_EXIST(65, "公司订单不存在"),
    GET_RESOURCE_BUSINESS_SUCCESS(66, "获取人才订单成功"),
    GET_COMPANY_BUSINESS_SUCCESS(67, "获取公司订单成功"),
    COMPANY_NOT_EXIST(68, "创建公司订单该公司不存在"),
    CREATE_RESOURCE_BUSINESS__PAYBACK_ERROR(69, "创建人才订单回款发生错误"),
    PAYBACK_RECORD_NOT_EXIST(70, "回款记录为空"),
    RESOURCE_PAYBACK_LIST_EMPTY(71, "人才订单回款为空"),
    COMPANY_PAYBACK_LIST_EMPTY(72, "公司订单回款为空"),
    COMPANY_RESOURCE_NOT_EXIST(73, "没有该人才订单"),
    COMPANY_BUSINESS_NOT_EXIST(74, "没有该公司订单"),
    DELETE_BUSINESS_ERROR(75, "删除订单错误"),
    SAVE_PERSONAL_INFO_ERROR(76, "保存个人信息发生错误"),
    USER_PHONE_EXIST(77, "手机号已存在"),
    SAVE_COMPANY_ERROR(78, "保存公司信息失败"),
    EMAIL_EMPTY(79, "邮箱为空"),
    SEND_MSG_ERROR(80, "发送验证码失败"),
    CORRECT_CODE(81, "验证码正确"),
    WRONG_CODE(82, "验证码错误"),
    RESET_PWD_ERROR(83, "重置密码失败"),
    RESET_PWD_SUCCESS(84, "重置密码成功"),
    SEND_CODE_SUCCESS(85, "发送验证码成功"),
    CREATE_COMPANY_BUSINESS_SUCCESS(86, "创建人才订单成功"),
    DELETE_RESOURCE_BUSINESS_SUCCESS(87, "删除人才订单成功"),
    DUPLICATE_PHONE(88, "电话号码已存在"),
    VALID_TOKEN(89, "token有效"),
    INVALLID_TOKEN(90, "token已过期"),
    EMPLOYEE_SALARY_EMPTY(91, "订单列表为空"),
    CREATE_SALARY_REGULATION_SUCCESS(92, "创建员工工资结算规则成功"),
    CREATE_SALARY_REGULATION_ERROR(93, "创建员工工资结算规则失败"),
    PAYBACK_TOO_MUCH(94, "回款金额大于欠款金额"),
    PAYBACK_RECORD_TEMP_NOT_EXIST(95, "回款记录空"),
    EXIST_PAYBACK_RECORD_ALREADY(96, "该订单已存在未审核回款记录"),
    FILE_IS_EMPTY(97, "文件为空"),
    IMPORT_FILE_EXCEPTION(98, "导入数据发生异常"),
    PASS_PAYBACK_SUCCESS(99, "审批回款成功(同意)"),
    REJECT_PAYBACK_SUCCESS(100, "审批回款成功（驳回）"),
    DOWNLOAD_EUCCESS(101, "导出人才信息成功"),





    ;
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
