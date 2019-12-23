package com.imooc.demo.repository;

import com.imooc.demo.model.PayBackRecord;
import com.imooc.demo.model.PayBackRecordTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PayBackRecordTempRepository extends JpaRepository<PayBackRecordTemp, Integer> {

    Page<PayBackRecordTemp> findPayBackRecordTempByStatus(Integer status, Pageable pageable);

    Page<PayBackRecordTemp> findPayBackRecordTempByStatusIsNot(Integer status, Pageable pageable);

    PayBackRecordTemp findPayBackRecordTempById(Integer id);

    Boolean existsByEmployeeIdAndBusinessIdAndStatus(String employeeId, String businessId, Integer status);
}

