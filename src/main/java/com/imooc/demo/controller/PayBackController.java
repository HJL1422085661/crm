package com.imooc.demo.controller;
/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-25 18:50
 **/

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import com.imooc.demo.model.*;
import com.imooc.demo.service.*;
import com.imooc.demo.utils.BeanCopyUtil;
import com.imooc.demo.utils.ResultVOUtil;
import com.imooc.demo.utils.TokenUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imooc.demo.utils.BeanCopyUtil.getNullPropertyNames;


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
    @Autowired
    public PayBackRecordTempService payBackRecordTempService;

    /**
     * 新建回款记录
     *
     * @param payBackRecordTemp: 回款信息
     * @return
     */
    @PostMapping("/createPayBackRecord")
    public ResultVO<Map<String, String>> createPayBackRecord(@RequestBody PayBackRecordTemp payBackRecordTemp,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        String token = TokenUtil.parseToken(request);
        if (token.equals("")) {
            log.error("【新建回款记录】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【新建回款记录】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
        // 封装员工姓名参数
        Employee dbEmployee = employeeService.getEmployeeByEmployeeId(payBackRecordTemp.getEmployeeId());
        if (dbEmployee == null) {
            log.error("【新建回款记录】没有对应的员工");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }

        //如果是普通员工，存在未审核的回款记录，，则不予创建
        if (employee.getEmployeeRole() == 1) {
            // 找到该员工该订单是否有未审核回款记录
            Boolean flag = payBackRecordTempService.isExist(employeeId, payBackRecordTemp.getBusinessId(), 0);
            if (flag) {
                log.error("【新建回款记录】该订单已存在未审核回款记录");
                return ResultVOUtil.fail(ResultEnum.EXIST_PAYBACK_RECORD_ALREADY, response);
            }
        }

        // 封装其他属性
        // 取得订单总金额
        BigDecimal orderPaySum = new BigDecimal("0");
        if (payBackRecordTemp.getBusinessType() == 1) {
            // 人才订单
            ResourceBusiness resourceBusiness = resourceBusinessService.findResourceBusinessByBusinessId(payBackRecordTemp.getBusinessId());
            orderPaySum = resourceBusiness.getOrderPaySum();
            // 封装成交企业名称
            payBackRecordTemp.setCompanyName(resourceBusiness.getCompanyName());
        } else {
            // 公司订单
            CompanyBusiness companyBusiness = companyBusinessService.getCompanyBusinessByBusinessId(payBackRecordTemp.getBusinessId());
            orderPaySum = companyBusiness.getOrderPaySum();
            // 封装成交企业名称
            payBackRecordTemp.setCompanyName(companyBusiness.getCompanyName());
        }

        // 获取该订单最新回款记录(已审核的)
        List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByBusinessId(payBackRecordTemp.getBusinessId());
        BigDecimal owePay = null;
        BigDecimal backPay = null;
        Integer backTimes = 0;
        if (payBackRecordList.size() != 0) {
            // 如果有，取最后一个
//            PayBackRecord payBackRecord = payBackRecordList.get(payBackRecordList.size() - 1);
            PayBackRecord p = Collections.min(payBackRecordList);
            owePay = p.getOwePay().subtract(payBackRecordTemp.getLaterBackPay());
            if (owePay.signum() < 0) {
                log.error("【创建回款记录】回款金额大于欠款金额");
                return ResultVOUtil.fail(ResultEnum.PAYBACK_TOO_MUCH, response);
            }
            backTimes = p.getBackTimes() + 1;
            backPay = p.getBackPay().add(payBackRecordTemp.getLaterBackPay());
        } else {
            // 没有回款记录
            backPay = new BigDecimal("0");
            owePay = orderPaySum.subtract(payBackRecordTemp.getLaterBackPay());
            if (owePay.signum() < 0) {
                log.error("【创建回款记录】回款金额大于欠款金额");
                return ResultVOUtil.fail(ResultEnum.PAYBACK_TOO_MUCH, response);
            }
            backTimes += 1;
            backPay = backPay.add(payBackRecordTemp.getLaterBackPay());
        }
        //封装属性参数
        payBackRecordTemp.setEmployeeName(employee.getEmployeeName());
        payBackRecordTemp.setOrderPaySum(orderPaySum);
        payBackRecordTemp.setOwePay(owePay);
        payBackRecordTemp.setBackPay(backPay);
        payBackRecordTemp.setBackTimes(backTimes);
        payBackRecordTemp.setLaterBackDate(payBackRecordTemp.getRecordDate());
        payBackRecordTemp.setLaterBackPay(payBackRecordTemp.getLaterBackPay());

        if (owePay.signum() == 0) {
            payBackRecordTemp.setIsCompleted(1);
            Integer businessType = payBackRecordTemp.getBusinessType();
            String businessId = payBackRecordTemp.getBusinessId();
            //2表示企业订单
            if (businessType.equals(2)) {
                CompanyBusiness companyBusiness = companyBusinessService.getCompanyBusinessByBusinessId(businessId);
                companyBusiness.setIsCompleted(1);
                companyBusinessService.createCompanyBusiness(companyBusiness);
            } else {
                ResourceBusiness resourceBusiness = resourceBusinessService.getResourceBusinessByBusinessId(businessId);
                resourceBusiness.setIsCompleted(1);
                resourceBusinessService.createResourceBusiness(resourceBusiness);
            }
        }

        //如果是老板则直接操作，不需要审批,但是需要记录操作
        if (employee.getEmployeeRole() == 2) {
            // 直接同意
            payBackRecordTemp.setCheckedStatus(1);
            // 并且直接在回款记录表插入
            PayBackRecord payBackRecord = new PayBackRecord();
            BeanUtils.copyProperties(payBackRecordTemp, payBackRecord, BeanCopyUtil.getNullPropertyNames(payBackRecordTemp));
            Boolean saveSuccess = payBackRecordService.savePayBackRecord(payBackRecord);

            if (!saveSuccess) {
                log.error("【创建回款记录】新建回款记录失败");
                return ResultVOUtil.fail(ResultEnum.CREATE_PAY_BACK_RECORD_ERROR, response);
            }
        } else {
            // 普通员工新建回款默认未审核
            payBackRecordTemp.setCheckedStatus(0);
        }

        // 保存回款记录
        Boolean flag = payBackRecordTempService.savePayBackRecordTemp(payBackRecordTemp);
        if (!flag) {
            log.error("【创建回款记录】新建临时表发生错误");
            return ResultVOUtil.fail(ResultEnum.CREATE_PAY_BACK_RECORD_ERROR, response);
        } else {
//            if (employee.getEmployeeRole() == 2) {
            Map<String, Object> map = new HashMap<>();
            map.put("employeeRole", employee.getEmployeeRole());
            return ResultVOUtil.success(map);
//            return ResultVOUtil.success();
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
                                                                            HttpServletRequest req,
                                                                            HttpServletResponse response) {
        String businessId = paramMap.get("businessId").toString();
        Integer businessType = Integer.parseInt(paramMap.get("orderType").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取回款记录】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取回款记录】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        // 用于返回结果
        Map<String, Object> map = new HashMap<>();

        // 取最新的回款记录
        List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByBusinessId(businessId);
        if (payBackRecordList.size() == 0) {
            // 如果没有回款记录，则取订单详情返回
            PayBackRecord payBackRecord = new PayBackRecord();
            if (businessType == 1) {
                // 人才订单
                ResourceBusiness resourceBusiness = resourceBusinessService.findResourceBusinessByBusinessId(businessId);
                if (resourceBusiness != null) {
                    BeanUtils.copyProperties(resourceBusiness, payBackRecord, BeanCopyUtil.getNullPropertyNames(resourceBusiness));
                } else {
                    log.error("【获取人才订单】没有这个订单");
                    return ResultVOUtil.fail(ResultEnum.COMPANY_RESOURCE_NOT_EXIST, response);
                }
            } else {
                // 公司订单
                CompanyBusiness companyBusiness = companyBusinessService.getCompanyBusinessByBusinessId(businessId);
                if (companyBusiness != null) {
                    BeanUtils.copyProperties(companyBusiness, payBackRecord, BeanCopyUtil.getNullPropertyNames(companyBusiness));
                } else {
                    log.error("【获取公司订单】没有这个订单");
                    return ResultVOUtil.fail(ResultEnum.COMPANY_BUSINESS_NOT_EXIST, response);
                }
            }
            map.put("payBackDetail", payBackRecord);
            map.put("progressRatio", 0);
        } else {
            // 返回最新一条回款记录
            PayBackRecord lastPayBackRecord = payBackRecordList.get(payBackRecordList.size() - 1);
            BigDecimal progressRatio = (lastPayBackRecord.getOrderPaySum().subtract(lastPayBackRecord.getOwePay())).divide(lastPayBackRecord.getOrderPaySum(), 2);
            map.put("payBackDetail", lastPayBackRecord);
            map.put("progressRatio", progressRatio);
        }
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
                                                                          HttpServletRequest req,
                                                                          HttpServletResponse response) {
        String businessId = paramMap.get("businessId").toString();
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取回款记录】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取回款记录】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByBusinessId(businessId);

        return ResultVOUtil.success(payBackRecordList);
    }

    /**
     * 根据人才ID查看回款记录
     *
     * @param paramMap
     * @param req
     * @return
     */
    @PostMapping("/getPayBackRecordListByResourceId")
    public ResultVO<Map<String, String>> getPayBackRecordListByResourceId(@RequestBody HashMap paramMap,
                                                                          HttpServletRequest req,
                                                                          HttpServletResponse response) {
        Integer resourceId = Integer.parseInt(paramMap.get("resourceId").toString());
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取回款记录】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取回款记录】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByResourceId(resourceId);

        return ResultVOUtil.success(payBackRecordList);
    }

    /**
     * 根据企业ID查看回款记录
     *
     * @param paramMap
     * @param req
     * @return
     */
    @PostMapping("/getPayBackRecordListByCompanyId")
    public ResultVO<Map<String, String>> getPayBackRecordListByCompanyId(@RequestBody HashMap paramMap,
                                                                         HttpServletRequest req,
                                                                         HttpServletResponse response) {
        Integer companyId = Integer.parseInt(paramMap.get("companyId").toString());
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取回款记录】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取回款记录】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        List<PayBackRecord> payBackRecordList = payBackRecordService.findAllPayBackRecordByCompanyId(companyId);

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
    @PostMapping("/getAllPayBackRecordTempListByStatus")
    public ResultVO<Map<String, String>> getAllPayBackRecordTempListByStatus(@RequestBody HashMap paramMap,
                                                                             HttpServletRequest req,
                                                                             HttpServletResponse response) {
        Integer page = Integer.parseInt(paramMap.get("page").toString());
        Integer size = Integer.parseInt(paramMap.get("pageSize").toString());
        Integer checkedStatus = Integer.parseInt(paramMap.get("checkedStatus").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【获取所有回款记录】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (employeeId.equals("")) {
            log.error("【获取所有回款记录】employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【获取所有回款记录】普通员工无权查看所有回款记录");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.DESC, "createDate");
        Page<PayBackRecordTemp> payBackRecordTempPage = null;
        if (checkedStatus == 0) {
            // 未审核
            payBackRecordTempPage = payBackRecordTempService.findPayBackRecordTempByCheckedStatus(checkedStatus, request);
        } else {
            // 已审核（同意、拒绝）
            payBackRecordTempPage = payBackRecordTempService.findPayBackRecordTempByCheckedStatusIsNot(0, request);
        }
        if (payBackRecordTempPage.isEmpty()) {
            return ResultVOUtil.success(ResultEnum.PAYBACK_RECORD_TEMP_NOT_EXIST);
        } else {
            return ResultVOUtil.success(payBackRecordTempPage);
        }
    }

    /**
     * 管理员审批回款代办事项
     *
     * @param map 审批状态 0: 未审批,1：同意 2:不同意;
     * @param req
     * @return
     */
    @PostMapping("/checkPayBackRecordCheckList")
    public ResultVO<Map<String, String>> checkPayBackRecordCheckList(@RequestBody HashMap map,
                                                                     HttpServletRequest req,
                                                                     HttpServletResponse response) {
        Integer id = Integer.parseInt(map.get("id").toString());
        Integer checkedStatus = Integer.parseInt(map.get("checkedStatus").toString());

        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【管理员审批回款代办事项】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String employeeId = loginTicketService.getEmployeeIdByTicket(token);
        if (StringUtils.isEmpty(employeeId)) {
            log.error("【管理员审批回款代办事项】 employeeId为空");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(employeeId).getEmployeeRole() != 2) {
            log.error("【管理员审批回款代办事项】普通员工无权查看所有回款记录");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }

        PayBackRecordTemp payBackRecordTemp = payBackRecordTempService.findPayBackRecordTempById(id);
        // 设置审核状态
        payBackRecordTemp.setCheckedStatus(checkedStatus);
        Boolean updateSuccess = payBackRecordTempService.savePayBackRecordTemp(payBackRecordTemp);
        if (!updateSuccess) {
            log.error("【创建回款记录】更新临时表发生错误");
            return ResultVOUtil.fail(ResultEnum.UPDATE_PAY_BACK_RECORD_ERROR, response);
        }
        if (checkedStatus == 1) {
            // 同意，则在回款记录表新建
            PayBackRecord payBackRecord = new PayBackRecord();
            BeanUtils.copyProperties(payBackRecordTemp, payBackRecord, BeanCopyUtil.getNullPropertyNames(payBackRecordTemp));
            Boolean flag = payBackRecordService.savePayBackRecord(payBackRecord);
            if (flag == false) {
                log.error("【创建回款记录】发生错误");
                return ResultVOUtil.fail(ResultEnum.CREATE_PAY_BACK_RECORD_ERROR, response);
            } else {
                return ResultVOUtil.success(ResultEnum.PASS_PAYBACK_SUCCESS);
            }
        } else if (checkedStatus == 2) {
            // 不同意，则不作操作
            return ResultVOUtil.success(ResultEnum.REJECT_PAYBACK_SUCCESS);
        } else {
            log.error("【创建回款记录】参数错误");
            return ResultVOUtil.fail(ResultEnum.PARAM_ERROR, response);
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
                                                               HttpServletRequest req,
                                                               HttpServletResponse response) {
        String token = TokenUtil.parseToken(req);
        if (token.equals("")) {
            log.error("【混合查询获取回款记录】Token为空");
            return ResultVOUtil.fail(ResultEnum.TOKEN_IS_EMPTY, response);
        }
        String adminId = loginTicketService.getEmployeeIdByTicket(token);
        if (adminId.equals("")) {
            log.error("【混合查询获取回款记录】AdminID不存在");
            return ResultVOUtil.fail(ResultEnum.EMPLOYEE_NOT_EXIST, response);
        }
        if (employeeService.getEmployeeByEmployeeId(adminId).getEmployeeRole() != 2) {
            log.error("【混合查询获取回款记录】普通员工无权查看所有回款记录");
            return ResultVOUtil.fail(ResultEnum.COMMON_EMPLOYEE_NO_RIGHT, response);
        }
        if (employeeId.trim().equals("") && startTime.trim().equals("")) {
            log.error("【混合查询获取回款记录】参数错误");
            return ResultVOUtil.fail(ResultEnum.SELECT_PAY_BACK_RECORD_PARAM_ERROR, response);
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
                                                             @RequestParam("paybackrecord") PayBackRecord payBackRecord,
                                                             HttpServletResponse response) {

        //首先获取数据库中对应的回款记录
        PayBackRecord payBackRecord1 = payBackRecordService.getPayBackRecordByRecordId(recordId);
        //TODO
        BeanUtils.copyProperties(payBackRecord, payBackRecord1); // (source, target)
        Boolean flag = payBackRecordService.savePayBackRecord(payBackRecord1);
        if (flag) {
            return ResultVOUtil.success();
        } else {
            log.error("【更改回款记录】发生错误");
            return ResultVOUtil.fail(ResultEnum.UPDATE_PAY_BACK_RECORD_ERROR, response);
        }
    }


}
