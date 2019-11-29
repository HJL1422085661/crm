package com.imooc.demo.repository;

import com.imooc.demo.modle.CompanyTemp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyTempRepository extends JpaRepository<CompanyTemp, String> {

    CompanyTemp findCompanyTempById(Integer id);
}
