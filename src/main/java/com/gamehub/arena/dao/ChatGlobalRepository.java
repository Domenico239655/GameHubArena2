package com.gamehub.arena.dao;

import com.gamehub.arena.model.ChatGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGlobalRepository extends JpaRepository<ChatGlobal, Long> {
    List<ChatGlobal> findTop50ByOrderByTimestampDesc();
}
