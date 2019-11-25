package com.imooc.demo.controller;/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-25 18:50
 **/

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.PayBackRecord;
import com.imooc.demo.service.PayBackRecordService;
import com.imooc.demo.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/payback")
@Slf4j
public class PayBackController {
    @Autowired
    public PayBackRecordService payBackRecordService;


    /**
     * 新建回款记录
     *
     * @param payBackRecord: 回款信息
     * @return
     */
    @PostMapping("/createPayBackRecord")
    public ResultVO<Map<String, String>> createPayBackRecord(@RequestParam("paybackrecord") PayBackRecord payBackRecord) {
        // 保存回款记录
        boolean isSuccess = payBackRecordService.createPayBackRecord(payBackRecord);
        if (isSuccess) {
            return ResultVOUtil.success();
        } else {
            log.error("【新建回款记录】发生错误");
            return ResultVOUtil.error(ResultEnum.CREATE_PAYBACKRECORD_ERROR);
        }
    }


    /**
     * 判断是否允许更改回款信息
     *
     * @param recordId:     回款记录ID
     * @param employeeRole: 与员工角色（普通员工、管理员）
     * @return
     */
    @PostMapping("/isAllowedToUpdateRecord")
    public boolean isAllowedToUpdateRecord(@RequestParam("recordId") String recordId,
                                           @RequestParam("employeeRole") Integer employeeRole) {
        // 1、如果是管理员：可直接修改（1:"普通员工"; 2:"管理员"）
        if (employeeRole == 2) {
            return true;
        }
        // 2、如果是普通员工：管理员确认前可修改，否则不予修改
        if (employeeRole == 1) {
            // 检查是否已确认
            PayBackRecord payBackRecord = payBackRecordService.getPayBackRecordByRecordId(recordId);
            return !payBackRecord.isChecked;
        }
        return false;
    }

    /**
     * 更改回款信息
     *
     * @param payBackRecord: 更改的回款记录
     * @return
     */
    @PostMapping("/updatePayBackRecord")
    public ResultVO<Map<String, String>> updatePayBackRecord(@RequestParam("recordId") String recordId,
                                                             @RequestParam("paybackrecord") PayBackRecord payBackRecord) {

        //首先获取数据库中对应的回款记录
        PayBackRecord payBackRecord1 = payBackRecordService.getPayBackRecordByRecordId(recordId);
        //TODO
        BeanUtils.copyProperties(payBackRecord, payBackRecord1); // (source, target)
        Boolean flag = payBackRecordService.savePayBackRecord(payBackRecord1);
        if (flag) {
            return ResultVOUtil.success();
        } else {
            log.error("【更改回款记录】发生错误");
            return ResultVOUtil.error(ResultEnum.UPDATE_PAYBACKRECORD_ERROR);
        }
    }

}
