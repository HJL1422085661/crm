package com.imooc.demo.service;

import com.imooc.demo.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    Employee createEmployee(Employee employee);
    Integer deleteEmployeeByEmployeeId(String employeeId);
    Boolean updateEmployeeRoleByEmployeeId(Integer employeeRole, String employeeId);
    List<Employee> findAllEmployee();
    List<Employee> findEmployeeByEmployeeRole(Integer role);
    List<Employee> findEmployeeByEmail(String email);
    List<Employee> findEmployeeByManagerId(String managerId);
    Boolean existsByPhoneNumber(String phoneNumber);
    Page<Employee> findAllEmployeePageable(Pageable pageable);
}
