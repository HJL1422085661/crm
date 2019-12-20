package com.imooc.demo.repository;

import com.imooc.demo.model.ResourceFollowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @Author emperor
 * @Date 2019/11/22 15:14
 * @Version 1.0
 */
public interface FollowRecordRepository extends JpaRepository<ResourceFollowRecord, String> {
    Page<ResourceFollowRecord> getResourceFollowRecordByResourceId(Integer resourceId, Pageable pageable);

}
