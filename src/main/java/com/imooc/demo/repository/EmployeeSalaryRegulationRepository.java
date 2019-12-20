package com.imooc.demo.repository;

import com.imooc.demo.model.EmployeeSalaryRegulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeSalaryRegulationRepository extends JpaRepository<EmployeeSalaryRegulation, Integer> {
    EmployeeSalaryRegulation findEmployeeSalaryRegulationByEmployeeIdAndMonth(String employeeId, String month);

}
