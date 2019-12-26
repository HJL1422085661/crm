package com.imooc.demo.repository;

import com.imooc.demo.model.ResourceBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ResourceBusinessRepository extends JpaRepository<ResourceBusiness, Integer> {

    Integer deleteResourceBusinessByBusinessId(String businessId);
    ResourceBusiness getResourceBusinessByBusinessId(String businessId);
    Page<ResourceBusiness> findResourceBusinessByEmployeeId(String employeeId, Pageable pageable);
    Page<ResourceBusiness> findAll(Pageable pageable);
    ResourceBusiness findResourceBusinessByBusinessId(String businessId);
    List<ResourceBusiness> findResourceBusinessByEmployeeIdAndCreateDateBetween(String employeeId, String startDate, String endDate);
    List<ResourceBusiness> findResourceBusinessByBusinessIdIn(List<String> businessId);
    List<ResourceBusiness> findAllByCreateDateBetween(String startDate, String endDate);
}
