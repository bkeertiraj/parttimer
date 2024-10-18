package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CO_OWNER")
public class CoOwner extends User {
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column
    private LocalDate joinDate;

    @Column
    private String responsibilities;

    // Methods specific to co-owners
    public void updateResponsibilities(String newResponsibilities) {
        this.responsibilities = newResponsibilities;
    }
}
