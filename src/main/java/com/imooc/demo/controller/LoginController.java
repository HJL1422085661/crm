package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.Employee;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.LoginTicketService;
import com.imooc.demo.utils.BeanCopyUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashMap;
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
    @Autowired
    public LoginTicketService loginTicketService;

    @PostMapping("/login")
    public ResultVO<Map<String, String>> login(@RequestBody Employee employee,
                                               @RequestParam(value = "remember", defaultValue = "false") Boolean remember,
                                               HttpServletResponse response) {
        try {
            Map<String, Object> map = employeeService.login(employee.getEmployeeId(), employee.getPassWord());
            if (map.containsKey("ticket")) {
                String token = map.get("ticket").toString();
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                if (remember) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                return getPersonalInfo(token, employee.getEmployeeId());
            }
            return ResultVOUtil.error(map);
        } catch (Exception e) {
            log.error("【登陆异常】" + e.getMessage());
            return ResultVOUtil.error(ResultEnum.LOGIN_ERROR);
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        employeeService.logout(ticket);

        return "redirect:/crm/login";
    }

    /**
     * 获取个人信息
     *
     * @param employeeId
     * @return
     */
    public ResultVO<Map<String, String>> getPersonalInfo(String token, String employeeId) {
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("user_name", employee.getEmployeeName());
        map.put("user_Id", employee.getEmployeeId());
        map.put("user_role", employee.getEmployRole().toString());

        return ResultVOUtil.success(map);
    }

    /**
     * 修改个人信息
     *
     * @param employee
     * @param request
     * @return
     */
    @Modifying
    @Transactional
    @PostMapping("/updatePersonalInfo")
    public ResultVO<Map<String, String>> updatePersonalInfo(@RequestBody Employee employee,
                                                            HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改个人信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【修改个人信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Employee dataBaseEmployee = employeeService.getEmployeeByEmployeeId(employeeId);
        Integer role = dataBaseEmployee.getEmployRole();

        BeanUtils.copyProperties(employee, dataBaseEmployee, BeanCopyUtil.getNullPropertyNames(employee));
        dataBaseEmployee.setEmployRole(role);
        try {
            Employee returnEmployee = employeeService.createEmployee(dataBaseEmployee);
            if (returnEmployee == null) {
                log.error("【修改个人信息】发生错误");
                return ResultVOUtil.error(ResultEnum.SAVE_PERSONAL_INFO_ERROR);
            } else {
                // 先把employee密码信息等删除再返回
                returnEmployee.setPassWord("");
                returnEmployee.setSalt("");
                return ResultVOUtil.success(returnEmployee);
            }
        } catch (Exception e) {
            log.error("【修改个人信息】发生异常");
            return ResultVOUtil.error(ResultEnum.SAVE_PERSONAL_INFO_EXCEPTION);
        }

    }

}
