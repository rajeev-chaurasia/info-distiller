package com.infodistiller.gateway.service;

import com.infodistiller.gateway.entity.Selection;
import com.infodistiller.gateway.repository.SelectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BriefingService {

    private final SelectionRepository selectionRepository;

    @Autowired
    public BriefingService(SelectionRepository selectionRepository) {
        this.selectionRepository = selectionRepository;
    }

    public Map<String, List<Selection>> getTodaysBriefing(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<LocalDate> dateRange = List.of(today, yesterday);

        List<Object[]> results = selectionRepository.findSelectionsWithInterestNameByDateRange(userId, dateRange);

        if (results.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // Map the raw results into Selection objects with the interestName set
        List<Selection> selectionsWithNames = results.stream().map(result -> {
            Selection selection = (Selection) result[0];
            String interestName = (String) result[1];
            selection.setInterestName(interestName);
            return selection;
        }).toList();

        // Determine the date of the most recent briefing found
        LocalDate mostRecentDate = selectionsWithNames.get(0).getPickedForDate();

        // Filter for the most recent date and group by interest name
        return selectionsWithNames.stream()
                .filter(s -> s.getPickedForDate().equals(mostRecentDate))
                .collect(Collectors.groupingBy(Selection::getInterestName, LinkedHashMap::new, Collectors.toList()));
    }
}