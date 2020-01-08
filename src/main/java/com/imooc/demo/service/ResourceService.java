package com.imooc.demo.service;

import com.imooc.demo.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

//    Page<Resource> findResourceByShareStatusAndPhoneNumberLike(Integer shareStatus, String phoneNumber, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndPhoneNumberLike(Integer shareStatus, String employeeId, String phoneNumber, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndResourceNameLike(Integer shareStatus, String resourceName, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndResourceNameLike(Integer shareStatus, String employeeId, String resourceName, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndQqLike(Integer shareStatus, String qq, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndQqLike(Integer shareStatus, String employeeId, String qq, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmailLike(Integer shareStatus, String email, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndEmailLike(Integer shareStatus, String employeeId, String email, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndInfoLike(Integer shareStatus, String info, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndInfoLike(Integer shareStatus, String employeeId, String info, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndCertificateLike(Integer shareStatus, String certificate, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndCertificateLike(Integer shareStatus, String employeeId, String certificate, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndProvinceLike(Integer shareStatus, String province, Pageable pageable);
//
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndProvinceLike(Integer shareStatus, String employeeId, String province, Pageable pageable);

    Page<Resource> findResourceByEmployeeIdAndLike(Integer shareStatus, String phoneNumber, String resourceName,
                                      String qq, String email, String info,
                                      String certificate, String province, String employeeId,
                                      Pageable pageable);

    Page<Resource> findResourceByLike(Integer shareStatus, String phoneNumber, String resourceName,
                                      String qq, String email, String info,
                                      String certificate, String province,
                                      Pageable pageable);


}
