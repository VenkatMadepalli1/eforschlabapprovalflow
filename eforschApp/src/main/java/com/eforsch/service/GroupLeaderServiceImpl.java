package com.eforsch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eforsch.GroupHierarchyResponse;
import com.eforsch.GroupLeaderApprovalRequest;
import com.eforsch.GroupLeaderApprovalResponse;
import com.eforsch.entity.UserDetails;
import com.eforsch.entity.UserRole;
import com.eforsch.repository.UserDetailsRepository;
import com.eforsch.repository.UserRoleRepository;

@Service
public class GroupLeaderServiceImpl  {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;
    
    public GroupLeaderApprovalResponse approveUser(GroupLeaderApprovalRequest request) {
        Optional<UserDetails> optionalUser = userDetailsRepository.findById(request.getUser().getUserId());

        if (!optionalUser.isPresent()) {
            return new GroupLeaderApprovalResponse("error", "User not found", "not_found");
        }

        UserDetails user = optionalUser.get();
        String newStatus = request.isApprove() ? "approved" : "denied";
        user.setStatus(newStatus);

        userDetailsRepository.save(user);

        return new GroupLeaderApprovalResponse(
                "success",
                "User " + newStatus + " successfully by Group Leader.",
                newStatus
        );
    }
    
    
    public GroupHierarchyResponse getGroupHierarchyByAdmin() {

    	List<UserDetails> groupLeaders;
    	// Step 1: Fetch all group leaders
    	 UserRole userRole = userRoleRepository.findByRole("groupLeader");
         if (userRole != null) {
              groupLeaders = userDetailsRepository.findByUserRole(userRole);
    	
        List<GroupHierarchyResponse.GroupData> groupDataList = new ArrayList<>();

        for (UserDetails leader : groupLeaders) {
            String groupName = leader.getGroupName();

            // Step 2: Fetch all members of this group (excluding the group leader)
            List<UserDetails> groupMembers = userDetailsRepository.findByGroupName(groupName)
                    .stream()
                    .filter(user -> !user.getUserRole().getRole().equalsIgnoreCase("groupLeader"))
                    .collect(Collectors.toList());

            // Step 3: Build members list
            List<GroupHierarchyResponse.Member> members = groupMembers.stream().map(member ->
                new GroupHierarchyResponse.Member(
                    member.getUserId(),
                    member.getFirstname() + " " + member.getLastname(),
                    member.getEmail(),
                    member.getUserRole().getRole(),
                    member.getStatus()
                )
            ).collect(Collectors.toList());

            // Step 4: Build group leader info
            GroupHierarchyResponse.GroupLeader leaderInfo = new GroupHierarchyResponse.GroupLeader(
                leader.getUserId(),
                leader.getFirstname()+ " " + leader.getLastname(),
                leader.getEmail()
            );

            // Step 5: Combine to GroupData
            GroupHierarchyResponse.GroupData groupData = new GroupHierarchyResponse.GroupData(
                groupName,
                leaderInfo,
                members
            );

            groupDataList.add(groupData);
        }

        return new GroupHierarchyResponse("success", "Admin", 0L, groupDataList);
    }
   return new GroupHierarchyResponse("error", "No group leaders found", 0L, new ArrayList<>());
   }
    
    
    public GroupHierarchyResponse getGroupHierarchyByGL(Long requesterId) {

    	// Step 1: Fetch all group leaders
    	 
    	UserDetails leader = (UserDetails) userDetailsRepository.findByUserId(requesterId);
    	
        List<GroupHierarchyResponse.GroupData> groupDataList = new ArrayList<>();

            String groupName = leader.getGroupName();

            // Step 2: Fetch all members of this group (excluding the group leader)
            List<UserDetails> groupMembers = userDetailsRepository.findByGroupName(groupName)
                    .stream()
                    .filter(user -> !user.getUserRole().getRole().equalsIgnoreCase("groupLeader"))
                    .collect(Collectors.toList());

            // Step 3: Build members list
            List<GroupHierarchyResponse.Member> members = groupMembers.stream().map(member ->
                new GroupHierarchyResponse.Member(
                    member.getUserId(),
                    member.getFirstname() + " " + member.getLastname(),
                    member.getEmail(),
                    member.getUserRole().getRole(),
                    member.getStatus()
                )
            ).collect(Collectors.toList());

            // Step 4: Build group leader info
            GroupHierarchyResponse.GroupLeader leaderInfo = new GroupHierarchyResponse.GroupLeader(
                leader.getUserId(),
                leader.getFirstname()+ " " + leader.getLastname(),
                leader.getEmail()
            );

            // Step 5: Combine to GroupData
            GroupHierarchyResponse.GroupData groupData = new GroupHierarchyResponse.GroupData(
                groupName,
                leaderInfo,
                members
            );

            groupDataList.add(groupData);
        
            // return the response
            if (!groupDataList.isEmpty()) {
            	return new GroupHierarchyResponse("success", "groupleader", leader.getUserId(), groupDataList);
    }
        return new GroupHierarchyResponse("error", "No group leaders found", 0L , new ArrayList<>());
   }
    
}
