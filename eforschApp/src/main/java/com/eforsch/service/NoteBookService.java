package com.eforsch.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eforsch.dto.AttachmentResponseDTO;
import com.eforsch.dto.AutosaveNoteRequest;
import com.eforsch.dto.AutosaveNoteResponse;
import com.eforsch.dto.ArchivedNoteDTO;
import com.eforsch.dto.CreateNoteRequest;
import com.eforsch.dto.FileDownload;
import com.eforsch.dto.NoteAttachmentUploadRequest;
import com.eforsch.dto.NoteContentDTO;
import com.eforsch.dto.NotePermissionDTO;
import com.eforsch.dto.NoteResponseDTO;
import com.eforsch.dto.NoteVersionDetailDTO;
import com.eforsch.dto.NoteVersionSummaryDTO;
import com.eforsch.dto.UploadedByDTO;
import com.eforsch.entity.NoteBookArchive;
import com.eforsch.entity.NoteBookArchiveAttachment;
import com.eforsch.entity.NoteBook;
import com.eforsch.entity.NoteBookAttachment;
import com.eforsch.entity.NoteBookPermission;
import com.eforsch.entity.NoteBookVersion;
import com.eforsch.repository.NoteBookArchiveRepository;
import com.eforsch.repository.NoteBookArchiveAttachmentRepository;
import com.eforsch.repository.NoteBookAttachmentRepository;
import com.eforsch.repository.NoteBookPermissionRepository;
import com.eforsch.repository.NoteBookRepository;
import com.eforsch.repository.NoteBookVersionRepository;
import com.eforsch.util.NotebookSpecification;

@Service
public class NoteBookService {

    private final NoteBookRepository repo;
    private final NoteBookArchiveRepository archiveRepo;
    private final NoteBookArchiveAttachmentRepository archiveAttachmentRepo;
    private final NoteBookAttachmentRepository attachmentRepo;
    private final NoteBookPermissionRepository permissionRepo;
    private final NoteBookVersionRepository versionRepo;
    private final SecureRandom random = new SecureRandom();

    public NoteBookService(NoteBookRepository repo, NoteBookArchiveRepository archiveRepo,
            NoteBookArchiveAttachmentRepository archiveAttachmentRepo,
            NoteBookAttachmentRepository attachmentRepo,
            NoteBookPermissionRepository permissionRepo,
            NoteBookVersionRepository versionRepo) {
        this.repo = repo;
        this.archiveRepo = archiveRepo;
        this.archiveAttachmentRepo = archiveAttachmentRepo;
        this.attachmentRepo = attachmentRepo;
        this.permissionRepo = permissionRepo;
        this.versionRepo = versionRepo;
    }

    @Transactional
    public NoteResponseDTO createNote(CreateNoteRequest request, MultipartFile[] attachments) {
        try {
            NoteBook note = new NoteBook();
            note.setNoteId(generateNoteId());
            if (request.getProjectId() != null && !request.getProjectId().isBlank()) {
                note.setProjectId(request.getProjectId());
            }
            if (request.getExperimentTitle() != null && !request.getExperimentTitle().isBlank()) {
                note.setExperimentTitle(request.getExperimentTitle());
            }
            if (!CollectionUtils.isEmpty(request.getBudgetIds())) {
                note.setBudgetIds(request.getBudgetIds());
            }
            note.setNoteDate(request.getNoteDate());
            if (request.getContent() != null) {
                note.setContentHtml(request.getContent().getHtml());
                note.setContentPlainText(request.getContent().getPlainText());
            }
            note.setGroupName(request.getGroupName());
            note.setRole(request.getRole());
            note.setCreatedBy(request.getName() != null ? request.getName() : "Unknown");
            note.setCreatedByUserId(request.getUserId());

            NoteBook saved = repo.save(note);
            saveNoteAttachments(saved.getNoteId(), attachments, new UploadedByDTO(request.getUserId(), request.getName()));
            saveVersion(saved, request.getName() != null ? request.getName() : "Unknown");
            return toNoteResponseDTO(saved);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating the note: " + e.getMessage(), e);
        }
    }

    public NoteResponseDTO createNote(CreateNoteRequest request) {
        return createNote(request, null);
    }

    private String generateNoteId() {
        for (int i = 0; i < 10; i++) {
            int n = random.nextInt(9000) + 1000;
            String id = "NOTE-" + n;
            if (!repo.existsByNoteId(id)) {
                return id;
            }
        }
        return "NOTE-" + (random.nextInt(90000000) + 10000000);
    }

@Transactional
	public AutosaveNoteResponse autosaveNote(String noteId, AutosaveNoteRequest request) {
        try {
            NoteBook note = repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));
            if (request.getContent() != null) {
                note.setContentHtml(request.getContent().getHtml());
                note.setContentPlainText(request.getContent().getPlainText());
            }
            Instant now = Instant.now();
            note.setAutosavedAt(now);
            repo.save(note);
            return new AutosaveNoteResponse(note.getNoteId(), now);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while autosaving the note: " + e.getMessage(), e);
        }
    }

    public Page<NoteBook> getNotes(String projectId, String budgetId, LocalDate fromDate, LocalDate toDate, int page,
            int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("noteDate").descending());
            Specification<NoteBook> spec = NotebookSpecification.filterNotes(projectId, budgetId, fromDate, toDate);
            return repo.findAll(spec, pageable);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching notes: " + e.getMessage(), e);
        }
    }

    public NoteResponseDTO getNoteById(String noteId) {
        try {
            NoteBook note = repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));
            return toNoteResponseDTO(note);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching the note: " + e.getMessage(), e);
        }
    }

@Transactional
	public NoteResponseDTO updateNote(String noteId, CreateNoteRequest request) {
		NoteBook note = repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));

		if (request.getExperimentTitle() != null) {
			note.setExperimentTitle(request.getExperimentTitle());
		}

		if (request.getBudgetIds() != null) {
			note.setBudgetIds(request.getBudgetIds());
		}

		if (request.getNoteDate() != null) {
			note.setNoteDate(request.getNoteDate());
		}

		if (request.getContent() != null) {
			if (request.getContent().getHtml() != null) {
				note.setContentHtml(request.getContent().getHtml());
			}
			if (request.getContent().getPlainText() != null) {
				note.setContentPlainText(request.getContent().getPlainText());
			}
		}

		Instant now = Instant.now();
		note.setAutosavedAt(now);
		NoteBook updated = repo.save(note);
		
		// Add null check for editedBy field - use editor name if provided, otherwise keep original creator
		String editorName = request.getName() != null ? request.getName() : note.getCreatedBy();
		saveVersion(updated, editorName);

		return toNoteResponseDTO(updated);
	}

	@Transactional
	public void deleteNote(String noteId) {
        if (noteId == null || noteId.isBlank()) {
            throw new IllegalArgumentException("Note ID is required");
        }

        NoteBook note = repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));
        List<NoteBookAttachment> attachments = attachmentRepo.findByNoteIdOrderByAttachmentIdAsc(noteId);

        archiveRepo.save(toArchiveEntity(note));
        if (!attachments.isEmpty()) {
            archiveAttachmentRepo.saveAll(attachments.stream()
                    .map(this::toArchiveAttachmentEntity)
                    .collect(Collectors.toList()));
        }
        attachmentRepo.deleteByNoteId(noteId);
        versionRepo.deleteByNoteId(noteId);
        repo.delete(note);
    }

    @Transactional
    public List<AttachmentResponseDTO> addAttachmentsToNote(String noteId, MultipartFile[] files,
            NoteAttachmentUploadRequest uploadRequest) {
        repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));
        UploadedByDTO uploadedBy = uploadRequest != null ? uploadRequest.getUploadedBy() : null;
        saveNoteAttachments(noteId, files, uploadedBy);

        return attachmentRepo.findByNoteIdOrderByAttachmentIdAsc(noteId).stream()
                .map(a -> new AttachmentResponseDTO("ATT-" + a.getAttachmentId(), a.getFileName(),
                        a.getContentType(), new UploadedByDTO(a.getUploadedByUserId(), a.getUploadedByName()),
                        a.getUploadedAt(), "/api/notebooks/" + noteId + "/attachments/download?fileName=" + a.getFileName()))
                .collect(Collectors.toList());
    }

    public FileDownload getNoteAttachment(String noteId, String fileName) {
        NoteBookAttachment attachment = attachmentRepo.findByNoteIdAndFileName(noteId, fileName)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        return new FileDownload(attachment.getFileName(), attachment.getContentType(), attachment.getFileData());
    }

    @Transactional
    public NoteResponseDTO deleteAttachmentFromNote(String noteId, String fileName) {
        NoteBook note = repo.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found: " + noteId));

        NoteBookAttachment attachment = attachmentRepo.findByNoteIdAndFileName(noteId, fileName)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + fileName));

        attachmentRepo.delete(attachment);
        return toNoteResponseDTO(note);
    }

    public List<String> getAttachmentNames(String noteId) {
        return attachmentRepo.findByNoteIdOrderByAttachmentIdAsc(noteId).stream().map(NoteBookAttachment::getFileName)
                .collect(Collectors.toList());
    }

    public Page<ArchivedNoteDTO> listArchivedNotes(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return archiveRepo.findAllByOrderByArchivedAtDesc(pageable)
                .map(this::toArchivedNoteDTO);
    }

    public Page<ArchivedNoteDTO> listArchivedNotesByUserId(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return archiveRepo.findByCreatedByUserIdOrderByArchivedAtDesc(userId, pageable)
                .map(this::toArchivedNoteDTO);
    }

    public Page<NotePermissionDTO> listPermissions(String noteId, int page, int size) {
        repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));
        Pageable pageable = PageRequest.of(page - 1, size);
        return permissionRepo.findByNoteIdOrderByGrantedAtDesc(noteId, pageable)
                .map(p -> new NotePermissionDTO(p.getUserId(), p.getAccessLevel(), p.getGrantedAt()));
    }

    public Page<NoteVersionSummaryDTO> listVersions(String noteId, int page, int size) {
        repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("version").ascending());
        return versionRepo.findByNoteIdOrderByVersionAsc(noteId, pageable)
                .map(v -> new NoteVersionSummaryDTO(v.getVersion(), v.getEditedBy(), v.getEditedAt()));
    }

    public NoteVersionDetailDTO getVersion(String noteId, int version) {
        repo.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found: " + noteId));
        NoteBookVersion v = versionRepo.findByNoteIdAndVersion(noteId, version)
                .orElseThrow(() -> new RuntimeException("Version " + version + " not found for note: " + noteId));

        return new NoteVersionDetailDTO(noteId, v.getVersion(), v.getExperimentTitle(),
                new NoteContentDTO(v.getContentHtml(), v.getContentPlainText()), v.getEditedBy(), v.getEditedAt());
    }

    private NoteResponseDTO toNoteResponseDTO(NoteBook note) {
        List<String> attachmentNames = attachmentRepo.findByNoteIdOrderByAttachmentIdAsc(note.getNoteId()).stream()
                .map(NoteBookAttachment::getFileName).collect(Collectors.toList());
        return new NoteResponseDTO(note.getNoteId(), note.getProjectId(), note.getExperimentTitle(),
                note.getBudgetIds(), new NoteContentDTO(note.getContentHtml(), note.getContentPlainText()),
                note.getNoteDate(), note.getCreatedAt(), note.getCreatedBy(), note.getGroupName(), note.getAutosavedAt(),
                attachmentNames);
    }

    private ArchivedNoteDTO toArchivedNoteDTO(NoteBookArchive archive) {
        List<String> attachmentNames = archiveAttachmentRepo.findByNoteIdOrderByAttachmentIdAsc(archive.getNoteId())
                .stream()
                .map(NoteBookArchiveAttachment::getFileName)
                .collect(Collectors.toList());
        return new ArchivedNoteDTO(archive.getNoteId(), archive.getExperimentTitle(), archive.getProjectId(),
                archive.getNoteDate(), archive.getCreatedBy(), archive.getCreatedByUserId(), attachmentNames,
                archive.getArchivedAt());
    }

    private NoteBookArchive toArchiveEntity(NoteBook note) {
        NoteBookArchive archive = new NoteBookArchive();
        archive.setNoteId(note.getNoteId());
        archive.setExperimentTitle(note.getExperimentTitle());
        archive.setProjectId(note.getProjectId());
        archive.setBudgetIds(note.getBudgetIds());
        archive.setNoteDate(note.getNoteDate());
        archive.setContentHtml(note.getContentHtml());
        archive.setContentPlainText(note.getContentPlainText());
        archive.setGroupName(note.getGroupName());
        archive.setRole(note.getRole());
        archive.setCreatedBy(note.getCreatedBy());
        archive.setCreatedByUserId(note.getCreatedByUserId());
        archive.setCreatedAt(note.getCreatedAt());
        archive.setAutosavedAt(note.getAutosavedAt());
        archive.setArchivedAt(Instant.now());
        return archive;
    }

    private NoteBookArchiveAttachment toArchiveAttachmentEntity(NoteBookAttachment attachment) {
        NoteBookArchiveAttachment archiveAttachment = new NoteBookArchiveAttachment();
        archiveAttachment.setNoteId(attachment.getNoteId());
        archiveAttachment.setFileName(attachment.getFileName());
        archiveAttachment.setContentType(attachment.getContentType());
        archiveAttachment.setFileSize(attachment.getFileSize());
        archiveAttachment.setFileData(attachment.getFileData());
        archiveAttachment.setUploadedByUserId(attachment.getUploadedByUserId());
        archiveAttachment.setUploadedByName(attachment.getUploadedByName());
        archiveAttachment.setUploadedAt(attachment.getUploadedAt());
        archiveAttachment.setArchivedAt(Instant.now());
        return archiveAttachment;
    }

    private void saveVersion(NoteBook note, String editedBy) {
        int nextVersion = versionRepo.findMaxVersionByNoteId(note.getNoteId()) + 1;
        NoteBookVersion version = new NoteBookVersion();
        version.setNoteId(note.getNoteId());
        version.setVersion(nextVersion);
        version.setExperimentTitle(note.getExperimentTitle());
        version.setContentHtml(note.getContentHtml());
        version.setContentPlainText(note.getContentPlainText());
        version.setEditedBy(editedBy);
        version.setEditedAt(Instant.now());
        versionRepo.save(version);
    }

    private void saveNoteAttachments(String noteId, MultipartFile[] attachments, UploadedByDTO uploadedBy) {
        if (attachments == null || attachments.length == 0) {
            return;
        }

        for (MultipartFile file : attachments) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            try {
                validateNoteFile(file);
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                NoteBookAttachment entity = attachmentRepo.findByNoteIdAndFileName(noteId, fileName)
                        .orElseGet(NoteBookAttachment::new);
                entity.setNoteId(noteId);
                entity.setFileName(fileName);
                entity.setContentType(file.getContentType());
                entity.setFileSize(file.getSize());
                entity.setFileData(file.getBytes());
                entity.setUploadedByUserId(uploadedBy != null ? uploadedBy.getUserId() : null);
                entity.setUploadedByName(uploadedBy != null ? uploadedBy.getName() : null);
                attachmentRepo.save(entity);
            } catch (Exception e) {
                throw new RuntimeException("Error saving attachment: " + e.getMessage(), e);
            }
        }
    }

    private void validateNoteFile(MultipartFile file) {
        long max = 50L * 1024 * 1024;
        if (file.getSize() > max) {
            throw new IllegalArgumentException("File too large. Max 50MB");
        }
        String ct = file.getContentType();
        if (ct != null) {
            boolean ok = ct.equals("application/pdf") || ct.equals("image/png") || ct.equals("image/jpeg")
                    || ct.equals("image/gif") || ct.equals("application/msword")
                    || ct.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    || ct.equals("application/vnd.ms-excel")
                    || ct.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            if (!ok) {
                throw new IllegalArgumentException("Unsupported file type: " + ct);
            }
        }
    }

    /**
     * Overloaded getNotes with userId and groupName filters for authorization
     * If userId is provided (SCIENTIST role): filter by createdByUserId
     * If userId is null (GROUP_LEADER role): filter by groupName only
     */
    public Page<NoteBook> getNotes(String projectId, String budgetId, LocalDate fromDate, LocalDate toDate, int page,
            int size, String userId, String groupName) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("noteDate").descending());
            Specification<NoteBook> spec = NotebookSpecification.filterNotes(projectId, budgetId, fromDate, toDate);
            
            // Add group filter and user filter
            if (groupName != null && !groupName.isBlank()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("groupName"), groupName));
            }
            
            // For regular scientists (non-GROUP_LEADER), filter by their userId
            if (userId != null && !userId.isBlank()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("createdByUserId"), userId));
            }
            
            return repo.findAll(spec, pageable);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching notes: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a user can access a note
     * - Owner can always access their own notes
     * - GROUP_LEADER can access all notes in their group
     * - Scientists can only access their own notes
     */
    public boolean canAccessNote(String noteId, String userId, String groupName, String role) {
        try {
            NoteBook note = repo.findById(noteId).orElse(null);
            if (note == null) {
                return false;
            }
            
            // Check if note belongs to user's group
            if (!groupName.equals(note.getGroupName())) {
                return false;
            }
            
            // GROUP_LEADER can access all notes in their group
            if (role != null && role.equalsIgnoreCase("GROUP_LEADER")) {
                return true;
            }
            
            // Regular scientists can only access their own notes
            return note.getCreatedByUserId() != null && note.getCreatedByUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Overloaded listArchivedNotes with userId and groupName filters
     * If userId is provided (SCIENTIST role): filter by createdByUserId
     * If userId is null (GROUP_LEADER role): filter by groupName only
     */
    public Page<ArchivedNoteDTO> listArchivedNotes(int page, int size, String userId, String groupName) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("archivedAt").descending());
            Page<NoteBookArchive> result;
            
            if (userId != null && !userId.isBlank()) {
                // Scientist: filter by their userId and groupName
                result = archiveRepo.findByCreatedByUserIdAndGroupNameOrderByArchivedAtDesc(userId, groupName, pageable);
            } else {
                // GROUP_LEADER: filter by groupName only
                result = archiveRepo.findByGroupNameOrderByArchivedAtDesc(groupName, pageable);
            }
            
            return result.map(this::toArchivedNoteDTO);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching archived notes: " + e.getMessage(), e);
        }
    }

    /**
     * Overloaded listArchivedNotesByUserId with groupName filter
     */
    public Page<ArchivedNoteDTO> listArchivedNotesByUserId(String userId, int page, int size, String groupName) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("archivedAt").descending());
            Page<NoteBookArchive> result = archiveRepo.findByCreatedByUserIdAndGroupNameOrderByArchivedAtDesc(userId, groupName, pageable);
            return result.map(this::toArchivedNoteDTO);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching archived notes by user: " + e.getMessage(), e);
        }
    }
}
