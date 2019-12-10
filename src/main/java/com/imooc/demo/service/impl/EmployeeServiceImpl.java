package com.imooc.demo.service.impl;

import com.imooc.demo.modle.LoginTicket;
import com.imooc.demo.repository.EmployeeRepository;
import com.imooc.demo.repository.LoginTicketRepository;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.modle.Employee;
import com.imooc.demo.utils.PassUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author emperor
 * @Date 2019/10/21 9:59
 * @Version 1.0
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LoginTicketRepository loginTicketRepository;


    @Override
    public Map<String, Object> login(String employeeId, String passWord) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isEmpty(employeeId)) {
            log.error("【用户登录】用户ID不能为空");

            map.put("msg", "用户ID不能为空");
            return map;
        }
        if (StringUtils.isEmpty(passWord)) {
            log.error("【用户登录】密码不能为空");
            map.put("msg", "用户密码不能为空");
            return map;
        }
        Employee employee = employeeRepository.getEmployeeByEmployeeId(employeeId);
        if (employee == null) {
            log.error("【用户登录】用户ID不存在");
            map.put("msg", "用户ID不存在");
            return map;
        }
        //验证密码
        if (!PassUtil.MD5(passWord + employee.getSalt()).equals(employee.getPassWord())) {
            log.error("【用户登录】密码输入错误");
            map.put("msg", "用户密码错误");
            return map;
        }

        //ticket
        String ticket = addLoginTicket(employee.getEmployeeId());
        map.put("ticket", ticket);

        return map;
    }

    @Override
    public Employee getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.getEmployeeByEmployeeId(employeeId);
    }

    @Override
    public String addLoginTicket(String employeeId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setEmployeeId(employeeId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 365 * 24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));

        loginTicketRepository.saveAndFlush(ticket);

        return ticket.getTicket();
    }

    @Override
    public void logout(String ticket) {
        LoginTicket loginTicket = loginTicketRepository.findLoginTicketByTicket(ticket);
        loginTicket.setStatus(1);
        loginTicketRepository.save(loginTicket);
    }

    @Override
    public Boolean saveEmployee(Employee employee) {
        Employee employee1 = employeeRepository.saveAndFlush(employee);
        if (employee1 != null) return true;
        else return false;
    }

    @Modifying
    @Transactional
    @Override
    public Integer deleteEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.deleteEmployeeByEmployeeId(employeeId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean updateEmployeeRoleByEmployeeId(Integer employeeRole, String employeeId) {
        if (employeeRepository.updateEmployeeRoleByEmployeeId(employeeRole, employeeId) != 0)
            return true;
        return false;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.saveAndFlush(employee);
    }

    @Override
    public List<Employee> findAllEmployee() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> findEmployeeByEmployeeRole(Integer role) {
        return employeeRepository.findEmployeeByEmployeeRole(role);
    }

    @Override
    public List<Employee> findEmployeeByManagerId(String managerId) {
        return employeeRepository.findEmployeeByEmployeeManagerId(managerId);
    }

    @Override
    public Boolean existsByPhoneNumber(String phoneNumber) {
        return employeeRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public List<Employee> findEmployeeByEmail(String email) {
        return employeeRepository.findEmployeeByEmail(email);
    }
}
