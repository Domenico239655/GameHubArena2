package com.gamehub.arena.model;

import jakarta.persistence.*;
import org.springframework.beans.factory.config.YamlProcessor;

import java.util.Date;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Tournament tournament;

    @ManyToOne
    private Team team1;

    @ManyToOne Team team2;

    @ManyToOne Team winner;

    private String status;
    private Date date;

    @ManyToOne
    private User player1;
    @ManyToOne
    private User player2;

    private Integer scorePlayer1;
    private Integer scorePlayer2;

    private Integer roundNumber;

    @Column(name = "screenshot_player1")
    private String screenshotPlayer1;

    @Column(name = "screenshot_player2")
    private String screenshotPlayer2;

    @Enumerated(EnumType.STRING)
    private MatchStatus stato;

    public Match(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public Integer getScorePlayer1() {
        return scorePlayer1;
    }

    public void setScorePlayer1(Integer scorePlayer1) {
        this.scorePlayer1 = scorePlayer1;
    }

    public Integer getScorePlayer2() {
        return scorePlayer2;
    }

    public void setScorePlayer2(Integer scorePlayer2) {
        this.scorePlayer2 = scorePlayer2;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public MatchStatus getStato() {
        return stato;
    }

    public void setStato(MatchStatus stato) {
        this.stato = stato;
    }

    public String getScreenshotPlayer1() {
        return screenshotPlayer1;
    }

    public void setScreenshotPlayer1(String screenshotPlayer1) {
        this.screenshotPlayer1 = screenshotPlayer1;
    }

    public String getScreenshotPlayer2() {
        return screenshotPlayer2;
    }

    public void setScreenshotPlayer2(String screenshotPlayer2) {
        this.screenshotPlayer2 = screenshotPlayer2;
    }
}
