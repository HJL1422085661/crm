package com.imooc.demo.controller;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.Resource;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.ResourceService;
import com.imooc.demo.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    @Modifying
    @Transactional
    @PostMapping("/createResource")
    public ResultVO<Map<String, String>> createResource(@RequestParam("resource") Resource resource) {
        try {
            Boolean flag = resourceService.saveResource(resource);
            if (!flag) {
                log.error("【录入人才信息】发生错误");
                return ResultVOUtil.error(ResultEnum.SAVE_RESOURCE_ERROR);
            }
        } catch (Exception e) {
            log.error("【录入人才信息】发生异常");
        }
        return ResultVOUtil.success();
    }


    /**
     * 修改人才信息(不修改人才状态)
     * @param resourceId
     * @param resource
     * @return
     */
    @PostMapping("/updateResource")
    public ResultVO<Map<String, String>> updateResource(@RequestParam("resourceId") String resourceId,
                                                            @RequestParam("resource") Resource resource) {
        Resource resource1 = resourceService.getResourceByResourceId(resourceId);
        BeanUtils.copyProperties(resource, resource1);
        Boolean flag = resourceService.saveResource(resource);
        if(flag){
            return ResultVOUtil.success();
        }else{
            log.error("【更新人才资源信息】发生错误");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
        }
    }

    /**
     * 修改人才状态
     * @param resourceId
     * @param shareStatus
     * @return
     */
    @PostMapping("/updateResourceShareStatus")
    public ResultVO<Map<String, String>> updateResourceShareStatus(@RequestParam("resourceId") String resourceId,
                                                        @RequestParam("shareStatus") String shareStatus) {
        Boolean flag = resourceService.updateShareStatusByResourceId(shareStatus, resourceId);
        //TODO
        if(flag){
            return ResultVOUtil.success();
        }else{
            log.error("【更新人才状态】发生错误");
            return ResultVOUtil.error(ResultEnum.UPDATE_RESOURCE_ERROR);
        }
    }
    @PostMapping("/deleteResource")
    public ResultVO<Map<String, String>> deleteResource(@RequestParam("resourceId") String resourceId) {
        Boolean flag = resourceService.deleteResourceByResourceId(resourceId);
        if(flag){
            return ResultVOUtil.success();
        }else{
            log.error("【删除人才资源信息】发生错误");
            return ResultVOUtil.error(ResultEnum.DELETE_RESOURCE_ERROR);
        }
    }

    //分页显示私有客户信息
    @GetMapping("/getResourceList")
    public ResultVO<Map<String, String>> getResourceList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        String employeeId = employeeService.getEmployeeIdByTicket(token);
        if(StringUtils.isEmpty(employeeId)){
            log.error("【获取人才列表】 employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        PageRequest request = PageRequest.of(page, size);
        Page<Resource> resourcePage = resourceService.findResourceByEmployeeId(employeeId, request);

        return ResultVOUtil.success(resourcePage.getContent());
    }


}
