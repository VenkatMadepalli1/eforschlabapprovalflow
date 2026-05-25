package com.eforsch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.GroupHierarchyResponse;
import com.eforsch.GroupLeaderApprovalRequest;
import com.eforsch.GroupLeaderApprovalResponse;
import com.eforsch.service.GroupLeaderServiceImpl;

@RestController
@RequestMapping("/api/group-leader")
public class GroupLeaderController {

    @Autowired
    private GroupLeaderServiceImpl groupLeaderService;

    @PostMapping("/approve-user")
    public ResponseEntity<?> approveUser(@RequestBody GroupLeaderApprovalRequest request) {
        GroupLeaderApprovalResponse response = groupLeaderService.approveUser(request);
        return ResponseEntity.ok(response);
    }
   
    @GetMapping("/groups/hierarchy")
    public ResponseEntity<GroupHierarchyResponse> getGroupHierarchy(@RequestParam String requesterRole,
            @RequestParam(required = false) Long requesterId) {
    	
    	if ("admin".equalsIgnoreCase(requesterRole)) {
    		 return ResponseEntity.ok(groupLeaderService.getGroupHierarchyByAdmin());
    	}else if ("groupLeader".equalsIgnoreCase(requesterRole)) {
			if (requesterId == null) {
				return ResponseEntity.badRequest()
						.body(new GroupHierarchyResponse("error", "Group Leader ID is required",0L, null));
			}
			 return ResponseEntity.ok(groupLeaderService.getGroupHierarchyByGL(requesterId));
			// Additional logic for group leader can be added here if needed
		} else {
			return ResponseEntity.badRequest()
					.body(new GroupHierarchyResponse("error", "Invalid requester role", 0L, null));
    	}
       
    }
    
}

