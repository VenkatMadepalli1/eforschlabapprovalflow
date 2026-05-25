package com.eforsch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.service.GroupService;
import com.eforsch.util.GroupVO;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/getGroupName/{groupName}")
    public GroupVO getGroupName(@PathVariable String groupName) {
        return groupService.getGroupByName(groupName);
    }
    
    @GetMapping("/getAllGroups")
    public List<GroupVO> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public GroupVO getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id);
    }

    @PostMapping("/createGroup")
    public GroupVO createGroup(@RequestBody GroupVO groupVO) {
        return groupService.createGroup(groupVO);
    }

    @PutMapping("/updateGroup")
    public GroupVO updateGroup(@RequestBody GroupVO groupVO) {
        return groupService.updateGroup(groupVO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}

