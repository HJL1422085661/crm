package com.imooc.demo.service.impl;

import com.imooc.demo.modle.Resource;
import com.imooc.demo.repository.ResourceRepository;
import com.imooc.demo.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:43
 * @Version 1.0
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    public ResourceRepository resourceRepository;
    @Override
    public List<Resource> getResourceByEmployeeId(String employeeId) {
        return resourceRepository.getResourceListByEmployeeId(employeeId);
    }

    @Override
    public Boolean saveResource(Resource resource) {
        Resource resource1 = resourceRepository.saveAndFlush(resource);
        if(resource1 != null) return true;
        else return false;
    }

    @Override
    public Resource getResourceByResourceId(String resourceId) {
        return resourceRepository.getResourceByResourceId(resourceId);
    }

    @Override
    public Boolean deleteResourceByResourceId(String resourceId) {
        return resourceRepository.deleteByResourceId(resourceId);
    }

    @Override
    public Boolean updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, String resourceId) {
        if(resourceRepository.updateShareStatusAndEmployeeIdByResourceId(shareStatus, employeeId, resourceId) != 0) return true;
        else return false;
    }
}
