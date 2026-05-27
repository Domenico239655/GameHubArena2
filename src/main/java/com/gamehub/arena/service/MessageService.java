package com.gamehub.arena.service;

import com.gamehub.arena.dto.MessageDTO;


import java.util.List;

public interface MessageService {
    MessageDTO saveMessage(String content, Long userId, Long tournamentId);
    List<MessageDTO> getMessages(Long tournamentId);

}
