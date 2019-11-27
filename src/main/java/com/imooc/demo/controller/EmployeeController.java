package com.imooc.demo.controller;

import com.google.gson.JsonObject;
import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.*;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import javafx.scene.input.DataFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author emperor
 * @Date 2019/10/21 10:15
 * @Version 1.0
 */

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    public ResourceService resourceService;
    @Autowired
    public EmployeeService employeeService;
    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    public FollowRecordService followRecordService;
    @Autowired
    public ResourceTempService resourceTempService;
    @Autowired
    public CompanyTempService companyTempService;

    @Modifying
    @Transactional
    @PostMapping("/createResource")
    public ResultVO<Map<String, String>> createResource(@RequestBody Resource resource,
                                                        HttpServletRequest request) {

        System.out.println(resource);
        resource.setShareStatus("private");
        //封装时间参数
        Date createDate = new Date();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        resource.setCreateDate(sim.format(createDate));

        String token = TokenUtil.parseToken(request);

        if (token.equals("")) {
            log.error("【创建人才资源】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才资源】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        resource.setEmployeeId(employeeId);

        Resource resource1 = null;
        try {
            resource1 = resourceService.createResource(resource);
            if (resource1 == null) {
                log.error("【录入人才信息】发生错误");
                return ResultVOUtil.error(ResultEnum.SAVE_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【录入人才信息】发生异常");
        }
        return ResultVOUtil.success(resource1);
    }


    /**
     * 修改人才信息(不修改人才状态)
     *
     * @param resourceId
     * @param resource
     * @return
     */
    @PostMapping("/updateResource")
    public ResultVO<Map<String, String>> updateResource(@RequestParam("resourceId") Integer resourceId,
                                                        @RequestParam("resource") Resource resource,
                                                        HttpServletRequest request) {
        String token = TokenUtil.parseToken(request);
        if (token.trim().equals("")) {
            log.error("【修改人才信息】，token不能为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【修改人才信息】员工ID不存在");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        Resource resource1 = resourceService.getResourceByResourceId(resourceId);
        BeanUtils.copyProperties(resource, resource1);
        Boolean flag = resourceService.saveResource(resource);
        if (flag) {
            return ResultVOUtil.success();
        } else {
            log.error("【更新人才资源信息】发生错误");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
        }
    }

    /**
     * 修改人才状态
     *
     * @param resourceId
     * @param shareStatus
     * @return
     */
    @PostMapping("/updateResourceShareStatus")
    public ResultVO<Map<String, String>> updateResourceShareStatus(@RequestParam("resourceId") Integer resourceId,
                                                                   @RequestParam("shareStatus") String shareStatus) {
        Boolean flag = resourceService.updateShareStatusByResourceId(shareStatus, resourceId);
        //TODO
        if (flag) {
            return ResultVOUtil.success();
        } else {
            log.error("【更新人才状态】发生错误");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
        }
    }

    @PostMapping("/deleteResource")
    public ResultVO<Map<String, String>> deleteResource(@RequestParam("resourceId") Integer resourceId) {
        Boolean flag = resourceService.deleteResourceByResourceId(resourceId);
        if (flag) {
            return ResultVOUtil.success();
        } else {
            log.error("【删除人才资源信息】发生错误");
            return ResultVOUtil.error(ResultEnum.DELETE_RESOURCE_ERROR);
        }
    }

    //分页显示私有客户信息
    @GetMapping("/getResourceList")
    public ResultVO<Map<String, String>> getResourceList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         HttpServletRequest req) {
        String token = TokenUtil.parseToken(req);

        System.out.println("token is:" + token);
        if (token.equals("")) {
            log.error("【获取人才列表】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【获取人才列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        PageRequest request = PageRequest.of(page, size);
        Page<Resource> resourcePage = resourceService.findResourceByEmployeeId(employeeId, request);
        if (resourcePage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        } else {
            System.out.println(resourcePage.getContent());
            return ResultVOUtil.success(resourcePage.getContent());
        }

    }

    /**
     * 获取人才跟进记录
     *
     * @param map
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/getResourceFollows")
    public ResultVO<Map<String, String>> getResourceFollows(@RequestBody HashMap map,
                                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Integer resourceId = (Integer) map.get("resourceId");

        System.out.println("resourceId:" + resourceId);
        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<ResourceFollowRecord> resourceFollowRecords = followRecordService.getFollowRecordsByResourceId(resourceId, request);
        if (resourceFollowRecords.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_FOLLOW_RECORD_EMPTY);
        } else {
            System.out.println(resourceFollowRecords.getContent());
            return ResultVOUtil.success(resourceFollowRecords.getContent());
        }
    }

    @PostMapping("/createResourceFollow")
    public ResultVO<Map<String, String>> createResourceFollow(@RequestBody ResourceFollowRecord resourceFollowRecord,
                                                              HttpServletRequest request) {
        System.out.println(resourceFollowRecord);
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【创建人才跟进信息】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (employeeId.equals("")) return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【创建人才跟进信息】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        resourceFollowRecord.setEmployeeId(employeeId);
        resourceFollowRecord.setEmployeeName(employee.getEmployeeName());
        System.out.println("调用时：" + resourceFollowRecord.toString());
        ResourceFollowRecord followRecord = followRecordService.createResourceFollow(resourceFollowRecord);
        if (followRecord != null) {
            return ResultVOUtil.success(resourceFollowRecord);
        } else {
            log.error("【创建人才跟进信息】失败");
            return ResultVOUtil.error(ResultEnum.CREATE_FOLLOW_RECORD_ERROR);
        }

    }

    /*员工修改或者删除人才客户*/
    @Modifying
    @Transactional
    @PostMapping("/modifyAndDelResource")
    public ResultVO<Map<String, String>> modifyAndDelResource(@RequestBody ResourceTemp resourceTemp,
                                                              HttpServletRequest request){
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【改删人才资源】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【改删人才资源】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }

        ResourceTemp resourceTemp1 = null;
        try {
            resourceTemp1 = resourceTempService.createResourceTemp(resourceTemp);
            if (resourceTemp1 == null) {
                log.error("【改删人才信息】发生错误");
                return ResultVOUtil.error(ResultEnum.MODIFY_DEL_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【改删人才信息】发生异常");
        }
        return ResultVOUtil.success();

    }

    /*员工修改或者删除企业客户*/
    @Modifying
    @Transactional
    @PostMapping("/modifyAndDelCompany")
    public ResultVO<Map<String, String>> modifyAndDelCompany(@RequestBody CompanyTemp companyTemp,
                                                             HttpServletRequest request){
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【改删企业资源】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【改删企业资源】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }

        CompanyTemp companyTemp1 = null;
        try {
            companyTemp1 = companyTempService.createCompanyTemp(companyTemp);
            if (companyTemp1 == null) {
                log.error("【改删企业资源息】发生错误");
                return ResultVOUtil.error(ResultEnum.MODIFY_DEL_COMPANY_ERROR);
            }
        } catch (Exception e) {
            log.error("【改删企业资源】发生异常");
        }
        return ResultVOUtil.success();

    }

    @PostMapping("/test")
    public String test() {
        return "hello world";
    }

}
