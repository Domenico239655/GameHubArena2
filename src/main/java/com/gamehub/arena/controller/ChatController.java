package com.gamehub.arena.controller;
import com.gamehub.arena.dao.ChatGlobalRepository;
import com.gamehub.arena.model.ChatGlobal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatGlobalRepository chatMessageRepository;
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatGlobal sendMessage(ChatGlobal chatMessage) {

        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }

    @GetMapping("/api/chat/history")
    public List<ChatGlobal> getChatHistory() {
        List<ChatGlobal> history = chatMessageRepository.findTop50ByOrderByTimestampDesc();
        Collections.reverse(history);

        return history;
    }
}
