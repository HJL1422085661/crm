package com.imooc.demo.service.impl;


import com.imooc.demo.modle.PayBackRecord;
import com.imooc.demo.repository.PayBackRecordRepository;
import com.imooc.demo.service.PayBackRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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
    public PayBackRecord getPayBackRecordByRecordId(Integer recordId) {
        //TO
        return payBackRecordRepository.findById(recordId).get();
    }

    public Boolean savePayBackRecord(PayBackRecord payBackRecord) {
        try {
            payBackRecordRepository.saveAndFlush(payBackRecord);
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
        return payBackRecordRepository.findAll(pageable);
    }

    @Override
    public Page<PayBackRecord> findPayBackRecordByTime(String startTime, String endTime, Pageable pageable){
        return payBackRecordRepository.findPayBackRecordsByCreateDateBetween(startTime, endTime, pageable);
    }

    @Override
    public Page<PayBackRecord> findPayBackRecordByEmployeeIdAndTime(String startTime, String endTime, String employeeId, Pageable pageable) {
        return payBackRecordRepository.findPayBackRecordsByCreateDateBetweenAndEmployeeId(startTime, endTime, employeeId, pageable);
    }
}
