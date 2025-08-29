package com.infodistiller.gateway.repository;

import com.infodistiller.gateway.entity.Brief;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BriefRepository extends JpaRepository<Brief, Integer> {
}