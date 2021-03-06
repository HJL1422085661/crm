package com.imooc.demo.service;

import com.imooc.demo.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:40
 * @Version 1.0
 */
public interface ResourceService {
    List<Resource> getResourceByEmployeeId(String employeeId);

    Boolean saveResource(Resource resource);

    Resource createResource(Resource resource);

    Resource getResourceByResourceId(Integer resourceId);

    Integer deleteResourceByResourceId(Integer resourceId);

    Boolean updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, Integer resourceId);

    Boolean updateShareStatusByResourceId(String shareStatus, Integer resourceId);

    Page<Resource> findResourceByEmployeeId(String employeeId, Pageable pageable);

    Page<Resource> findAllResourcePageable(Pageable pageable);

    List<Resource> findAllResource();

    List<Resource> findResourceByShareStatus(Integer shareStatus);

    Page<Resource> findResourceByShareStatusPageable(Integer shareStatus, Pageable pageable);

    Boolean existsByPhoneNumber(String phoneNumber);

    List<Resource> getNewResourceClients(String employeeId, String searchStartDate, String searchEndDate);

    List<Resource> getAllNewResourceClients(String searchStartDate, String searchEndDate);

    Resource findResourceByPhoneNumber(String phoneNumber);

    HashSet<String> getAllResourcePhoneNumber();

    Page<Resource> findResourceByEmployeeIdAndLike(Integer shareStatus, String phoneNumber, String resourceName,
                                      String qq, String email, String info,
                                      String certificate, String province, String employeeId,
                                      Pageable pageable);

    Page<Resource> findResourceByLike(Integer shareStatus, String phoneNumber, String resourceName,
                                      String qq, String email, String info,
                                      String certificate, String province,
                                      Pageable pageable);


}
