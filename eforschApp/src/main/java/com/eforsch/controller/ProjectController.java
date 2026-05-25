package com.eforsch.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eforsch.ProjectResponse;
import com.eforsch.dto.FileDownload;
import com.eforsch.dto.ProjectCreateRequest;
import com.eforsch.dto.ProjectRequest;
import com.eforsch.dto.ProjectVO;
import com.eforsch.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects")
public class ProjectController {

	private final ProjectService projectService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping
	public ResponseEntity<ProjectResponse> getProjects(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size, @RequestParam(required = false) String search,
			@RequestParam(required = true) String groupName) {
		if (groupName == null || groupName.isBlank()) {
			return ResponseEntity.badRequest().body(
				new ProjectResponse(400, "FAILED", "groupName is required", null, null, null));
		}
		ProjectResponse response = projectService.getProjects(page, size, search, null, groupName);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{projectId}")
	public ResponseEntity<ProjectResponse> getProjectById(@PathVariable String projectId, @RequestParam(required = true) String groupName) {
		try {
			if (projectId == null || projectId.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "projectId is required", null, null, null));
			}
			if (groupName == null || groupName.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "groupName is required", null, null, null));
			}
			ProjectVO projectVO = projectService.getProjectById(projectId, groupName);
			if (projectVO == null) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Project does not belong to your group", null, null, null));
			}
			return ResponseEntity.ok(
				new ProjectResponse(200, "SUCCESS", "Project fetched successfully", Collections.singletonList(projectVO), null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error", null, null, null));
		}
	}

	@PostMapping(value = "/addProjects")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest requestBody) {
		try {
			ProjectRequest request = requestBody.getRequest();
			com.eforsch.dto.User userDetails = requestBody.getUserDetails();
			
			if (request == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "Project request is required", null, null, null));
			}
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			request.setGroupName(userDetails.getGroupName());
			ProjectVO created = projectService.createProject(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(
				new ProjectResponse(201, "SUCCESS", "Project created successfully", Collections.singletonList(created),
						null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error: " + ex.getMessage(), null, null, null));
		}
	}

	@PostMapping(value = "/addProjectsWithAttachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ProjectResponse> createProjectWithAttachments(
			@RequestPart("project") String projectJson,
			@RequestPart("userDetails") String userDetailsJson,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {
		try {
			// Parse JSON strings to objects
			ProjectRequest request = objectMapper.readValue(projectJson, ProjectRequest.class);
			com.eforsch.dto.User userDetails = objectMapper.readValue(userDetailsJson, com.eforsch.dto.User.class);
			
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			
			request.setGroupName(userDetails.getGroupName());
			
			ProjectVO created = projectService.createProject(request, attachments);
			return ResponseEntity.status(HttpStatus.CREATED).body(
				new ProjectResponse(201, "SUCCESS", "Project created successfully", Collections.singletonList(created),
						null, null));
		} catch (com.fasterxml.jackson.core.JsonParseException ex) {
			return ResponseEntity.badRequest().body(
				new ProjectResponse(400, "FAILED", "Invalid JSON format: " + ex.getMessage(), null, null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error: " + ex.getMessage(), null, null, null));
		}
	}

	@PutMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ProjectResponse> updateProject(@PathVariable String projectId, @Valid @RequestPart("project") ProjectRequest request, @RequestPart("userDetails") com.eforsch.dto.User userDetails) {
		try {
			if (projectId == null || projectId.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "projectId is required", null, null, null));
			}
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			ProjectVO updatedProject = projectService.updateProject(projectId, request, userDetails.getGroupName());
			if (updatedProject == null) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Access denied. Project does not belong to your group", null, null, null));
			}
			return ResponseEntity.ok(
				new ProjectResponse(200, "SUCCESS", "Project updated successfully", List.of(updatedProject), null, null));
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ProjectResponse(404, "FAILED", ex.getMessage(), null, null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error", null, null, null));
		}
	}

	@PutMapping(value = "/{projectId}/updateprojectattachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ProjectResponse> updateProjectWithAttachments(@PathVariable String projectId, 
			@RequestPart("project") String projectJson, 
			@RequestPart("userDetails") String userDetailsJson,
			@RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {
		try {
			if (projectId == null || projectId.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "projectId is required", null, null, null));
			}
			
			// Parse JSON strings to objects
			ProjectRequest request = objectMapper.readValue(projectJson, ProjectRequest.class);
			com.eforsch.dto.User userDetails = objectMapper.readValue(userDetailsJson, com.eforsch.dto.User.class);
			
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			ProjectVO updatedProject = projectService.updateProject(projectId, request, userDetails.getGroupName());
			if (updatedProject == null) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Project does not belong to your group", null, null, null));
			}
			// Add attachments if provided
			if (attachments != null && attachments.length > 0) {
				ProjectVO projectWithAttachments = projectService.addAttachmentsToProject(projectId, attachments, userDetails.getGroupName());
				if (projectWithAttachments == null) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
						new ProjectResponse(403, "FAILED", "Project does not belong to your group", null, null, null));
				}
				return ResponseEntity.ok(
					new ProjectResponse(200, "SUCCESS", "Project and attachments updated successfully", Collections.singletonList(projectWithAttachments), null, null));
			}
			return ResponseEntity.ok(
				new ProjectResponse(200, "SUCCESS", "Project updated successfully", Collections.singletonList(updatedProject), null, null));
		} catch (com.fasterxml.jackson.core.JsonParseException ex) {
			return ResponseEntity.badRequest().body(
				new ProjectResponse(400, "FAILED", "Invalid JSON format: " + ex.getMessage(), null, null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error: " + ex.getMessage(), null, null, null));
		}
	}

	@DeleteMapping(value = "/{projectId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ProjectResponse> deleteProject(@PathVariable String projectId, 
			@RequestBody com.eforsch.dto.User userDetails) {
		try {
			if (projectId == null || projectId.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "projectId is required", null, null, null));
			}
			if (userDetails == null || userDetails.getGroupName() == null || userDetails.getRole() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details, groupName and role are required", null, null, null));
			}
			if (!userDetails.getRole().equalsIgnoreCase("groupleader")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Only GROUP_LEADER can delete projects", null, null, null));
			}
			boolean deleted = projectService.deleteProject(projectId, userDetails.getGroupName());
			if (!deleted) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Project does not belong to your group", null, null, null));
			}
			return ResponseEntity.ok(
				new ProjectResponse(200, "SUCCESS", "Project archived successfully", null, null, null));
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ProjectResponse(404, "FAILED", ex.getMessage(), null, null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error", null, null, null));
		}
	}

	@GetMapping("/archives")
	public ResponseEntity<ProjectResponse> getProjectArchives(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@ModelAttribute com.eforsch.dto.User userDetails) {
		if (userDetails == null || userDetails.getGroupName() == null) {
			return ResponseEntity.badRequest().body(
				new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
		}
		ProjectResponse response = projectService.getProjectArchives(page, size, userDetails.getGroupName());
		return ResponseEntity.status(response.getCode()).body(response);
	}

	@GetMapping("/archives/user/{userId}")
	public ResponseEntity<ProjectResponse> getProjectArchivesByUser(@PathVariable String userId,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@ModelAttribute com.eforsch.dto.User userDetails) {
		try {
			if (userId == null || userId.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "userId is required", null, null, null));
			}
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			if (userDetails.getRole() == null || userDetails.getRole().isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User role is required", null, null, null));
			}
			boolean hasAccess = projectService.canAccessUserArchives(userId, userDetails.getGroupName(), userDetails.getRole());
			if (!hasAccess) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "You do not have permission to view this user's archives", null, null, null));
			}
			ProjectResponse response = projectService.getProjectArchivesByUser(userId, page, size, userDetails.getGroupName());
			return ResponseEntity.status(response.getCode()).body(response);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error", null, null, null));
		}
	}

	@PostMapping(value = "/{projectId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ProjectResponse> addAttachmentsToProject(@PathVariable String projectId,
			@RequestPart("attachments") MultipartFile[] attachments, @RequestPart("userDetails") String userDetailsJson) {
		try {
			if (projectId == null || projectId.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "projectId is required", null, null, null));
			}
			
			// Parse userDetails JSON string to object
			com.eforsch.dto.User userDetails = objectMapper.readValue(userDetailsJson, com.eforsch.dto.User.class);
			
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			ProjectVO updated = projectService.addAttachmentsToProject(projectId, attachments, userDetails.getGroupName());
			if (updated == null) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Project does not belong to your group", null, null, null));
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(
				new ProjectResponse(201, "SUCCESS", "Attachments added successfully", Collections.singletonList(updated),
						null, null));
		} catch (com.fasterxml.jackson.core.JsonParseException ex) {
			return ResponseEntity.badRequest().body(
				new ProjectResponse(400, "FAILED", "Invalid JSON format: " + ex.getMessage(), null, null, null));
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ProjectResponse(404, "FAILED", ex.getMessage(), null, null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error", null, null, null));
		}
	}

	@DeleteMapping("/{projectId}/attachments")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ProjectResponse> deleteAttachment(@PathVariable String projectId, @RequestParam String fileName, @RequestBody com.eforsch.dto.User userDetails) {
		try {
			if (projectId == null || projectId.isBlank() || fileName == null || fileName.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "projectId and fileName are required", null, null, null));
			}
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			ProjectVO updated = projectService.deleteAttachmentFromProject(projectId, fileName, userDetails.getGroupName());
			if (updated == null) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Project does not belong to your group", null, null, null));
			}
			return ResponseEntity.ok(
				new ProjectResponse(200, "SUCCESS", "Attachment deleted successfully", Collections.singletonList(updated),
						null, null));
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ProjectResponse(404, "FAILED", ex.getMessage(), null, null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error", null, null, null));
		}
	}

	@GetMapping("/{projectId}/attachments/download")
	public ResponseEntity<?> downloadProjectAttachment(@PathVariable String projectId,
			@RequestParam String fileName, @ModelAttribute com.eforsch.dto.User userDetails) {
		try {
			if (projectId == null || projectId.isBlank() || fileName == null || fileName.isBlank()) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "projectId and fileName are required", null, null, null));
			}
			if (userDetails == null || userDetails.getGroupName() == null) {
				return ResponseEntity.badRequest().body(
					new ProjectResponse(400, "FAILED", "User details and groupName are required", null, null, null));
			}
			boolean hasAccess = projectService.canAccessProject(projectId, userDetails.getGroupName());
			if (!hasAccess) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					new ProjectResponse(403, "FAILED", "Project does not belong to your group", null, null, null));
			}
			FileDownload fileDownload = projectService.getProjectAttachment(projectId, fileName);
			if (fileDownload == null || fileDownload.getData() == null) {
				return ResponseEntity.notFound().build();
			}
			ContentDisposition contentDisposition = ContentDisposition.attachment()
					.filename(fileDownload.getFileName() == null ? "attachment" : fileDownload.getFileName())
					.build();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
					.contentType(MediaType.parseMediaType(fileDownload.getContentType() == null
							? MediaType.APPLICATION_OCTET_STREAM_VALUE
							: fileDownload.getContentType()))
					.body(fileDownload.getData());
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ProjectResponse(404, "FAILED", ex.getMessage(), null, null, null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ProjectResponse(500, "FAILED", "Internal server error", null, null, null));
		}
	}

}
