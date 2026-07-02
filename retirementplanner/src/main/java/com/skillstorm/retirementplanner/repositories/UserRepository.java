package com.skillstorm.retirementplanner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.retirementplanner.models.User;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    /**
     * UserRepository: functioning as a DAO that handles the database interactions for User Model
     * 
     * Custom Methods:
     * - findByUsername(String username): Find and return the user by username
     * - findByEmail(String email): Find and return the user by email
     * - existsByUsername(String username): find if the username exists, and returns a boolean
     * - existsByEmail(String email): find if the email exists, and returns a boolean
     */
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // login accepts either email or username as identifier
    Optional<User> findByEmailOrUsername(String email, String username);
}
