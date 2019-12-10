package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.Employee;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.LoginTicketService;
import com.imooc.demo.utils.BeanCopyUtil;
import com.imooc.demo.utils.PassUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Slf4j
@Controller
@RestController
public class LoginController {

    @Autowired
    public EmployeeService employeeService;
    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    private JavaMailSender mailSender;

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
//            response.setStatus(ResultEnum.LOGIN_ERROR.getCode());
//            response.sendError(ResultEnum.LOGIN_ERROR.getCode(), map.toString());
            return ResultVOUtil.fail(map, response);
        } catch (Exception e) {
            log.error("【登陆异常】" + e.getMessage());
            return ResultVOUtil.fail(ResultEnum.LOGIN_ERROR, response);
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        employeeService.logout(ticket);

        return "redirect:/crm/login";
    }


    /**
     * 获取验证码
     *
     * @param response
     * @return
     */
    @RequestMapping(value = "/getCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultVO<Map<String, String>> getCode(@RequestBody HashMap paramMap, HttpServletResponse response) throws MessagingException {

        String email = paramMap.get("employeeEmail").toString();
        if (StringUtils.isEmpty(email)) {
            log.error("【找回密码】邮箱不能为空");
            return ResultVOUtil.fail(ResultEnum.EMAIL_EMPTY, response);
        }
        // 根据email在数据库中匹配
        List<Employee> employeeList = employeeService.findEmployeeByEmail(email);
        if (employeeList.size() == 0 || employeeList == null) {
            log.error("【找回密码】没有匹配的邮箱");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeList.get(0);

        // 生成邮箱验证码，5分钟后过期
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        Timestamp verifyCodeExpireDate = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000);
        // 将验证码和过期日期存到数据库
        employee.setVerifyCode(verifyCode);
        employee.setVerifyCodeExpireTime(verifyCodeExpireDate);
        Boolean flag = employeeService.saveEmployee(employee);
        if (!flag) {
            log.error("【找回密码】发送验证码失败");
            return ResultVOUtil.fail(ResultEnum.SEND_MSG_ERROR, response);
        }
        // 构建验证码内容
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title></title></head><body>");
        stringBuilder.append("您好<br/>");
        stringBuilder.append("您的验证码是：").append(verifyCode).append("<br/>");
        stringBuilder.append("您可以复制此验证码并返回至XXX，以验证您的邮箱。<br/>");
        stringBuilder.append("此验证码只能使用一次，在5分钟内有效。验证成功则自动失效。<br/>");
        stringBuilder.append("如果您没有进行上述操作，请忽略此邮件。");
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        //发送验证码到邮箱
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("wangchiyaan@163.com"); //这里只是设置username 并没有设置host和password，因为host和password在springboot启动创建JavaMailSender实例的时候已经读取了
        mimeMessageHelper.setTo(email);
        mimeMessage.setSubject("邮箱验证-XXX");
        mimeMessageHelper.setText(stringBuilder.toString(), true);
        mailSender.send(mimeMessage);

        return ResultVOUtil.success(ResultEnum.SEND_CODE_SUCCESS);
    }


    /**
     * 验证码比对
     *
     * @param response
     * @return
     */
    @RequestMapping("/verifyCode")
    public ResultVO<Map<String, String>> verifyCode(@RequestBody HashMap paramMap, HttpServletResponse response){

        String email = paramMap.get("employeeEmail").toString();
        String code = paramMap.get("verifyCode").toString();
        if (StringUtils.isEmpty(email)) {
            log.error("【验证码比对】邮箱不能为空");
            return ResultVOUtil.fail(ResultEnum.EMAIL_EMPTY, response);
        }
        // 根据email在数据库中匹配
        List<Employee> employeeList = employeeService.findEmployeeByEmail(email);
        if (employeeList.size() == 0 || employeeList == null) {
            log.error("【找回验证码比对密码】没有匹配的邮箱");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeList.get(0);

        // 验证码比对
        if (code.equals(employee.getVerifyCode())){
            return ResultVOUtil.success(ResultEnum.CORRECT_CODE);
        }else {
            return ResultVOUtil.success(ResultEnum.WRONG_CODE);
        }
    }
     /**
     * 验证码比对
     *
     * @param response
     * @return
     */
    @RequestMapping("/resetPassword")
    public ResultVO<Map<String, String>> resetPassword(@RequestBody HashMap paramMap, HttpServletResponse response){

        String email = paramMap.get("employeeEmail").toString();
        String passWord = paramMap.get("newPassword").toString();
        if (StringUtils.isEmpty(email)) {
            log.error("【重置密码】邮箱不能为空");
            return ResultVOUtil.fail(ResultEnum.EMAIL_EMPTY, response);
        }
        // 根据email在数据库中匹配
        List<Employee> employeeList = employeeService.findEmployeeByEmail(email);
        if (employeeList.size() == 0 || employeeList == null) {
            log.error("【重置密码】没有匹配的邮箱");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeList.get(0);

        // 重置密码
        employee.setPassWord(PassUtil.MD5(passWord + employee.getSalt()));
        Boolean flag = employeeService.saveEmployee(employee);
        if (!flag){
            log.error("【重置密码】失败");
            return ResultVOUtil.fail(ResultEnum.RESET_PWD_ERROR, response);
        }else {
            return ResultVOUtil.success(ResultEnum.RESET_PWD_SUCCESS);
        }
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
        map.put("user_role", employee.getEmployeeRole().toString());
        map.put("user_phonenumber", employee.getPhoneNumber());
        map.put("user_email", employee.getEmail());

        return ResultVOUtil.success(map);
    }

    /**
     * 获取个人信息(外部接口API)
     *
     * @param request
     * @return
     */
    @GetMapping("/getPersonalInfoApi")
    public ResultVO<Map<String, String>> getPersonalInfoApi(HttpServletRequest request,
                                                            HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【获取个人信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取个人信息】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("user_name", employee.getEmployeeName());
        map.put("user_Id", employee.getEmployeeId());
        map.put("user_role", employee.getEmployeeRole().toString());
        map.put("user_phonenumber", employee.getPhoneNumber());
        map.put("user_email", employee.getEmail());

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
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【修改个人信息】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【修改个人信息】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee dataBaseEmployee = employeeService.getEmployeeByEmployeeId(employeeId);
        //Integer role = dataBaseEmployee.getEmployeeRole();

        BeanUtils.copyProperties(employee, dataBaseEmployee, BeanCopyUtil.getNullPropertyNames(employee));
        //dataBaseEmployee.setEmployeeRole(role);
        try {
            Employee returnEmployee = employeeService.createEmployee(dataBaseEmployee);
            if (returnEmployee == null) {
                log.error("【修改个人信息】发生错误");
                return ResultVOUtil.fail(ResultEnum.SAVE_PERSONAL_INFO_ERROR, response);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("user_name", employee.getEmployeeName());
                map.put("user_phonenumber", employee.getPhoneNumber());
                map.put("user_email", employee.getEmail());
                return ResultVOUtil.success(map);
            }
        } catch (Exception e) {
            log.error("【修改个人信息】发生异常");
            return ResultVOUtil.fail(ResultEnum.SAVE_PERSONAL_INFO_EXCEPTION, response);
        }

    }

}
