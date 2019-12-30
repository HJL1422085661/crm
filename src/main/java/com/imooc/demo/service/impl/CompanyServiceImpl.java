package com.imooc.demo.service.impl;/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-22 12:13
 **/

import com.imooc.demo.model.Company;
import com.imooc.demo.repository.CompanyRepository;
import com.imooc.demo.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    public CompanyRepository companyRepository;

    @Override
    public Company createCompany(Company company) {
        try {
            return companyRepository.saveAndFlush(company);
        } catch (Exception e) {
            log.error("【创建公司】发生异常");
            return null;
        }
    }

    @Override
    public Page<Company> findCompanyByEmployeeId(String employeeId, Pageable pageable) {
        return companyRepository.findCompanyByEmployeeId(employeeId, pageable);
    }

    @Modifying
    @Transactional
    @Override
    public Integer deleteCompanyByCompanyId(Integer companyId) {
        return companyRepository.deleteCompanyByCompanyId(companyId);
    }

    @Override
    public Boolean saveCompany(Company company) {
        Company company1 = companyRepository.saveAndFlush(company);
        if (company1 == null) return false;
        return true;
    }

    @Override
    public Company getCompanyByCompanyId(Integer companyId) {
        return companyRepository.getCompanyByCompanyId(companyId);
    }

    @Override
    public Page<Company> findAllCompanyPageable(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Override
    public List<Company> findAllCompany() {
        return companyRepository.findAll();
    }

    @Override
    public List<Company> getCompanyByEmployeeId(String employeeId) {
        return companyRepository.findCompanyByEmployeeId(employeeId);
    }

    @Override
    public Page<Company> findCompanyByShareStatusPageable(Integer shareStatus, Pageable pageable) {
        return companyRepository.findCompanyByShareStatus(shareStatus, pageable);
    }

    @Override
    public Boolean updateShareStatusByCompanyId(String shareStatus, Integer companyId) {
        if (companyRepository.updateShareStatusByCompanyId(shareStatus, companyId) != 0) return true;
        else return false;
    }

    @Override
    public List<Company> getNewCompanyClients(String employeeId, String searchStartDate, String searchEndDate) {
        return companyRepository.findCompanyByEmployeeIdAndCreateDateBetween(employeeId, searchStartDate, searchEndDate);
    }

    @Override
    public List<Company> getAllNewCompanyClients(String searchStartDate, String searchEndDate) {
        return companyRepository.findCompanyByCreateDateBetween(searchStartDate, searchEndDate);
    }

    @Override
    public List<Company> findCompanyByShareStatus(Integer shareStatus) {
        return companyRepository.findCompanyByShareStatus(shareStatus);
    }
}
