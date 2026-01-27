package org.ananie.parishManagementSystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ====================================================================
 * DTO for updating an existing donation
 * All fields are optional to allow partial updates
 * ====================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationUpdateDTO {

    /**
     * Updated year (optional).
     */
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    /**
     * Updated amount (optional).
     */
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;

    /**
     * Updated date (optional).
     */
    @PastOrPresent(message = "Date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Updated contribution type (optional).
     */
    @Size(max = 50, message = "Contribution type must not exceed 50 characters")
    private String contributionType;

    /**
     * Updated payment method (optional).
     */
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;

    /**
     * Updated reference number (optional).
     */
    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    private String referenceNumber;

    /**
     * Updated notes (optional).
     */
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    /**
     * Updated recorder name (optional).
     */
    @Size(max = 100, message = "Recorder name must not exceed 100 characters")
    private String recordedBy;
}