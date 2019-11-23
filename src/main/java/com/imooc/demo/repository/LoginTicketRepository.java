package com.imooc.demo.repository;

import com.imooc.demo.modle.LoginTicket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author emperor
 * @Date 2019/11/21 10:40
 * @Version 1.0
 */
public interface LoginTicketRepository extends JpaRepository<LoginTicket, Integer> {

    LoginTicket findLoginTicketByTicket(String ticket);

}
