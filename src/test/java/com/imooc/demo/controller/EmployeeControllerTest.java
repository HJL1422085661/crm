package com.imooc.demo.controller;

import com.imooc.demo.modle.Company;
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

        resource.setQq("1422123344");
        resource.setEmail("xxx.qq.com");
        resource.setPhone("13731345667");
        resource.setProvince("四川");
        resource.setInfo("London No,2 Lake Park");
        resource.setCertificate("四六级证书");
        resource.setIdentify("12414135");
        resource.setCreateDate("2019-11-20");
        resource.setEndDate("2020-10-20");
        resource.setShareStatus("private");
        resource.setEmployeeName("test");
//        resource.setCreateTime(date);
//        resource.setEndTime(date);

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd");

        for (int i = 1; i < 20; i++) {
            resource.setStatus((i % 4) + 1);
            resource.setGender(i);
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

    @Test
    void createResource1() {
    }

    @Test
    void createCompany() {
        Company company = new Company();
        company.setCompanyName("江西建筑集团有限公司");
        company.setCompanyCategory("建筑公司");
        company.setContactorName("胡总");
        company.setPhoneNumber("12342536");
        company.setEmployeeId("张三");
        company.setGender(1);

    }
}