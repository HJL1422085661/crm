package com.imooc.demo.repository;

import com.imooc.demo.model.ResourceTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ResourceTempRepository  extends JpaRepository<ResourceTemp, Integer> {
    Page<ResourceTemp> findResourceTempByCheckedStatusAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable);
    Page<ResourceTemp> findResourceTempByCheckedStatusIsNotAndRequestStatus(Integer checkedStatus, Integer requestStatus, Pageable pageable);
    ResourceTemp findResourceTempById(Integer resourceTempId);
    ResourceTemp findResourceTempByResourceIdAndCheckedStatus(Integer resourceId, Integer checkedStatus);

}
