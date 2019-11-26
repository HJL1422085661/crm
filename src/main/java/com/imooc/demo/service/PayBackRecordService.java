package com.imooc.demo.service;

import com.imooc.demo.modle.PayBackRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * @Author emperor
 * @Date 2019/11/22 15:40
 * @Version 1.0
 */
public interface PayBackRecordService {

    PayBackRecord getPayBackRecordByRecordId(String recordId);
    Boolean savePayBackRecord(PayBackRecord payBackRecord);
    Page<PayBackRecord> findPayBackRecordByEmployeeId(String employeeId, Pageable pageable);
    Page<PayBackRecord> findPayBackRecord(Pageable pageable);
    Page<PayBackRecord> findPayBackRecordByTime(Date startTime, Date endTime, Pageable pageable);


}
