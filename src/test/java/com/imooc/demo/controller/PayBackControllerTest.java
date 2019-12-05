package com.imooc.demo.controller;

import com.imooc.demo.modle.PayBackRecord;
import com.imooc.demo.service.PayBackRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author emperor
 * @Date 2019/11/28 16:42
 * @Version 1.0
 */
class PayBackControllerTest {

    @Autowired
    private PayBackRecordService payBackRecordService;
    @Test
    void getPayBackRecordList() {
    }

    @Test
    void getAllPayBackRecordList() {
    }

    @Test
    void createPayBackRecord() {
        PayBackRecord payBackRecord = new PayBackRecord();
        payBackRecord.setBusinessId("1");


    }
}