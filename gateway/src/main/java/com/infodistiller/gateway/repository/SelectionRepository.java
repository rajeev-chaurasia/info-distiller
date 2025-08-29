package com.infodistiller.gateway.repository;

import com.infodistiller.gateway.entity.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Integer> {

    List<Selection> findByUserIdAndPickedForDate(Integer userId, LocalDate date);
}