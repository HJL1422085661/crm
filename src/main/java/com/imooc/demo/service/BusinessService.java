package com.imooc.demo.service;

import com.imooc.demo.modle.Business;

public interface BusinessService {
    Boolean createBusiness(Business business);
    Boolean updateBusinessStatusById(Integer businessId, Integer businessStatus);



}
