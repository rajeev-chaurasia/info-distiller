package com.infodistiller.gateway.repository;

import com.infodistiller.gateway.entity.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Integer> {

    @Query("SELECT s, i.name FROM Selection s JOIN Interest i ON s.userId = i.userId WHERE s.userId = :userId AND s.pickedForDate IN :dates ORDER BY s.pickedForDate DESC, i.name ASC")
    List<Object[]> findSelectionsWithInterestNameByDateRange(@Param("userId") Integer userId, @Param("dates") List<LocalDate> dates);
}