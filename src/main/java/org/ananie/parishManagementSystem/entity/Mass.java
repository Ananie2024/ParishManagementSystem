package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import org.ananie.parishManagementSystem.utilities.LiturgicalSeason;
import org.ananie.parishManagementSystem.utilities.MassType;

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


}
