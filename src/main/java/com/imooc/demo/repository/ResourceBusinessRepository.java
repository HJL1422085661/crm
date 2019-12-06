package com.imooc.demo.repository;

import com.imooc.demo.modle.ResourceBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;



public interface ResourceBusinessRepository extends JpaRepository<ResourceBusiness, Integer> {

    Integer deleteResourceBusinessByBusinessId(String businessId);
    ResourceBusiness getResourceBusinessByBusinessId(String businessId);
    Page<ResourceBusiness> findResourceBusinessByEmployeeId(String employeeId, Pageable pageable);
    Page<ResourceBusiness> findAll(Pageable pageable);
    ResourceBusiness findResourceBusinessByBusinessId(String businessId);
}
