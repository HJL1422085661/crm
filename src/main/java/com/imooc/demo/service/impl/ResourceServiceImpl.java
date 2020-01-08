package com.imooc.demo.service.impl;

import com.imooc.demo.model.Resource;
import com.imooc.demo.repository.ResourceRepository;
import com.imooc.demo.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:43
 * @Version 1.0
 */
@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {


    @Autowired
    public ResourceRepository resourceRepository;

    @Override
    public List<Resource> getResourceByEmployeeId(String employeeId) {
        return resourceRepository.getResourceListByEmployeeId(employeeId);
    }

    @Override
    public Page<Resource> findResourceByEmployeeIdAndLike(Integer shareStatus, String phoneNumber, String resourceName,
                                             String qq, String email, String info,
                                             String certificate, String province, String employeeId, Pageable pageable) {
        return resourceRepository.findResourceByEmployeeIdAndLike(shareStatus, phoneNumber, resourceName,
                qq, email, info, certificate, province, employeeId, pageable);
    }

    @Override
    public Page<Resource> findResourceByLike(Integer shareStatus, String phoneNumber, String resourceName,
                                             String qq, String email, String info,
                                             String certificate, String province, Pageable pageable) {
        return resourceRepository.findResourceByLike(shareStatus, phoneNumber, resourceName,
                qq, email, info, certificate, province, pageable);
    }

    //    @Override
//    public Page<Resource> findResourceByShareStatusAndPhoneNumberLike(Integer shareStatus, String phoneNumber, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndPhoneNumberLike(shareStatus, phoneNumber, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmployeeIdAndPhoneNumberLike(Integer shareStatus, String employeeId, String phoneNumber, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmployeeIdAndPhoneNumberLike(shareStatus, employeeId, phoneNumber, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndResourceNameLike(Integer shareStatus, String resourceName, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndResourceNameLike(shareStatus, resourceName, pageable);
//    }
//
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmployeeIdAndResourceNameLike(Integer shareStatus, String employeeId, String resourceName, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmployeeIdAndResourceNameLike(shareStatus, employeeId, resourceName, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndQqLike(Integer shareStatus, String qq, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndQqLike(shareStatus, qq, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmployeeIdAndQqLike(Integer shareStatus, String employeeId, String qq, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmployeeIdAndQqLike(shareStatus, employeeId, qq, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmailLike(Integer shareStatus, String email, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmailLike(shareStatus, email, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmployeeIdAndEmailLike(Integer shareStatus, String employeeId, String email, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmployeeIdAndEmailLike(shareStatus, employeeId, email, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndInfoLike(Integer shareStatus, String info, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndInfoLike(shareStatus, info, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmployeeIdAndInfoLike(Integer shareStatus, String employeeId, String info, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmployeeIdAndInfoLike(shareStatus, employeeId, info, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndCertificateLike(Integer shareStatus, String certificate, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndCertificateLike(shareStatus, certificate, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmployeeIdAndCertificateLike(Integer shareStatus, String employeeId, String certificate, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmployeeIdAndCertificateLike(shareStatus, employeeId, certificate, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndProvinceLike(Integer shareStatus, String province, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndProvinceLike(shareStatus, province, pageable);
//    }
//
//    @Override
//    public Page<Resource> findResourceByShareStatusAndEmployeeIdAndProvinceLike(Integer shareStatus, String employeeId, String province, Pageable pageable) {
//        return resourceRepository.findResourceByShareStatusAndEmployeeIdAndProvinceLike(shareStatus, employeeId, province, pageable);
//    }

    @Override
    public Boolean saveResource(Resource resource) {
//        resourceRepository.flush();
        Resource resource1 = resourceRepository.saveAndFlush(resource);
        if (resource1 == null) return false;
        return true;
    }

    @Override
    public Resource findResourceByPhoneNumber(String phoneNumber) {
        return resourceRepository.findResourceByPhoneNumber(phoneNumber);
    }

    @Transactional
    @Override
    public Resource createResource(Resource resource) {
//        resourceRepository.flush();
        Resource resource1;
        try {
            resource1 = resourceRepository.saveAndFlush(resource);
        } catch (Exception e) {
            log.error("【创建人才资源】发生异常");
            return null;
        }
        return resource1;
    }

    @Override
    public Resource getResourceByResourceId(Integer resourceId) {
        return resourceRepository.getResourceByResourceId(resourceId);
    }

    @Modifying
    @Transactional
    @Override
    public Integer deleteResourceByResourceId(Integer resourceId) {
        Integer res = resourceRepository.deleteResourceByResourceId(resourceId);
//        resourceRepository.flush();
        return res;
    }

    @Override
    public Boolean updateShareStatusAndEmployeeIdByResourceId(String shareStatus, String employeeId, Integer resourceId) {
        if (resourceRepository.updateShareStatusAndEmployeeIdByResourceId(shareStatus, employeeId, resourceId) != 0)
            return true;
        else return false;
    }


    @Override
    public Boolean updateShareStatusByResourceId(String sharStatus, Integer resourceId) {
        if (resourceRepository.updateShareStatusByResourceId(sharStatus, resourceId) != 0) return true;
        else return false;
    }

    @Override
    public Page<Resource> findResourceByEmployeeId(String employeeId, Pageable pageable) {

        return resourceRepository.findResourceByEmployeeId(employeeId, pageable);
    }

    @Override
    public Page<Resource> findAllResourcePageable(Pageable pageable) {
        return resourceRepository.findAll(pageable);
    }

    @Override
    public List<Resource> findResourceByShareStatus(Integer shareStatus) {
        return resourceRepository.findResourceByShareStatus(shareStatus);
    }

    @Override
    public List<Resource> findAllResource() {
        return resourceRepository.findAll();
    }

    @Override
    public Page<Resource> findResourceByShareStatusPageable(Integer shareStatus, Pageable pageable) {
        return resourceRepository.findResourceByShareStatus(shareStatus, pageable);
    }

    @Override
    public Boolean existsByPhoneNumber(String phoneNumber) {
        return resourceRepository.existsByPhoneNumber(phoneNumber);
    }


    @Override
    public List<Resource> getNewResourceClients(String employeeId, String searchStartDate, String searchEndDate) {
        return resourceRepository.findResourceByEmployeeIdAndCreateDateBetween(employeeId, searchStartDate, searchEndDate);
    }

    @Override
    public List<Resource> getAllNewResourceClients(String searchStartDate, String searchEndDate) {
        return resourceRepository.findResourceByCreateDateBetween(searchStartDate, searchEndDate);
    }
}
