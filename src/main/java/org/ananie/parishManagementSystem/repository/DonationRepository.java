package org.ananie.parishManagementSystem.repository;

import org.ananie.parishManagementSystem.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing Donation entity persistence operations.
 * Provides CRUD operations and custom queries for donation tracking.
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    // --- FIND BY FAITHFUL ---
    /**
     * Retrieve all donations made by a specific faithful member.
     */
    List<Donation> findByFaithfulId(Long faithfulId);

    /**
     * Find donations by faithful, ordered by date (newest first).
     */
    List<Donation> findByFaithfulIdOrderByDateDesc(Long faithfulId);

    // --- FIND BY YEAR ---
    /**
     * Get all donations for a specific year (useful for annual reports).
     */
    List<Donation> findByYear(Integer year);

    /**
     * Get donations by a specific faithful for a specific year.
     */
    List<Donation> findByFaithfulIdAndYear(Long faithfulId, Integer year);

    // --- FIND BY DATE RANGE ---
    /**
     * Retrieve donations made within a date range (for periodic reporting).
     */
    List<Donation> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Get a specific faithful's donations within a date range.
     */
    List<Donation> findByFaithfulIdAndDateBetween(Long faithfulId, LocalDate startDate, LocalDate endDate);

    // --- FIND BY CONTRIBUTION TYPE ---
    /**
     * Filter donations by type (TITHE, OFFERING, BUILDING_FUND, etc.).
     */
    List<Donation> findByContributionType(String contributionType);

    /**
     * Get donations of a specific type within a date range.
     */
    List<Donation> findByContributionTypeAndDateBetween(String contributionType, LocalDate startDate, LocalDate endDate);

    // --- FIND BY PAYMENT METHOD ---
    /**
     * Filter donations by payment method (CASH, MOBILE_MONEY, etc.).
     */
    List<Donation> findByPaymentMethod(String paymentMethod);

    // --- CUSTOM AGGREGATION QUERIES ---

    /**
     * Calculate total donations for a specific faithful member.
     */
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.faithful.id = :faithfulId")
    BigDecimal getTotalDonationsByFaithful(@Param("faithfulId") Long faithfulId);

    /**
     * Calculate total donations for a specific year.
     */
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.year = :year")
    BigDecimal getTotalDonationsByYear(@Param("year") Integer year);

    /**
     * Calculate total donations within a date range.
     */
    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalDonationsByDateRange(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    /**
     * Get total donations by contribution type for a specific year.
     */
    @Query("SELECT d.contributionType, SUM(d.amount) FROM Donation d WHERE d.year = :year GROUP BY d.contributionType")
    List<Object[]> getTotalByContributionTypeAndYear(@Param("year") Integer year);

    /**
     * Get top donors for a specific year (ordered by total amount).
     */
    @Query("SELECT d.faithful, SUM(d.amount) as total FROM Donation d WHERE d.year = :year GROUP BY d.faithful ORDER BY total DESC")
    List<Object[]> getTopDonorsByYear(@Param("year") Integer year);

    /**
     * Count total number of donations by a faithful member.
     */
    @Query("SELECT COUNT(d) FROM Donation d WHERE d.faithful.id = :faithfulId")
    Long countDonationsByFaithful(@Param("faithfulId") Long faithfulId);

    /**
     * Get monthly donation summary for a specific year.
     * Returns array of [month, total_amount].
     */
    @Query("SELECT MONTH(d.date), SUM(d.amount) FROM Donation d WHERE d.year = :year GROUP BY MONTH(d.date) ORDER BY MONTH(d.date)")
    List<Object[]> getMonthlyDonationSummary(@Param("year") Integer year);

    /**
     * Find donations above a certain amount (for large donations tracking).
     */
    List<Donation> findByAmountGreaterThanEqual(BigDecimal minAmount);

    /**
     * Search donations by reference number (for payment tracking).
     */
    List<Donation> findByReferenceNumberContainingIgnoreCase(String referenceNumber);
}