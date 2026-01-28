package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import org.ananie.parishManagementSystem.utilities.LiturgicalSeason;
import org.ananie.parishManagementSystem.utilities.MassType;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MASS")

public class Mass extends Event {

    @Enumerated(EnumType.STRING)
    @Column(name = "mass_type", nullable = false)
    private MassType massType;

    @Enumerated(EnumType.STRING)
    @Column(name = "liturgical_season")
    private LiturgicalSeason liturgicalSeason;

    @Column(name = "readings")
    private String readings;

    // Main celebrant (required)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_celebrant_id")
    private Priest mainCelebrant;

    // Concelebrants (optional, multiple priests)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mass_concelebrants",
            joinColumns = @JoinColumn(name = "mass_id"),
            inverseJoinColumns = @JoinColumn(name = "priest_id")
    )
    private List<Priest> concelebrants = new ArrayList<>();
    @OneToMany(mappedBy = "mass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Intention> intentions = new ArrayList<>();
}
