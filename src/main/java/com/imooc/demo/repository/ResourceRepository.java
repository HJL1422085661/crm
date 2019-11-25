package com.imooc.demo.repository;

import com.imooc.demo.modle.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:44
 * @Version 1.0
 */
public interface ResourceRepository extends JpaRepository<Resource, String> {
    List<Resource> getResourceListByEmployeeId(String employeeId);
    Resource getResourceByResourceId(String resourceId);
    Boolean deleteByResourceId(String resourceId);
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE resource SET shareStatus = ?1 ,employeeId = ?2 WHERE resourceId = ?3 ")
    int updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, String resourceId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE resource SET shareStatus = ?1 WHERE resourceId = ?2 ")
    int updateShareStatusByResourceId(String shareStatus, String resourceId);

    Page<Resource> findResourceByEmployeeId(String employeeId, Pageable pageable);
}
