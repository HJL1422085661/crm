package com.imooc.demo.repository;

import com.imooc.demo.model.CompanyBusiness;
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
    CompanyBusiness getCompanyBusinessByBusinessId(String businessId);
    Integer deleteCompanyBusinessByBusinessId(String businessId);
    Page<CompanyBusiness> findCompanyBusinessByEmployeeId(String employeeId,  Pageable pageable);
    List<CompanyBusiness> findCompanyBusinessesByEmployeeIdAndCreateDateBetween(String employeeId, String startDate, String endDate);
    List<CompanyBusiness> findCompanyBusinessByBusinessIdIsIn(List<String> businessId);
}
