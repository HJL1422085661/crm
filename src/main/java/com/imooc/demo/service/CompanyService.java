package com.imooc.demo.service;

import com.imooc.demo.modle.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    Company createCompany(Company company);
    Page<Company> findCompanyByEmployeeId(String employeeId, Pageable pageable);
    Page<Company> findAllCompany(Pageable pageable);
    Integer deleteCompanyByCompanyId(Integer companyId);
    Boolean saveCompany(Company company);
    Company getCompanyByCompanyId(Integer companyId);

}
