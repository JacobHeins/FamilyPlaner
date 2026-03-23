package com.heins.familyplaner.family.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Entity
@Table(name="familyMembers")
@EntityListeners(AuditingEntityListener.class)
public class FamilyMember {

    public FamilyMember() {}

    public FamilyMember(String name, FamilyRole role) {

        this.name = name;
        this.role = role;
    }

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

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyRole role;

    @Setter
    @ManyToOne
    @JoinColumn(name = "family_id")  // foreign key
    private Family family;
}
