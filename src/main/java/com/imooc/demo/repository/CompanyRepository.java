package com.imooc.demo.repository;

import com.imooc.demo.modle.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, String> {
}
