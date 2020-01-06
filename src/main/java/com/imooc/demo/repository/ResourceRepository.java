package com.imooc.demo.repository;

import com.imooc.demo.model.Resource;
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
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    List<Resource> getResourceListByEmployeeId(String employeeId);
    Resource getResourceByResourceId(Integer resourceId);

    @Modifying
    @Transactional
    @Query("DELETE from Resource re where re.resourceId = ?1")
    Integer deleteResourceByResourceId(Integer resourceId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE resource SET shareStatus = ?1 ,employeeId = ?2 WHERE resourceId = ?3 ")
    int updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, Integer resourceId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE resource SET shareStatus = ?1 WHERE resourceId = ?2 ")
    int updateShareStatusByResourceId(String shareStatus, Integer resourceId);

    Page<Resource> findResourceByEmployeeId(String employeeId, Pageable pageable);

    Page<Resource> findResourceByShareStatus(Integer shareStatus, Pageable pageable);

    List<Resource> findResourceByShareStatus(Integer shareStatus);

    Boolean existsByPhoneNumber(String phoneNumber);

    List<Resource> findResourceByEmployeeIdAndCreateDateBetween(String employeeId, String startDate, String endDate);

    List<Resource> findResourceByCreateDateBetween(String startDate, String endDate);

    Resource findResourceByPhoneNumber(String phoneNumber);
}
