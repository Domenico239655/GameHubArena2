package com.gamehub.arena.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    private Game game;


    private String status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "max_participants")
    private Integer maxParticipants = 4;

    @ManyToOne
    private User organizer;

    @ManyToMany
    private List<User> participants = new ArrayList<>();

    public Tournament(){}

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public LocalDate getDate() {
//        return date;
//    }

//    public void setDate(LocalDate date) {
//        this.date = date;
//    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(LocalDateTime startDate) {this.startDate = startDate;}

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
}
