package com.heins.familyplanner.accounts.entities;

import com.heins.familyplanner.family.entities.Family;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "family_accounts")
@NoArgsConstructor
public class FamilyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Public identifier used for login — e.g. "smith-family"
    @Column(nullable = false, unique = true)
    private String publicSlug;

    // Hashed password for family account login
    @Setter
    @Column(nullable = false)
    private String passwordHash;

    // Link to the domain entity — one account per family
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false, unique = true)
    private Family family;

    // Security metadata — belongs here not in Family
    @Setter
    private Instant lastLoginAt;

    @Setter
    private int failedLoginAttempts;

    @Setter
    private boolean locked;

    public FamilyAccount(String publicSlug, String passwordHash, Family family) {
        this.publicSlug = publicSlug;
        this.passwordHash = passwordHash;
        this.family = family;
    }
}
