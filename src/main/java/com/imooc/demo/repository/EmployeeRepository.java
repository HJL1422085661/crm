package com.imooc.demo.repository;

import com.imooc.demo.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/10/21 9:51
 * @Version 1.0
 */
public interface EmployeeRepository extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {

    //通过员工ID和密码查询用户
    Employee getEmployeeByEmployeeIdAndPassWord(String employeeId, String passWord);
    Employee getEmployeeByEmployeeId(String employeeId);
    Integer deleteEmployeeByEmployeeId(String employeeId);

    List<Employee> findAll();

    List<Employee> findEmployeeByEmployeeRole(Integer employeeRole);

    List<Employee> findEmployeeByEmail(String email);

    List<Employee> findEmployeeByEmployeeManagerId(String employeeManagerId);

    Boolean existsByPhoneNumber(String phoneNumber);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE employee SET employeeRole = ?1  WHERE employeeId = ?2 ")
    int updateEmployeeRoleByEmployeeId(Integer employeeRole, String employeeId);

    @Override
    Page<Employee> findAll(Pageable pageable);

    Employee getEmployeeByEmployeeName(String employeeName);
}
