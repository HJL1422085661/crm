package com.imooc.demo.service.impl;


import com.imooc.demo.modle.PayBackRecord;
import com.imooc.demo.repository.PayBackRecordRepository;
import com.imooc.demo.service.PayBackRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class PayBackRecordServiceImpl implements PayBackRecordService {
    @Autowired
    public PayBackRecordRepository payBackRecordRepository;


    /**
     * 根据回款记录ID取回款记录
     * @param recordId: 回款记录ID
     * @return
     */
    public PayBackRecord getPayBackRecordByRecordId(String recordId) {
        //TO
        return payBackRecordRepository.findById(recordId).get();
    }

    public Boolean savePayBackRecord(PayBackRecord payBackRecord) {
        try {
            PayBackRecord payBackRecord1 = payBackRecordRepository.saveAndFlush(payBackRecord);
        }catch (Exception e){
            log.error("【保存回款记录】失败");
            return false;
        }
        return true;
    }

    @Override
    public Page<PayBackRecord> findPayBackRecordByEmployeeId(String employeeId, Pageable pageable) {
        return payBackRecordRepository.findPayBackRecordByEmployeeId(employeeId, pageable);
    }

    @Override
    public Page<PayBackRecord> findPayBackRecord(Pageable pageable) {
        return payBackRecordRepository.findPayBackRecordsByRecordId("0", pageable);
//        return null;
    }

    @Override
    public Page<PayBackRecord> findPayBackRecordByTime(Date startTime, Date endTime, Pageable pageable){
        return payBackRecordRepository.findPayBackRecordsByCreateTimeBetween(startTime, endTime, pageable);
    }
}
