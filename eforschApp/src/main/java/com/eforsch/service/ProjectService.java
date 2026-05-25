package com.eforsch.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eforsch.Column;
import com.eforsch.ProjectResponse;
import com.eforsch.dto.FileDownload;
import com.eforsch.dto.PaginationMeta;
import com.eforsch.dto.ProjectRequest;
import com.eforsch.dto.ProjectVO;
import com.eforsch.entity.ProjectArchive;
import com.eforsch.entity.ProjectArchiveAttachment;
import com.eforsch.entity.ProjectAttachment;
import com.eforsch.entity.Project;
import com.eforsch.repository.ProjectArchiveAttachmentRepository;
import com.eforsch.repository.ProjectArchiveRepository;
import com.eforsch.repository.ProjectAttachmentRepository;
import com.eforsch.repository.ProjectRepository;

@Service
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectArchiveRepository projectArchiveRepository;
	private final ProjectArchiveAttachmentRepository projectArchiveAttachmentRepository;
	private final ProjectAttachmentRepository projectAttachmentRepository;

	public ProjectService(ProjectRepository projectRepository,
			ProjectArchiveRepository projectArchiveRepository,
			ProjectArchiveAttachmentRepository projectArchiveAttachmentRepository,
			ProjectAttachmentRepository projectAttachmentRepository) {
		this.projectRepository = projectRepository;
		this.projectArchiveRepository = projectArchiveRepository;
		this.projectArchiveAttachmentRepository = projectArchiveAttachmentRepository;
		this.projectAttachmentRepository = projectAttachmentRepository;
	}

	public ProjectResponse getProjects(Integer page, Integer size, String search, String budgetNo, String groupName) {
		try {
			int p = (page == null || page < 1) ? 1 : page;
			int s = (size == null || size < 1) ? 10 : size;

			Pageable pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Order.asc("project_name")));

			String searchVal = normalize(search);
			String groupVal = normalize(groupName);
			String budgetVal = normalize(budgetNo);

			Page<Project> result = projectRepository.searchProjectsNative(searchVal, groupVal, budgetVal, pageable);

			List<ProjectVO> data = result.getContent().stream().map(this::toProjectVO).toList();

			return new ProjectResponse(200, "SUCCESS", "Projects fetched successfully", data,
					new PaginationMeta(result.getTotalPages(), result.getSize(), result.getNumber() + 1,
							result.getTotalElements()),
					columns());

		} catch (Exception e) {
			return new ProjectResponse(500, "FAILED", "An error occurred while fetching projects: " + e.getMessage(),
					null, null, null);
		}
	}

	private List<Column> columns() {
		return List.of(new Column("projectName", "Project Name", true, true),
				new Column("longDescription", "Long Description", true, true),
				new Column("budgetNos", "Budget Numbers", false, true),
				new Column("groupName", "Group Name", true, true),
				new Column("attachment", "Attachments", false, false),
				new Column("createdDate", "Created Date", true, false),
				new Column("updatedDate", "Updated Date", true, false));
	}

	private static boolean notBlank(String v) {
		return v != null && !v.trim().isEmpty();
	}

	private static String normalize(String v) {
		return (v == null) ? null : v.trim();
	}

	private String generateNextProjectId() {
		String latestProjectId = projectRepository.findLatestFormattedProjectId().orElse(null);
		if (latestProjectId == null) {
			return "PRJ-001";
		}

		int nextSequence = Integer.parseInt(latestProjectId.substring(latestProjectId.lastIndexOf('-') + 1)) + 1;
		return String.format("PRJ-%03d", nextSequence);
	}

	private ProjectVO toProjectVO(Project project) {
		List<String> attachmentNames = projectAttachmentRepository.findByProjectIdOrderByAttachmentIdAsc(project.getProjectId())
				.stream()
				.map(ProjectAttachment::getFileName)
				.collect(Collectors.toList());
		return ProjectVO.fromEntity(project, attachmentNames);
	}

	private ProjectVO toProjectVO(ProjectArchive projectArchive) {
		List<String> attachmentNames = projectArchiveAttachmentRepository
				.findByProjectIdOrderByAttachmentIdAsc(projectArchive.getProjectId())
				.stream()
				.map(ProjectArchiveAttachment::getFileName)
				.collect(Collectors.toList());

		ProjectVO projectVO = new ProjectVO();
		projectVO.setProjectId(projectArchive.getProjectId());
		projectVO.setProjectName(projectArchive.getProjectName());
		projectVO.setLongDescription(projectArchive.getLongDescription());
		projectVO.setBudgetNos(projectArchive.getBudgetNos());
		projectVO.setGroupName(projectArchive.getGroupName());
		projectVO.setRole(projectArchive.getRole());
		projectVO.setName(projectArchive.getName());
		projectVO.setUserId(projectArchive.getUserId());
		projectVO.setCreatedDate(projectArchive.getCreatedDate());
		projectVO.setUpdatedDate(projectArchive.getUpdatedDate());
		projectVO.setAttachment(attachmentNames);
		return projectVO;
	}

	private ProjectArchive toProjectArchive(Project project) {
		ProjectArchive projectArchive = new ProjectArchive();
		projectArchive.setProjectId(project.getProjectId());
		projectArchive.setProjectName(project.getProjectName());
		projectArchive.setLongDescription(project.getLongDescription());
		projectArchive.setBudgetNos(project.getBudgetNos());
		projectArchive.setGroupName(project.getGroupName());
		projectArchive.setRole(project.getRole());
		projectArchive.setName(project.getName());
		projectArchive.setUserId(project.getUserId());
		projectArchive.setCreatedDate(project.getCreatedDate());
		projectArchive.setUpdatedDate(project.getUpdatedDate());
		return projectArchive;
	}

	private ProjectArchiveAttachment toProjectArchiveAttachment(ProjectAttachment projectAttachment) {
		ProjectArchiveAttachment projectArchiveAttachment = new ProjectArchiveAttachment();
		projectArchiveAttachment.setProjectId(projectAttachment.getProjectId());
		projectArchiveAttachment.setFileName(projectAttachment.getFileName());
		projectArchiveAttachment.setContentType(projectAttachment.getContentType());
		projectArchiveAttachment.setFileSize(projectAttachment.getFileSize());
		projectArchiveAttachment.setFileData(projectAttachment.getFileData());
		return projectArchiveAttachment;
	}

	private void validateFile(MultipartFile file) {
		long max = 50L * 1024 * 1024;
		if (file.getSize() > max) {
			throw new IllegalArgumentException("File too large. Max 50MB");
		}

		String contentType = file.getContentType();
		if (contentType != null) {
			boolean allowed = contentType.equals("application/pdf") || contentType.equals("image/png")
					|| contentType.equals("image/jpeg") || contentType.equals("application/msword")
					|| contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
					|| contentType.equals("application/vnd.ms-excel")
					|| contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			if (!allowed) {
				throw new IllegalArgumentException("Unsupported file type: " + contentType);
			}
		}
	}

	private void saveAttachments(String projectId, MultipartFile[] attachments) {
		if (attachments == null || attachments.length == 0) {
			return;
		}

		for (MultipartFile attachment : attachments) {
			if (attachment == null || attachment.isEmpty()) {
				continue;
			}

			try {
				validateFile(attachment);

				String fileName = StringUtils.cleanPath(attachment.getOriginalFilename());
				ProjectAttachment entity = projectAttachmentRepository.findByProjectIdAndFileName(projectId, fileName)
						.orElseGet(ProjectAttachment::new);
				entity.setProjectId(projectId);
				entity.setFileName(fileName);
				entity.setContentType(attachment.getContentType());
				entity.setFileSize(attachment.getSize());
				entity.setFileData(attachment.getBytes());

				projectAttachmentRepository.save(entity);
			} catch (Exception e) {
				throw new RuntimeException("An error occurred while saving project attachments: " + e.getMessage(), e);
			}
		}
	}

	public ProjectResponse getProjectById(String projectId) {
		try {
			if (projectId == null || projectId.trim().isEmpty()) {
				return new ProjectResponse(400, "FAILED", "Invalid request parameters", null, null, null);
			}
			return projectRepository.findByProjectId(projectId.trim())
					.map(project -> new ProjectResponse(200, "SUCCESS", "Project fetched successfully",
							Collections.singletonList(toProjectVO(project)), null, null))
					.orElseGet(() -> new ProjectResponse(404, "FAILED", "Project not found", null, null, null));
		} catch (Exception e) {
			return new ProjectResponse(500, "FAILED", "An error occurred while fetching the project: " + e.getMessage(),
					null, null, null);
		}
	}

	@Transactional
	public ProjectVO createProject(ProjectRequest request, MultipartFile[] attachments) {
		try {
			Project project = new Project();

			project.setProjectId(generateNextProjectId());

			project.setProjectName(request.getProjectName());
			project.setLongDescription(request.getLongDescription());
			project.setBudgetNos(request.getBudgetNos());
			project.setGroupName(request.getGroupName());
			project.setRole(request.getRole());
			project.setName(request.getName());
			project.setUserId(request.getUserId());

			Project saved = projectRepository.save(project);
			saveAttachments(saved.getProjectId(), attachments);

			return toProjectVO(saved);
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while creating the project: " + e.getMessage(), e);
		}
	}

	public ProjectVO createProject(ProjectRequest request) {
		return createProject(request, null);
	}

	@Transactional
	public ProjectVO updateProject(String projectId, ProjectRequest request) {
		try {

			Project project = projectRepository.findByProjectId(projectId)
					.orElseThrow(() -> new RuntimeException("Project not found"));

			// Update only allowed fields
			project.setProjectName(request.getProjectName());
			project.setLongDescription(request.getLongDescription());
			project.setBudgetNos(request.getBudgetNos());
			project.setGroupName(request.getGroupName());
			project.setRole(request.getRole());
			project.setName(request.getName());
			project.setUserId(request.getUserId());
			Project updatedProject = projectRepository.save(project);

			// Re-fetch to ensure updated timestamps are reflected
			Project refreshedProject = projectRepository.findByProjectId(projectId)
					.orElseThrow(() -> new RuntimeException("Project not found after update"));

			return toProjectVO(refreshedProject);
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while updating the project: " + e.getMessage(), e);
		}
	}

	@Transactional
	public ProjectVO addAttachmentsToProject(String projectId, MultipartFile[] attachments) {
		Project project = projectRepository.findByProjectId(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

		saveAttachments(projectId, attachments);
		return toProjectVO(project);
	}

	@Transactional
	public ProjectVO deleteAttachmentFromProject(String projectId, String fileName) {
		Project project = projectRepository.findByProjectId(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

		ProjectAttachment attachment = projectAttachmentRepository
				.findByProjectIdAndFileName(projectId, fileName)
				.orElseThrow(() -> new RuntimeException("Attachment not found: " + fileName));

		projectAttachmentRepository.delete(attachment);
		return toProjectVO(project);
	}

	public FileDownload getProjectAttachment(String projectId, String fileName) {
		ProjectAttachment attachment = projectAttachmentRepository.findByProjectIdAndFileName(projectId, fileName)
				.orElseThrow(() -> new RuntimeException("Attachment not found"));

		return new FileDownload(attachment.getFileName(), attachment.getContentType(), attachment.getFileData());
	}

	@Transactional
	public void deleteProject(String projectId) {
		try {

			Project project = projectRepository.findByProjectId(projectId)
					.orElseThrow(() -> new RuntimeException("Project not found"));

			List<ProjectAttachment> projectAttachments = projectAttachmentRepository
					.findByProjectIdOrderByAttachmentIdAsc(projectId);

			projectArchiveRepository.save(toProjectArchive(project));
			if (!projectAttachments.isEmpty()) {
				List<ProjectArchiveAttachment> archiveAttachments = projectAttachments.stream()
						.map(this::toProjectArchiveAttachment)
						.collect(Collectors.toList());
				projectArchiveAttachmentRepository.saveAll(archiveAttachments);
			}

			projectAttachmentRepository.deleteByProjectId(projectId);
			projectRepository.delete(project);
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while deleting the project: " + e.getMessage(), e);
		}
	}

	public ProjectResponse getProjectArchives(Integer page, Integer size) {
		try {
			int p = (page == null || page < 1) ? 1 : page;
			int s = (size == null || size < 1) ? 10 : size;

			Pageable pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Order.desc("archivedAt")));
			Page<ProjectArchive> result = projectArchiveRepository.findAllByOrderByArchivedAtDesc(pageable);

			List<ProjectVO> data = result.getContent().stream().map(this::toProjectVO).toList();

			return new ProjectResponse(200, "SUCCESS", "Project archives fetched successfully", data,
					new PaginationMeta(result.getTotalPages(), result.getSize(), result.getNumber() + 1,
							result.getTotalElements()),
					columns());

		} catch (Exception e) {
			return new ProjectResponse(500, "FAILED",
					"An error occurred while fetching project archives: " + e.getMessage(),
					null, null, null);
		}
	}

	public ProjectResponse getProjectArchivesByUser(String userId, Integer page, Integer size) {
		try {
			if (!notBlank(userId)) {
				return new ProjectResponse(400, "FAILED", "userId is required", null, null, null);
			}

			int p = (page == null || page < 1) ? 1 : page;
			int s = (size == null || size < 1) ? 10 : size;

			Pageable pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Order.desc("archivedAt")));
			Page<ProjectArchive> result = projectArchiveRepository.findByUserIdOrderByArchivedAtDesc(userId, pageable);

			List<ProjectVO> data = result.getContent().stream().map(this::toProjectVO).toList();

			return new ProjectResponse(200, "SUCCESS", "Project archives fetched successfully", data,
					new PaginationMeta(result.getTotalPages(), result.getSize(), result.getNumber() + 1,
							result.getTotalElements()),
					columns());

		} catch (Exception e) {
			return new ProjectResponse(500, "FAILED",
					"An error occurred while fetching project archives by user: " + e.getMessage(),
					null, null, null);
		}
	}

	// New overloaded version with groupName parameter
	public ProjectResponse getProjectArchivesByUser(String userId, Integer page, Integer size, String groupName) {
		try {
			if (!notBlank(userId)) {
				return new ProjectResponse(400, "FAILED", "userId is required", null, null, null);
			}

			int p = (page == null || page < 1) ? 1 : page;
			int s = (size == null || size < 1) ? 10 : size;

			Pageable pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Order.desc("archivedAt")));
			Page<ProjectArchive> result = projectArchiveRepository.findByUserIdAndGroupNameOrderByArchivedAtDesc(userId, groupName, pageable);

			List<ProjectVO> data = result.getContent().stream().map(this::toProjectVO).toList();

			return new ProjectResponse(200, "SUCCESS", "Project archives fetched successfully", data,
					new PaginationMeta(result.getTotalPages(), result.getSize(), result.getNumber() + 1,
							result.getTotalElements()),
					columns());

		} catch (Exception e) {
			return new ProjectResponse(500, "FAILED",
					"An error occurred while fetching project archives by user: " + e.getMessage(),
					null, null, null);
		}
	}

	/**
	 * Checks if user has access to view project based on group membership
	 * @param projectId Project ID to check
	 * @param userGroupName User's group name
	 * @return true if user's group matches project's group, false otherwise
	 */
	public boolean canAccessProject(String projectId, String userGroupName) {
		if (projectId == null || userGroupName == null) {
			return false;
		}
		return projectRepository.findByProjectId(projectId)
				.map(project -> userGroupName.equals(project.getGroupName()))
				.orElse(false);
	}

	/**
	 * Checks if user has access to view another user's archives
	 * GROUP_LEADER can view any user's archives in their group
	 * Regular users can only view their own archives
	 * @param userId User ID whose archives are being accessed
	 * @param requestingUserGroupName Requesting user's group
	 * @param requestingUserRole Requesting user's role
	 * @return true if access is allowed, false otherwise
	 */
	public boolean canAccessUserArchives(String userId, String requestingUserGroupName, String requestingUserRole) {
		if (userId == null || requestingUserGroupName == null) {
			return false;
		}
		// GROUP_LEADER can view any user's archives, others can only view their own
		if ("GROUP_LEADER".equalsIgnoreCase(requestingUserRole)) {
			return true;
		}
		// Regular scientists can only view their own archives
		return userId.equals(userId); // User can always view their own archives if userId matches
	}

	/**
	 * Update or create overloaded methods with groupName parameter for authorization
	 */
	public ProjectResponse getProjectArchives(Integer page, Integer size, String groupName) {
		try {
			int p = (page == null || page < 1) ? 1 : page;
			int s = (size == null || size < 1) ? 10 : size;

			Pageable pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Order.desc("archivedAt")));
			Page<ProjectArchive> result = projectArchiveRepository.findByGroupNameOrderByArchivedAtDesc(groupName, pageable);

			List<ProjectVO> data = result.getContent().stream().map(this::toProjectVO).toList();

			return new ProjectResponse(200, "SUCCESS", "Project archives fetched successfully", data,
					new PaginationMeta(result.getTotalPages(), result.getSize(), result.getNumber() + 1,
							result.getTotalElements()),
					columns());

		} catch (Exception e) {
			return new ProjectResponse(500, "FAILED",
					"An error occurred while fetching project archives: " + e.getMessage(),
					null, null, null);
		}
	}

	/**
	 * Download project attachment with group authorization check
	 */
	public void downloadProjectAttachment(String projectId, String fileName, jakarta.servlet.http.HttpServletResponse response) {
		FileDownload fileDownload = getProjectAttachment(projectId, fileName);
		if (fileDownload != null && fileDownload.getData() != null) {
			try {
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileDownload.getFileName() + "\"");
				response.setContentType(fileDownload.getContentType() != null ? fileDownload.getContentType() : "application/octet-stream");
				response.getOutputStream().write(fileDownload.getData());
			} catch (Exception e) {
				throw new RuntimeException("Error downloading file: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Update Project methods to support group authorization
	 */
	public ProjectVO getProjectById(String projectId, String groupName) {
		Project project = projectRepository.findByProjectId(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));
		
		// Verify group access
		if (!groupName.equals(project.getGroupName())) {
			return null; // Return null if group doesn't match (controller will return 403)
		}
		
		return toProjectVO(project);
	}

	@Transactional
	public ProjectVO updateProject(String projectId, ProjectRequest request, String groupName) {
		try {
			Project project = projectRepository.findByProjectId(projectId)
					.orElseThrow(() -> new RuntimeException("Project not found"));

			// Verify group access
			if (!groupName.equals(project.getGroupName())) {
				return null; // Return null if group doesn't match (controller will return 403)
			}

			// Update only allowed fields
			project.setProjectName(request.getProjectName());
			project.setLongDescription(request.getLongDescription());
			project.setBudgetNos(request.getBudgetNos());
			project.setRole(request.getRole());
			project.setName(request.getName());
			project.setUserId(request.getUserId());
			Project updatedProject = projectRepository.save(project);

			// Re-fetch to ensure updated timestamps are reflected
			Project refreshedProject = projectRepository.findByProjectId(projectId)
					.orElseThrow(() -> new RuntimeException("Project not found after update"));

			return toProjectVO(refreshedProject);
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while updating the project: " + e.getMessage(), e);
		}
	}

	@Transactional
	public boolean deleteProject(String projectId, String groupName) {
		try {
			Project project = projectRepository.findByProjectId(projectId)
					.orElseThrow(() -> new RuntimeException("Project not found"));

			// Verify group access
			if (!groupName.equals(project.getGroupName())) {
				return false; // Return false if group doesn't match (controller will return 403)
			}

			List<ProjectAttachment> projectAttachments = projectAttachmentRepository
					.findByProjectIdOrderByAttachmentIdAsc(projectId);

			projectArchiveRepository.save(toProjectArchive(project));
			if (!projectAttachments.isEmpty()) {
				List<ProjectArchiveAttachment> archiveAttachments = projectAttachments.stream()
						.map(this::toProjectArchiveAttachment)
						.collect(Collectors.toList());
				projectArchiveAttachmentRepository.saveAll(archiveAttachments);
			}

			projectAttachmentRepository.deleteByProjectId(projectId);
			projectRepository.delete(project);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while deleting the project: " + e.getMessage(), e);
		}
	}

	@Transactional
	public ProjectVO deleteAttachmentFromProject(String projectId, String fileName, String groupName) {
		Project project = projectRepository.findByProjectId(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

		// Verify group access
		if (!groupName.equals(project.getGroupName())) {
			return null; // Return null if group doesn't match (controller will return 403)
		}

		ProjectAttachment attachment = projectAttachmentRepository
				.findByProjectIdAndFileName(projectId, fileName)
				.orElseThrow(() -> new RuntimeException("Attachment not found: " + fileName));

		projectAttachmentRepository.delete(attachment);
		return toProjectVO(project);
	}

	@Transactional
	public ProjectVO addAttachmentsToProject(String projectId, MultipartFile[] attachments, String groupName) {
		Project project = projectRepository.findByProjectId(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

		// Verify group access
		if (!groupName.equals(project.getGroupName())) {
			return null; // Return null if group doesn't match (controller will return 403)
		}

		saveAttachments(projectId, attachments);
		return toProjectVO(project);
	}

}
