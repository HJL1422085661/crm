package com.imooc.demo.service;

import com.imooc.demo.model.CompanyTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyTempService {
    Boolean saveCompanyTemp(CompanyTemp companyTemp);

    CompanyTemp createCompanyTemp(CompanyTemp companyTemp);

    CompanyTemp findCompanyTempById(Integer id);

    CompanyTemp findCompanyTempByCompanyIdAndCheckedStatus(Integer companyId, Integer checkedStatus);

    Page<CompanyTemp> findCompanyTempByCheckedStatusAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable);

    Page<CompanyTemp> findCompanyTempByCheckedStatusIsNotAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable);

}
