package com.eforsch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    // Add custom query methods here if needed
	    UserRole findByRolename(String rolename);
	    UserRole findByRole(String role);
}
