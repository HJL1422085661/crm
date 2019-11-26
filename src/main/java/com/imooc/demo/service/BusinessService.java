package com.imooc.demo.service;

import com.imooc.demo.modle.Business;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 14:40
 * @Version 1.0
 */
public interface BusinessService {
    List<Business> getBusinessByEmployeeId(String employeeId);

    Boolean createPublicBusiness(Business business);

    Business getBusinessByBusinessId(String businessId);

    Boolean updateBusinessStatusById(Integer businessId, Integer businessStatus);

    Boolean createBusiness(Business business);

}
