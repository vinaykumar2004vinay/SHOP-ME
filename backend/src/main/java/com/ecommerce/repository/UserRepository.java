package com.ecommerce.repository;

import com.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository — Spring Data JPA auto-implements all CRUD.
 * Custom query methods follow Spring naming convention (findBy...).
 * No SQL needed — Hibernate generates queries from method names.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during login to find user by email address
    Optional<User> findByEmail(String email);

    // Check if email already registered — prevents duplicate accounts
    boolean existsByEmail(String email);

    // Admin: find all users by role and active status
    List<User> findByRoleAndIsActive(User.Role role, Boolean isActive);

    // Admin search: matches first name, last name, or email (case-insensitive)
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);
}
