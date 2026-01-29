package org.ananie.parishManagementSystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ====================================================================
 * DTO for returning donation data (Response to client)
 * Includes all donation details plus faithful member information
 * ====================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationResponseDTO {

    /**
     * Unique identifier of the donation record.
     */
    private Long id;

    /**
     * ID of the faithful member who made the donation.
     */
    private Long faithfulId;

    /**
     * Name of the faithful member (for easy display without additional queries).
     */
    private String faithfulName;

    /**
     * Year for which the donation is designated.
     */
    private Integer year;

    /**
     * Donation amount.
     */
    private BigDecimal amount;

    /**
     * Date when the donation was made.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Type of contribution.
     */
    private String contributionType;

    /**
     * Payment method used.
     */
    private String paymentMethod;

    /**
     * Reference number for tracking.
     */
    private String referenceNumber;

    /**
     * Additional notes.
     */
    private String notes;

    /**
     * Name of person who recorded the donation.
     */
    private String recordedBy;

    /**
     * Timestamp when the record was created.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}