package com.imooc.demo.repository;

import com.imooc.demo.model.CompanyTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyTempRepository extends JpaRepository<CompanyTemp, String> {

    CompanyTemp findCompanyTempById(Integer id);

    CompanyTemp findCompanyTempByCompanyIdAndCheckedStatus(Integer companyId, Integer checkedStatus);
    Page<CompanyTemp> findCompanyTempByCheckedStatusAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable);
}
