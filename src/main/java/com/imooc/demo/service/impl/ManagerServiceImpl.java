package com.imooc.demo.service.impl;

import com.imooc.demo.modle.Employee;
import com.imooc.demo.modle.LoginTicket;
import com.imooc.demo.repository.LoginTicketRepository;
import com.imooc.demo.repository.ManagerRepository;
import com.imooc.demo.service.ManagerService;
import com.imooc.demo.utils.PassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @Author emperor
 * @Date 2019/11/18 15:05
 * @Version 1.0
 */
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    public ManagerRepository managerRepository;
    @Autowired
    public LoginTicketRepository loginTicketRepository;


    @Override
    public Employee register(Employee employee){
        employee.setSalt(UUID.randomUUID().toString().substring(0, 5));
        //对密码加密
        employee.setPassWord(PassUtil.MD5(employee.getPassWord() + employee.getSalt()));

       return managerRepository.saveAndFlush(employee);
    }

    @Override
    public Employee getManagerByEmployeeId(String employeeId) {
        return  managerRepository.getManagerByEmployeeId(employeeId);
    }
}
