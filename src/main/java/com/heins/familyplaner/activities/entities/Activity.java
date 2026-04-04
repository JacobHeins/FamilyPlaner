package com.heins.familyplaner.activities.entities;

import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "activities")
@EntityListeners(AuditingEntityListener.class)
public class Activity {

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
    private String description;

    @Setter
    private String location;

    /** The calendar day on which the activity takes place. */
    @Setter
    @Column(nullable = false)
    private LocalDate day;

    /** Optional time-of-day the activity starts. */
    @Setter
    private LocalTime startTime;

    /** Optional time-of-day the activity ends. */
    @Setter
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToMany
    @JoinTable(name = "activity_participants_mapping")
    private final List<FamilyMember> participants = new ArrayList<>();

    public void Update(String name,
                       String description,
                       String location,
                       LocalDate day,
                       LocalTime startTime,
                       LocalTime endTime,
                       List<FamilyMember> participants) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants.addAll(participants);
    }

    protected Activity() {}

    public Activity(
            String name,
            String description,
            String location,
            LocalDate day,
            LocalTime startTime,
            LocalTime endTime,
            Family family,
            List<FamilyMember> participants) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.family = family;
        this.participants.addAll(participants);
    }
}