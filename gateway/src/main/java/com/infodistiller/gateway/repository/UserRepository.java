package com.infodistiller.gateway.repository;

import com.infodistiller.gateway.entity.User; // Make sure to import the new User entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}