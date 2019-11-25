package com.imooc.demo.service;

import com.imooc.demo.modle.LoginTicket;

/**
 * @Author emperor
 * @Date 2019/11/21 19:37
 * @Version 1.0
 */

public interface LoginTicketService {

    void addTicket(LoginTicket ticket);
    LoginTicket selectByTicket(String ticket);
    void updateStatus(String ticket, int status);
    String addLoginTicket(String employeeId);
    String findEmployeeIdByTicket(String ticket);

}
