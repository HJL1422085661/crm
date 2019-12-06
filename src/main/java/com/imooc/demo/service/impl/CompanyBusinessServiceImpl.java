package com.imooc.demo.service.impl;

import com.imooc.demo.modle.CompanyBusiness;
import com.imooc.demo.repository.CompanyBusinessRepository;
import com.imooc.demo.service.CompanyBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:13
 * @Version 1.0
 */
@Service
@Slf4j
public class CompanyBusinessServiceImpl implements CompanyBusinessService {

    @Autowired
    public CompanyBusinessRepository companyBusinessRepository;

    @Override
    @Transactional
    @Modifying
    public CompanyBusiness createCompanyBusiness(CompanyBusiness companyBusiness) {
        return companyBusinessRepository.saveAndFlush(companyBusiness);
    }

    @Override
    public CompanyBusiness getCompanyBusinessById(Integer id) {
        return companyBusinessRepository.getCompanyBusinessById(id);
    }

    @Modifying
    @Transactional
    @Override
    public Integer deleteCompanyBusinessByBusinessId(String businessId) {
        return companyBusinessRepository.deleteCompanyBusinessByBusinessId(businessId);
    }

    @Override
    public Page<CompanyBusiness> findAllCompanyBusinessPageable(Pageable pageable) {
        return companyBusinessRepository.findAll(pageable);
    }

    @Override
    public Page<CompanyBusiness> findCompanyBusinessByEmployeeId(String employeeId, Pageable pageable) {
        return companyBusinessRepository.findCompanyBusinessByEmployeeId(employeeId, pageable);
    }

    @Override
    public CompanyBusiness getCompanyBusinessByBusinessId(String businessId) {
        return companyBusinessRepository.getCompanyBusinessByBusinessId(businessId);
    }
}
