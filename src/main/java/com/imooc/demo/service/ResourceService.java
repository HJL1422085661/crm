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
    /**
     * 录入人才信息
     **/
    Boolean addResource(Resource resource);

    List<Resource> getResourceByEmployeeId(String employeeId);
    Boolean saveResource(Resource resource);
    Resource getResourceByResourceId(String resourceId);
    Boolean deleteResourceByResourceId(String resourceId);
    Boolean updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, String resourceId);
    Boolean updateShareStatusByResourceId(String sharStatus, String resourceId);
    Page<Resource> findResourceByEmployeeId(String employeeId, Pageable pageable);
}
