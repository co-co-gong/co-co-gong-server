package com.server.domain.notification.dto;

import java.util.UUID;

import com.server.domain.notification.enums.NotificationType;

import lombok.Getter;

@Getter
public class NotificationDto {
    private UUID id;
    private UUID receiptUserId;
    private NotificationType type;
    private String title;
    private String message;
    private String href;
    private boolean view;
}
