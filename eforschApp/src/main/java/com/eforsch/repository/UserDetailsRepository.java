package com.eforsch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eforsch.dto.User;
import com.eforsch.entity.UserDetails;
import com.eforsch.entity.UserRole;



@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
	 
	UserDetails findByUserId(Long id);
	//UserDetails findByEmail(String email);
	List<UserDetails> findByUserRole(UserRole role);
	List<UserDetails> findByGroupName(String groupName);
	//List<UserDetails> findByRoleNot(String role);
	
	@Query("select u from UserDetails u join fetch u.userRole where u.email = :email")
	UserDetails findByEmailWithRole(@Param("email") String email);
	
	@Query("select u from UserDetails u left join fetch u.userRole")
	List<UserDetails> findAllWithRole();
	
	@Query("select u from UserDetails u left join fetch u.userRole where u.groupName = :groupName")
	List<UserDetails> findByGroupNameWithRole(@Param("groupName") String groupName);
}