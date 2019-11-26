package com.imooc.demo.repository;

import com.imooc.demo.modle.PayBackRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;


public interface PayBackRecordRepository extends JpaRepository<PayBackRecord, String> {

    Page<PayBackRecord> findPayBackRecordByEmployeeId(String employeeId, Pageable pageable);
    Page<PayBackRecord> findAllPayBackRecord(Pageable pageable);


    Page<PayBackRecord> findPayBackRecordsByCreateTimeBetween(Date startTime, Date endTime, Pageable pageable);
}

