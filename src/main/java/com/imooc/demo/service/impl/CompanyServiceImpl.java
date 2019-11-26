package com.imooc.demo.service.impl;/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-22 12:13
 **/

import com.imooc.demo.modle.Company;
import com.imooc.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CompanyServiceImpl {
    @Autowired
    public CompanyRepository companyRepository;

    Boolean addCompany(Company company){
        Company company1 = companyRepository.save(company);
        if (company1 != null) {
            return true;
        }else {
            return false;
        }
    }

}
