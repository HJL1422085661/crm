package com.imooc.demo.repository;

import com.imooc.demo.modle.CompanyFollowRecord;
import com.imooc.demo.modle.ResourceFollowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @Author emperor
 * @Date 2019/11/22 15:14
 * @Version 1.0
 */
public interface CompanyFollowRecordRepository extends JpaRepository<CompanyFollowRecord, Integer> {
    Page<CompanyFollowRecord> getCompanyFollowRecordByCompanyId(Integer companyId, Pageable pageable);

}
