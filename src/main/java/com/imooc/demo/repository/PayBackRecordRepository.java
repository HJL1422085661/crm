package com.imooc.demo.repository;

import com.imooc.demo.modle.PayBackRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface PayBackRecordRepository extends JpaRepository<PayBackRecord, Integer> {

    Page<PayBackRecord> findPayBackRecordByEmployeeId(String employeeId, Pageable pageable);
    Page<PayBackRecord> findPayBackRecordsByCreateDateBetween(String startTime, String endTime, Pageable pageable);

    @Query(nativeQuery = true, value = "select * FROM paybackrecord where recordId = ?1")
    Page<PayBackRecord> findPayBackRecordsByRecordId(String recordId, Pageable pageable);


}

