package com.heins.familyplaner.family.entities;

import jakarta.persistence.*;

@Entity
@Table(name="persons")
public class Person {

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "family_id")  // foreign key
    private Family family;

    public long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Family getFamily() { return family; }
    public void setFamily(Family family) { this.family = family; }
}
