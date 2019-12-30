package com.imooc.demo.repository;

import com.imooc.demo.model.PayBackRecord;
import com.imooc.demo.model.PayBackRecordTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PayBackRecordTempRepository extends JpaRepository<PayBackRecordTemp, Integer> {


    PayBackRecordTemp findPayBackRecordTempById(Integer id);

    Boolean existsByEmployeeIdAndBusinessIdAndCheckedStatus(String employeeId, String businessId, Integer checkedStatus);

    Page<PayBackRecordTemp> findPayBackRecordTempByCheckedStatus(Integer checkedStatus, Pageable pageable);

    Page<PayBackRecordTemp> findPayBackRecordTempByCheckedStatusIsNot(Integer checkedStatus, Pageable pageable);
}

