package com.eforsch.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eforsch.ApiResponse;
import com.eforsch.dto.ArchivedNoteDTO;
import com.eforsch.dto.AttachmentResponseDTO;
import com.eforsch.dto.CreateNoteRequest;
import com.eforsch.dto.FileDownload;
import com.eforsch.dto.InvalidMultipartJsonException;
import com.eforsch.dto.NoteAttachmentUploadRequest;
import com.eforsch.dto.NotePermissionDTO;
import com.eforsch.dto.NoteResponseDTO;
import com.eforsch.dto.NoteVersionDetailDTO;
import com.eforsch.dto.NoteVersionSummaryDTO;
import com.eforsch.dto.PaginationMeta;
import com.eforsch.entity.NoteBook;
import com.eforsch.service.NoteBookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/api/notebooks")
public class NoteBookController {

    private final NoteBookService notebookService;
    private final ObjectMapper objectMapper;

    public NoteBookController(NoteBookService notebookService, ObjectMapper objectMapper) {
        this.notebookService = notebookService;
        this.objectMapper = objectMapper;
    }
 
    @PostMapping
	public ResponseEntity<ApiResponse> createNote(@Valid @RequestBody CreateNoteRequest request) {
		ApiResponse response = new ApiResponse();
		try {
			if (request.getUserId() == null || request.getUserId().isBlank() || 
			    request.getGroupName() == null || request.getGroupName().isBlank()) {
				response.setCode(400);
				response.setStatus("FAILED");
				response.setMessage("User ID, group name, and other required fields are required");
				response.setData(null);
				return ResponseEntity.badRequest().body(response);
			}
			NoteResponseDTO noteDTO = notebookService.createNote(request);
			response.setCode(201);
			response.setStatus("SUCCESS");
			response.setMessage("Note created successfully");
			response.setData(noteDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (ValidationException ex) {
			response.setCode(400);
			response.setStatus("FAILED");
			response.setMessage("Validation failed");
			response.setData(null);
			return ResponseEntity.badRequest().body(response);
		} catch (Exception ex) {
			response.setCode(500);
			response.setStatus("FAILED");
			response.setMessage("Internal server error");
			response.setData(null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
           
    

    @GetMapping
    public ResponseEntity<?> getNotes(@RequestParam(required = false) String projectId,
            @RequestParam(required = false) String budgetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        try {
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "User ID and group name are required");
                return ResponseEntity.badRequest().body(error);
            }
            // For SCIENTIST: filter by userId; for GROUP_LEADER: filter by groupName
            String filterUserId = (role != null && role.equalsIgnoreCase("GROUP_LEADER")) ? null : userId;
            Page<NoteBook> notePage = notebookService.getNotes(projectId, budgetId, fromDate, toDate, page, size, filterUserId, groupName);

            List<Map<String, Object>> notes = notePage.getContent().stream().map(note -> {
                Map<String, Object> map = new HashMap<>();
                map.put("noteId", note.getNoteId());
                map.put("projectId", note.getProjectId());
                map.put("experimentTitle", note.getExperimentTitle());
                map.put("budgetIds", note.getBudgetIds());
                map.put("noteDate", note.getNoteDate());
                map.put("lastModifiedAt", note.getAutosavedAt());
                map.put("groupName", note.getGroupName());
                map.put("createdBy", note.getCreatedBy());
                return map;
            }).toList();

            notes.forEach(m -> m.put("attachment", notebookService.getAttachmentNames((String) m.get("noteId"))));

            Map<String, Object> pagination = new HashMap<>();
            pagination.put("totalPages", notePage.getTotalPages());
            pagination.put("pageSize", size);
            pagination.put("currentPage", page);
            pagination.put("totalRecords", notePage.getTotalElements());

            List<Map<String, Object>> columns = List.of(
                    Map.of("key", "experimentTitle", "label", "Experiment Title", "sortable", true, "filterable", true),
                    Map.of("key", "noteDate", "label", "Note Date", "sortable", true, "filterable", true),
                    Map.of("key", "budgetIds", "label", "Budget IDs", "sortable", false, "filterable", true),
                    Map.of("key", "projectId", "label", "Project ID", "sortable", true, "filterable", true),
                    Map.of("key", "attachment", "label", "Attachment", "sortable", false, "filterable", false));

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("status", "SUCCESS");
            response.put("message", "Notes fetched successfully");
            response.put("data", notes);
            response.put("pagination", pagination);
            response.put("columns", columns);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("status", "FAILED");
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse> getNoteById(@PathVariable String noteId, 
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        ApiResponse response = new ApiResponse();
        try {
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                response.setCode(400);
                response.setStatus("FAILED");
                response.setMessage("User ID and group name are required");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
            NoteResponseDTO noteDTO = notebookService.getNoteById(noteId);
            // Verify user has access to this note
            if (!notebookService.canAccessNote(noteId, userId, groupName, role)) {
                response.setCode(403);
                response.setStatus("FAILED");
                response.setMessage("You do not have permission to access this note");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            response.setCode(200);
            response.setStatus("SUCCESS");
            response.setMessage("Note fetched successfully");
            response.setData(noteDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            response.setCode(404);
            response.setStatus("FAILED");
            response.setMessage(ex.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception ex) {
            response.setCode(500);
            response.setStatus("FAILED");
            response.setMessage("Internal server error");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{noteId}")
	public ResponseEntity<ApiResponse> updateNote(@PathVariable String noteId, @Valid @RequestBody CreateNoteRequest request) {
		ApiResponse response = new ApiResponse();
		try {
			if (request.getUserId() == null || request.getUserId().isBlank() || 
			    request.getGroupName() == null || request.getGroupName().isBlank()) {
				response.setCode(400);
				response.setStatus("FAILED");
				response.setMessage("User ID, group name, and other required fields are required");
				response.setData(null);
				return ResponseEntity.badRequest().body(response);
			}
			if (!notebookService.canAccessNote(noteId, request.getUserId(), request.getGroupName(), request.getRole())) {
				response.setCode(403);
				response.setStatus("FAILED");
				response.setMessage("You do not have permission to update this note");
				response.setData(null);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
			}
			NoteResponseDTO noteDTO = notebookService.updateNote(noteId, request);
			response.setCode(200);
			response.setStatus("SUCCESS");
			response.setMessage("Note updated successfully");
			response.setData(noteDTO);
			return ResponseEntity.ok(response);
		} catch (ValidationException ex) {
			response.setCode(400);
			response.setStatus("FAILED");
			response.setMessage("Validation failed");
			response.setData(null);
			return ResponseEntity.badRequest().body(response);
		} catch (RuntimeException ex) {
			response.setCode(404);
			response.setStatus("FAILED");
			response.setMessage(ex.getMessage());
			response.setData(null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} catch (Exception ex) {
			response.setCode(500);
			response.setStatus("FAILED");
			response.setMessage("Internal server error");
			response.setData(null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
         

    @PostMapping(value = "/createNoteWithAttachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createNoteWithAttachments(
            @RequestPart("note") String noteJson,
            @RequestPart("userDetails") com.eforsch.dto.User userDetails,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {
        ApiResponse response = new ApiResponse();
        try {
            if (userDetails == null || userDetails.getGroupName() == null) {
                response.setCode(400);
                response.setStatus("FAILED");
                response.setMessage("User details and groupName are required");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
            CreateNoteRequest request = readMultipartJson(noteJson, CreateNoteRequest.class, "note");
            request.setGroupName(userDetails.getGroupName());
            request.setUserId(String.valueOf(userDetails.getUserId()));
            NoteResponseDTO noteDTO = notebookService.createNote(request, attachments);
            response.setCode(201);
            response.setStatus("SUCCESS");
            response.setMessage("Note created successfully");
            response.setData(noteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidMultipartJsonException ex) {
            response.setCode(400);
            response.setStatus("FAILED");
            response.setMessage(ex.getMessage());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            response.setCode(500);
            response.setStatus("FAILED");
            response.setMessage("Internal server error: " + ex.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/{noteId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadAttachments(@PathVariable String noteId,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("userDetails") com.eforsch.dto.User userDetails,
            @RequestPart(value = "metadata", required = false) String metadataJson) {
        ApiResponse response = new ApiResponse();
        try {
            if (noteId == null || noteId.isBlank()) {
                response.setCode(400);
                response.setStatus("FAILED");
                response.setMessage("noteId is required");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
            NoteAttachmentUploadRequest metadata = readOptionalMultipartJson(metadataJson,
                    NoteAttachmentUploadRequest.class, "metadata");
            List<AttachmentResponseDTO> result = notebookService.addAttachmentsToNote(noteId, files, metadata);
            response.setCode(200);
            response.setStatus("SUCCESS");
            response.setMessage("Attachments uploaded successfully");
            response.setData(result);
            return ResponseEntity.ok(response);
        } catch (InvalidMultipartJsonException ex) {
            response.setCode(400);
            response.setStatus("FAILED");
            response.setMessage(ex.getMessage());
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException ex) {
            response.setCode(ex.getMessage() != null && ex.getMessage().contains("not found") ? 404 : 400);
            response.setStatus("FAILED");
            response.setMessage(ex.getMessage());
            response.setData(null);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception ex) {
            response.setCode(500);
            response.setStatus("FAILED");
            response.setMessage("Internal server error");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{noteId}/attachments/download")
    public ResponseEntity<?> downloadNoteAttachment(@PathVariable String noteId,
            @RequestParam String fileName,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        try {
            if (noteId == null || noteId.isBlank() || fileName == null || fileName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "noteId and fileName are required");
                return ResponseEntity.badRequest().body(error);
            }
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "User ID and group name are required");
                return ResponseEntity.badRequest().body(error);
            }
            if (!notebookService.canAccessNote(noteId, userId, groupName, role)) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 403);
                error.put("status", "FAILED");
                error.put("message", "You do not have permission to download attachments from this note");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            FileDownload fd = notebookService.getNoteAttachment(noteId, fileName);
            if (fd == null || fd.getData() == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 404);
                error.put("status", "FAILED");
                error.put("message", "Attachment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            ContentDisposition cd = ContentDisposition.attachment()
                    .filename(fd.getFileName() == null ? "attachment" : fd.getFileName()).build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                    .contentType(MediaType.parseMediaType(
                            fd.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fd.getContentType()))
                    .body(fd.getData());
        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("status", "FAILED");
            error.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("status", "FAILED");
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{noteId}/attachments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> deleteAttachment(@PathVariable String noteId, @RequestParam String fileName,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        ApiResponse response = new ApiResponse();
        try {
            if (noteId == null || noteId.isBlank() || fileName == null || fileName.isBlank()) {
                response.setCode(400);
                response.setStatus("FAILED");
                response.setMessage("noteId and fileName are required");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                response.setCode(400);
                response.setStatus("FAILED");
                response.setMessage("User ID and group name are required");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
            if (!notebookService.canAccessNote(noteId, userId, groupName, role)) {
                response.setCode(403);
                response.setStatus("FAILED");
                response.setMessage("You do not have permission to delete attachments from this note");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            NoteResponseDTO updated = notebookService.deleteAttachmentFromNote(noteId, fileName);
            response.setCode(200);
            response.setStatus("SUCCESS");
            response.setMessage("Attachment deleted successfully");
            response.setData(updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            response.setCode(ex.getMessage() != null && ex.getMessage().contains("not found") ? 404 : 400);
            response.setStatus("FAILED");
            response.setMessage(ex.getMessage());
            response.setData(null);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception ex) {
            response.setCode(500);
            response.setStatus("FAILED");
            response.setMessage("Internal server error");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> deleteNote(@PathVariable String noteId,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        ApiResponse response = new ApiResponse();
        try {
            if (noteId == null || noteId.isBlank()) {
                response.setCode(400);
                response.setStatus("FAILED");
                response.setMessage("noteId is required");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                response.setCode(400);
                response.setStatus("FAILED");
                response.setMessage("User ID and group name are required");
                response.setData(null);
                return ResponseEntity.badRequest().body(response);
            }
            if (!notebookService.canAccessNote(noteId, userId, groupName, role)) {
                response.setCode(403);
                response.setStatus("FAILED");
                response.setMessage("You do not have permission to delete this note");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            notebookService.deleteNote(noteId);
            response.setCode(200);
            response.setStatus("SUCCESS");
            response.setMessage("Note deleted successfully along with all associated attachments");
            response.setData(null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            response.setCode(ex.getMessage() != null && ex.getMessage().contains("not found") ? 404 : 400);
            response.setStatus("FAILED");
            response.setMessage(ex.getMessage());
            response.setData(null);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception ex) {
            response.setCode(500);
            response.setStatus("FAILED");
            response.setMessage("Internal server error");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{noteId}/versions")
    public ResponseEntity<Map<String, Object>> listVersions(@PathVariable String noteId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        try {
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "User ID and group name are required");
                return ResponseEntity.badRequest().body(error);
            }
            if (!notebookService.canAccessNote(noteId, userId, groupName, role)) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 403);
                error.put("status", "FAILED");
                error.put("message", "You do not have permission to view this note's versions");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            Page<NoteVersionSummaryDTO> versionPage = notebookService.listVersions(noteId, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("status", "SUCCESS");
            response.put("message", "Note versions fetched successfully");
            response.put("data", versionPage.getContent());
            response.put("pagination", new PaginationMeta(
                    versionPage.getTotalPages(), size, page, versionPage.getTotalElements()));
            response.put("columns", List.of(
                    Map.of("key", "version", "label", "Version", "sortable", true, "filterable", false),
                    Map.of("key", "editedBy", "label", "Edited By", "sortable", true, "filterable", true),
                    Map.of("key", "editedAt", "label", "Edited At", "sortable", true, "filterable", true)));
            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("status", "FAILED");
            error.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("status", "FAILED");
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{noteId}/versions/{version}")
    public ResponseEntity<Map<String, Object>> getVersion(@PathVariable String noteId,
            @PathVariable int version,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        try {
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "User ID and group name are required");
                return ResponseEntity.badRequest().body(error);
            }
            if (!notebookService.canAccessNote(noteId, userId, groupName, role)) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 403);
                error.put("status", "FAILED");
                error.put("message", "You do not have permission to view this note version");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            NoteVersionDetailDTO detail = notebookService.getVersion(noteId, version);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("status", "SUCCESS");
            response.put("message", "Note version fetched successfully");
            response.put("data", List.of(detail));
            response.put("pagination", new PaginationMeta(1, 1, 1, 1));
            response.put("columns", List.of(
                    Map.of("key", "version", "label", "Version", "sortable", false, "filterable", false),
                    Map.of("key", "editedBy", "label", "Edited By", "sortable", false, "filterable", false),
                    Map.of("key", "editedAt", "label", "Edited At", "sortable", false, "filterable", false)));
            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("status", "FAILED");
            error.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("status", "FAILED");
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/archives")
    public ResponseEntity<Map<String, Object>> listArchivedNotes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        try {
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "User ID and group name are required");
                return ResponseEntity.badRequest().body(error);
            }
            // For SCIENTIST: filter by userId; for GROUP_LEADER: filter by groupName
            String filterUserId = (role != null && role.equalsIgnoreCase("GROUP_LEADER")) ? null : userId;
            Page<ArchivedNoteDTO> archivePage = notebookService.listArchivedNotes(page, size, filterUserId, groupName);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("status", "SUCCESS");
            response.put("message", "Archived notes fetched successfully");
            response.put("data", archivePage.getContent());
            response.put("pagination", new PaginationMeta(
                    archivePage.getTotalPages(), size, page, archivePage.getTotalElements()));
            response.put("columns", List.of(
                    Map.of("key", "experimentTitle", "label", "Experiment Title", "sortable", true, "filterable", true),
                    Map.of("key", "projectId", "label", "Project ID", "sortable", true, "filterable", true),
                    Map.of("key", "createdBy", "label", "Created By", "sortable", true, "filterable", true),
                    Map.of("key", "attachment", "label", "Attachment", "sortable", false, "filterable", false),
                    Map.of("key", "archivedAt", "label", "Archived At", "sortable", true, "filterable", true)));
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("status", "FAILED");
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/archives/user/{userId}")
    public ResponseEntity<Map<String, Object>> listArchivedNotesByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(required = false) String requestUserId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        try {
            if (requestUserId == null || requestUserId.isBlank() || groupName == null || groupName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "User ID and group name are required");
                return ResponseEntity.badRequest().body(error);
            }
            // GROUP_LEADER can view any user's archives, others can only view their own
            if (!role.equalsIgnoreCase("GROUP_LEADER") && !requestUserId.equals(userId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 403);
                error.put("status", "FAILED");
                error.put("message", "You do not have permission to view this user's archives");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            Page<ArchivedNoteDTO> archivePage = notebookService.listArchivedNotesByUserId(userId, page, size, groupName);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("status", "SUCCESS");
            response.put("message", "Archived notes fetched successfully");
            response.put("data", archivePage.getContent());
            response.put("pagination", new PaginationMeta(
                    archivePage.getTotalPages(), size, page, archivePage.getTotalElements()));
            response.put("columns", List.of(
                    Map.of("key", "experimentTitle", "label", "Experiment Title", "sortable", true, "filterable", true),
                    Map.of("key", "projectId", "label", "Project ID", "sortable", true, "filterable", true),
                    Map.of("key", "noteDate", "label", "Note Date", "sortable", true, "filterable", true),
                    Map.of("key", "attachment", "label", "Attachment", "sortable", false, "filterable", false),
                    Map.of("key", "archivedAt", "label", "Archived At", "sortable", true, "filterable", true)));
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("status", "FAILED");
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{noteId}/permissions")
    public ResponseEntity<Map<String, Object>> listSharedUsers(@PathVariable String noteId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(required = false) String userId,
            @RequestHeader(required = false) String groupName,
            @RequestHeader(required = false) String role) {
        try {
            if (userId == null || userId.isBlank() || groupName == null || groupName.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("status", "FAILED");
                error.put("message", "User ID and group name are required");
                return ResponseEntity.badRequest().body(error);
            }
            if (!notebookService.canAccessNote(noteId, userId, groupName, role)) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 403);
                error.put("status", "FAILED");
                error.put("message", "You do not have permission to view permissions for this note");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            Page<NotePermissionDTO> permissionPage = notebookService.listPermissions(noteId, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("status", "SUCCESS");
            response.put("message", "Shared users fetched successfully");
            response.put("data", permissionPage.getContent());
            response.put("pagination", new PaginationMeta(
                    permissionPage.getTotalPages(), size, page, permissionPage.getTotalElements()));
            response.put("columns", List.of(
                    Map.of("key", "userId", "label", "User ID", "sortable", true, "filterable", true),
                    Map.of("key", "accessLevel", "label", "Access Level", "sortable", true, "filterable", true),
                    Map.of("key", "grantedAt", "label", "Granted At", "sortable", true, "filterable", true)));
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("status", "FAILED");
            error.put("message", "Invalid request");
            error.put("errors", List.of(Map.of("message", ex.getMessage())));
            return ResponseEntity.badRequest().body(error);
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("status", "FAILED");
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private <T> T readMultipartJson(String json, Class<T> targetType, String partName) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (Exception e) {
            throw new InvalidMultipartJsonException("Invalid JSON in multipart part '" + partName + "'", e);
        }
    }

    private <T> T readOptionalMultipartJson(String json, Class<T> targetType, String partName) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return readMultipartJson(json, targetType, partName);
    }
}
