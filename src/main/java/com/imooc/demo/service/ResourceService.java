package com.imooc.demo.service;

import com.imooc.demo.modle.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:40
 * @Version 1.0
 */
public interface ResourceService {
    List<Resource> getResourceByEmployeeId(String employeeId);
    Boolean saveResource(Resource resource);
    Resource createResource(Resource resource);
    Resource getResourceByResourceId(Integer resourceId);
    Boolean deleteResourceByResourceId(Integer resourceId);
    Boolean updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, Integer resourceId);
    Boolean updateShareStatusByResourceId(String shareStatus, Integer resourceId);
    Page<Resource> findResourceByEmployeeId(String employeeId, Pageable pageable);
}
