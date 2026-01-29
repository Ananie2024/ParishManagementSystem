package org.ananie.parishManagementSystem.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ====================================================================
 * DTO for creating a new donation (Request from client)
 * ====================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationRequestDTO {

    /**
     * ID of the faithful member making the donation (required).
     */
    @NotNull(message = "Faithful ID is required")
    @Positive(message = "Faithful ID must be a positive number")
    private Long faithfulId;

    /**
     * Year for which the donation is designated (required).
     */
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    private Integer year;

    /**
     * Amount of the donation (required, must be positive).
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;

    /**
     * Date when the donation was made (required).
     */
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Type of donation (optional but recommended).
     * Examples: TITHE, OFFERING, SPECIAL_COLLECTION, BUILDING_FUND
     */
    @Size(max = 50, message = "Contribution type must not exceed 50 characters")
    private String contributionType;

    /**
     * Payment method (optional).
     * Examples: CASH, MOBILE_MONEY, BANK_TRANSFER, CHECK
     */
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;

    /**
     * Reference number for electronic payments or checks (optional).
     */
    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    private String referenceNumber;

    /**
     * Additional notes about the donation (optional).
     */
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    /**
     * Name of the person recording this donation (optional).
     */
    @Size(max = 100, message = "Recorder name must not exceed 100 characters")
    private String recordedBy;
}
