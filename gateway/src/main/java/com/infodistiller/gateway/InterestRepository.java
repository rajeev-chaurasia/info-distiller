package com.infodistiller.gateway;

import com.infodistiller.gateway.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// By extending JpaRepository<Interest, Integer>, we get a complete set of CRUD
// (Create, Read, Update, Delete) methods for the Interest entity, which has an ID of type Integer.
@Repository
public interface InterestRepository extends JpaRepository<Interest, Integer> {
    // Spring Data JPA is so powerful, we don't even need to write a method body for findAll().
    // We could define custom queries here later if we needed them.
}