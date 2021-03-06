package com.imooc.demo.repository;

import com.imooc.demo.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Page<Company> findCompanyByEmployeeId(String employeeId, Pageable pageable);

    Page<Company> findCompanyByShareStatus(Integer shareStatus, Pageable pageable);

    Integer deleteCompanyByCompanyId(Integer companyId);

    Company getCompanyByCompanyId(Integer companyId);

//    List<Company> findAllCompany();

    List<Company> findCompanyByEmployeeId(String employeeId);

    List<Company> findCompanyByShareStatus(Integer shareStatus);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE company SET shareStatus = ?1 WHERE companyId = ?2 ")
    int updateShareStatusByCompanyId(String shareStatus, Integer companyId);

    List<Company> findCompanyByEmployeeIdAndCreateDateBetween(String employeeId, String searchStartDate, String searchEndDate);

    List<Company> findCompanyByCreateDateBetween(String searchStartDate, String searchEndDate);

}
