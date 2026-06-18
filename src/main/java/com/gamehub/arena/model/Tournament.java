package com.gamehub.arena.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    //private LocalDate date;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;

    @Column(name = "rating")
    private Double rating = 0.0;

    @ManyToOne
    private User organizer;

    @ManyToMany
    private List<User> participants = new ArrayList<>();

    public Tournament(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getStatus() {
        return status;
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

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {this.registrationDeadline = registrationDeadline;}

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
