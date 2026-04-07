package com.heins.familyplanner.family.repositories;

import com.heins.familyplanner.family.entities.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Long> {
}
