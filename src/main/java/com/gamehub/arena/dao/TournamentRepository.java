package com.gamehub.arena.dao;

import com.gamehub.arena.model.Tournament;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


// TIPS: Pattern DAO. Questa interfaccia gestisce l'accesso ai dati (Data Access Object).
// Spring Data JPA crea l'implementazione automaticamente a runtime estendendo JpaRepository.
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    // TIPS: @EntityGraph risolve il problema delle query N+1 (Proxy e Lazy Loading nativi di Hibernate).
    // Specifica di caricare 'participants' e 'game' immediatamente (Eager) in questa singola query.
    @Override
    @EntityGraph(attributePaths = {"participants", "game"})
    List<Tournament> findAll();

    // TIPS: Esempio di custom query in JPQL. Molto utile per dimostrare come gestire query complesse e raggruppamenti.
    @org.springframework.data.jpa.repository.Query("SELECT t.winner.username, COUNT(t) as wins FROM Tournament t WHERE t.status = 'CONCLUSO' AND EXTRACT(MONTH FROM t.endDate) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM t.endDate) = EXTRACT(YEAR FROM CURRENT_DATE) GROUP BY t.winner.username ORDER BY wins DESC")
    List<Object[]> findPlayerofTheMonth();
    @org.springframework.data.jpa.repository.Query("SELECT t.status FROM Tournament t WHERE t.id = :id")
    String getStatusById(@org.springframework.data.repository.query.Param("id") Long id);
}
