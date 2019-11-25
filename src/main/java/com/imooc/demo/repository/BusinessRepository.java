package com.imooc.demo.repository;

import com.imooc.demo.modle.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author emperor
 * @Date 2019/11/22 15:14
 * @Version 1.0
 */
public interface BusinessRepository extends JpaRepository<Business, String> {
    List<Business> getBusinessByEmployeeId(String employeeId);

    Business getBusinessByBusinessId(String businessId);

    public Boolean createPublicBusiness(Business business);

    Boolean createBusiness(Business business);

    Boolean updateBusinessStatusById(String businessId, Integer businessStatus);

}
