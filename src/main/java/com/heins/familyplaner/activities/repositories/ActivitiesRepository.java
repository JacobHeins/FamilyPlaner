package com.heins.familyplaner.activities.repositories;

import com.heins.familyplaner.activities.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivitiesRepository extends JpaRepository<Activity,Long>, JpaSpecificationExecutor<Activity> {
}
