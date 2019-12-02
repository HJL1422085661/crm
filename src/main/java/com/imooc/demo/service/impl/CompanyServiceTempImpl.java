package com.imooc.demo.service.impl;/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-22 12:13
 **/

import com.imooc.demo.modle.CompanyTemp;
import com.imooc.demo.repository.CompanyTempRepository;
import com.imooc.demo.service.CompanyTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompanyServiceTempImpl implements CompanyTempService {
    @Autowired
    public CompanyTempRepository companyTempRepository;


    @Override
    public CompanyTemp createCompanyTemp(CompanyTemp companyTemp) {
        CompanyTemp companyTemp1;
        try {
            companyTemp1 = companyTempRepository.saveAndFlush(companyTemp);
        }catch (Exception e){
            log.error("【改善企业资源】发生异常");
            return null;
        }
        return companyTemp1;
    }

    @Override
    public Boolean saveCompanyTemp(CompanyTemp companyTemp) {
        CompanyTemp companyTemp1 = companyTempRepository.saveAndFlush(companyTemp);
        if (companyTemp1 == null) return false;
        return true;
    }

    @Override
    public CompanyTemp findCompanyTempById(Integer id) {
        return companyTempRepository.findCompanyTempById(id);
    }

    @Override
    public CompanyTemp findCompanyTempByCompanyIdAndCheckedStatus(Integer companyId, Integer checkedStatus) {
        return companyTempRepository.findCompanyTempByCompanyIdAndCheckedStatus(companyId, checkedStatus);
    }
}
