package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.ReportRepository;
import com.gamehub.arena.model.Report;
import com.gamehub.arena.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository repo;

    public ReportServiceImpl(ReportRepository repo){
        this.repo = repo;
    }

    @Override
    public Report create(Report r) {
        r.setDate(new Date());
        return repo.save(r);
    }
}
