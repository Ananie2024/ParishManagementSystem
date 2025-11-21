package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Entity
@Table(name = "lapse_events")
public class LapseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g., 'irregular_union', 'divorced_remarried', 'schism'
    @Column(name = "lapse_type", nullable = false, length = 50)
    private String lapseType;

    @Column(name = "lapse_date")
    private LocalDate lapseDate; // Date the lapse/irregularity began (if known)

    @Column(name = "lapse_reason", length = 500)
    private String lapseReason; // Detailed reason/text from textarea

    @Column(name = "return_date")
    private LocalDate returnDate; // Date of reconciliation/return to full communion

    // Many Lapse Events belong to One Faithful
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faithful_id", nullable = false)
    private Faithful faithful;
}