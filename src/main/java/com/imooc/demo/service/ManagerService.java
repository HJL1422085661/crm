package com.imooc.demo.service;

import com.imooc.demo.model.Employee;


/**
 * @Author emperor
 * @Date 2019/11/18 15:01
 * @Version 1.0
 */
public interface ManagerService {
    Employee register(Employee employee);
    Employee getManagerByEmployeeId(String employeeId);
}
