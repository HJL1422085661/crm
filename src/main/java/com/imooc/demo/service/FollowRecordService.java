package com.imooc.demo.service;

import com.imooc.demo.model.ResourceFollowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * @Author emperor
 * @Date 2019/11/27 13:16
 * @Version 1.0
 */
public interface FollowRecordService {
    Page<ResourceFollowRecord> getFollowRecordsByResourceId(Integer resourceId, Pageable pageable);
    ResourceFollowRecord createResourceFollow(ResourceFollowRecord resourceFollowRecord);
}
