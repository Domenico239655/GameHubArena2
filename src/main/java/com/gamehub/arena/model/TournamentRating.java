package com.gamehub.arena.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tournament_rating")
public class TournamentRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int score;
}
