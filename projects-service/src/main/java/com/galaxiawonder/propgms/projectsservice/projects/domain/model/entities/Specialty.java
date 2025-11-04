package com.galaxiawonder.propgms.projectsservice.projects.domain.model.entities;


import com.galaxiawonder.propgms.projectsservice.projects.domain.model.valueobjects.Specialties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Specialties name;

    public Specialty(Specialties name) {
        this.name = name;
    }

    public static Specialty getDefaultSpecialty() {
        return new Specialty(Specialties.ARCHITECTURE);
    }

    public static Specialty toSpecialtyFromName(String name) {
        return new Specialty(Specialties.valueOf(name));
    }

    public static List<Specialty> validateSpecialtySet(List<Specialty> specialities) {
        return specialities == null || specialities.isEmpty()
                ? List.of(getDefaultSpecialty())
                : specialities;
    }

    public String getStringName() {
        return name.name();
    }
}