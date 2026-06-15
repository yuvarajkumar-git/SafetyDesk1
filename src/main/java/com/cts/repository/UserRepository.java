package com.cts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.User;

/**
 * Data access for User. Extending JpaRepository gives us
 * save, findById, findAll, deleteById, etc. for free.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring derives the query from the method name.
    // Used to enforce the unique-email rule before saving.
    Optional<User> findByEmail(String email);
    
    java.util.List<com.cts.entity.User> findByRole(com.cts.enums.Role role);

    java.util.List<com.cts.entity.User> findByRoleAndSiteId(com.cts.enums.Role role, Long siteId);

    boolean existsByEmail(String email);
}