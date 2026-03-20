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
    private List<Person> familyMembers = new ArrayList<>();

    public void addPerson(Person person) {
        familyMembers.add(person);
        person.setFamily(this);
    }

    // --- GETTERS ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Person> getFamilyMembers() {
        return familyMembers;
    }
}
