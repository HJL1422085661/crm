package com.imooc.demo.service;

import com.imooc.demo.modle.Resource;
import com.imooc.demo.modle.ResourceBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResourceBusinessService {
//    List<ResourceBusiness> getResourceBusiness(String employeeId);
//    Boolean saveResourceBusiness(ResourceBusiness resourceBusiness);
    ResourceBusiness getResourceBusinessById(Integer id);
    ResourceBusiness createResourceBusiness(ResourceBusiness resourceBusiness);
    Integer deleteResourceBusinessById(Integer id);
    Page<ResourceBusiness> findResourceBusinessByEmployeeId(String employeeId, Pageable pageable);
    Page<ResourceBusiness> findAllResourceBusinessPageable(Pageable pageable);}
