package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.NotificationRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.NotificationCreateDTO;
import com.gamehub.arena.dto.NotificationResponseDTO;
import com.gamehub.arena.model.Notification;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    public final NotificationRepository repo;
    public final UserRepository userRepo;


    public NotificationServiceImpl(NotificationRepository repo, UserRepository userRepo){
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public NotificationResponseDTO send(NotificationCreateDTO dto){
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(dto.getMessage());
        n.setRead(false);
        n.setType(dto.getType());
        n.setRelatedId(dto.getRelatedId());

        repo.save(n);
        return toDTO(n);
    }

    @Override
    public List<NotificationResponseDTO> getUserNotifications(Long userId){
        return repo.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public NotificationResponseDTO markAsRead(Long id){
        Notification n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifica non trovata"));
        n.setRead(true);
        repo.save(n);
        return toDTO(n);
    }

    private NotificationResponseDTO toDTO(Notification n){
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(n.getId());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setType(n.getType());
        dto.setUsername(n.getUser().getUsername());

        dto.setUserId(n.getUser().getId());
        dto.setRelatedId(n.getRelatedId());


        return dto;
    }

    @Override
    public void sendNotification(Notification notification) {
        repo.save(notification);
    }
}
