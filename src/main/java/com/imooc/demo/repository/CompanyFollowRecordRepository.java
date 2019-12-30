package com.imooc.demo.repository;

import com.imooc.demo.model.CompanyFollowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * @Author emperor
 * @Date 2019/11/22 15:14
 * @Version 1.0
 */
public interface CompanyFollowRecordRepository extends JpaRepository<CompanyFollowRecord, Integer> {
    Page<CompanyFollowRecord> getCompanyFollowRecordByCompanyId(Integer companyId, Pageable pageable);

    List<CompanyFollowRecord> findAllByCreateDateBetween(String searchStartDate, String searchEndDate);

    List<CompanyFollowRecord> findAllByEmployeeIdAndCreateDateBetween(String employeeId, String searchStartDate, String searchEndDate);

}
