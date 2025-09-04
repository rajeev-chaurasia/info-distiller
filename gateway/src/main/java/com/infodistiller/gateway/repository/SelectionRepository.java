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

    @Query("SELECT s FROM Selection s WHERE s.userId = :userId AND s.pickedForDate IN :dates ORDER BY s.pickedForDate DESC")
    List<Selection> findByUserIdAndDateRange(@Param("userId") Integer userId, @Param("dates") List<LocalDate> dates);
}