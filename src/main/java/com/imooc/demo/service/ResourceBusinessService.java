package com.imooc.demo.service;

import com.imooc.demo.model.ResourceBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ResourceBusinessService {

    ResourceBusiness getResourceBusinessByBusinessId(String businessId);

    ResourceBusiness createResourceBusiness(ResourceBusiness resourceBusiness);

    Integer deleteResourceBusinessByBusinessId(String businessId);

    Page<ResourceBusiness> findResourceBusinessByEmployeeId(String employeeId, Pageable pageable);

    Page<ResourceBusiness> findAllResourceBusinessPageable(Pageable pageable);

    ResourceBusiness findResourceBusinessByBusinessId(String businessId);

    List<ResourceBusiness> findResourceBusinessByEmployeeIdAndDate(String employeeId, String startDate, String endDate);

    List<ResourceBusiness> findResourceBusinessByBusinessIdList(List<String> resourceBusinessIdList);

    List<ResourceBusiness> getAllResourceBusiness(String startDate, String endDate);

    List<ResourceBusiness> getResourceBusiness(String employeeId, String startDate, String endDate);

    List<ResourceBusiness> findResourceBusinessByResourceId(Integer resourceId);

}
