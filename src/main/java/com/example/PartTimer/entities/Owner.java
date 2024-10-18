package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("OWNER")
public class Owner extends User {

    @OneToOne(mappedBy = "owner")
    private Organization ownedOrganization;

//    // Methods specific to owners
//    public void assignCoOwner(CoOwner coOwner) {
//        if (this.ownedOrganization != null) {
//            this.ownedOrganization.addCoOwner(coOwner);
//        }
//    }
//
//    public void removeCoOwner(CoOwner coOwner) {
//        if (this.ownedOrganization != null) {
//            this.ownedOrganization.removeCoOwner(coOwner);
//        }
//    }

    @ManyToMany
    @JoinTable(
            name = "owner_assignments",
            joinColumns = @JoinColumn(name = "owner_id", referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(name = "assigned_owner_id", referencedColumnName = "userId")
    )
    private Set<Owner> assignedOwners = new HashSet<>(); //other owners that this owner can assign tasks to
}
