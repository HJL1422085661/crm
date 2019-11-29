package com.imooc.demo.service;

import com.imooc.demo.modle.CompanyTemp;

public interface CompanyTempService {
    Boolean saveCompanyTemp(CompanyTemp companyTemp);

    CompanyTemp createCompanyTemp(CompanyTemp companyTemp);

    CompanyTemp findCompanyTempById(Integer id);


}
