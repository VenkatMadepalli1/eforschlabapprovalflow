package com.eforsch.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    Page<Project> findAll(Pageable pageable);

    Optional<Project> findByProjectId(String projectId);

    @Query(value = """
            SELECT p.project_id
            FROM project p
            WHERE p.project_id REGEXP '^PRJ-[0-9]+$'
            ORDER BY CAST(SUBSTRING_INDEX(p.project_id, '-', -1) AS UNSIGNED) DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<String> findLatestFormattedProjectId();

    Page<Project> findByGroupNameContainingIgnoreCase(String groupName, Pageable pageable);

    Page<Project> findByProjectNameContainingIgnoreCase(String projectName, Pageable pageable);

    Page<Project> findByRoleIgnoreCase(String role, Pageable pageable);

    Page<Project> findByGroupNameContainingIgnoreCaseAndRoleIgnoreCase(String groupName, String role, Pageable pageable);
    
    Page<Project> findByProjectNameContainingIgnoreCaseAndGroupNameContainingIgnoreCaseAndRoleIgnoreCase(
            String projectName, String groupName, String role, Pageable pageable);

    Page<Project> findByProjectNameContainingIgnoreCaseAndGroupNameContainingIgnoreCase(
            String projectName, String groupName, Pageable pageable);

    Page<Project> findByProjectNameContainingIgnoreCaseAndRoleIgnoreCase(
            String projectName, String role, Pageable pageable);

	
    @Query(value = """
            SELECT * 
            FROM project p
            WHERE
              (:search IS NULL OR :search = '' 
                  OR LOWER(p.project_name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND
              (:groupName IS NULL OR :groupName = '' 
                  OR LOWER(p.group_name) LIKE LOWER(CONCAT('%', :groupName, '%')))
              AND
              (
                 :budgetNo IS NULL OR :budgetNo = ''
                 OR FIND_IN_SET(:budgetNo, REPLACE(p.budget_nos, ' ', '')) > 0
                 OR p.budget_nos LIKE CONCAT('%\"', :budgetNo, '\"%')
              )
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM project p
            WHERE
              (:search IS NULL OR :search = '' 
                  OR LOWER(p.project_name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND
              (:groupName IS NULL OR :groupName = '' 
                  OR LOWER(p.group_name) LIKE LOWER(CONCAT('%', :groupName, '%')))
              AND
              (
                 :budgetNo IS NULL OR :budgetNo = ''
                 OR FIND_IN_SET(:budgetNo, REPLACE(p.budget_nos, ' ', '')) > 0
                 OR p.budget_nos LIKE CONCAT('%\"', :budgetNo, '\"%')
              )
            """,
            nativeQuery = true)
        Page<Project> searchProjectsNative(@Param("search") String search,
                                           @Param("groupName") String groupName,
                                           @Param("budgetNo") String budgetNo,
                                           Pageable pageable);

}
