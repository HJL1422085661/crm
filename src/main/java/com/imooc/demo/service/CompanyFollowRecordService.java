package com.imooc.demo.service;

import com.imooc.demo.modle.CompanyFollowRecord;
import com.imooc.demo.modle.ResourceFollowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * @Author emperor
 * @Date 2019/11/27 13:16
 * @Version 1.0
 */
public interface CompanyFollowRecordService {
    Page<CompanyFollowRecord> getCompanyFollowRecordByCompanyId(Integer companyId, Pageable pageable);
    CompanyFollowRecord createCompanyFollowRecord(CompanyFollowRecord companyFollowRecord);
}
