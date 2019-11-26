package com.imooc.demo.repository;

import com.imooc.demo.modle.LoginTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author emperor
 * @Date 2019/11/21 10:40
 * @Version 1.0
 */
public interface LoginTicketRepository extends JpaRepository<LoginTicket, Integer>, JpaSpecificationExecutor<LoginTicket> {

    LoginTicket findLoginTicketByTicket(String ticket);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "SELECT employeeId FROM loginticket WHERE ticket = ?1 ")
    String getEmployeeIdByTicket(String ticket);

}
