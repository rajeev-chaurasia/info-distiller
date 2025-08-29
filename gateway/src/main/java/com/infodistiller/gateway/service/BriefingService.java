package com.infodistiller.gateway.service;

import com.infodistiller.gateway.entity.Selection;
import com.infodistiller.gateway.repository.SelectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class BriefingService {

    private final SelectionRepository selectionRepository;

    @Autowired
    public BriefingService(SelectionRepository selectionRepository) {
        this.selectionRepository = selectionRepository;
    }

    public List<Selection> getTodaysBriefing(Integer userId) {
        LocalDate today = LocalDate.now();
        return selectionRepository.findByUserIdAndPickedForDate(userId, today);
    }
}