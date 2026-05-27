package com.gamehub.arena.websocket;

import com.gamehub.arena.dto.MessageDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    /*@MessageMapping("/chat/{tournamentId}")
    @SendTo("/topic/chat/{tournamentId}")
    public MessageDTO sendMessage(@DestinationVariable Long tournamentId, MessageDTO message){
        return message;
    }*/
}
