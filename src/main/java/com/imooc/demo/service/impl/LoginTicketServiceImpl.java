package com.imooc.demo.service.impl;

import com.imooc.demo.model.LoginTicket;
import com.imooc.demo.repository.LoginTicketRepository;
import com.imooc.demo.service.LoginTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author emperor
 * @Date 2019/11/21 19:44
 * @Version 1.0
 */
@Service
public class LoginTicketServiceImpl implements LoginTicketService {

    @Autowired
    private LoginTicketRepository loginTicketRepository;
    @Override
    public void addTicket(LoginTicket ticket) {

    }

    @Override
    public LoginTicket selectByTicket(String ticket) {
        return null;
    }

    @Override
    public void updateStatus(String ticket, int status) {

    }

    @Override
    public String addLoginTicket(String employeeId) {
        return null;
    }

    @Override
    public String getEmployeeIdByTicket(String ticket) {
//        System.out.println("ticket: " + ticket);
//        String res = loginTicketRepository.getEmployeeIdByTicket(ticket);
//        System.out.println("res:" + res);
        LoginTicket loginTicket = loginTicketRepository.findLoginTicketByTicket(ticket);
        System.out.println("为空:" + loginTicket == null);
        return loginTicket.getEmployeeId();

    }

    @Override
    public LoginTicket findLoginTicketByTicket(String ticket) {
        return loginTicketRepository.findLoginTicketByTicket(ticket);
    }
}
