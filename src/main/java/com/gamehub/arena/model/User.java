package com.gamehub.arena.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

// TIPS: Esempio di Entity JPA che mappa una tabella sul database.
// Hibernate crea dei Proxy attorno a queste entità per gestire il Lazy Loading delle collezioni.
@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
@Repository
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    private int rank;
    private int elo = 1000;

    // TIPS: Relazione Molti-a-Molti. Di default, le relazioni @ManyToMany e @OneToMany sono caricate in modo 'Lazy'.
    // Quando si accede a 'personalLibrary', Hibernate usa un Pattern Proxy per eseguire la query solo in quel momento.
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name= "user_games",
            joinColumns = @JoinColumn(name= "user_id"),
            inverseJoinColumns = @JoinColumn(name= "game_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Game> personalLibrary = new HashSet<>();
}


