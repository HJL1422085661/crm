package com.imooc.demo.service.impl;

import com.imooc.demo.model.CompanyBusiness;
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
    public Page<CompanyBusiness> findCompanyBusinessByEmployeeIdAndIsCompleted(String employeeId, Integer isCompleted, Pageable pageable) {
        return companyBusinessRepository.findCompanyBusinessByEmployeeIdAndIsCompleted(employeeId, isCompleted, pageable);
    }

    @Override
    public Page<CompanyBusiness> findAllCompanyBusinessByIsCompletedPageable(Integer isCompleted, Pageable pageable) {
        return companyBusinessRepository.findAllCompanyBusinessByIsCompleted(isCompleted, pageable);
    }

    @Override
    public CompanyBusiness getCompanyBusinessByBusinessId(String businessId) {
        return companyBusinessRepository.getCompanyBusinessByBusinessId(businessId);
    }

    @Override
    public List<CompanyBusiness> findCompanyBusinessByBusinessIdList(List<String> businessId) {
        return companyBusinessRepository.findCompanyBusinessByBusinessIdIsIn(businessId);
    }

    @Override
    public List<CompanyBusiness> findCompanyBusinessByEmployeeIdAndDate(String employeeId, String startDate, String endDate) {
        return companyBusinessRepository.findCompanyBusinessesByEmployeeIdAndCreateDateBetween(employeeId, startDate, endDate);
    }

    @Override
    public List<CompanyBusiness> findCompanyBusinessByCompanyId(Integer companyId) {
        return companyBusinessRepository.findCompanyBusinessesByCompanyId(companyId);
    }

    @Override
    public List<CompanyBusiness> getAllCompanyBusiness(String startDate, String endDate) {
        return companyBusinessRepository.findAllCompanyBusinessByCreateDateBetween(startDate, endDate);
    }

    @Override
    public List<CompanyBusiness> getCompanyBusiness(String employeeId, String startDate, String endDate) {
        return companyBusinessRepository.findCompanyBusinessesByEmployeeIdAndCreateDateBetween(employeeId, startDate, endDate);
    }
}
