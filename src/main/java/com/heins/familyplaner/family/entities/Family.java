package com.heins.familyplaner.family.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="families")
public class Family {
    public Family(String name) {
        this.name = name;
    }

    public Family() { }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<FamilyMember> familyMembers = new ArrayList<>();

    public void addPerson(FamilyMember familyMember) {
        familyMembers.add(familyMember);
        familyMember.setFamily(this);
    }

    // --- GETTERS ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }
}
