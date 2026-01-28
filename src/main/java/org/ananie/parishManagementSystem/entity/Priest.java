package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.PriestType;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "priests")
public class Priest {
    @Id
    private Long id;

    @NotBlank(message = "name is required")
    @Column(name = "amazina")
    private String names;


    @Enumerated(EnumType.STRING)
    @Column(name = "priest_type", nullable = false)
    private PriestType priestType;

    @Column(name = "ordination_date")
    private LocalDate ordinationDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "parish_of_origin")
    private String parishOfOrign;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,}$", message = "Invalid phone number")
    private String phone;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
}
