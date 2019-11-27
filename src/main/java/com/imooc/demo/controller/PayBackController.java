package com.imooc.demo.controller;
/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-25 18:50
 **/

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.PayBackRecord;
import com.imooc.demo.modle.Resource;
import com.imooc.demo.service.EmployeeService;
import com.imooc.demo.service.LoginTicketService;
import com.imooc.demo.service.PayBackRecordService;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/payback")
@Slf4j
public class PayBackController {
    @Autowired
    public PayBackRecordService  payBackRecordService;
    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    public EmployeeService employeeService;


    /**
     * 根据员工ID查看回款记录
     *
     * @param
     * @return
     */
    @PostMapping("/getPayBackRecordList")
    public ResultVO<Map<String, String>> getPayBackRecordList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                             HttpServletRequest req) {
        String token  = TokenUtil.parseToken(req);
        if (token.equals("")){
            log.error("【获取回款记录】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if(employeeId.equals("")){
            log.error("【获取回款记录】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createTime");
        Page<PayBackRecord> payBackRecordPage = payBackRecordService.findPayBackRecordByEmployeeId(employeeId, request);
        System.out.println(payBackRecordPage.getContent());
        if(payBackRecordPage.isEmpty()){
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        }else{
            System.out.println(payBackRecordPage.getContent());
            return ResultVOUtil.success(payBackRecordPage.getContent());
        }
    }


    /**
     * 管理员查看回款记录
     *
     * @param
     * @return
     */
    @PostMapping("/getAllPayBackRecordList")
    public ResultVO<Map<String, String>> getAllPayBackRecordList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                              HttpServletRequest req) {
        String token  = TokenUtil.parseToken(req);
        if (token.equals("")){
            log.error("【获取所有回款记录】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if(employeeId.equals("")){
            log.error("【获取所有回款记录】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if(employeeService.getEmployeeByEmployeeId(employeeId).getEmployRole() != 2){
            log.error("【获取所有回款记录】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        PageRequest request = PageRequest.of(page, size,Sort.Direction.DESC, "createTime" );
        Page<PayBackRecord> payBackRecordPage = payBackRecordService.findPayBackRecord(request);
        System.out.println(payBackRecordPage.getContent());
        if(payBackRecordPage.isEmpty()){
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        }else{
            System.out.println(payBackRecordPage.getContent());
            return ResultVOUtil.success(payBackRecordPage.getContent());
        }
    }

    /**
     * 管理员根据起始日期 | 员工ID查看回款信息
     * 起始时间与员工ID两个条件不能同时为空，这个需要前端进行验证
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param employeeId
     * @param req
     * @return
     */
    @PostMapping("/getPayBackRecordAdmin")
    public ResultVO<Map<String, String>> getPayBackRecordAdmin(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                 @RequestParam("startTime") String startTime,
                                                                @RequestParam("endTime") String endTime,
                                                                 @RequestParam("employeeId") String employeeId,
                                                                 HttpServletRequest req) {
        String token  = TokenUtil.parseToken(req);
        if (token.equals("")){
            log.error("【混合查询获取回款记录】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId1 = loginTicketService.getEmployeeIdByTicket(token);
        if(employeeId1.equals("")){
            log.error("【混合查询获取回款记录】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if(employeeService.getEmployeeByEmployeeId(employeeId1).getEmployRole() != 2){
            log.error("【混合查询获取回款记录】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        if(employeeId.trim().equals("") && startTime.trim().equals("")){
            log.error("【混合查询获取回款记录】参数错误");
            return ResultVOUtil.error(ResultEnum.SELECT_PAY_BACK_RECORD_PARAM_ERROR);
        }

        PageRequest request = PageRequest.of(page, size,Sort.Direction.DESC, "createTime" );
        Page<PayBackRecord> payBackRecordPage = null;
        Date start = null;
        Date end = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
             start = format.parse(startTime);
             end = format.parse(endTime);
        }catch (ParseException  e){
            log.error("【混合查询获取回款记录】时间解析发生异常" + e.getMessage());
            return ResultVOUtil.error(ResultEnum.PARSE_TIME_EXCEPTION);
        }
        //只指定起始时间查询
        if(employeeId.equals("")){
            payBackRecordPage  = payBackRecordService.findPayBackRecordByTime(start, end, request);
        }
        //只指定员工employeeId查询
        else if(startTime == null){
            payBackRecordPage  = payBackRecordService.findPayBackRecordByEmployeeId(employeeId, request);
        }
        //指定员工ID以及起始日期查询
        else{

        }
        System.out.println(payBackRecordPage.getContent());
        if(payBackRecordPage.isEmpty()){
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        }else{
            System.out.println(payBackRecordPage.getContent());
            return ResultVOUtil.success(payBackRecordPage.getContent());
        }
    }





















    /**
     * 新建回款记录
     * @param payBackRecord: 回款信息
     * @return
     */
    @PostMapping("/createPayBackRecord")
    public ResultVO<Map<String, String>> createPayBackRecord(@RequestParam("payBackRecord") PayBackRecord payBackRecord) {
        // 保存回款记录
        boolean isSuccess = payBackRecordService.savePayBackRecord(payBackRecord);
        if (isSuccess) {
            return ResultVOUtil.success();
        } else {
            log.error("【新建回款记录】发生错误");
            return ResultVOUtil.error(ResultEnum.CREATE_PAY_BACK_RECORD_ERROR);
        }
    }


    /**
     * 判断是否允许更改回款信息
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
            if(payBackRecord.isChecked == 0) return true;

        }
        return false;
    }

    /**
     * 更改回款信息
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
            return ResultVOUtil.error(ResultEnum.UPDATE_PAY_BACK_RECORD_ERROR);
        }
    }

}
