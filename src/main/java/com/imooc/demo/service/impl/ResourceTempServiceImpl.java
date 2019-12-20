package com.imooc.demo.service.impl;
/*
 * @program: demo
 * @author: Mason Wang
 * @create: 2019-11-27 17:49
 **/

import com.imooc.demo.model.ResourceTemp;
import com.imooc.demo.repository.ResourceTempRepository;
import com.imooc.demo.service.ResourceTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResourceTempServiceImpl implements ResourceTempService {


    @Autowired
    public ResourceTempRepository resourceTempRepository;

    @Override
    public Boolean saveResourceTemp(ResourceTemp resourceTemp) {
        ResourceTemp resourceTemp1 = resourceTempRepository.saveAndFlush(resourceTemp);
        if (resourceTemp1 == null) return false;
        return true;
    }

    @Override
    public ResourceTemp createResourceTemp(ResourceTemp resourceTemp) {

        ResourceTemp resourceTemp1;
        try {
            resourceTemp1 = resourceTempRepository.saveAndFlush(resourceTemp);
        } catch (Exception e) {
            log.error("【创建人才资源（改删）】发生异常");
            return null;
        }
        return resourceTemp1;
    }


    @Override
    public ResourceTemp findResourceTempById(Integer id) {
        return resourceTempRepository.findResourceTempById(id);
    }

    @Override
    public ResourceTemp findResourceTempByResourceIdAndCheckedStatus(Integer resourceId, Integer checkedStatus) {
        return resourceTempRepository.findResourceTempByResourceIdAndCheckedStatus(resourceId, checkedStatus);
    }

    @Override
    public Page<ResourceTemp> findResourceTempByCheckedStatusAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable) {
        return resourceTempRepository.findResourceTempByCheckedStatusAndRequestStatus(checkedStatus, requestStatus, pageable);
    }
}
