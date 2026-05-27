package com.gamehub.arena.controller;


import com.gamehub.arena.dto.MessageDTO;
import com.gamehub.arena.model.Message;
import com.gamehub.arena.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service){
        this.service = service;
    }

    @PostMapping
    public MessageDTO send(@RequestBody MessageDTO dto) {
        return service.saveMessage(
                dto.getContent(),
                Long.valueOf(dto.getSender()),
                dto.getTournamentId()
        );

    }

    @GetMapping("/{tournamentId}")
    public List<MessageDTO> getMessages(@PathVariable Long tournamentId) {
        return service.getMessages(tournamentId);
    }
}
