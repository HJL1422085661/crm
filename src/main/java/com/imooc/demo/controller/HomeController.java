package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.Employee;
import com.imooc.demo.modle.HostHolder;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author emperor
 * @Date 2019/11/21 20:03
 * @Version 1.0
 */
@Controller
@RestController
@Slf4j
public class HomeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 获取用户信息
     * @param employeeId
     * @return
     */
    @GetMapping("/getInfo")
    public ResultVO<Map<String, String>> getInfo(@RequestParam("employeeId") String employeeId){
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        Map<String, String> map = new HashMap<>();
        map.put("employeeId", employee.getEmployeeId());
        map.put("employeeName", employee.getEmployeeName());
        map.put("sex", employee.getSex());
        map.put("iphoneNumber", employee.getIphoneNumber());

        return ResultVOUtil.success(map);
    }
    @Modifying
    @Transactional
    @PostMapping("/saveInfo")
    public ResultVO<Map<String, String>> saveInfo(@RequestParam("employeeId") String employeeId,
                                                  @RequestParam("employee") Employee employee){
        Employee employee1 = employeeService.getEmployeeByEmployeeId(employeeId);
        BeanUtils.copyProperties(employee, employee1);
        try {
            employeeService.saveEmployee(employee);
        }catch (Exception e){
            log.error("【用户信息】保存发生异常");
            return ResultVOUtil.error(ResultEnum.SAVE_PERSONAL_INFO_EXCEPTION);
        }
        return ResultVOUtil.success();
    }

}
