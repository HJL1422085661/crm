package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.Business;
import com.imooc.demo.modle.Employee;
import com.imooc.demo.modle.HostHolder;
import com.imooc.demo.modle.Resource;
import com.imooc.demo.service.BusinessService;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.ResourceService;
import com.imooc.demo.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private BusinessService businessService;


    //分页显示public客户信息
    @GetMapping("/getPublicResourceList")
    public ResultVO<Map<String, String>> getPublicResourceList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageRequest request = PageRequest.of(page, size);
        Page<Resource> resourcePage = resourceService.findResourceByEmployeeId("0", request);

        return ResultVOUtil.success(resourcePage.getContent());
    }


}
