package com.imooc.demo.repository;

import com.imooc.demo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author emperor
 * @Date 2019/11/19 19:41
 * @Version 1.0
 */
public interface ManagerRepository extends JpaRepository<Employee, String> {

    //通过员工ID和密码查询用户
    Employee getManagerByEmployeeIdAndPassWordAndEmployeeRole(String employeeId, String passWord, Integer employeeRole);
    //通过员工ID查询是否存在该用户
    Employee getManagerByEmployeeId(String employeeId);


}
