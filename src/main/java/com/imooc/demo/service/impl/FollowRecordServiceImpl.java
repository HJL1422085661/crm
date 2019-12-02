package com.imooc.demo.service.impl;

import com.imooc.demo.modle.ResourceFollowRecord;
import com.imooc.demo.repository.FollowRecordRepository;
import com.imooc.demo.service.FollowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * @Author emperor
 * @Date 2019/11/27 13:19
 * @Version 1.0
 */
@Service
@Slf4j
public class FollowRecordServiceImpl implements FollowRecordService {
    @Autowired
    public FollowRecordRepository followRecordRepository;

    @Override
    public Page<ResourceFollowRecord> getFollowRecordsByResourceId(Integer resourceId, Pageable pageable) {
        return followRecordRepository.getResourceFollowRecordByResourceId(resourceId, pageable);
    }

    @Override
    public ResourceFollowRecord createResourceFollow(ResourceFollowRecord resourceFollowRecord) {
        try {
            return  followRecordRepository.saveAndFlush(resourceFollowRecord);
        }catch (Exception e){
            log.error("【创建人才跟进信息】发生异常");
            return null;
        }

    }
}
