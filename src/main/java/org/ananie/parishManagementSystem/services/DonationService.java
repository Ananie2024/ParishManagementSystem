package org.ananie.parishManagementSystem.services;

import org.ananie.parishManagementSystem.dto.*;
import org.ananie.parishManagementSystem.entity.Donation;
import org.ananie.parishManagementSystem.entity.Faithful;
import org.ananie.parishManagementSystem.repository.DonationRepository;
import org.ananie.parishManagementSystem.repository.FaithfulRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing donations/contributions.
 * Handles business logic, validation, and DTO-Entity conversions.
 */
@Service
@Transactional
public class DonationService {

    private final DonationRepository donationRepository;
    private final FaithfulRepository faithfulRepository;

    @Autowired
    public DonationService(DonationRepository donationRepository, FaithfulRepository faithfulRepository) {
        this.donationRepository = donationRepository;
        this.faithfulRepository = faithfulRepository;
    }

    // ==================== CREATE ====================
    /**
     * Create a new donation record.
     * Validates that the faithful exists before creating.
     */
    public DonationResponseDTO createDonation(DonationRequestDTO requestDTO) {
        // Validate faithful exists
        Faithful faithful = faithfulRepository.findById(requestDTO.getFaithfulId())
                .orElseThrow(() -> new IllegalArgumentException("Umukristu ntabwo abonetse (ID: " + requestDTO.getFaithfulId() + ")"));

        // Convert DTO to Entity
        Donation donation = new Donation();
        donation.setFaithful(faithful);
        donation.setYear(requestDTO.getYear());
        donation.setAmount(requestDTO.getAmount());
        donation.setDate(requestDTO.getDate());
        donation.setContributionType(requestDTO.getContributionType());
        donation.setPaymentMethod(requestDTO.getPaymentMethod());
        donation.setReferenceNumber(requestDTO.getReferenceNumber());
        donation.setNotes(requestDTO.getNotes());
        donation.setRecordedBy(requestDTO.getRecordedBy());

        // Save and return
        Donation savedDonation = donationRepository.save(donation);
        return convertToResponseDTO(savedDonation);
    }

    // ==================== READ ====================
    /**
     * Get donation by ID.
     */
    public DonationResponseDTO getDonationById(Long id) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ituro ntiribonetse (ID: " + id + ")"));
        return convertToResponseDTO(donation);
    }

    /**
     * Get all donations (use with caution - can be large dataset).
     */
    public List<DonationResponseDTO> getAllDonations() {
        return donationRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all donations for a specific faithful member.
     */
    public List<DonationResponseDTO> getDonationsByFaithful(Long faithfulId) {
        return donationRepository.findByFaithfulIdOrderByDateDesc(faithfulId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get donations for a specific year.
     */
    public List<DonationResponseDTO> getDonationsByYear(Integer year) {
        return donationRepository.findByYear(year).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get donations within a date range.
     */
    public List<DonationResponseDTO> getDonationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return donationRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get donations by contribution type.
     */
    public List<DonationResponseDTO> getDonationsByType(String contributionType) {
        return donationRepository.findByContributionType(contributionType).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== UPDATE ====================
    /**
     * Update an existing donation.
     * Only updates non-null fields from the UpdateDTO.
     */
    public DonationResponseDTO updateDonation(Long id, DonationUpdateDTO updateDTO) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ituro ntiribonetse (ID: " + id + ")"));

        // Update only non-null fields
        if (updateDTO.getYear() != null) {
            donation.setYear(updateDTO.getYear());
        }
        if (updateDTO.getAmount() != null) {
            donation.setAmount(updateDTO.getAmount());
        }
        if (updateDTO.getDate() != null) {
            donation.setDate(updateDTO.getDate());
        }
        if (updateDTO.getContributionType() != null) {
            donation.setContributionType(updateDTO.getContributionType());
        }
        if (updateDTO.getPaymentMethod() != null) {
            donation.setPaymentMethod(updateDTO.getPaymentMethod());
        }
        if (updateDTO.getReferenceNumber() != null) {
            donation.setReferenceNumber(updateDTO.getReferenceNumber());
        }
        if (updateDTO.getNotes() != null) {
            donation.setNotes(updateDTO.getNotes());
        }
        if (updateDTO.getRecordedBy() != null) {
            donation.setRecordedBy(updateDTO.getRecordedBy());
        }

        Donation updatedDonation = donationRepository.save(donation);
        return convertToResponseDTO(updatedDonation);
    }

    // ==================== DELETE ====================
    /**
     * Delete a donation by ID.
     */
    public void deleteDonation(Long id) {
        if (!donationRepository.existsById(id)) {
            throw new IllegalArgumentException("Ituro ntiribonetse (ID: " + id + ")");
        }
        donationRepository.deleteById(id);
    }

    /**
     * Delete all donations for a specific faithful (used when deleting a faithful).
     */
    public void deleteAllDonationsByFaithful(Long faithfulId) {
        List<Donation> donations = donationRepository.findByFaithfulId(faithfulId);
        donationRepository.deleteAll(donations);
    }

    // ==================== SUMMARY & STATISTICS ====================
    /**
     * Get total donations for a faithful member.
     */
    public BigDecimal getTotalDonationsByFaithful(Long faithfulId) {
        BigDecimal total = donationRepository.getTotalDonationsByFaithful(faithfulId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total donations for a specific year (overall parish total).
     * Equivalent to getTotalContributions(year) from JavaFX app.
     */
    public BigDecimal getTotalDonationsByYear(Integer year) {
        BigDecimal total = donationRepository.getTotalDonationsByYear(year);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total donations for all years combined.
     */
    public BigDecimal getTotalAllDonations() {
        return donationRepository.findAll().stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total donations within a date range.
     */
    public BigDecimal getTotalDonationsByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = donationRepository.getTotalDonationsByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get donation summary (totals, averages, counts) for a specific period.
     */
    public DonationSummaryDTO getDonationSummary(LocalDate startDate, LocalDate endDate, String period) {
        List<Donation> donations = donationRepository.findByDateBetween(startDate, endDate);

        if (donations.isEmpty()) {
            return new DonationSummaryDTO(BigDecimal.ZERO, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, period);
        }

        BigDecimal total = donations.stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long count = (long) donations.size();

        BigDecimal average = total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        BigDecimal max = donations.stream()
                .map(Donation::getAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal min = donations.stream()
                .map(Donation::getAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new DonationSummaryDTO(total, count, average, max, min, period);
    }

    /**
     * Get available years that have donation records (for year filter dropdown).
     */
    public List<Integer> getAvailableYears() {
        return donationRepository.findAll().stream()
                .map(Donation::getYear)
                .distinct()
                .sorted(Comparator.reverseOrder()) // Most recent first
                .collect(Collectors.toList());
    }

    /**
     * Get donations grouped by contribution type for a specific year.
     * Returns Map of type -> total amount.
     */
    public Map<String, BigDecimal> getTotalsByContributionType(Integer year) {
        List<Object[]> results = donationRepository.getTotalByContributionTypeAndYear(year);

        Map<String, BigDecimal> totals = new HashMap<>();
        for (Object[] result : results) {
            String type = (String) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            totals.put(type != null ? type : "Unspecified", amount);
        }
        return totals;
    }

    /**
     * Get monthly donation summary for a specific year.
     * Returns Map of month (1-12) -> total amount.
     */
    public Map<Integer, BigDecimal> getMonthlyDonationSummary(Integer year) {
        List<Object[]> results = donationRepository.getMonthlyDonationSummary(year);

        Map<Integer, BigDecimal> monthlySummary = new HashMap<>();
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            monthlySummary.put(month, amount);
        }
        return monthlySummary;
    }

    /**
     * Get top donors for a specific year.
     * Returns list of faithful IDs with their total donations.
     */
    public List<Map<String, Object>> getTopDonorsByYear(Integer year, int limit) {
        List<Object[]> results = donationRepository.getTopDonorsByYear(year);

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Faithful faithful = (Faithful) result[0];
                    BigDecimal total = (BigDecimal) result[1];

                    Map<String, Object> donor = new HashMap<>();
                    donor.put("faithfulId", faithful.getId());
                    donor.put("faithfulName", faithful.getName());
                    donor.put("totalAmount", total);
                    return donor;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get donations totals grouped by SubParish for a specific year.
     * Returns Map of SubParish name -> total amount.
     */
    public Map<String, BigDecimal> getTotalsBySubParish(Integer year) {
        List<Donation> donations;
        if (year != null) {
            donations = donationRepository.findByYear(year);
        } else {
            donations = donationRepository.findAll();
        }

        Map<String, BigDecimal> totals = new HashMap<>();
        for (Donation donation : donations) {
            Faithful faithful = donation.getFaithful();
            if (faithful != null && faithful.getSubparish() != null) {
                String subparish = faithful.getSubparish();
                totals.merge(subparish, donation.getAmount(), BigDecimal::add);
            }
        }
        return totals;
    }

    /**
     * Get donations totals grouped by BEC for a specific SubParish and year.
     * Returns Map of BEC name -> total amount.
     */
    public Map<String, BigDecimal> getTotalsByBecInSubParish(String subParishName, Integer year) {
        List<Donation> donations;
        if (year != null) {
            donations = donationRepository.findByYear(year);
        } else {
            donations = donationRepository.findAll();
        }

        Map<String, BigDecimal> totals = new HashMap<>();
        for (Donation donation : donations) {
            Faithful faithful = donation.getFaithful();
            if (faithful != null &&
                    subParishName.equals(faithful.getSubparish()) &&
                    faithful.getBasicEcclesialCommunity() != null) {

                String bec = faithful.getBasicEcclesialCommunity();
                totals.merge(bec, donation.getAmount(), BigDecimal::add);
            }
        }
        return totals;
    }

    // ==================== DTO CONVERSION ====================
    /**
     * Convert Donation entity to DonationResponseDTO.
     */
    private DonationResponseDTO convertToResponseDTO(Donation donation) {
        DonationResponseDTO dto = new DonationResponseDTO();
        dto.setId(donation.getId());
        dto.setFaithfulId(donation.getFaithful().getId());
        dto.setFaithfulName(donation.getFaithful().getName()); // Include name for easy display
        dto.setYear(donation.getYear());
        dto.setAmount(donation.getAmount());
        dto.setDate(donation.getDate());
        dto.setContributionType(donation.getContributionType());
        dto.setPaymentMethod(donation.getPaymentMethod());
        dto.setReferenceNumber(donation.getReferenceNumber());
        dto.setNotes(donation.getNotes());
        dto.setRecordedBy(donation.getRecordedBy());
        dto.setCreatedAt(donation.getCreatedAt());
        dto.setUpdatedAt(donation.getUpdatedAt());
        return dto;
    }
}