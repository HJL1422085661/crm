package com.imooc.demo.repository;

import com.imooc.demo.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:44
 * @Version 1.0
 */
public interface ResourceRepository extends JpaRepository<Resource, Integer>, JpaSpecificationExecutor {


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
//
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
//    Page<Resource> findResourceByShareStatusAndEmployeeIdAndEmailLike(Integer shareStatus, String email, String qq, Pageable pageable);
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


    @Query(nativeQuery = true, value = "select * from Resource r where r.shareStatus = ?1 " +
            "and (r.phoneNumber like CONCAT('%', ?2, '%') or ?2 = '') "
            + "and (r.resourceName like CONCAT('%', ?3, '%') or ?3  = '') " +
            "and (r.qq like CONCAT('%', ?4, '%') or ?4  = '') " +
            "and (r.email like CONCAT('%', ?5, '%') or ?5  = '') "
            + "and (r.info like CONCAT('%', ?6, '%') or ?6  = '') " +
            "and (r.certificate like CONCAT('%', ?7, '%') or ?7 = '') " +
            "and (r.province like CONCAT('%', ?8, '%') or ?8  = '')")
    Page<Resource> findResourceByLike(Integer shareStatus, String phoneNumber, String resourceName,
                                      String qq, String email, String info,
                                      String certificate, String province, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from Resource r where r.shareStatus = ?1 " +
            "and (r.phoneNumber like CONCAT('%', ?2, '%') or ?2 = '') "
            + "and (r.resourceName like CONCAT('%', ?3, '%') or ?3  = '') " +
            "and (r.qq like CONCAT('%', ?4, '%') or ?4  = '') " +
            "and (r.email like CONCAT('%', ?5, '%') or ?5  = '') "
            + "and (r.info like CONCAT('%', ?6, '%') or ?6  = '') " +
            "and (r.certificate like CONCAT('%', ?7, '%') or ?7  = '') " +
            "and (r.province like CONCAT('%', ?8, '%') or ?8  = '') "
            + " and (r.employeeId = ?9 or ?9  = '')")
    Page<Resource> findResourceByEmployeeIdAndLike(Integer shareStatus, String phoneNumber, String resourceName,
                                                   String qq, String email, String info,
                                                   String certificate, String province, String employeeId, Pageable pageable);


}
