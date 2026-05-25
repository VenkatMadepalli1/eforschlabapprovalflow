package com.eforsch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eforsch.entity.Company;
import com.eforsch.repository.CompanyRepository;
import com.eforsch.util.CompanyVO;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    // Convert Entity to VO
    private CompanyVO entityToVO(Company company) {
        CompanyVO companyVO = new CompanyVO();
        companyVO.setId(company.getId());
        companyVO.setCompanyNo(company.getCompanyNo());
        companyVO.setCompanyName(company.getCompanyName());
        companyVO.setCreatedAt(company.getCreatedAt());
        companyVO.setUpdatedAt(company.getUpdatedAt());
        return companyVO;
    }

    // Convert VO to Entity
    private Company voToEntity(CompanyVO companyVO) {
        Company company = new Company();
        if (companyVO.getId() != null && companyVO.getId() > 0) {
            company.setId(companyVO.getId());
        }
        company.setCompanyNo(companyVO.getCompanyNo());
        company.setCompanyName(companyVO.getCompanyName());
        if (companyVO.getCreatedAt() != null) {
            company.setCreatedAt(companyVO.getCreatedAt());
        } else {
            company.setCreatedAt(LocalDateTime.now());
        }
        company.setUpdatedAt(LocalDateTime.now());
        return company;
    }

    // GET - Get all companies
    public List<CompanyVO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    // GET - Get company by ID
    public CompanyVO getCompanyById(Integer id) {
        return companyRepository.findById(id)
                .map(this::entityToVO)
                .orElse(null);
    }

    // GET - Get all companies by company number (multiple companies can have same company_no)
    public List<CompanyVO> getCompaniesByCompanyNo(String companyNo) {
        return companyRepository.findByCompanyNo(companyNo).stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    // GET - Get all companies by company name
    public List<CompanyVO> getCompaniesByCompanyName(String companyName) {
        return companyRepository.findByCompanyName(companyName).stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    // POST - Create new company
    @Transactional
    public CompanyVO createCompany(CompanyVO companyVO) {
        Company company = voToEntity(companyVO);
        company = companyRepository.save(company);
        return entityToVO(company);
    }

    // PUT - Update existing company
    @Transactional
    public CompanyVO updateCompany(CompanyVO companyVO) {
        if (companyVO.getId() == null) {
            throw new IllegalArgumentException("Company ID is required for update");
        }

        Company existingCompany = companyRepository.findById(companyVO.getId())
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyVO.getId()));

        existingCompany.setCompanyNo(companyVO.getCompanyNo());
        existingCompany.setCompanyName(companyVO.getCompanyName());
        existingCompany.setUpdatedAt(LocalDateTime.now());

        Company updatedCompany = companyRepository.save(existingCompany);
        return entityToVO(updatedCompany);
    }

    // DELETE - Delete company by ID
    @Transactional
    public void deleteCompany(Integer id) {
        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found with ID: " + id);
        }
        companyRepository.deleteById(id);
    }

    // DELETE - Delete all companies by company number
    @Transactional
    public void deleteCompaniesByCompanyNo(String companyNo) {
        List<Company> companies = companyRepository.findByCompanyNo(companyNo);
        companyRepository.deleteAll(companies);
    }

    // Check if company exists
    public boolean isCompanyExists(Integer id) {
        return companyRepository.existsById(id);
    }

    // Check if company exists by company number and name combination
    public boolean isCompanyExists(String companyNo, String companyName) {
        return companyRepository.findByCompanyNoAndCompanyName(companyNo, companyName).isPresent();
    }
}
