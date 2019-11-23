package com.imooc.demo.modle;

import org.springframework.stereotype.Component;

/**
 * Created by emperor on 2018/10/8.
 *
 * @date 2018/10/8 9:03
 */
@Component
public class HostHolder {
    private static ThreadLocal<Employee> users = new ThreadLocal<Employee>();

    public Employee getUser(){

        return users.get();
    }
    public void setUser(Employee user){

        users.set(user);

    }
    public void clear(){

        users.remove();
    }
}
