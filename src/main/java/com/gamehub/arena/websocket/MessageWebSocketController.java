package com.gamehub.arena.websocket;

import com.gamehub.arena.dto.MessageDTO;
import com.gamehub.arena.dto.NotificationDTO;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.MessageService;
import com.gamehub.arena.service.TournamentService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageWebSocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TournamentService tournamentService;
    private final NotificationWebSocketController wsNotification;
    private final OnlineUsersTracker onlineUsers;

    public MessageWebSocketController(MessageService messageService, SimpMessagingTemplate messagingTemplate, TournamentService tournamentService, NotificationWebSocketController wsNotification, OnlineUsersTracker onlineUsers) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.tournamentService = tournamentService;
        this.wsNotification = wsNotification;
        this.onlineUsers = onlineUsers;
    }

    @MessageMapping("/chat/{tournamentId}")
    public void sendMessage(
            @DestinationVariable Long tournamentId,
            MessageDTO incoming
    ) {
        MessageDTO saved = messageService.saveMessage(
                incoming.getContent(),
                Long.valueOf(incoming.getSender()),
                tournamentId
        );

        messagingTemplate.convertAndSend("/topic/chat/" + tournamentId, saved);

        NotificationDTO notification = new NotificationDTO();
        notification.setType("MESSAGE");
        notification.setMessage("Nuovo messaggio da: " + saved.getSender());
        notification.setRelatedId(tournamentId);

        for(User user: tournamentService.getParticipants(tournamentId)){
            notification.setUserId(user.getId());
            wsNotification.sendNotification(user.getId(), notification);
        }
    }

    @MessageMapping("/chat/{tournamentId}/join")
    public void joinChat(@DestinationVariable Long tournametId, MessageDTO incoming){
        onlineUsers.userJoined(tournametId, incoming.getSender());
        messagingTemplate.convertAndSend("/topic/chat/" + tournametId + "/online",onlineUsers.getOnlineUsers(tournametId)
        );
    }

    @MessageMapping("/chat/{tournamentId}/leave")
    public void leaveChat(@DestinationVariable Long tournamentId, MessageDTO incoming){
        onlineUsers.userLeft(tournamentId, incoming.getSender());
        messagingTemplate.convertAndSend(
                "/topic/chat" + tournamentId + "/online", onlineUsers.getOnlineUsers(tournamentId)
        );

    }
}
