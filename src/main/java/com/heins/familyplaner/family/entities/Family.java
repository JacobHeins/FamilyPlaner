package com.heins.familyplaner.family.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "families")
@EntityListeners(AuditingEntityListener.class)
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @Setter
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<FamilyMember> familyMembers = new ArrayList<>();

    public Family(String name) {
        this.name = name;
    }

    public Family() { }

    public void addMember(FamilyMember familyMember) {
        familyMembers.add(familyMember);
        familyMember.setFamily(this);
    }

    public void removeMember(FamilyMember familyMember) {
        familyMembers.remove(familyMember);
        familyMember.setFamily(null); // wegen orphanRemoval sinnvoll
    }
}
