package com.imooc.demo.service;

import com.imooc.demo.modle.CompanyBusiness;
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

    Integer deleteCompanyBusinessById(Integer id);

    Page<CompanyBusiness> findAllCompanyBusinessPageable(Pageable pageable);

    Page<CompanyBusiness> findCompanyBusinessByEmployeeId(String employeeId, Pageable pageable);
}
