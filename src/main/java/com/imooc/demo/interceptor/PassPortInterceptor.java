package com.imooc.demo.interceptor;

import com.imooc.demo.modle.Employee;
import com.imooc.demo.modle.HostHolder;
import com.imooc.demo.modle.LoginTicket;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.LoginTicketService;
import com.imooc.demo.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/21 19:27
 * @Version 1.0
 */
@Component
public class PassPortInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketService loginTicketService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String ticket = null;
        if(httpServletRequest.getCookies() != null){
            for(Cookie cookie : httpServletRequest.getCookies()){
                if(cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        //去数据库中查询cookie是否存在
        if(ticket != null){
            LoginTicket loginTicket = loginTicketService.selectByTicket(ticket);
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                return true;
            }
            Employee employee = employeeService.getEmployeeByEmployeeId(loginTicket.getEmployeeId());
            hostHolder.setUser(employee);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null && hostHolder.getUser() != null){
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception exception) throws Exception {

        hostHolder.clear();
    }
}
