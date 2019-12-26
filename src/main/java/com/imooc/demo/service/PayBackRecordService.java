package com.imooc.demo.service;

import com.imooc.demo.model.PayBackRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * @Author emperor
 * @Date 2019/11/22 15:40
 * @Version 1.0
 */
public interface PayBackRecordService {

    PayBackRecord getPayBackRecordByRecordId(Integer recordId);
    Boolean savePayBackRecord(PayBackRecord payBackRecord);
    Page<PayBackRecord> findPayBackRecordByEmployeeId(String employeeId, Pageable pageable);
    Page<PayBackRecord> findPayBackRecord(Pageable pageable);
    Page<PayBackRecord> findPayBackRecordByTime(String startTime, String endTime, Pageable pageable);
    Page<PayBackRecord> findPayBackRecordByEmployeeIdAndTime(String startTime, String endTime, String employeeId, Pageable pageable);
    List<PayBackRecord> findAllPayBackRecordByBusinessId(String businessId);
    List<PayBackRecord> findPayBackRecordByBusinessIdAndDate(String businessId, String startDate, String endDate);
    List<PayBackRecord> findPayBackRecordByEmployeeIdAndDate(String employeeId, String startDate, String endDate);
    Page<PayBackRecord>  findPayBackRecordByEmployeeIdAndBusinessType(String employeeId, Integer businessType, Pageable pageable);

    List<PayBackRecord> getAllPayBackRecords(String startDate, String endDate);
    List<PayBackRecord> getPayBackRecords(String employeeId, String startDate, String endDate);



}
