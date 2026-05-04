package com.heins.familyplanner.family.repositories;

import com.heins.familyplanner.family.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    @Query("SELECT f FROM Family f JOIN FamilyAccount fa ON fa.family = f WHERE f.id = :familyId AND fa.id = :accountId")
    Optional<Family> findByIdAndAccountId(@Param("familyId") Long familyId, @Param("accountId") Long accountId);

    Optional<Family> findByName(String name);
}
