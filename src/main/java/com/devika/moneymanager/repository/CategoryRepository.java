package com.devika.moneymanager.repository;

import com.devika.moneymanager.dto.CategoryDTO;
import com.devika.moneymanager.entity.CategoryEntity;
import com.devika.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

     // select * from tbl_categories where profileId =?1
     List<CategoryEntity> findByProfileEntity_Id(Long profileId);

     // select * from tbl_categories where id=?1 and profileId=?2
     Optional<CategoryEntity> findByIdAndProfileEntity_Id(Long id, Long profileId);

    // select * from tbl_categories where type=?1 and profileId=?2
     List<CategoryEntity> findByTypeAndProfileEntity_Id(String type, Long profileId);

     Boolean existsByNameAndProfileEntity_Id(String name, Long profileId);
}
