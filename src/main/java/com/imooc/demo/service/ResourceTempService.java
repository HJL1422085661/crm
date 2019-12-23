package com.imooc.demo.service;

import com.imooc.demo.model.ResourceTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourceTempService {

    Boolean saveResourceTemp(ResourceTemp resourceTemp);

    ResourceTemp createResourceTemp(ResourceTemp resourceTemp);

    Page<ResourceTemp> findResourceTempByCheckedStatusAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable);

    Page<ResourceTemp> findResourceTempByCheckedStatusIsNotAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable);

    ResourceTemp findResourceTempById(Integer id);

    ResourceTemp findResourceTempByResourceIdAndCheckedStatus(Integer resourceId, Integer checkedStatus);

}
