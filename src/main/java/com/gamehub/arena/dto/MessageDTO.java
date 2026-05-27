package com.gamehub.arena.dto;


import lombok.Data;

@Data
public class MessageDTO {
    private Long id;
    private String sender;
    private String content;
    private Long tournamentId;
    private String timestamp;


}
