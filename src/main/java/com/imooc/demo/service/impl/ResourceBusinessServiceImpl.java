package com.imooc.demo.service.impl;

import com.imooc.demo.modle.ResourceBusiness;
import com.imooc.demo.repository.ResourceBusinessRepository;
import com.imooc.demo.service.ResourceBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ResourceBusinessServiceImpl implements ResourceBusinessService {

    @Autowired
    public ResourceBusinessRepository resourceBusinessRepository;

    @Override
    public ResourceBusiness createResourceBusiness(ResourceBusiness resourceBusiness) {
        try {
            return resourceBusinessRepository.saveAndFlush(resourceBusiness);
        } catch (Exception e) {
            log.error("【创建人才订单】发生异常");
            return null;
        }
    }

    @Override
    public Integer deleteResourceBusinessById(Integer id) {
        return resourceBusinessRepository.deleteResourceBusinessById(id);
    }

    @Override
    public ResourceBusiness getResourceBusinessById(Integer id) {
        return resourceBusinessRepository.getResourceBusinessById(id);
    }

    @Override
    public Page<ResourceBusiness> findResourceBusinessByEmployeeId(String employeeId, Pageable pageable) {
        return resourceBusinessRepository.findResourceBusinessByEmployeeId(employeeId, pageable);
    }

    @Override
    public Page<ResourceBusiness> findAllResourceBusinessPageable(Pageable pageable) {
        return resourceBusinessRepository.findAll(pageable);
    }
}
