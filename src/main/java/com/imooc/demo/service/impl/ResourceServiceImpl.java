package com.imooc.demo.service.impl;

import com.imooc.demo.modle.Resource;
import com.imooc.demo.repository.ResourceRepository;
import com.imooc.demo.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:43
 * @Version 1.0
 */
@Service
@Slf4j
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
        if (resource1 == null) return false;
        return true;
    }

    @Override
    public Resource createResource(Resource resource) {
        Resource resource1;
        try {
            resource1 = resourceRepository.saveAndFlush(resource);
        }catch (Exception e){
            log.error("【创建人才资源】发生异常");
            return null;
        }
        return resource1;
    }

    @Override
    public Resource getResourceByResourceId(Integer resourceId) {
        return resourceRepository.getResourceByResourceId(resourceId);
    }

    @Override
    public Integer deleteResourceByResourceId(Integer resourceId) {
        return resourceRepository.deleteResourceByResourceId(resourceId);
    }

    @Override
    public Boolean updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, Integer resourceId) {
        if (resourceRepository.updateShareStatusAndEmployeeIdByResourceId(shareStatus, employeeId, resourceId) != 0)
            return true;
        else return false;
    }

    @Override
    public Boolean updateShareStatusByResourceId(String sharStatus, Integer resourceId) {
        if (resourceRepository.updateShareStatusByResourceId(sharStatus, resourceId) != 0) return true;
        else return false;
    }

    @Override
    public Page<Resource> findResourceByEmployeeId(String employeeId, Pageable pageable) {

        return resourceRepository.findResourceByEmployeeId(employeeId, pageable);
    }
}
