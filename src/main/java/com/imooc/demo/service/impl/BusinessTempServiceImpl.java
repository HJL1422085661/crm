package com.imooc.demo.service.impl;

import com.imooc.demo.modle.BusinessTemp;
import com.imooc.demo.repository.BusinessTempRepository;
import com.imooc.demo.service.BusinessTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessTempServiceImpl implements BusinessTempService {
    @Autowired
    public BusinessTempRepository businessTempRepository;

    public BusinessTemp createBusinessTemp(BusinessTemp businessTemp){
        try {
            BusinessTemp businessTemp1 = businessTempRepository.saveAndFlush(businessTemp);
            return businessTemp1;
        }catch (Exception e){
            log.error("创建订单发生错误！！！");
            return null;
        }
    }


}
