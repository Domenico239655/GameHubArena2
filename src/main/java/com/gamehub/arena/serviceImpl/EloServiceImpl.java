package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.EloHistoryRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.model.EloHistory;
import com.gamehub.arena.model.Match;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.EloService;
import org.springframework.stereotype.Service;

@Service
public class EloServiceImpl implements EloService {
    private final UserRepository userRepo;
    private final EloHistoryRepository historyRepo;

    public EloServiceImpl(UserRepository userRepo, EloHistoryRepository historyRepo){
        this.userRepo = userRepo;
        this.historyRepo = historyRepo;
    }

    @Override
    public void updateEloAfterMatch(Match match) {
        User p1 = match.getPlayer1();
        User p2 = match.getPlayer2();

        int elo1 = p1.getElo();
        int elo2 = p2.getElo();

        int score1 = match.getScorePlayer1();
        int score2 = match.getScorePlayer2();

        double S1 = score1 > score2 ? 1.0 : (score1 == score2 ? 0.5 : 0.0);
        double S2 = score2 > score1 ? 1.0 : (score2 == score1 ? 0.5 : 0.0);

        double E1 = 1.0 / (1 + Math.pow(10, (elo2 - elo1) / 400.0));
        double E2 = 1.0 / (1 + Math.pow(10, (elo1 - elo2) / 400.0));

        int k = 32;

        int newElo1 = (int) Math.round(elo1 + k * (S1 - E1));
        int newElo2 = (int) Math.round(elo2 + k * (S2 - E2));

        EloHistory h1 = new EloHistory();
        h1.setUser(p1);
        h1.setOldElo(elo1);
        h1.setNewElo(newElo1);
        h1.setMatchId(match.getId());
        historyRepo.save(h1);

        EloHistory h2 = new EloHistory();
        h2.setUser(p2);
        h2.setOldElo(elo2);
        h2.setNewElo(newElo2);
        h2.setMatchId(match.getId());
        historyRepo.save(h2);

        p1.setElo(newElo1);
        p2.setElo(newElo2);

        userRepo.save(p1);
        userRepo.save(p2);

    }
}
