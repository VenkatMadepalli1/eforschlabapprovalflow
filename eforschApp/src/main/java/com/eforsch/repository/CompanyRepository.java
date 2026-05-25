package com.eforsch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.Company;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    // Find all companies by company number (since multiple companies can have same company_no)
    List<Company> findByCompanyNo(String companyNo);

    // Find all companies by company name
    List<Company> findByCompanyName(String companyName);

    // Find a company by both company_no and company_name
    Optional<Company> findByCompanyNoAndCompanyName(String companyNo, String companyName);
}
