package com.heins.familyplanner.todos.entities;

import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.family.entities.FamilyMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Entity
@Table(name = "todos")
@EntityListeners(AuditingEntityListener.class)
public class Todo {

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
    private Instant dueDate;

    @Setter
    @Column(nullable = false)
    private Boolean completed = false;

    @Setter
    @ManyToOne()
    @JoinColumn(name = "family_id")
    private Family family;

    @Setter
    @ManyToOne()
    @JoinColumn(name = "family_member_id")
    private FamilyMember assignee = null;

    public void update(String name, String description, Instant dueDate, Boolean completed, FamilyMember assignee) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.assignee = assignee;
    }

    public Todo(){}

    public Todo(String name, String description, Instant dueDate ,Family family, FamilyMember assignee){
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.family = family;
        this.assignee = assignee;
    }
}
