package com.imooc.demo.repository;

import com.imooc.demo.modle.Company;
import com.imooc.demo.modle.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Page<Company> findCompanyByEmployeeId(String employeeId, Pageable pageable);

    Integer deleteCompanyByCompanyId(Integer companyId);

    Company getCompanyByCompanyId(Integer companyId);

//    List<Company> findAllCompany();

    List<Company> findCompanyByEmployeeId(String employeeId);

    List<Company> findCompanyByShareStatus(Integer shareStatus);


}
