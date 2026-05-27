package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.MessageRepository;
import com.gamehub.arena.dao.TournamentRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.MessageDTO;
import com.gamehub.arena.model.Message;
import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repo;
    private final UserRepository userRepo;
    private final TournamentRepository tournamentRepo;

    public MessageServiceImpl(MessageRepository repo, UserRepository userRepo, TournamentRepository tournamentRepo){
        this.repo = repo;
        this.userRepo = userRepo;
        this.tournamentRepo = tournamentRepo;
    }

    @Override
    public MessageDTO saveMessage(String content, Long userId, Long tournamentId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato!"));
        Tournament tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo non trovato!"));

        Message msg = new Message();
        msg.setContent(content);
        msg.setSender(user);
        msg.setTournament(tournament);
        msg.setTimestamp(LocalDateTime.now());

        repo.save(msg);
        return toDTO(msg);
    }

    @Override
    public List<MessageDTO> getMessages(Long tournamentId) {
        return repo.findByTournamentIdOrderByTimestampAsc(tournamentId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private MessageDTO toDTO(Message message){
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSender(message.getSender().getUsername());
        dto.setTournamentId(message.getTournament().getId());
        dto.setTimestamp(message.getTimestamp().toString());
        return dto;
    }
}
