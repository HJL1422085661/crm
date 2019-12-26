package com.imooc.demo.repository;

import com.imooc.demo.model.ResourceFollowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * @Author emperor
 * @Date 2019/11/22 15:14
 * @Version 1.0
 */
public interface ResourceFollowRecordRepository extends JpaRepository<ResourceFollowRecord, String> {
    Page<ResourceFollowRecord> getResourceFollowRecordByResourceId(Integer resourceId, Pageable pageable);

    @Query("select r from ResourceFollowRecord r ORDER BY r.createDate DESC")
    List<ResourceFollowRecord> findResourceFollowRecordsByEmployeeIdAndCreateDateBetween(String employeeId, String startDate, String endDate);

    @Query("select r from ResourceFollowRecord r ORDER BY r.createDate DESC")
    List<ResourceFollowRecord> findResourceFollowRecordsByCreateDateBetween(String startDate, String endDate);

}
