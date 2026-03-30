package com.heins.familyplaner.todos.repositories;

import com.heins.familyplaner.todos.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo,Long>, JpaSpecificationExecutor<Todo> {
}
