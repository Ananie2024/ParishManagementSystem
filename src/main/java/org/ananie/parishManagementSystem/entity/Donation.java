package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing financial contributions/donations made by parish members.
 * Tracks tithes, offerings, and other monetary donations.
 */
@Data
@Getter
@Setter
@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELATIONSHIP TO FAITHFUL ---
    /**
     * Links contribution to the faithful member who made it.
     * EAGER fetch ensures faithful info is always loaded with contribution.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faithful_id", nullable = false)
    private Faithful faithful;

    // --- CONTRIBUTION DETAILS ---
    /**
     * Year for which the contribution is designated (e.g., 2024 annual tithe).
     */
    @Column(name = "year", nullable = false)
    private Integer year;

    /**
     * Amount contributed in local currency.
     * BigDecimal ensures precise financial calculations.
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Date when the contribution was made or received.
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // --- CONTRIBUTION CATEGORY ---
    /**
     * Type of contribution: TITHE, OFFERING, SPECIAL_COLLECTION, BUILDING_FUND, etc.
     * Helps categorize donations for reporting.
     */
    @Column(name = "contribution_type", length = 50)
    private String contributionType;

    /**
     * Payment method used: CASH, MOBILE_MONEY, BANK_TRANSFER, CHECK, etc.
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Reference number for electronic payments or check numbers.
     */
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    // --- ADDITIONAL INFORMATION ---
    /**
     * Optional notes about the contribution (purpose, special intentions, etc.).
     */
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * Name of the person who recorded this contribution (for accountability).
     */
    @Column(name = "recorded_by", length = 100)
    private String recordedBy;

    // --- METADATA ---
    /**
     * Timestamp when the record was created in the system.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Automatically sets creation timestamp when a new contribution is saved.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Automatically updates the timestamp whenever the contribution is modified.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}