package com.imooc.demo.service.impl;

import com.imooc.demo.modle.Business;
import com.imooc.demo.repository.BusinessRepository;
import com.imooc.demo.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:13
 * @Version 1.0
 */
@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    public BusinessRepository businessRepository;
    @Override
    public List<Business> getBusinessByEmployeeId(String employeeId) {
        return businessRepository.getBusinessByEmployeeId(employeeId);
    }
    @Override
    public Business createBusiness(Business business) {
        try {
            Business business1 = businessRepository.saveAndFlush(business);
            return business1;
        }catch (Exception e){
            log.error("【创建订单】发生异常");
            return null;
        }
    }

    @Override
    public Boolean saveBusiness(Business business) {
        try {
            Business business1 = businessRepository.saveAndFlush(business);
            return true;
        }catch (Exception e){
            log.error("【保存订单】发生异常");
            return false;
        }

    }
}
