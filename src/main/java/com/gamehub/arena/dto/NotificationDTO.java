package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private String type;
    private String message;
    private Long userId;
    private Long relatedId;
}
