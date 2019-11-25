package com.imooc.demo.modle;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="ticket")
public class Ticket {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "employeeId")
    private String employeeId;
    private Date expired; //过期
    private int status;// 0有效，1无效
    private String ticket;

}
