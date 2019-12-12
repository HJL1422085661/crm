package com.imooc.demo.service;

import com.imooc.demo.modle.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    Company createCompany(Company company);

    Page<Company> findCompanyByEmployeeId(String employeeId, Pageable pageable);

    Page<Company> findAllCompanyPageable(Pageable pageable);

    Page<Company> findCompanyByShareStatusPageable(Integer shareStatus, Pageable pageable);

    Integer deleteCompanyByCompanyId(Integer companyId);

    Boolean saveCompany(Company company);

    Company getCompanyByCompanyId(Integer companyId);

    List<Company> findAllCompany();

    List<Company> getCompanyByEmployeeId(String employeeId);

    List<Company> findCompanyByShareStatus(Integer shareStatus);

    Boolean updateShareStatusByCompanyId(String shareStatus, Integer companyId);

}
