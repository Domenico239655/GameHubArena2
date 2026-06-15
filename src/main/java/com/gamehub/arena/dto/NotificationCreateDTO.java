package com.gamehub.arena.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationCreateDTO {
    @JsonProperty("userId")
    private Long userId;
    private String message;
    private String type;

    @JsonProperty("relatedId")
    private Long relatedId;
    @JsonProperty("senderUsername")
    private String senderUsername;
}
