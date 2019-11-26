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
        return business1 != null;
    }

    @Override
    public Business getBusinessByBusinessId(String businessId) {
        return null;
    }

    @Override
    public Boolean updateBusinessStatusById(Integer businessId, Integer businessStatus) {
        return null;
    }

    public Boolean createBusiness(Business business) {
        Business business1 = businessRepository.save(business);
        return business1 != null;
    }

    Boolean updateBusinessStatusById(String businessId, Integer businessStatus){
        Business business = businessRepository.getBusinessByBusinessId(businessId);
        business.setBusinessStatus(businessStatus);
        Business business1 =  businessRepository.save(business);

        return business1 != null;
    }


}
