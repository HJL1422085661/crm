package com.imooc.demo.service;

import com.imooc.demo.modle.PayBackRecord;

public interface PayBackRecordService {
    Boolean createPayBackRecord(PayBackRecord payBackRecord);

    PayBackRecord getPayBackRecordByRecordId(String recordId);

    public Boolean savePayBackRecord(PayBackRecord payBackRecord);


    }
