package com.imooc.demo.service;

import com.imooc.demo.model.CompanyBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 14:40
 * @Version 1.0
 */
public interface CompanyBusinessService {
    CompanyBusiness getCompanyBusinessById(Integer id);

    CompanyBusiness createCompanyBusiness(CompanyBusiness companyBusiness);

    CompanyBusiness getCompanyBusinessByBusinessId(String businessId);

    Integer deleteCompanyBusinessByBusinessId(String businessId);

    Page<CompanyBusiness> findAllCompanyBusinessByIsCompletedPageable(Integer isCompleted, Pageable pageable);

    Page<CompanyBusiness> findCompanyBusinessByEmployeeIdAndIsCompleted(String employeeId, Integer isCompleted, Pageable pageable);

    List<CompanyBusiness> findCompanyBusinessByEmployeeIdAndDate(String employeeId, String startDate, String endDate);

    List<CompanyBusiness> findCompanyBusinessByBusinessIdList(List<String> companyBusinessIdList);

    List<CompanyBusiness> getAllCompanyBusiness(String startDate, String endDate);

    List<CompanyBusiness> getCompanyBusiness(String employeeId, String startDate, String endDate);

    List<CompanyBusiness> findCompanyBusinessByCompanyId(Integer companyId);

}
