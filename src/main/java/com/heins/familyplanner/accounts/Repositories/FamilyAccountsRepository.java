package com.heins.familyplanner.accounts.Repositories;

import com.heins.familyplanner.accounts.entities.FamilyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyAccountsRepository extends JpaRepository<FamilyAccount, Long> {
    Optional<FamilyAccount> findByPublicSlug(String publicSlug);
}
