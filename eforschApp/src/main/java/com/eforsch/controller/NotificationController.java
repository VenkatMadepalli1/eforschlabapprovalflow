package com.eforsch.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.dto.NotificationVO;
import com.eforsch.dto.User;
import com.eforsch.service.NotificationService;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

   
    @GetMapping("/{id}")
    public ResponseEntity<NotificationVO> getNotificationById(@PathVariable Long id) {
        NotificationVO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/notifications")
    public ResponseEntity<List<NotificationVO>> getAllNotifications(@RequestBody User user) {
        List<NotificationVO> notifications = notificationService.getAllNotifications(user);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/notifications/{id}/read")
    public ResponseEntity<NotificationVO> markNotificationAsRead(@PathVariable Long id) {
    	NotificationVO notificationVO = notificationService.markNotificationAsRead(id);
           return  ResponseEntity.ok(notificationVO);
    }

   }

