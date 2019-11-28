package com.imooc.demo.service.impl;/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-22 12:13
 **/

import com.imooc.demo.modle.Company;
import com.imooc.demo.modle.Resource;
import com.imooc.demo.repository.CompanyRepository;
import com.imooc.demo.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    public CompanyRepository companyRepository;

    @Override
    public Company createCompany(Company company) {
        try{
            return companyRepository.saveAndFlush(company);
        }catch (Exception e){
            log.error("【创建公司】发生异常");
            return null;
        }
    }

    @Override
    public Page<Company> findCompanyByEmployeeId(String employeeId, Pageable pageable) {
        return  companyRepository.findCompanyByEmployeeId(employeeId, pageable);
    }

    @Override
    public Boolean deleteCompanyByCompanyId(Integer companyId) {
        return companyRepository.deleteCompanyByCompanyId(companyId);
    }

    @Override
    public Boolean saveCompany(Company company) {
        Company company1 = companyRepository.saveAndFlush(company);
        if (company1 == null) return false;
        return true;
    }
}
