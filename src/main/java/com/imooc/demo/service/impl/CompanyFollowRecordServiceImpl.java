package com.imooc.demo.service.impl;

import com.imooc.demo.modle.CompanyFollowRecord;
import com.imooc.demo.repository.CompanyFollowRecordRepository;
import com.imooc.demo.service.CompanyFollowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @Author emperor
 * @Date 2019/11/30 15:11
 * @Version 1.0
 */
@Service
public class CompanyFollowRecordServiceImpl implements CompanyFollowRecordService {

    @Autowired
    public CompanyFollowRecordRepository companyFollowRecordRepository;
    @Override
    public Page<CompanyFollowRecord> getCompanyFollowRecordByCompanyId(Integer companyId, Pageable pageable) {
        return companyFollowRecordRepository.getCompanyFollowRecordByCompanyId(companyId, pageable);
    }

    @Override
    public CompanyFollowRecord createCompanyFollowRecord(CompanyFollowRecord companyFollowRecord) {
        return companyFollowRecordRepository.saveAndFlush(companyFollowRecord);
    }
}
