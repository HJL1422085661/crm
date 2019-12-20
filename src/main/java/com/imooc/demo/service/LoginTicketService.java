package com.imooc.demo.service;

import com.imooc.demo.model.LoginTicket;

/**
 * @Author emperor
 * @Date 2019/11/21 19:37
 * @Version 1.0
 */

public interface LoginTicketService {

    void addTicket(LoginTicket ticket);

    LoginTicket selectByTicket(String ticket);

    LoginTicket findLoginTicketByTicket(String ticket);

    void updateStatus(String ticket, int status);

    String addLoginTicket(String employeeId);

    String getEmployeeIdByTicket(String ticket);

}
