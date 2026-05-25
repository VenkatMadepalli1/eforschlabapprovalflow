package com.eforsch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.service.CompanyService;
import com.eforsch.util.CompanyVO;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // GET - Retrieve all companies
    @GetMapping("/getAllCompanies")
    public ResponseEntity<List<CompanyVO>> getAllCompanies() {
        List<CompanyVO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    // GET - Retrieve company by ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyVO> getCompanyById(@PathVariable Integer id) {
        CompanyVO company = companyService.getCompanyById(id);
        if (company != null) {
            return ResponseEntity.ok(company);
        }
        return ResponseEntity.notFound().build();
    }

    // GET - Retrieve all companies by company number (multiple companies can have same company_no)
    @GetMapping("/byCompanyNo/{companyNo}")
    public ResponseEntity<List<CompanyVO>> getCompaniesByCompanyNo(@PathVariable String companyNo) {
        List<CompanyVO> companies = companyService.getCompaniesByCompanyNo(companyNo);
        if (companies != null && !companies.isEmpty()) {
            return ResponseEntity.ok(companies);
        }
        return ResponseEntity.notFound().build();
    }

    // GET - Retrieve all companies by company name
    @GetMapping("/byCompanyName/{companyName}")
    public ResponseEntity<List<CompanyVO>> getCompaniesByCompanyName(@PathVariable String companyName) {
        List<CompanyVO> companies = companyService.getCompaniesByCompanyName(companyName);
        if (companies != null && !companies.isEmpty()) {
            return ResponseEntity.ok(companies);
        }
        return ResponseEntity.notFound().build();
    }

    // POST - Create a new company
    @PostMapping("/createCompany")
    public ResponseEntity<CompanyVO> createCompany(@RequestBody CompanyVO companyVO) {
        if (companyVO.getCompanyNo() == null || companyVO.getCompanyNo().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (companyVO.getCompanyName() == null || companyVO.getCompanyName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CompanyVO createdCompany = companyService.createCompany(companyVO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
    }

    // PUT - Update an existing company
    @PutMapping("/updateCompany")
    public ResponseEntity<CompanyVO> updateCompany(@RequestBody CompanyVO companyVO) {
        if (companyVO.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (companyVO.getCompanyNo() == null || companyVO.getCompanyNo().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (companyVO.getCompanyName() == null || companyVO.getCompanyName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            CompanyVO updatedCompany = companyService.updateCompany(companyVO);
            return ResponseEntity.ok(updatedCompany);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Delete a company by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
        try {
            companyService.deleteCompany(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Delete all companies by company number
    @DeleteMapping("/byCompanyNo/{companyNo}")
    public ResponseEntity<Void> deleteCompaniesByCompanyNo(@PathVariable String companyNo) {
        try {
            companyService.deleteCompaniesByCompanyNo(companyNo);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
