package com.imooc.demo.controller;
/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-25 18:50
 **/

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.modle.CompanyBusiness;
import com.imooc.demo.modle.PayBackRecord;
import com.imooc.demo.modle.ResourceBusiness;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.BeanCopyUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/payback")
@Slf4j
public class PayBackController {
    @Autowired
    public PayBackRecordService payBackRecordService;
    @Autowired
    public LoginTicketService loginTicketService;
    @Autowired
    public EmployeeService employeeService;
    @Autowired
    public ResourceBusinessService resourceBusinessService;
    @Autowired
    public CompanyBusinessService companyBusinessService;


    /**
     * 新建回款记录
     *
     * @param payBackRecord: 回款信息
     * @return
     */
    @PostMapping("/createPayBackRecord")
    public ResultVO<Map<String, String>> createPayBackRecord(@RequestBody PayBackRecord payBackRecord) {
        PayBackRecord createPayBackRecord = new PayBackRecord();
        BeanUtils.copyProperties(payBackRecord, createPayBackRecord, BeanCopyUtil.getNullPropertyNames(payBackRecord));
        // 封装其他属性
        if (payBackRecord.getBusinessType() == 1) {
            // 人才订单
            ResourceBusiness resourceBusiness = resourceBusinessService.findResourceBusinessByBusinessId(payBackRecord.getBusinessId());
            BigDecimal orderPaySum = resourceBusiness.getOrderPaySum();

            // 获取该订单最新回款记录
            List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByBusinessId(payBackRecord.getBusinessId());
            BigDecimal owePay = null;
            Integer backTimes = 0;

            if (payBackRecordList.size() != 0) {
                // 取最后一个
                PayBackRecord payBackRecordTemp = payBackRecordList.get(payBackRecordList.size() - 1);
                owePay = payBackRecordTemp.getOwePay().subtract(payBackRecord.getBackPay());
                if (owePay.signum() <= 0) owePay = new BigDecimal("0");
                backTimes = payBackRecordTemp.getBackTimes() + 1;
            } else {
                owePay = orderPaySum.subtract(payBackRecord.getBackPay());
                if (owePay.signum() <= 0) owePay = new BigDecimal("0");
                backTimes += 1;
            }
            //封装属性参数
            createPayBackRecord.setOrderPaySum(orderPaySum);
            createPayBackRecord.setOwePay(owePay);
            createPayBackRecord.setBackTimes(backTimes);

            Boolean flag = payBackRecordService.savePayBackRecord(createPayBackRecord);
            if (flag == false) {
                log.error("【创建人才订单回款】发生错误");
                return ResultVOUtil.error(ResultEnum.CREATE_RESOURCE_BUSINESS__PAYBACK_ERROR);
            } else {
                return ResultVOUtil.success();
            }
        }


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
     * 根据订单ID查看回款详情
     *
     * @param paramMap
     * @param req
     * @return
     */
    @PostMapping("/getPayBackRecordDetailByBusinessId")
    public ResultVO<Map<String, String>> getPayBackRecordDetailByBusinessId(@RequestBody HashMap paramMap,
                                                                            HttpServletRequest req) {
        String businessId = paramMap.get("businessId").toString();
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取回款记录】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取回款记录】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        // 取最新的回款记录
        List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByBusinessId(businessId);
        if (payBackRecordList.size() == 0) {
            return ResultVOUtil.success(ResultEnum.PAYBACK_RECORD_NOT_EXIST);
        }
        PayBackRecord lastPayBackRecord = payBackRecordList.get(payBackRecordList.size() - 1);
        BigDecimal progressRatio = (lastPayBackRecord.getOrderPaySum().subtract(lastPayBackRecord.getOwePay())).divide(lastPayBackRecord.getOrderPaySum());
        Map<String, Object> map = new HashMap<>();
        map.put("businessDetail", lastPayBackRecord);
        map.put("progressRatio", progressRatio);
        return ResultVOUtil.success(map);
    }

    /**
     * 根据订单ID查看回款记录
     *
     * @param paramMap
     * @param req
     * @return
     */
    @PostMapping("/getPayBackRecordListByBusinessId")
    public ResultVO<Map<String, String>> getPayBackRecordListByBusinessId(@RequestBody HashMap paramMap,
                                                                          HttpServletRequest req) {
        String businessId = paramMap.get("businessId").toString();
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取回款记录】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取回款记录】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByBusinessId(businessId);
        if (payBackRecordList.size() == 0) {
            return ResultVOUtil.success(ResultEnum.PAYBACK_RECORD_NOT_EXIST);
        }
        return ResultVOUtil.success(payBackRecordList);
    }

    /**
     * 根据员工ID和订单类型取相应的回款记录
     * 1表示人才订单 2表示公司订单
     *
     * @param paramMap
     * @param req
     * @return
     */
    @PostMapping("/getPayBackRecordByEmployeeId")
    public ResultVO<Map<String, String>> getPayBackRecordByEmployeeId(@RequestBody HashMap paramMap,
                                                                      HttpServletRequest req) {
        String employeeId = paramMap.get("employeeId").toString();
        Integer businessType = Integer.parseInt(paramMap.get("businessType").toString());
        Integer page = Integer.parseInt(paramMap.get("page").toString());
        Integer size = Integer.parseInt(paramMap.get("pageSize").toString());
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "recordDate");
        Page<PayBackRecord> payBackRecordPage = payBackRecordService.findPayBackRecordByEmployeeIdAndBusinessType(employeeId, businessType, request);
        if (payBackRecordPage.isEmpty()) {
            if (businessType == 1) return ResultVOUtil.success(ResultEnum.RESOURCE_PAYBACK_LIST_EMPTY);
            else return ResultVOUtil.success(ResultEnum.COMPANY_PAYBACK_LIST_EMPTY);
        } else {
            System.out.println(payBackRecordPage.getContent());
            return ResultVOUtil.success(payBackRecordPage);
        }
    }

    /**
     * 管理员查看所有的回款记录
     *
     * @param
     * @return
     */
    @PostMapping("/getAllPayBackRecordList")
    public ResultVO<Map<String, String>> getAllPayBackRecordList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                 HttpServletRequest req) {
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取所有回款记录】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取所有回款记录】employeeId为空");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployRole() != 2) {
            log.error("【获取所有回款记录】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<PayBackRecord> payBackRecordPage = payBackRecordService.findPayBackRecord(request);
        System.out.println(payBackRecordPage.getContent());
        if (payBackRecordPage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        } else {
            System.out.println(payBackRecordPage.getContent());
            return ResultVOUtil.success(payBackRecordPage);
        }
    }

    /**
     * 管理员根据起始日期 | 员工ID查看回款信息
     * 起始时间与员工ID两个条件不能同时为空，这个需要前端进行验证
     *
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
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【混合查询获取回款记录】Token为空");
            return ResultVOUtil.error(ResultEnum.TOKEN_IS_EMPTY);
        }
        String adminId = loginTicketService.getEmployeeIdByTicket(token);
        if (adminId.equals("")) {
            log.error("【混合查询获取回款记录】AdminID不存在");
            return ResultVOUtil.error(ResultEnum.EMPLOYEE_NOT_EXIST);
        }
        if (employeeService.getEmployeeByEmployeeId(adminId).getEmployRole() != 2) {
            log.error("【混合查询获取回款记录】普通员工无权查看所有回款记录");
            return ResultVOUtil.error(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT);
        }
        if (employeeId.trim().equals("") && startTime.trim().equals("")) {
            log.error("【混合查询获取回款记录】参数错误");
            return ResultVOUtil.error(ResultEnum.SELECT_PAY_BACK_RECORD_PARAM_ERROR);
        }

        PageRequest request = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<PayBackRecord> payBackRecordPage = null;
        //只指定起始时间查询
        if (employeeId.equals("")) {
            payBackRecordPage = payBackRecordService.findPayBackRecordByTime(startTime, endTime, request);
        }
        //只指定员工employeeId查询
        else if (startTime == null) {
            payBackRecordPage = payBackRecordService.findPayBackRecordByEmployeeId(employeeId, request);
        }
        //指定员工ID以及起始日期查询
        else {
            payBackRecordPage = payBackRecordService.findPayBackRecordByEmployeeIdAndTime(startTime, endTime, employeeId, request);
        }
        System.out.println(payBackRecordPage.getContent());
        if (payBackRecordPage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.RESOURCE_LIST_EMPTY);
        } else {
            System.out.println(payBackRecordPage.getContent());
            return ResultVOUtil.success(payBackRecordPage.getContent());
        }
    }
//
//    /**
//     * 判断是否允许更改回款信息
//     *
//     * @param recordId:     回款记录ID
//     * @param employeeRole: 与员工角色（普通员工、管理员）
//     * @return
//     */
//    @PostMapping("/isAllowedToUpdateRecord")
//    public boolean isAllowedToUpdateRecord(@RequestParam("recordId") Integer recordId,
//                                           @RequestParam("employeeRole") Integer employeeRole) {
//        // 1、如果是管理员：可直接修改（1:"普通员工"; 2:"管理员"）
//        if (employeeRole == 2) {
//            return true;
//        }
//        // 2、如果是普通员工：管理员确认前可修改，否则不予修改
//        if (employeeRole == 1) {
//            // 检查是否已确认
//            PayBackRecord payBackRecord = payBackRecordService.getPayBackRecordByRecordId(recordId);
//            if (payBackRecord.isChecked == 0) return true;
//
//        }
//        return false;
//    }

    /**
     * 更改回款信息
     *
     * @param payBackRecord: 更改的回款记录
     * @return
     */
    @PostMapping("/updatePayBackRecord")
    public ResultVO<Map<String, String>> updatePayBackRecord(@RequestParam("recordId") Integer recordId,
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
