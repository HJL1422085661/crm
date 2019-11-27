package com.imooc.demo.service;

import com.imooc.demo.modle.Employee;


/**
 * @Author emperor
 * @Date 2019/11/18 15:01
 * @Version 1.0
 */
public interface ManagerService {
    Employee register(String employeeId, String passWord, Integer employeeRole, Integer gender);
    Employee getManagerByEmployeeId(String employeeId);
}
