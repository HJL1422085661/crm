package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author emperor
 * @Date 2019/11/21 18:20
 * @Version 1.0
 */
@Slf4j
@Controller
@RestController
public class LoginController {

    @Autowired
    public EmployeeService employeeService;
    @PostMapping("/login")
    public ResultVO<String> login(@RequestParam("employeeId") String employeeId,
                                  @RequestParam("passWord") String passWord,
                                  @RequestParam(value = "remember", defaultValue = "false") boolean remember,
                                  HttpServletResponse response){
        try{
            Map<String, Object> map = employeeService.login(employeeId, passWord);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                if(remember){
                    cookie.setMaxAge(3600*24*5);
                }
                return ResultVOUtil.success(map);
            }
            return ResultVOUtil.error(map);
        }catch (Exception e){
            log.error("【登陆异常】" + e.getMessage());
            return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
        }
    }
   @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        employeeService.logout(ticket);

        return "redirect:/login";
    }
}
