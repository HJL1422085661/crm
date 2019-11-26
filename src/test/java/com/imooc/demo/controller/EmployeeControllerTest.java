package com.imooc.demo.controller;

import com.imooc.demo.modle.Resource;
import com.imooc.demo.repository.ResourceRepository;
import com.imooc.demo.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Author emperor
 * @Date 2019/11/25 22:08
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class EmployeeControllerTest {

    @Autowired
    public ResourceRepository resourceRepository;
    @Test
    void createResource() {
        Resource resource = new Resource();

        resource.setEmployeeId("3");
        resource.setResourceName("李四");
        resource.setShareStatus("private");

        resource.setQQ("1422123344");
        resource.setEmail("xxx.qq.com");
        resource.setGender(1);
        resource.setPhone("13731345667");
        resource.setProvince("四川");
        resource.setInfo("London No,2 Lake Park");
        resource.setCertificate("四六级证书");
        Date date = new Date();
        resource.setCreateTime(date);
        resource.setEndTime(date);

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd");

        for (int i = 1; i < 20; i++) {
            resource.setResourceId(String.valueOf(i));
            resource.setStatus((i % 4) + 1);
            resourceRepository.saveAndFlush(resource);
        }

    }

    @Test
    void updateResource() {
    }

    @Test
    void updateResourceShareStatus() {
    }

    @Test
    void deleteResource() {
    }

    @Test
    void getResourceList() {
    }
}