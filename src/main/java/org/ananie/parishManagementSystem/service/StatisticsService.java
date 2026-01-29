package org.ananie.parishManagementSystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ananie.parishManagementSystem.entity.*;
import org.ananie.parishManagementSystem.repository.*;
import org.ananie.parishManagementSystem.utilities.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Centralized service for all statistical operations in the parish management system.
 * Handles analytics for Masses, Intentions, Priests, Events, and provides dashboard metrics.
 *
 * This service consolidates all statistical methods to keep other service classes
 * focused on their core CRUD operations.
 *
 * @author Parish Management System
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final MassRepository massRepository;
    private final IntentionRepository intentionRepository;
    private final PriestRepository priestRepository;
    private final EventRepository eventRepository;

    // ============================================================================
    // MASS STATISTICS
    // ============================================================================

    /**
     * Gets comprehensive mass statistics for a given period.
     * Includes total masses, breakdown by type, and top celebrating priests.
     *
     * @param start the start date
     * @param end the end date
     * @return map containing various mass statistics
     */
    public Map<String, Object> getMassStatistics(LocalDate start, LocalDate end) {
        log.debug("Fetching mass statistics between {} and {}", start, end);

        Map<String, Object> stats = new HashMap<>();

        // Total masses in period
        List<Mass> masses = massRepository.findByEventDateBetween(start, end);
        stats.put("totalMasses", masses.size());

        // Masses by type
        List<Object[]> massesByType = massRepository.countMassesByTypeInPeriod(start, end);
        Map<String, Long> massTypeCounts = new HashMap<>();
        for (Object[] row : massesByType) {
            MassType type = (MassType) row[0];
            Long count = (Long) row[1];
            massTypeCounts.put(type.toString(), count);
        }
        stats.put("massesByType", massTypeCounts);

        // Top celebrating priests in the period
        List<Object[]> topPriests = massRepository.countMassesByPriestInPeriod(start, end);
        List<Map<String, Object>> priestStats = new ArrayList<>();
        for (Object[] row : topPriests) {
            Priest priest = (Priest) row[0];
            Long count = (Long) row[1];

            Map<String, Object> priestData = new HashMap<>();
            priestData.put("priestId", priest.getId());
            priestData.put("priestName", priest.getNames());
            priestData.put("priestType", priest.getPriestType().name());
            priestData.put("massCount", count);

            priestStats.add(priestData);
        }
        stats.put("topCelebratingPriests", priestStats);

        return stats;
    }

    /**
     * Counts the number of masses celebrated by a specific priest in a given period.
     * Useful for workload analysis and performance reviews.
     *
     * @param priestId the priest ID
     * @param start the start date
     * @param end the end date
     * @return count of masses celebrated
     */
    public Long countMassesByPriest(Long priestId, LocalDate start, LocalDate end) {
        log.debug("Counting masses for priest {} between {} and {}", priestId, start, end);
        return massRepository.countMassesByPriestInPeriod(priestId, start, end);
    }

    /**
     * Gets yearly mass counts grouped by year.
     * Useful for annual reports and long-term trend analysis.
     *
     * @return list of arrays containing [year, count]
     */
    public List<Object[]> getYearlyMassCounts() {
        log.debug("Fetching yearly mass counts");
        return massRepository.countMassesByYear();
    }

    /**
     * Gets mass type distribution for a given period.
     * Returns a map with mass type names as keys and counts as values.
     *
     * @param start the start date
     * @param end the end date
     * @return map of mass type to count
     */
    public Map<String, Long> getMassTypeDistribution(LocalDate start, LocalDate end) {
        log.debug("Fetching mass type distribution between {} and {}", start, end);

        List<Object[]> results = massRepository.countMassesByTypeInPeriod(start, end);
        Map<String, Long> distribution = new HashMap<>();

        for (Object[] result : results) {
            MassType type = (MassType) result[0];
            Long count = (Long) result[1];
            distribution.put(type.name(), count);
        }

        return distribution;
    }

    /**
     * Gets the top N celebrating priests across all time.
     *
     * @param limit the number of top priests to return
     * @return list of maps containing priest details and mass counts
     */
    public List<Map<String, Object>> getTopCelebratingPriests(int limit) {
        log.debug("Fetching top {} celebrating priests", limit);

        List<Object[]> allPriests = massRepository.findTopCelebratingPriests();
        List<Map<String, Object>> topPriests = new ArrayList<>();

        int count = Math.min(allPriests.size(), limit);
        for (int i = 0; i < count; i++) {
            Object[] result = allPriests.get(i);
            Long priestId = (Long) result[0];
            Long massCount = (Long) result[1];

            // Fetch priest details
            int finalI = i;
            priestRepository.findById(priestId).ifPresent(priest -> {
                Map<String, Object> priestData = new HashMap<>();
                priestData.put("rank", finalI + 1);
                priestData.put("priestId", priest.getId());
                priestData.put("priestName", priest.getNames());
                priestData.put("priestType", priest.getPriestType().name());
                priestData.put("email", priest.getEmail());
                priestData.put("massCount", massCount);

                topPriests.add(priestData);
            });
        }

        return topPriests;
    }

    /**
     * Gets all celebrating priests across all time with their mass counts.
     * Useful for comprehensive workload reports.
     *
     * @return list of maps containing priest details and mass counts
     */
    public List<Map<String, Object>> getAllCelebratingPriests() {
        log.debug("Fetching all celebrating priests with mass counts");

        List<Object[]> results = massRepository.findTopCelebratingPriests();
        return results.stream()
                .map(result -> {
                    Long priestId = (Long) result[0];
                    Long massCount = (Long) result[1];

                    return priestRepository.findById(priestId)
                            .map(priest -> {
                                Map<String, Object> data = new HashMap<>();
                                data.put("priestId", priest.getId());
                                data.put("priestName", priest.getNames());
                                data.put("priestType", priest.getPriestType().name());
                                data.put("massCount", massCount);
                                return data;
                            })
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ============================================================================
    // INTENTION STATISTICS
    // ============================================================================

    /**
     * Gets comprehensive intention statistics for a given period.
     * Includes total intentions, breakdown by type, and payment status.
     *
     * @param start the start date
     * @param end the end date
     * @return map containing various intention statistics
     */
    public Map<String, Object> getIntentionStatistics(LocalDate start, LocalDate end) {
        log.debug("Fetching intention statistics between {} and {}", start, end);

        Map<String, Object> stats = new HashMap<>();

        // Total intentions
        Long totalIntentions = intentionRepository.countIntentionsBetween(start, end);
        stats.put("totalIntentions", totalIntentions);

        // Intentions by type
        List<Object[]> intentionsByType = intentionRepository.countIntentionsByTypeInPeriod(start, end);
        Map<String, Long> intentionTypeCounts = new HashMap<>();
        for (Object[] row : intentionsByType) {
            IntentionType type = (IntentionType) row[0];
            Long count = (Long) row[1];
            intentionTypeCounts.put(type.toString(), count);
        }
        stats.put("intentionsByType", intentionTypeCounts);

        // Deceased intentions specifically
        Long deceasedIntentions = intentionRepository.countDeceasedIntentionsBetween(start, end);
        stats.put("deceasedIntentions", deceasedIntentions);

        // Payment statistics
        List<Intention> unpaidIntentions = intentionRepository.findByIsPaidFalse();
        stats.put("unpaidIntentionsCount", unpaidIntentions.size());

        // Calculate payment rate
        if (totalIntentions > 0) {
            double paymentRate = ((double) (totalIntentions - unpaidIntentions.size()) / totalIntentions) * 100;
            stats.put("paymentRate", String.format("%.2f%%", paymentRate));
        } else {
            stats.put("paymentRate", "N/A");
        }

        return stats;
    }

    /**
     * Gets intention count breakdown by type for a given period.
     *
     * @param start the start date
     * @param end the end date
     * @return map of intention type to count
     */
    public Map<String, Long> getIntentionCountByType(LocalDate start, LocalDate end) {
        log.debug("Fetching intention counts by type between {} and {}", start, end);

        List<Object[]> results = intentionRepository.countIntentionsByTypeInPeriod(start, end);
        Map<String, Long> counts = new HashMap<>();

        for (Object[] row : results) {
            IntentionType type = (IntentionType) row[0];
            Long count = (Long) row[1];
            counts.put(type.toString(), count);
        }

        return counts;
    }

    /**
     * Gets all unpaid intentions with detailed information.
     * Useful for financial tracking and follow-up.
     *
     * @return list of maps containing unpaid intention details
     */
    public List<Map<String, Object>> getUnpaidIntentionsDetails() {
        log.debug("Fetching unpaid intentions with details");

        List<Intention> unpaidIntentions = intentionRepository.findByIsPaidFalse();

        return unpaidIntentions.stream()
                .map(intention -> {
                    Map<String, Object> details = new HashMap<>();
                    details.put("intentionId", intention.getId());
                    details.put("intentionType", intention.getIntentionType().name());
                    details.put("intentionText", intention.getIntentionText());
                    details.put("requestedDate", intention.getRequestedDate());

                    // Requestor information
                    String requestorName = intention.getFaithful() != null
                            ? intention.getFaithful().getName()
                            : intention.getExternalFaithfulName();
                    details.put("requestorName", requestorName);

                    // Mass information if available
                    if (intention.getMass() != null) {
                        details.put("massId", intention.getMass().getId());
                        details.put("massDate", intention.getMass().getEventDate());
                    }

                    return details;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets deceased intentions count for a specific period.
     * Important for memorial services tracking.
     *
     * @param start the start date
     * @param end the end date
     * @return count of deceased intentions
     */
    public Long getDeceasedIntentionsCount(LocalDate start, LocalDate end) {
        log.debug("Counting deceased intentions between {} and {}", start, end);
        return intentionRepository.countDeceasedIntentionsBetween(start, end);
    }

    // ============================================================================
    // PRIEST STATISTICS
    // ============================================================================

    /**
     * Gets comprehensive priest statistics.
     * Includes total priests, breakdown by type, and activity metrics.
     *
     * @return map containing various priest statistics
     */
    public Map<String, Object> getPriestStatistics() {
        log.debug("Fetching priest statistics");

        Map<String, Object> stats = new HashMap<>();

        // Total priests
        Long totalPriests = priestRepository.count();
        stats.put("totalPriests", totalPriests);

        // Priests by type
        List<Object[]> priestsByType = priestRepository.countPriestsByType();
        Map<String, Long> priestTypeCounts = new HashMap<>();
        for (Object[] row : priestsByType) {
            PriestType type = (PriestType) row[0];
            Long count = (Long) row[1];
            priestTypeCounts.put(type.toString(), count);
        }
        stats.put("priestsByType", priestTypeCounts);

        // Active priests (currently assigned)
        long activePriests = priestRepository.findAll().stream()
                .filter(Priest::isAssigned)
                .count();
        stats.put("activePriests", activePriests);
        stats.put("inactivePriests", totalPriests - activePriests);

        // Priests celebrating masses this month
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        List<Priest> celebratingPriests = priestRepository.findPriestsCelebratingInPeriod(startOfMonth, endOfMonth);
        stats.put("priestsCelebratingThisMonth", celebratingPriests.size());

        return stats;
    }

    /**
     * Gets priests who are celebrating masses in a given period.
     * Useful for scheduling and workload distribution.
     *
     * @param start the start date
     * @param end the end date
     * @return list of maps containing priest details
     */
    public List<Map<String, Object>> getCelebratingPriestsInPeriod(LocalDate start, LocalDate end) {
        log.debug("Fetching celebrating priests between {} and {}", start, end);

        List<Priest> priests = priestRepository.findPriestsCelebratingInPeriod(start, end);

        return priests.stream()
                .map(priest -> {
                    Map<String, Object> priestData = new HashMap<>();
                    priestData.put("priestId", priest.getId());
                    priestData.put("priestName", priest.getNames());
                    priestData.put("priestType", priest.getPriestType().name());
                    priestData.put("email", priest.getEmail());
                    priestData.put("phone", priest.getPhone());

                    // Get mass count for this period
                    Long massCount = massRepository.countMassesByPriestInPeriod(
                            priest.getId(), start, end);
                    priestData.put("massCount", massCount);

                    return priestData;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets priest workload statistics for a given period.
     * Shows all priests with their mass counts, sorted by workload.
     *
     * @param start the start date
     * @param end the end date
     * @return list of maps containing priest details and workload
     */
    public List<Map<String, Object>> getPriestWorkloadStats(LocalDate start, LocalDate end) {
        log.debug("Fetching priest workload statistics between {} and {}", start, end);

        List<Object[]> results = massRepository.countMassesByPriestInPeriod(start, end);

        return results.stream()
                .map(result -> {
                    Priest priest = (Priest) result[0];
                    Long massCount = (Long) result[1];

                    Map<String, Object> priestStat = new HashMap<>();
                    priestStat.put("priestId", priest.getId());
                    priestStat.put("priestName", priest.getNames());
                    priestStat.put("priestType", priest.getPriestType().name());
                    priestStat.put("massCount", massCount);
                    priestStat.put("isAssigned", priest.isAssigned());

                    return priestStat;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets detailed breakdown of priest types with counts and percentages.
     *
     * @return map of priest type statistics
     */
    public Map<String, Object> getPriestTypeBreakdown() {
        log.debug("Fetching priest type breakdown");

        List<Object[]> priestsByType = priestRepository.countPriestsByType();
        Long totalPriests = priestRepository.count();

        Map<String, Object> breakdown = new HashMap<>();
        List<Map<String, Object>> typeDetails = new ArrayList<>();

        for (Object[] row : priestsByType) {
            PriestType type = (PriestType) row[0];
            Long count = (Long) row[1];

            Map<String, Object> typeData = new HashMap<>();
            typeData.put("priestType", type.name());
            typeData.put("count", count);

            if (totalPriests > 0) {
                double percentage = ((double) count / totalPriests) * 100;
                typeData.put("percentage", String.format("%.2f%%", percentage));
            }

            typeDetails.add(typeData);
        }

        breakdown.put("totalPriests", totalPriests);
        breakdown.put("typeBreakdown", typeDetails);

        return breakdown;
    }

    // ============================================================================
    // DASHBOARD & GENERAL STATISTICS
    // ============================================================================

    /**
     * Gets comprehensive dashboard statistics for a given period.
     * Provides a high-level overview of all parish activities.
     *
     * @param start the start date
     * @param end the end date
     * @return map containing dashboard metrics
     */
    public Map<String, Object> getDashboardStatistics(LocalDate start, LocalDate end) {
        log.debug("Fetching dashboard statistics between {} and {}", start, end);

        Map<String, Object> dashboard = new HashMap<>();

        // Core counts
        dashboard.put("totalMasses", massRepository.findByEventDateBetween(start, end).size());
        dashboard.put("totalIntentions", intentionRepository.countIntentionsBetween(start, end));
        dashboard.put("totalEvents", eventRepository.countEventsBetween(start, end));
        dashboard.put("totalPriests", priestRepository.count());

        // Intention details
        Long deceasedIntentions = intentionRepository.countDeceasedIntentionsBetween(start, end);
        dashboard.put("deceasedIntentions", deceasedIntentions);

        List<Intention> unpaid = intentionRepository.findByIsPaidFalse();
        dashboard.put("unpaidIntentions", unpaid.size());

        // Mass types breakdown
        List<Object[]> massTypes = massRepository.countMassesByTypeInPeriod(start, end);
        Map<String, Long> massTypeMap = new HashMap<>();
        for (Object[] row : massTypes) {
            MassType type = (MassType) row[0];
            Long count = (Long) row[1];
            massTypeMap.put(type.name(), count);
        }
        dashboard.put("massTypeBreakdown", massTypeMap);

        // Recent activity indicator
        LocalDate today = LocalDate.now();
        int massesToday = massRepository.findByEventDateBetween(today, today).size();
        dashboard.put("massesToday", massesToday);

        return dashboard;
    }

    /**
     * Gets period statistics for a specific year.
     * Provides monthly breakdowns for comprehensive annual reporting.
     *
     * @param year the year to analyze
     * @return map containing yearly statistics
     */
    public Map<String, Object> getPeriodStatistics(int year) {
        log.debug("Fetching period statistics for year {}", year);

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        Map<String, Object> yearlyStats = new HashMap<>();
        yearlyStats.put("year", year);

        // Monthly mass breakdowns
        List<Map<String, Object>> monthlyMasses = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            LocalDate monthStart = LocalDate.of(year, month, 1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            int massCount = massRepository.findByEventDateBetween(monthStart, monthEnd).size();

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("monthName", monthStart.getMonth().name());
            monthData.put("massCount", massCount);

            monthlyMasses.add(monthData);
        }
        yearlyStats.put("monthlyMasses", monthlyMasses);

        // Monthly intention offerings
        List<Object[]> monthlyOfferings = intentionRepository.getMonthlyOfferings(year);
        List<Map<String, Object>> offeringData = new ArrayList<>();

        for (Object[] row : monthlyOfferings) {
            Map<String, Object> data = new HashMap<>();
            data.put("month", row[0]);
            data.put("offeringCount", row[1]);
            offeringData.add(data);
        }
        yearlyStats.put("monthlyOfferings", offeringData);

        // Yearly totals
        yearlyStats.put("totalMassesForYear", massRepository.findByEventDateBetween(start, end).size());
        yearlyStats.put("totalIntentionsForYear", intentionRepository.countIntentionsBetween(start, end));

        return yearlyStats;
    }

    /**
     * Gets comparative statistics between two periods.
     * Useful for year-over-year or month-over-month comparisons.
     *
     * @param period1Start first period start date
     * @param period1End first period end date
     * @param period2Start second period start date
     * @param period2End second period end date
     * @return map containing comparative statistics
     */
    public Map<String, Object> getComparativeStatistics(
            LocalDate period1Start, LocalDate period1End,
            LocalDate period2Start, LocalDate period2End) {

        log.debug("Fetching comparative statistics");

        Map<String, Object> comparison = new HashMap<>();

        // Period 1 statistics
        Map<String, Object> period1Stats = new HashMap<>();
        period1Stats.put("startDate", period1Start);
        period1Stats.put("endDate", period1End);
        period1Stats.put("masses", massRepository.findByEventDateBetween(period1Start, period1End).size());
        period1Stats.put("intentions", intentionRepository.countIntentionsBetween(period1Start, period1End));

        // Period 2 statistics
        Map<String, Object> period2Stats = new HashMap<>();
        period2Stats.put("startDate", period2Start);
        period2Stats.put("endDate", period2End);
        period2Stats.put("masses", massRepository.findByEventDateBetween(period2Start, period2End).size());
        period2Stats.put("intentions", intentionRepository.countIntentionsBetween(period2Start, period2End));

        comparison.put("period1", period1Stats);
        comparison.put("period2", period2Stats);

        // Calculate changes
        int massDifference = (int) period2Stats.get("masses") - (int) period1Stats.get("masses");
        long intentionDifference = (long) period2Stats.get("intentions") - (long) period1Stats.get("intentions");

        Map<String, Object> changes = new HashMap<>();
        changes.put("massChange", massDifference);
        changes.put("intentionChange", intentionDifference);

        // Calculate percentage changes
        if ((int) period1Stats.get("masses") > 0) {
            double massPercentChange = ((double) massDifference / (int) period1Stats.get("masses")) * 100;
            changes.put("massPercentChange", String.format("%.2f%%", massPercentChange));
        }

        if ((long) period1Stats.get("intentions") > 0) {
            double intentionPercentChange = ((double) intentionDifference / (long) period1Stats.get("intentions")) * 100;
            changes.put("intentionPercentChange", String.format("%.2f%%", intentionPercentChange));
        }

        comparison.put("changes", changes);

        return comparison;
    }

    /**
     * Gets current month statistics summary.
     * Provides a quick overview of the current month's activities.
     *
     * @return map containing current month statistics
     */
    public Map<String, Object> getCurrentMonthSummary() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        log.debug("Fetching current month summary");

        return getDashboardStatistics(startOfMonth, endOfMonth);
    }

    /**
     * Gets statistics for the current week.
     * Useful for weekly reports and short-term planning.
     *
     * @return map containing current week statistics
     */
    public Map<String, Object> getCurrentWeekSummary() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        log.debug("Fetching current week summary");

        return getDashboardStatistics(startOfWeek, endOfWeek);
    }
}