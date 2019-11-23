package com.imooc.demo.service;

import com.imooc.demo.modle.Resource;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:40
 * @Version 1.0
 */
public interface ResourceService {
    List<Resource> getResourceByEmployeeId(String employeeId);
    Boolean saveResource(Resource resource);
    Resource getResourceByResourceId(String resourceId);
    Boolean deleteResourceByResourceId(String resourceId);
    Boolean updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, String resourceId);
}
