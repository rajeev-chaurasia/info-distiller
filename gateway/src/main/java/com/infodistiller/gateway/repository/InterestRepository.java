package com.infodistiller.gateway.repository;

import com.infodistiller.gateway.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Integer> {
    // Find all interests for a specific user ID
    List<Interest> findByUserId(Integer userId);
}