package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.MessageRepository;
import com.gamehub.arena.model.Message;
import com.gamehub.arena.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repo;

    public MessageServiceImpl(MessageRepository repo){
        this.repo = repo;
    }

    @Override
    public Message send(Message m) {
        m.setTimestamp(new Date());
        return repo.save(m);

    }
}
