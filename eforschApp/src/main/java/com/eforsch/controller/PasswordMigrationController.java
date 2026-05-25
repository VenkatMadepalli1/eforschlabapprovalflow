package com.eforsch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.ApiResponse;
import com.eforsch.service.PasswordMigrationService;

@RestController
@RequestMapping("/api/admin")
public class PasswordMigrationController {

    @Autowired
    private PasswordMigrationService passwordMigrationService;

    @PostMapping("/migrate-passwords")
    public ApiResponse migratePasswords() {
        return passwordMigrationService.migratePasswords();
    }
}
