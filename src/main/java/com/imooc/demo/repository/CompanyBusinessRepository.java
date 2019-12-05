package com.imooc.demo.repository;

import com.imooc.demo.modle.CompanyBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ Author: yangfan
 * @ Date: 2019/12/2
 * @ Version: 1.0
 */

public interface CompanyBusinessRepository extends JpaRepository<CompanyBusiness, Integer> {
    List<CompanyBusiness> getCompanyBusinessByEmployeeId(String employeeId);
    CompanyBusiness getCompanyBusinessById(Integer id);
    Integer deleteCompanyBusinessById(Integer id);
    Page<CompanyBusiness> findCompanyBusinessByEmployeeId(String employeeId,  Pageable pageable);
}
