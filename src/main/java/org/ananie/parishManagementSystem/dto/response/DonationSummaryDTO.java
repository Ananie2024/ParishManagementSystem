package org.ananie.parishManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ====================================================================
 * DTO for donation summary statistics
 * Used for reports and dashboards
 * ====================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationSummaryDTO {

    /**
     * Total amount of donations.
     */
    private BigDecimal totalAmount;

    /**
     * Total number of donation transactions.
     */
    private Long donationCount;

    /**
     * Average donation amount.
     */
    private BigDecimal averageAmount;

    /**
     * Largest single donation amount.
     */
    private BigDecimal maxAmount;

    /**
     * Smallest single donation amount.
     */
    private BigDecimal minAmount;

    /**
     * Time period for this summary (e.g., "2024", "January 2024", "Q1 2024").
     */
    private String period;
}