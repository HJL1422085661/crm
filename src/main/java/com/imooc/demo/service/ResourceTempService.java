package com.imooc.demo.service;

import com.imooc.demo.modle.ResourceTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourceTempService {

    Boolean saveResourceTemp(ResourceTemp resourceTemp);

    ResourceTemp createResourceTemp(ResourceTemp resourceTemp);

    Page<ResourceTemp> findResourceTempByCheckedStatus(Integer checkedStatus, Pageable pageable);

    ResourceTemp findResourceTempById(Integer id);

}
