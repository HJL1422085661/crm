package com.imooc.demo.repository;

import com.imooc.demo.modle.ResourceBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @ Author: yangfan
 * @ Date: 2019/12/2
 * @ Version: 1.0
 */


public interface ResourceBusinessRepository extends JpaRepository<ResourceBusiness, Integer> {
    Integer deleteResourceBusinessById(Integer id);
    ResourceBusiness getResourceBusinessById(Integer id);
    Page<ResourceBusiness> findResourceBusinessByEmployeeId(String employeeId, Pageable pageable);
    Page<ResourceBusiness> findAll(Pageable pageable);
    ResourceBusiness findResourceBusinessByBusinessId(String businessId);
}
