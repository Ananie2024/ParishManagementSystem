package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "ministries")
public class Ministry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g., 'lector', 'catechist', 'choir_member'
    @Column(name = "ministry_type", nullable = false, length = 50)
    private String ministryType;

    // Many Ministries belong to One Faithful
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faithful_id", nullable = false)
    private Faithful faithful;
}