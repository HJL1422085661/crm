package com.imooc.demo.service.impl;

import com.imooc.demo.modle.Business;
import com.imooc.demo.repository.BusinessRepository;
import com.imooc.demo.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:13
 * @Version 1.0
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    public BusinessRepository businessRepository;
    @Override
    public List<Business> getBusinessByEmployeeId(String employeeId) {
        return businessRepository.getBusinessByEmployeeId(employeeId);
    }
    @Override
    public Boolean createPublicBusiness(Business business) {
        Business business1 = businessRepository.saveAndFlush(business);
        if(business1 != null) return true;
        return false;
    }
}
