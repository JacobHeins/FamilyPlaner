package com.heins.familyplanner.family.repositories;

import com.heins.familyplanner.family.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family,Long> {
    Optional<Family> findByName(String name);
}
