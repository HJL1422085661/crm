package com.imooc.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="loginticket")
public class LoginTicket {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "employeeId")
    private String employeeId;

    @Column(name = "expired")
    private Date expired; //过期

    @Column(name = "status")
    private int status;// 0有效，1无效

    @Column(name = "ticket")
    private String ticket;

}
