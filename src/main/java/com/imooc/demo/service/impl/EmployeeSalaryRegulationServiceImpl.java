package com.imooc.demo.service.impl;

import com.imooc.demo.model.EmployeeSalaryRegulation;
import com.imooc.demo.repository.EmployeeSalaryRegulationRepository;
import com.imooc.demo.service.EmployeeSalaryRegulationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmployeeSalaryRegulationServiceImpl implements EmployeeSalaryRegulationService {
    @Autowired
    public EmployeeSalaryRegulationRepository employeeSalaryRegulationRepository;

    @Override
    public EmployeeSalaryRegulation createEmployeeSalaryRegulation(EmployeeSalaryRegulation employeeSalaryRegulation) {
        try {
            return employeeSalaryRegulationRepository.saveAndFlush(employeeSalaryRegulation);
        } catch (Exception e) {
            log.error("【创建员工工资结算规则】发生异常");
            return null;
        }
    }


    @Override
    public EmployeeSalaryRegulation findEmployeeSalaryRegulationByEmployeeIdAndMonth(String employeeId, String month) {
        return employeeSalaryRegulationRepository.findEmployeeSalaryRegulationByEmployeeIdAndMonth(employeeId, month);
    }
}
