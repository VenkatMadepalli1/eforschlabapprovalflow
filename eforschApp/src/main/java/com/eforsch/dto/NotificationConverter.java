package com.eforsch.dto;


import com.eforsch.entity.NotificationEntity;

public class NotificationConverter {

    // Convert NotificationVO to NotificationEntity
    public static NotificationEntity toEntity(NotificationVO vo) {
        if (vo == null) {
            return null;
        }
        NotificationEntity entity = new NotificationEntity();
        entity.setNotificationId(vo.getNotificationId());
        entity.setTitle(vo.getTitle());
        entity.setMessage(vo.getMessage());
        entity.setType(vo.getType());
        entity.setEntityId(vo.getEntityId());
        entity.setEntityType(vo.getEntityType());
        entity.setRole(vo.getRole());
        entity.setMetadata(vo.getMetadata());
        entity.setCreatedAt(vo.getCreatedAt());
        entity.setRead(vo.isRead());
        entity.setGroupName(vo.getGroupName());
        return entity;
    }

    // Convert NotificationEntity to NotificationVO
    public static NotificationVO toVO(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }
        NotificationVO vo = new NotificationVO();
        vo.setNotificationId(entity.getNotificationId());
        vo.setTitle(entity.getTitle());
        vo.setMessage(entity.getMessage());
        vo.setType(entity.getType());
        vo.setEntityId(entity.getEntityId());
        vo.setEntityType(entity.getEntityType());
        vo.setRole(entity.getRole());
        vo.setMetadata(entity.getMetadata());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setRead(entity.isRead());
        vo.setGroupName(entity.getGroupName());
        return vo;
    }
}

