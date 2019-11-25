package com.imooc.demo.service.impl;/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-22 12:35
 **/

import com.imooc.demo.modle.Business;
import com.imooc.demo.repository.BusinessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class BusinessServiceImpl {
    @Autowired
    public BusinessRepository businessRepository;


    Boolean createBusiness(Business business) {
        Business business1 = businessRepository.save(business);
        if (business1 != null) {
            return true;
        } else {
            return false;
        }
    }

    Boolean updateBusinessStatusById(String businessId, Integer businessStatus){
        Business business = businessRepository.getBusinessByBusinessId(businessId);
        business.setBusinessStatus(businessStatus);
        Business business1 =  businessRepository.save(business);

        if (business1 != null) {
            return true;
        } else {
            return false;
        }
    }


}
