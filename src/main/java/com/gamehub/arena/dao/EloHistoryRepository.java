package com.gamehub.arena.dao;

import com.gamehub.arena.model.EloHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EloHistoryRepository extends JpaRepository<EloHistory, Long> {
    List<EloHistory> findByUserIdOrderByTimestampAsc(Long userId);

}
