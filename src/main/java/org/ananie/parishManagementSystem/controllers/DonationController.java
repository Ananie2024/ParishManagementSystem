package org.ananie.parishManagementSystem.controllers;

import jakarta.validation.Valid;
import org.ananie.parishManagementSystem.dto.*;
import org.ananie.parishManagementSystem.services.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for managing donations/contributions.
 * Provides endpoints for CRUD operations and statistical reports.
 */
@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*") // Adjust for production
public class DonationController {

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    // ==================== CREATE ====================
    /**
     * Create a new donation.
     * POST /api/donations
     */
    @PostMapping
    public ResponseEntity<?> createDonation(@Valid @RequestBody DonationRequestDTO requestDTO,
                                            BindingResult bindingResult) {
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            DonationResponseDTO created = donationService.createDonation(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ikosa ritunguranye: " + e.getMessage()));
        }
    }
    // ==================== READ ====================
    /**
     * Get donation by ID.
     * GET /api/donations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable Long id) {
        try {
            DonationResponseDTO donation = donationService.getDonationById(id);
            return ResponseEntity.ok(donation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all donations (with optional filters).
     * GET /api/donations?faithfulId=1&year=2024&type=TITHE
     */
    @GetMapping
    public ResponseEntity<List<DonationResponseDTO>> getAllDonations(
            @RequestParam(required = false) Long faithfulId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String contributionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DonationResponseDTO> donations;

        // Apply filters based on query parameters
        if (faithfulId != null) {
            donations = donationService.getDonationsByFaithful(faithfulId);
        } else if (year != null) {
            donations = donationService.getDonationsByYear(year);
        } else if (startDate != null && endDate != null) {
            donations = donationService.getDonationsByDateRange(startDate, endDate);
        } else if (contributionType != null) {
            donations = donationService.getDonationsByType(contributionType);
        } else {
            donations = donationService.getAllDonations();
        }

        return ResponseEntity.ok(donations);
    }

    /**
     * Get all donations for a specific faithful member.
     * GET /api/donations/faithful/{faithfulId}
     */
    @GetMapping("/faithful/{faithfulId}")
    public ResponseEntity<List<DonationResponseDTO>> getDonationsByFaithful(@PathVariable Long faithfulId) {
        List<DonationResponseDTO> donations = donationService.getDonationsByFaithful(faithfulId);
        return ResponseEntity.ok(donations);
    }

    /**
     * Get donations by year.
     * GET /api/donations/year/{year}
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<DonationResponseDTO>> getDonationsByYear(@PathVariable Integer year) {
        List<DonationResponseDTO> donations = donationService.getDonationsByYear(year);
        return ResponseEntity.ok(donations);
    }

    // ==================== UPDATE ====================
    /**
     * Update an existing donation.
     * PUT /api/donations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonation(@PathVariable Long id,
                                            @Valid @RequestBody DonationUpdateDTO updateDTO,
                                            BindingResult bindingResult) {
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            DonationResponseDTO updated = donationService.updateDonation(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ikosa ritunguranye: " + e.getMessage()));
        }
    }

    // ==================== DELETE ====================
    /**
     * Delete a donation by ID.
     * DELETE /api/donations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonation(@PathVariable Long id) {
        try {
            donationService.deleteDonation(id);
            return ResponseEntity.ok(Map.of("message", "Ituro ryasibwe neza"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ikosa ritunguranye: " + e.getMessage()));
        }
    }

    // ==================== STATISTICS & SUMMARIES ====================
    /**
     * Get total donations for a faithful member.
     * GET /api/donations/statistics/faithful/{faithfulId}/total
     */
    @GetMapping("/statistics/faithful/{faithfulId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalByFaithful(@PathVariable Long faithfulId) {
        BigDecimal total = donationService.getTotalDonationsByFaithful(faithfulId);
        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * Get total donations for a specific year (overall parish).
     * GET /api/donations/statistics/year/{year}/total
     */
    @GetMapping("/statistics/year/{year}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalByYear(@PathVariable Integer year) {
        BigDecimal total = donationService.getTotalDonationsByYear(year);
        return ResponseEntity.ok(Map.of("total", total, "year", BigDecimal.valueOf(year)));
    }

    /**
     * Get total donations for all years combined.
     * GET /api/donations/statistics/total
     */
    @GetMapping("/statistics/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalAllDonations() {
        BigDecimal total = donationService.getTotalAllDonations();
        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * Get donation summary for a date range.
     * GET /api/donations/statistics/summary?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/statistics/summary")
    public ResponseEntity<DonationSummaryDTO> getDonationSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String period) {

        if (period == null) {
            period = startDate.getYear() + " - " + endDate.getYear();
        }

        DonationSummaryDTO summary = donationService.getDonationSummary(startDate, endDate, period);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get available years that have donation records (for filters).
     * GET /api/donations/statistics/available-years
     */
    @GetMapping("/statistics/available-years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        List<Integer> years = donationService.getAvailableYears();
        return ResponseEntity.ok(years);
    }

    /**
     * Get donations grouped by contribution type for a year.
     * GET /api/donations/statistics/year/{year}/by-type
     */
    @GetMapping("/statistics/year/{year}/by-type")
    public ResponseEntity<Map<String, BigDecimal>> getTotalsByType(@PathVariable Integer year) {
        Map<String, BigDecimal> totals = donationService.getTotalsByContributionType(year);
        return ResponseEntity.ok(totals);
    }

    /**
     * Get monthly donation summary for a specific year.
     * GET /api/donations/statistics/year/{year}/monthly
     */
    @GetMapping("/statistics/year/{year}/monthly")
    public ResponseEntity<Map<Integer, BigDecimal>> getMonthlyDonations(@PathVariable Integer year) {
        Map<Integer, BigDecimal> monthlySummary = donationService.getMonthlyDonationSummary(year);
        return ResponseEntity.ok(monthlySummary);
    }

    /**
     * Get top donors for a specific year.
     * GET /api/donations/statistics/year/{year}/top-donors?limit=10
     */
    @GetMapping("/statistics/year/{year}/top-donors")
    public ResponseEntity<List<Map<String, Object>>> getTopDonors(
            @PathVariable Integer year,
            @RequestParam(defaultValue = "10") int limit) {

        List<Map<String, Object>> topDonors = donationService.getTopDonorsByYear(year, limit);
        return ResponseEntity.ok(topDonors);
    }

    /**
     * Get donations totals grouped by SubParish.
     * GET /api/donations/statistics/by-subparish?year=2024
     */
    @GetMapping("/statistics/by-subparish")
    public ResponseEntity<Map<String, BigDecimal>> getTotalsBySubParish(
            @RequestParam(required = false) Integer year) {

        Map<String, BigDecimal> totals = donationService.getTotalsBySubParish(year);
        return ResponseEntity.ok(totals);
    }

    /**
     * Get donations totals grouped by BEC for a specific SubParish.
     * GET /api/donations/statistics/by-bec?subParish=Remera&year=2024
     */
    @GetMapping("/statistics/by-bec")
    public ResponseEntity<Map<String, BigDecimal>> getTotalsByBec(
            @RequestParam String subParish,
            @RequestParam(required = false) Integer year) {

        Map<String, BigDecimal> totals = donationService.getTotalsByBecInSubParish(subParish, year);
        return ResponseEntity.ok(totals);
    }
}