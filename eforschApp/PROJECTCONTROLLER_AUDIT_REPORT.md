# ProjectController Audit Report

**Date:** 2026-03-28  
**Audit Scope:** ProjectController.java - All HTTP endpoints validation  
**Comparison Base:** NoteBookController.java

---

## Executive Summary

**Critical Issues Found: 11**  
**Major Issues Found: 8**  
**Minor Issues Found: 6**

ProjectController has **significant architectural inconsistencies** compared to NoteBookController:
- ❌ No exception handling (no try-catch blocks)
- ❌ Missing @Valid annotations on request bodies
- ❌ Inconsistent return types and status codes
- ❌ No path variable or parameter validation
- ❌ Status code mismatch between HTTP header and response body

---

## Detailed Endpoint Analysis

### 1. GET /projects
**Line:** 38–44  
**URL Path:** `/projects`  
**HTTP Method:** GET  
**Return Type:** `ResponseEntity<ProjectResponse>`  
**Status Code:** `200 (OK)`

**Parameters:**
- `@RequestParam(required = false) Integer page`
- `@RequestParam(required = false) Integer size`
- `@RequestParam(required = false) String search`
- `@RequestParam(required = false) String budgetNo`
- `@RequestParam(required = false) String groupName`

**Issues:**
- ⚠️ **No exception handling** - Any service exception will result in HTTP 500 with raw stack trace
- ⚠️ **No null checks** - Parameters could be invalid (negative page, invalid size)
- ⚠️ **Inconsistent with NoteBookController** - NBC has full try-catch with detailed error responses

**Expected Behavior (per NoteBookController pattern):**
```java
@GetMapping("/projects")
public ResponseEntity<?> getProjects(...) {
    try {
        ProjectResponse response = projectService.getProjects(...);
        return ResponseEntity.ok(response);
    } catch (Exception ex) {
        // Return structured error response
    }
}
```

---

### 2. GET /{projectId}
**Line:** 46–49  
**URL Path:** `/projects/{projectId}`  
**HTTP Method:** GET  
**Return Type:** `ResponseEntity<ProjectResponse>`  
**Status Code:** Dynamic (`response.getCode()`)

**Path Variables:**
- `@PathVariable String projectId` ⚠️ **Not validated**

**Issues:**
- 🔴 **CRITICAL: No validation of projectId** - Could be null/blank
- ⚠️ **No exception handling** - No try-catch for RuntimeException (not found)
- ⚠️ **Dynamic status code** - Relies on service response object rather than HTTP status
- ⚠️ **Inconsistent with NoteBookController** - NBC catches RuntimeException and returns 404

**Expected Behavior:**
```java
@GetMapping("/{projectId}")
public ResponseEntity<ApiResponse> getProjectById(@PathVariable String projectId) {
    ApiResponse response = new ApiResponse();
    try {
        if (projectId == null || projectId.isBlank()) {
            response.setCode(400);
            response.setMessage("projectId is required");
            return ResponseEntity.badRequest().body(response);
        }
        ProjectResponseDTO dto = projectService.getProjectById(projectId);
        response.setCode(200);
        response.setStatus("SUCCESS");
        response.setData(dto);
        return ResponseEntity.ok(response);
    } catch (RuntimeException ex) {
        response.setCode(404);
        response.setStatus("FAILED");
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception ex) {
        response.setCode(500);
        response.setStatus("FAILED");
        response.setMessage("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

### 3. POST /addProjects
**Line:** 51–57  
**URL Path:** `/projects/addProjects`  
**HTTP Method:** POST  
**Return Type:** `ProjectResponse` (Not wrapped in ResponseEntity)  
**Status Code:** `201 (CREATED)` via `@ResponseStatus`  
**Exception Handling:** ❌ None

**Request Body:**
- `@RequestBody ProjectRequest request` ⚠️ **Missing @Valid**

**Issues:**
- 🔴 **CRITICAL: Missing @Valid annotation** - Request validation will NOT be enforced
- 🔴 **CRITICAL: Status code mismatch** - Returns `@ResponseStatus(HttpStatus.CREATED)` BUT response body has hardcoded `code: 200`
- ❌ **No exception handling** - ValidationException/RuntimeException not caught
- ❌ **Inconsistent return type** - Returns `ProjectResponse` directly, not `ResponseEntity`
- ❌ **Inconsistent with NoteBookController** - NBC returns `ResponseEntity<ApiResponse>` with wrapped response

**Code Issue:**
```java
@ResponseStatus(HttpStatus.CREATED) // HTTP 201
public ProjectResponse createProject(@RequestBody ProjectRequest request) {
    // ...
    return new ProjectResponse(200, "SUCCESS", ...); // ← WRONG: says 200 in body
}
```

**Expected Behavior:**
```java
@PostMapping("/addProjects")
public ResponseEntity<ApiResponse> createProject(@Valid @RequestBody ProjectRequest request) {
    ApiResponse response = new ApiResponse();
    try {
        ProjectVO created = projectService.createProject(request);
        response.setCode(201);
        response.setStatus("SUCCESS");
        response.setMessage("Project created successfully");
        response.setData(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (ValidationException ex) {
        response.setCode(400);
        response.setStatus("FAILED");
        response.setMessage("Validation failed");
        return ResponseEntity.badRequest().body(response);
    } catch (Exception ex) {
        response.setCode(500);
        response.setStatus("FAILED");
        response.setMessage("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

### 4. POST /addProjectsWithAttachments
**Line:** 59–66  
**URL Path:** `/projects/addProjectsWithAttachments`  
**HTTP Method:** POST (Multipart)  
**Return Type:** `ProjectResponse` (Not wrapped)  
**Status Code:** `201 (CREATED)` via `@ResponseStatus`  
**Exception Handling:** ❌ None

**Request Body:**
- `@RequestPart("project") ProjectRequest request` ⚠️ **Missing @Valid**
- `@RequestPart(value = "attachments", required = false) MultipartFile[] attachments`

**Issues:**
- 🔴 **CRITICAL: Missing @Valid annotation** - No validation on ProjectRequest
- 🔴 **CRITICAL: Status code mismatch** - Same as endpoint #3 (201 HTTP but 200 in body)
- ❌ **No exception handling** - InvalidMultipartJsonException not caught (NoteBookController has this)
- ❌ **Inconsistent with NoteBookController** - NBC has proper multipart JSON parsing + error handling

**Expected Behavior:** See NoteBookController line 120–145 as reference

---

### 5. PUT /{projectId}
**Line:** 68–75  
**URL Path:** `/projects/{projectId}`  
**HTTP Method:** PUT  
**Return Type:** `ProjectResponse` (Not wrapped)  
**Status Code:** `200 (OK)` via `@ResponseStatus`  
**Exception Handling:** ❌ None

**Path Variables:**
- `@PathVariable String projectId` ⚠️ **Not validated**

**Request Body:**
- `@RequestBody ProjectRequest request` ⚠️ **Missing @Valid**

**Issues:**
- 🔴 **CRITICAL: Missing @Valid annotation** - Request validation not enforced
- 🔴 **CRITICAL: No projectId validation** - Could be null/blank
- ❌ **No exception handling** - RuntimeException (not found) not caught
- ❌ **No null check** - Request body could be null
- ❌ **Inconsistent return type** - Should return `ResponseEntity`

**Expected Behavior:**
```java
@PutMapping("/{projectId}")
public ResponseEntity<ApiResponse> updateProject(
        @PathVariable String projectId, 
        @Valid @RequestBody ProjectRequest request) {
    ApiResponse response = new ApiResponse();
    try {
        if (projectId == null || projectId.isBlank()) {
            response.setCode(400);
            response.setMessage("projectId is required");
            return ResponseEntity.badRequest().body(response);
        }
        ProjectVO updated = projectService.updateProject(projectId, request);
        response.setCode(200);
        response.setStatus("SUCCESS");
        response.setMessage("Project updated successfully");
        response.setData(updated);
        return ResponseEntity.ok(response);
    } catch (ValidationException ex) {
        response.setCode(400);
        response.setStatus("FAILED");
        response.setMessage("Validation failed");
        return ResponseEntity.badRequest().body(response);
    } catch (RuntimeException ex) {
        response.setCode(404);
        response.setStatus("FAILED");
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception ex) {
        response.setCode(500);
        response.setStatus("FAILED");
        response.setMessage("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

### 6. DELETE /{projectId}
**Line:** 77–84  
**URL Path:** `/projects/{projectId}`  
**HTTP Method:** DELETE  
**Return Type:** `ProjectResponse` (Not wrapped)  
**Status Code:** `200 (OK)` via `@ResponseStatus`  
**Exception Handling:** ❌ None

**Path Variables:**
- `@PathVariable String projectId` ⚠️ **Not validated**

**Issues:**
- 🔴 **CRITICAL: No projectId validation** - Could be null/blank
- 🔴 **CRITICAL: No exception handling** - RuntimeException (not found) not caught
- ⚠️ **Questionable status code** - DELETE should return `204 (NO_CONTENT)` or `200` with response
- ⚠️ **Returns null data** - Response has `data: null` even on success
- ❌ **Inconsistent with NoteBookController** - NBC pattern uses proper status codes

**Best Practice Issues:**
- RESTful DELETE typically returns `204 NO_CONTENT` (no body needed)
- Or returns `200 OK` with confirmation message
- Current approach returns `200` with `null` data which is ambiguous

**Expected Behavior:**
```java
@DeleteMapping("/{projectId}")
public ResponseEntity<ApiResponse> deleteProject(@PathVariable String projectId) {
    ApiResponse response = new ApiResponse();
    try {
        if (projectId == null || projectId.isBlank()) {
            response.setCode(400);
            response.setMessage("projectId is required");
            return ResponseEntity.badRequest().body(response);
        }
        projectService.deleteProject(projectId);
        response.setCode(204);
        response.setStatus("SUCCESS");
        response.setMessage("Project deleted successfully");
        return ResponseEntity.noContent().build();
        // OR
        // response.setCode(200);
        // response.setData(null);
        // return ResponseEntity.ok(response);
    } catch (RuntimeException ex) {
        response.setCode(404);
        response.setStatus("FAILED");
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception ex) {
        response.setCode(500);
        response.setStatus("FAILED");
        response.setMessage("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

### 7. GET /archives
**Line:** 86–91  
**URL Path:** `/projects/archives`  
**HTTP Method:** GET  
**Return Type:** `ResponseEntity<ProjectResponse>`  
**Status Code:** Dynamic (`response.getCode()`)  
**Exception Handling:** ❌ None

**Parameters:**
- `@RequestParam(required = false) Integer page`
- `@RequestParam(required = false) Integer size`

**Issues:**
- ⚠️ **No exception handling** - Should catch and return proper error response
- ⚠️ **No parameter validation** - page/size could be invalid (negative, too large)
- ⚠️ **Dynamic status code** - Relies on service response

**Expected Behavior:** Wrap in try-catch like NoteBookController.listArchivedNotes() (line 346)

---

### 8. GET /archives/user/{userId}
**Line:** 93–101  
**URL Path:** `/projects/archives/user/{userId}`  
**HTTP Method:** GET  
**Return Type:** `ResponseEntity<ProjectResponse>`  
**Status Code:** Dynamic (`response.getCode()`)  
**Exception Handling:** ❌ None

**Path Variables:**
- `@PathVariable String userId` ⚠️ **Not validated**

**Parameters:**
- `@RequestParam(required = false) Integer page`
- `@RequestParam(required = false) Integer size`

**Issues:**
- 🔴 **CRITICAL: No userId validation** - Could be null/blank
- ⚠️ **No exception handling** - No try-catch
- ⚠️ **No parameter validation** - page/size not validated
- ⚠️ **Dynamic status code** - Bad practice

---

### 9. POST /{projectId}/attachments
**Line:** 103–111  
**URL Path:** `/projects/{projectId}/attachments`  
**HTTP Method:** POST (Multipart)  
**Return Type:** `ProjectResponse` (Not wrapped)  
**Status Code:** `200 (OK)` via `@ResponseStatus` ⚠️ **Should be 201**  
**Exception Handling:** ❌ None

**Path Variables:**
- `@PathVariable String projectId` ⚠️ **Not validated**

**Request Parts:**
- `@RequestPart("attachments") MultipartFile[] attachments`

**Issues:**
- 🔴 **CRITICAL: No projectId validation** - Could be null/blank
- 🔴 **CRITICAL: Status code is WRONG** - POST creating resource should return `201 CREATED`, not `200`
- ❌ **No exception handling** - No try-catch for file errors, not found, etc.
- ❌ **Inconsistent with NoteBookController** - NBC uses `201` for POST attachments (line 143)
- ❌ **No attachments validation** - Array could be empty or contain invalid files

**Expected Behavior:**
```java
@PostMapping(value = "/{projectId}/attachments", 
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ProjectResponse> addAttachmentsToProject(
        @PathVariable String projectId,
        @RequestPart("attachments") MultipartFile[] attachments) {
    ProjectResponse response = new ProjectResponse();
    try {
        if (projectId == null || projectId.isBlank()) {
            response.setCode(400);
            response.setMessage("projectId is required");
            return ResponseEntity.badRequest().body(response);
        }
        if (attachments == null || attachments.length == 0) {
            response.setCode(400);
            response.setMessage("At least one attachment is required");
            return ResponseEntity.badRequest().body(response);
        }
        ProjectVO updated = projectService.addAttachmentsToProject(projectId, attachments);
        response.setCode(201);
        response.setStatus("SUCCESS");
        response.setMessage("Attachments added successfully");
        response.setData(Collections.singletonList(updated));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (RuntimeException ex) {
        response.setCode(404);
        response.setStatus("FAILED");
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception ex) {
        response.setCode(500);
        response.setStatus("FAILED");
        response.setMessage("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

### 10. DELETE /{projectId}/attachments
**Line:** 113–121  
**URL Path:** `/projects/{projectId}/attachments`  
**HTTP Method:** DELETE  
**Return Type:** `ProjectResponse` (Not wrapped)  
**Status Code:** `200 (OK)` via `@ResponseStatus`  
**Exception Handling:** ❌ None

**Path Variables:**
- `@PathVariable String projectId` ⚠️ **Not validated**

**Request Parameters:**
- `@RequestParam String fileName` ⚠️ **Not validated**

**Issues:**
- 🔴 **CRITICAL: No parameter validation** - projectId and fileName could be null/blank
- ❌ **No exception handling** - No try-catch for file not found, access denied, etc.
- ⚠️ **Missing null check** - Should validate fileName is not blank
- ❌ **Inconsistent with NoteBookController** - NBC validates parameters (line 217–223)

---

### 11. GET /{projectId}/attachments/download
**Line:** 123–135  
**URL Path:** `/projects/{projectId}/attachments/download`  
**HTTP Method:** GET  
**Return Type:** `ResponseEntity<byte[]>`  
**Status Code:** `200 (OK)` (implicit)  
**Exception Handling:** ❌ None

**Path Variables:**
- `@PathVariable String projectId` ⚠️ **Not validated**

**Request Parameters:**
- `@RequestParam String fileName` ⚠️ **Not validated**

**Issues:**
- 🔴 **CRITICAL: No parameter validation** - projectId/fileName could be null/blank
- 🔴 **CRITICAL: Incomplete null check** - Checks `fileDownload.getFileName()` but NOT if `fileDownload` itself is null
- ❌ **No exception handling** - No try-catch for file not found, access denied, I/O errors
- ⚠️ **Inconsistent with NoteBookController** - NBC validates parameters and has full exception handling (line 227–257)

**Code Issue:**
```java
public ResponseEntity<byte[]> downloadProjectAttachment(...) {
    FileDownload fileDownload = projectService.getProjectAttachment(projectId, fileName);
    // ↑ fileDownload could be NULL here, but no check!
    
    ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename(fileDownload.getFileName() == null ? "attachment" : fileDownload.getFileName())
            // ↑ This will throw NPE if fileDownload is null
            .build();
    
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(MediaType.parseMediaType(fileDownload.getContentType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                    : fileDownload.getContentType()))
            .body(fileDownload.getData());
}
```

**Expected Behavior:**
```java
@GetMapping("/{projectId}/attachments/download")
public ResponseEntity<?> downloadProjectAttachment(
        @PathVariable String projectId,
        @RequestParam String fileName) {
    try {
        if (projectId == null || projectId.isBlank() || fileName == null || fileName.isBlank()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("status", "FAILED");
            error.put("message", "projectId and fileName are required");
            return ResponseEntity.badRequest().body(error);
        }
        FileDownload fd = projectService.getProjectAttachment(projectId, fileName);
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
                        fd.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE 
                                : fd.getContentType()))
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
```

---

## Summary Table of All Endpoints

| # | Endpoint | Method | Return Type | HTTP Status | @Valid | Exception Handling | Path Var Validation | Status Code Match |
|---|----------|--------|-------------|-------------|--------|-------------------|---------------------|------------------|
| 1 | /projects | GET | ResponseEntity | 200 | N/A | ❌ | N/A | ✓ |
| 2 | /{projectId} | GET | ResponseEntity | Dynamic | N/A | ❌ | ❌ | ❌ |
| 3 | /addProjects | POST | ProjectResponse | 201 | ❌ | ❌ | N/A | ❌ (200 in body) |
| 4 | /addProjectsWithAttachments | POST | ProjectResponse | 201 | ❌ | ❌ | N/A | ❌ (200 in body) |
| 5 | /{projectId} | PUT | ProjectResponse | 200 | ❌ | ❌ | ❌ | ❌ |
| 6 | /{projectId} | DELETE | ProjectResponse | 200 | N/A | ❌ | ❌ | ⚠️ |
| 7 | /archives | GET | ResponseEntity | Dynamic | N/A | ❌ | N/A | ❌ |
| 8 | /archives/user/{userId} | GET | ResponseEntity | Dynamic | N/A | ❌ | ❌ | ❌ |
| 9 | /{projectId}/attachments | POST | ProjectResponse | 200 | N/A | ❌ | ❌ | ❌ (should be 201) |
| 10 | /{projectId}/attachments | DELETE | ProjectResponse | 200 | N/A | ❌ | ❌ | ✓ |
| 11 | /{projectId}/attachments/download | GET | ResponseEntity<byte[]> | 200 | N/A | ❌ | ❌ | ✓ |

---

## Comparison with NoteBookController - Key Differences

### NoteBookController ✅ (Best Practices)

1. **Consistent Response Wrapper:**
   ```java
   ResponseEntity<ApiResponse> vs ResponseEntity<?>
   ```
   - All endpoints wrap response in ApiResponse
   - Provides consistent structure for clients

2. **Full Exception Handling:**
   ```java
   try {
       // ...
   } catch (ValidationException ex) { ... }
   } catch (RuntimeException ex) { ... }
   } catch (Exception ex) { ... }
   ```

3. **Proper Status Codes:**
   - 201 for resource creation (POST endpoints)
   - 200 for success
   - 404 for not found
   - 400 for validation errors
   - 500 for internal errors

4. **@Valid on Request Bodies:**
   ```java
   public ResponseEntity<ApiResponse> createNote(@Valid @RequestBody CreateNoteRequest request)
   ```

5. **Parameter Validation:**
   ```java
   if (noteId == null || noteId.isBlank()) {
       // Return 400 error
   }
   ```

6. **Null Checks:**
   ```java
   if (fd == null || fd.getData() == null) {
       // Return 404 error
   }
   ```

### ProjectController ❌ (Current Issues)

1. **Inconsistent Response Types:**
   - Some: `ResponseEntity<ProjectResponse>`
   - Some: `ProjectResponse` (raw)
   - Some: `ResponseEntity<?>`

2. **No Exception Handling:**
   - No try-catch blocks anywhere
   - Raw exceptions bubble up

3. **Wrong/Mismatched Status Codes:**
   - POST returns 201 HTTP but 200 in body
   - POST attachment returns 200 should be 201
   - Dynamic status codes from service layer

4. **Missing @Valid:**
   - Request bodies not validated at controller level
   - Relying on service layer for validation

5. **No Parameter Validation:**
   - Path variables passed directly to service
   - No null/blank checks

6. **Incomplete Null Checks:**
   - fileDownload not null-checked before dereferencing

---

## Critical Issues Requiring Immediate Fix

### P0 - Must Fix Before Production

1. **POST /addProjects & /addProjectsWithAttachments**
   - Add `@Valid` to ProjectRequest
   - Return 201 in response body (not 200)
   - Add exception handling
   - Wrap in ResponseEntity<ApiResponse>

2. **GET /{projectId}**
   - Validate projectId parameter
   - Add exception handling
   - Use consistent response wrapper

3. **PUT /{projectId}**
   - Add `@Valid` to ProjectRequest
   - Validate projectId parameter
   - Add exception handling

4. **DELETE /{projectId}**
   - Validate projectId parameter
   - Add exception handling
   - Consider returning 204 NO_CONTENT

5. **POST /{projectId}/attachments**
   - Validate projectId parameter
   - Change status code from 200 to 201
   - Add exception handling

6. **DELETE /{projectId}/attachments**
   - Validate projectId and fileName
   - Add exception handling

7. **GET /{projectId}/attachments/download**
   - Validate path variables
   - Add null check on fileDownload object itself
   - Add exception handling
   - Return 404 if file not found

### P1 - Should Fix

8. **GET /archives & /archives/user/{userId}**
   - Validate parameters (page, size, userId)
   - Add exception handling
   - Use consistent response wrapper

---

## Recommended Refactoring Plan

### Phase 1: Create Base Response Wrapper (Immediate)
Create abstract base controller or utility following NoteBookController pattern:
- Standardized ApiResponse structure
- Consistent exception handling
- Reusable validation helpers

### Phase 2: Update ProjectController (This Sprint)
1. Update all endpoints to use ResponseEntity<ApiResponse>
2. Add @Valid to all request bodies
3. Add exception handling to all methods
4. Add parameter validation

### Phase 3: Code Review & Testing (Next Sprint)
1. Integration tests for all error scenarios
2. API contract tests
3. Security testing (path traversal in fileName, etc.)

---

## Code Pattern Reference from NoteBookController

**Recommended Pattern for ProjectController:**

```java
@PostMapping("/{projectId}/attachments")
public ResponseEntity<ApiResponse> addAttachmentsToProject(
        @PathVariable String projectId,
        @RequestPart("attachments") MultipartFile[] attachments) {
    ApiResponse response = new ApiResponse();
    try {
        // Input validation
        if (projectId == null || projectId.isBlank()) {
            response.setCode(400);
            response.setStatus("FAILED");
            response.setMessage("projectId is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (attachments == null || attachments.length == 0) {
            response.setCode(400);
            response.setStatus("FAILED");
            response.setMessage("At least one attachment is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Business logic
        ProjectVO updated = projectService.addAttachmentsToProject(projectId, attachments);
        
        // Success response
        response.setCode(201);
        response.setStatus("SUCCESS");
        response.setMessage("Attachments added successfully");
        response.setData(updated);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
    } catch (InvalidMultipartJsonException ex) {
        response.setCode(400);
        response.setStatus("FAILED");
        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    } catch (RuntimeException ex) {
        response.setCode(404);
        response.setStatus("FAILED");
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception ex) {
        response.setCode(500);
        response.setStatus("FAILED");
        response.setMessage("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

## End of Report
