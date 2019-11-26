package com.imooc.demo.service.impl;/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-22 12:35
 **/
import com.imooc.demo.modle.PayBackRecord;


import com.imooc.demo.modle.Resource;
import com.imooc.demo.repository.PayBackRecordRepository;
import com.imooc.demo.service.PayBackRecordService;
import org.springframework.beans.factory.annotation.Autowired;

public class PayBackRecordServiceImpl {
    @Autowired
    public PayBackRecordRepository payBackRecordRepository;


    Boolean createPayBackRecord(PayBackRecord payBackRecord) {

        PayBackRecord payBackRecord1 = payBackRecordRepository.save(payBackRecord);
        if (payBackRecord1 != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据回款记录ID取回款记录
     *
     * @param recordId:     回款记录ID
     * @return
     */
    PayBackRecord getPayBackRecordByRecordId(String recordId){
        return payBackRecordRepository.findById(recordId).get();
    }


    public Boolean savePayBackRecord(PayBackRecord payBackRecord) {
        PayBackRecord payBackRecord1 = payBackRecordRepository.saveAndFlush(payBackRecord);
        if(payBackRecord1 != null) return true;
        else return false;
    }

}
