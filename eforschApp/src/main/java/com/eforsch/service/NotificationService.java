package com.eforsch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eforsch.dto.NotificationConverter;
import com.eforsch.dto.NotificationVO;
import com.eforsch.dto.User;
import com.eforsch.entity.NotificationEntity;
import com.eforsch.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationVO createNotification(NotificationVO notificationVO) {
        NotificationEntity entity = NotificationConverter.toEntity(notificationVO);
        NotificationEntity savedEntity = notificationRepository.save(entity);
        return NotificationConverter.toVO(savedEntity);
    }

    public NotificationVO getNotificationById(Long id) {
        NotificationEntity entity = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return NotificationConverter.toVO(entity);
    }

    public List<NotificationVO> getAllNotifications(User user) {
    	
		if (user.getRole().equals("scientist") || user.getRole().equals("Admin")  || user.getRole().equals("groupleader")) {
			List<NotificationEntity> entities = notificationRepository.findByGroupName(user.getGroupName());
			return entities.stream().map(NotificationConverter::toVO).collect(Collectors.toList());
		} else if (user.getRole().equals("labMgmt") ) {
			List<NotificationEntity> entities = notificationRepository.findByRole(user.getRole());
			return entities.stream().map(NotificationConverter::toVO).collect(Collectors.toList());
		}
		List<NotificationEntity> entities = notificationRepository.findByRole(user.getRole());
		return entities.stream().map(NotificationConverter::toVO).collect(Collectors.toList());
        }

    public NotificationVO updateNotification(Long id, NotificationVO notificationVO) {
        NotificationEntity existingEntity = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        NotificationEntity updatedEntity = NotificationConverter.toEntity(notificationVO);
        updatedEntity.setNotificationId(existingEntity.getNotificationId());
        NotificationEntity savedEntity = notificationRepository.save(updatedEntity);
        return NotificationConverter.toVO(savedEntity);
    }

    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }
    
    public NotificationVO markNotificationAsRead(Long id) {
        NotificationEntity notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
        notification.setRead(true);
        notification = notificationRepository.save(notification);
        return NotificationConverter.toVO(notification);
    }
}
