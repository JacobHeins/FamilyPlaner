package com.heins.familyplanner.activities.repositories;

import com.heins.familyplanner.activities.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivitiesRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {
    Optional<Activity> findByIdAndFamily_Id(Long id, Long familyId);

    boolean existsByIdAndFamily_Id(Long id, Long familyId);
}
