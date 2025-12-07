package com.devika.moneymanager.repository;

import com.devika.moneymanager.entity.IncomeEntity;
import com.devika.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {
    // select * from tbl_expenses where profile_id = ?1 where order by date desc;
    List<IncomeEntity> findByProfileEntity_IdOrderByDateDesc(Long profileId);

    List<IncomeEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM IncomeEntity e where e.profileEntity.id = :profileId")
    BigDecimal findTotalIncomeByProfileEntity_id(@Param("profileId") Long profileId);

    // select * from tbl_expenses where profileId = ?1 and date between ?2 and ?3 and name like %?4%
    List<IncomeEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_expenses where profileId = ?1 and date between ?2 and ?3
    List<IncomeEntity> findByProfileEntity_IdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
