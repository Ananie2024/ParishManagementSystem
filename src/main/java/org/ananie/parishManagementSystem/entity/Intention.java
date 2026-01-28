package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.IntentionType;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "intentions")
public class Intention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Intention type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "intention_type", nullable = false)
    private IntentionType intentionType;

    @NotBlank(message = "Intention text is required")
    @Column(name = "intention_text", length = 1000, nullable = false)
    private String intentionText; // Complete, free-form text

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @Column(name = "offering_amount")
    private BigDecimal offeringAmount;

    @Column(name = "is_paid")
    private boolean isPaid = false;

    @Column(name = "is_fulfilled")
    private boolean isFulfilled = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mass_id")
    private Mass mass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faithful_id")
    private Faithful faithful;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_priest_id")
    private Priest requestedByPriest;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (requestedDate == null) {
            requestedDate = LocalDate.now();
        }
    }

    // Getters and setters...
}