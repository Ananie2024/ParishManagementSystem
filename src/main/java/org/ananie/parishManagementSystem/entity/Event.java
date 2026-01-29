package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name = "event_category", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter

public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "description is required")
    @Size(max = 100, message = "description must be less than 2000 characters")
    @Column(length = 2000)
    private String description;

    @Column(name="event_date")
    private LocalDate eventDate;

    @NotBlank( message = "location is required")
    private String location;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "event_category", insertable = false, updatable = false)
    private String eventCategory; // Read-only, managed by JPA

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "visibility")
    private boolean isPublic;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
