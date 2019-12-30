package com.imooc.demo.repository;

import com.imooc.demo.model.PayBackRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PayBackRecordRepository extends JpaRepository<PayBackRecord, Integer> {

    Page<PayBackRecord> findPayBackRecordByEmployeeId(String employeeId, Pageable pageable);

    Page<PayBackRecord> findPayBackRecordsByCreateDateBetween(String startTime, String endTime, Pageable pageable);

    @Query(nativeQuery = true, value = "select * FROM paybackrecord where recordId = ?1")
    Page<PayBackRecord> findPayBackRecordsByRecordId(Integer recordId, Pageable pageable);

    Page<PayBackRecord> findPayBackRecordsByCreateDateBetweenAndEmployeeId(String startTime, String endTime, String employeeId, Pageable pageable);

    List<PayBackRecord> findAllByBusinessId(String businessId);

    List<PayBackRecord> findPayBackRecordByBusinessIdAndLaterBackDateBetween(String businessId, String startDate, String endDate);

    List<PayBackRecord> findPayBackRecordByEmployeeIdAndBusinessTypeAndLaterBackDateBetween(String employeeId, Integer businessType, String startDate, String endDate);

    Page<PayBackRecord> findPayBackRecordByEmployeeIdAndBusinessType(String employeeId, Integer businessType, Pageable pageable);

    List<PayBackRecord> findPayBackRecordByBusinessTypeAndLaterBackDateBetween(Integer businessType, String startDate, String endDate);

    List<PayBackRecord> findPayBackRecordByEmployeeIdAndLaterBackDateBetween(String employeeId, String startDate, String endDate);

}

