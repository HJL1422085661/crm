package com.imooc.demo.service.impl;


import com.imooc.demo.model.PayBackRecord;
import com.imooc.demo.model.PayBackRecordTemp;
import com.imooc.demo.repository.PayBackRecordRepository;
import com.imooc.demo.repository.PayBackRecordTempRepository;
import com.imooc.demo.service.PayBackRecordService;
import com.imooc.demo.service.PayBackRecordTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class PayBackRecordTempServiceImpl implements PayBackRecordTempService {
    @Autowired
    public PayBackRecordTempRepository payBackRecordTempRepository;

    @Override
    public PayBackRecordTemp findPayBackRecordTempById(Integer id) {
        return payBackRecordTempRepository.findPayBackRecordTempById(id);
    }

    @Override
    public Boolean isExist(String employeeId, String businessId, Integer status) {
        return payBackRecordTempRepository.existsByEmployeeIdAndBusinessIdAndStatus(employeeId, businessId, status);
    }


    @Override
    public Boolean savePayBackRecordTemp(PayBackRecordTemp payBackRecordTemp) {
        try {
            payBackRecordTempRepository.saveAndFlush(payBackRecordTemp);
        } catch (Exception e) {
            log.error("【保存回款记录】失败");
            return false;
        }
        return true;
    }

    @Override
    public Page<PayBackRecordTemp> findPayBackRecordTempByStatusIsNot(Integer status, Pageable pageable) {
        return payBackRecordTempRepository.findPayBackRecordTempByStatusIsNot(status, pageable);
    }

    @Override
    public Page<PayBackRecordTemp> findPayBackRecordTempByStatus(Integer status, Pageable pageable) {
        return payBackRecordTempRepository.findPayBackRecordTempByStatus(status, pageable);
    }
}
