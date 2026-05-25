package com.eforsch.dto;

import java.time.LocalDate;
import java.util.List;

public class CreateNoteRequest {
    private String projectId;
    private String experimentTitle;
    private List<String> budgetIds;
    private LocalDate noteDate;
    private NoteContentDTO content;
    private String groupName;
    private String role;
    private String name;
    private String userId;

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getExperimentTitle() { return experimentTitle; }
    public void setExperimentTitle(String experimentTitle) { this.experimentTitle = experimentTitle; }

    public List<String> getBudgetIds() { return budgetIds; }
    public void setBudgetIds(List<String> budgetIds) { this.budgetIds = budgetIds; }

    public LocalDate getNoteDate() { return noteDate; }
    public void setNoteDate(LocalDate noteDate) { this.noteDate = noteDate; }

    public NoteContentDTO getContent() { return content; }
    public void setContent(NoteContentDTO content) { this.content = content; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
