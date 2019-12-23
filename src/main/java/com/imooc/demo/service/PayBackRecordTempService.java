package com.imooc.demo.service;

import com.imooc.demo.model.PayBackRecord;
import com.imooc.demo.model.PayBackRecordTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * @Author emperor
 * @Date 2019/11/22 15:40
 * @Version 1.0
 */
public interface PayBackRecordTempService {
    Boolean savePayBackRecordTemp(PayBackRecordTemp payBackRecordTemp);

    Boolean isExist(String employeeId, String businessId, Integer status);

    Page<PayBackRecordTemp> findPayBackRecordTempByStatus(Integer status, Pageable pageable);

    Page<PayBackRecordTemp> findPayBackRecordTempByStatusIsNot(Integer status, Pageable pageable);

    PayBackRecordTemp findPayBackRecordTempById(Integer id);


}
