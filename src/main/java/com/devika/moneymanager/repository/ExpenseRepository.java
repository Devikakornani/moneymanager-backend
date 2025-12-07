package com.devika.moneymanager.repository;

import com.devika.moneymanager.entity.ExpenseEntity;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {
    // select * from tbl_expenses where profile_id = ?1 where order by date desc;
    List<ExpenseEntity> findByProfileEntity_IdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e where e.profileEntity.id = :profileId")
    BigDecimal findTotalExpenseByProfileEntity_id(@Param("profileId") Long profileId);

    // select * from tbl_expenses where profileId = ?1 and date between ?2 and ?3 and name like %?4%
    List<ExpenseEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_expenses where profileId = ?1 and date between ?2 and ?3
    List<ExpenseEntity> findByProfileEntity_IdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    // select * from tbl_expenses where profile_id = ?1 and date = ?2
    List<ExpenseEntity> findByProfileEntity_IdAndDate(Long profileId, LocalDate date);
}
