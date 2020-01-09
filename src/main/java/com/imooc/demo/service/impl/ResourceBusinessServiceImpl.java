package com.imooc.demo.service.impl;

import com.imooc.demo.model.ResourceBusiness;
import com.imooc.demo.repository.ResourceBusinessRepository;
import com.imooc.demo.service.ResourceBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
public class ResourceBusinessServiceImpl implements ResourceBusinessService {

    @Autowired
    public ResourceBusinessRepository resourceBusinessRepository;

    @Override
    public List<ResourceBusiness> findResourceBusinessByResourceId(Integer resourceId) {
        return resourceBusinessRepository.findResourceBusinessByResourceId(resourceId);
    }

    @Override
    public Page<ResourceBusiness> findResourceBusinessByEmployeeIdAndIsCompleted(String employeeId, Integer isCompleted, Pageable pageable) {
        return resourceBusinessRepository.findResourceBusinessByEmployeeIdAndIsCompleted(employeeId, isCompleted, pageable);
    }

    @Override
    public ResourceBusiness createResourceBusiness(ResourceBusiness resourceBusiness) {
        try {
            return resourceBusinessRepository.saveAndFlush(resourceBusiness);
        } catch (Exception e) {
            log.error("【创建人才订单】发生异常");
            return null;
        }
    }

    @Modifying
    @Transactional
    @Override
    public Integer deleteResourceBusinessByBusinessId(String  businessId) {
        return resourceBusinessRepository.deleteResourceBusinessByBusinessId(businessId);
    }

    @Override
    public ResourceBusiness getResourceBusinessByBusinessId(String  businessId) {
        return resourceBusinessRepository.getResourceBusinessByBusinessId(businessId);
    }

    @Override
    public Page<ResourceBusiness> findResourceBusinessByEmployeeId(String employeeId, Pageable pageable) {
        return resourceBusinessRepository.findResourceBusinessByEmployeeId(employeeId, pageable);
    }

    @Override
    public Page<ResourceBusiness> findAllResourceBusinessByIsCompletedPageable( Integer isCompleted,Pageable pageable) {
        return resourceBusinessRepository.findAllResourceBusinessByIsCompleted(isCompleted, pageable);
    }

    @Override
    public ResourceBusiness findResourceBusinessByBusinessId(String businessId) {
        return resourceBusinessRepository.findResourceBusinessByBusinessId(businessId);
    }

    @Override
    public List<ResourceBusiness> findResourceBusinessByBusinessIdList(List<String> businessId) {
        return resourceBusinessRepository.findResourceBusinessByBusinessIdIn(businessId);
    }

    @Override
    public List<ResourceBusiness> findResourceBusinessByEmployeeIdAndDate(String employeeId, String startDate, String endDate) {
        return resourceBusinessRepository.findResourceBusinessByEmployeeIdAndCreateDateBetween(employeeId, startDate, endDate);
    }

    @Override
    public List<ResourceBusiness> getAllResourceBusiness(String startDate, String endDate) {
        return resourceBusinessRepository.findAllByCreateDateBetween(startDate, endDate);
    }

    @Override
    public List<ResourceBusiness> getResourceBusiness(String employeeId, String startDate, String endDate) {
        return resourceBusinessRepository.findResourceBusinessByEmployeeIdAndCreateDateBetween(employeeId, startDate, endDate);
    }
}
