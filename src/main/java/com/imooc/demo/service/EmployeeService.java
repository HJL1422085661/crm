package com.imooc.demo.service;

import com.imooc.demo.modle.Employee;

import java.util.Map;

/**
 * @Author emperor
 * @Date 2019/10/21 9:56
 * @Version 1.0
 */

public interface EmployeeService {

    /** 员工登录 **/
    Map<String, Object> login(String employeeId, String passWord);
    Employee getEmployeeByEmployeeId(String employeeId);
    String addLoginTicket(String employeeId);
    void logout(String ticket);
    Boolean saveEmployee(Employee employee);
    Boolean deleteEmployee(String employeeId);
    Boolean updateEmployeeRoleByEmployeeId(Integer employeeRole, String employeeId);

}
