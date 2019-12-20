package com.imooc.demo.service;

import com.imooc.demo.model.EmployeeSalaryRegulation;

public interface EmployeeSalaryRegulationService {
    EmployeeSalaryRegulation findEmployeeSalaryRegulationByEmployeeIdAndMonth(String employeeId, String month);

    EmployeeSalaryRegulation createEmployeeSalaryRegulation(EmployeeSalaryRegulation employeeSalaryRegulation);
}
