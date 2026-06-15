package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private boolean read;
    private String type;

    private Long userId;
    private Long relatedId;
    private String username;
    private String senderUsername;
    private String date;
}
