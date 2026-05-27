package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class NotificationCreateDTO {
    private Long userId;
    private String message;
    private String type;
    private Long relatedId;

}
