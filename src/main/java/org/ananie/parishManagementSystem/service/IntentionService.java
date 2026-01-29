package org.ananie.parishManagementSystem.service;

import org.ananie.parishManagementSystem.entity.Intention;
import org.ananie.parishManagementSystem.utilities.IntentionType;
import org.ananie.parishManagementSystem.repository.IntentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntentionService {

    @Autowired
    private IntentionRepository intentionRepository;

    public Intention saveIntention(Intention intention) {
        return intentionRepository.save(intention);
    }

    public List<Intention> getIntentionsByPeriod(LocalDate start, LocalDate end) {
        return intentionRepository.findByRequestedDateBetween(start, end);
    }

    public List<Intention> getDeceasedIntentions(LocalDate start, LocalDate end) {
        List<Intention> allIntentions = intentionRepository.findByRequestedDateBetween(start, end);
        return allIntentions.stream()
                .filter(i -> i.getIntentionType() == IntentionType.DECEASED)
                .collect(java.util.stream.Collectors.toList());
    }

    public Long countDeceasedIntentions(LocalDate start, LocalDate end) {
        return intentionRepository.countDeceasedIntentionsBetween(start, end);
    }

    public Map<String, Long> getIntentionTypeCounts(LocalDate start, LocalDate end) {
        List<Object[]> results = intentionRepository.countIntentionsByTypeInPeriod(start, end);
        Map<String, Long> counts = new HashMap<>();

        for (Object[] row : results) {
            IntentionType type = (IntentionType) row[0];
            Long count = (Long) row[1];
            counts.put(type.toString(), count);
        }

        return counts;
    }
}