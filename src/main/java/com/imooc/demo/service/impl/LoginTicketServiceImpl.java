package com.imooc.demo.service.impl;

import com.imooc.demo.modle.LoginTicket;
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
    public String findEmployeeIdByTicket(String ticket) {
        return loginTicketRepository.findEmployeeIdByTicket(ticket);
    }
}
