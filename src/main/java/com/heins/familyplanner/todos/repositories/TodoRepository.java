package com.heins.familyplanner.todos.repositories;

import com.heins.familyplanner.todos.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {
    Optional<Todo> findByIdAndFamily_Id(Long id, Long familyId);

    boolean existsByIdAndFamily_Id(Long id, Long familyId);
}
