package com.devika.moneymanager.service;

import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.dto.IncomeDTO;
import com.devika.moneymanager.entity.CategoryEntity;
import com.devika.moneymanager.entity.ExpenseEntity;
import com.devika.moneymanager.entity.IncomeEntity;
import com.devika.moneymanager.entity.ProfileEntity;
import com.devika.moneymanager.repository.CategoryRepository;
import com.devika.moneymanager.repository.ExpenseRepository;
import com.devika.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryService categoryService;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    public IncomeDTO addIncome(IncomeDTO incomeDTO){
        ProfileEntity currentUser= profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category Not found"));
        IncomeEntity newIncome = toEntity(incomeDTO,currentUser,category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    // retrieve all the incomes for current month based on start and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentProfile() {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> incomes=incomeRepository.findByProfileEntity_IdAndDateBetween(currentProfile.getId(),startDate,endDate );
        return incomes.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        IncomeEntity income=incomeRepository.findById(incomeId)
                .orElseThrow(()-> new RuntimeException("Income is not found"));
        if(!(income.getProfileEntity().getId() == currentUser.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(income);

    }


    // get latest 5 incomes for current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<IncomeEntity> top5Incomes = incomeRepository.findTop5ByProfileEntity_IdOrderByDateDesc(currentUser.getId());
        return top5Incomes.stream().map(this::toDTO).toList();
    }

    //get total income for current user
    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        BigDecimal total =incomeRepository.findTotalIncomeByProfileEntity_id(currentUser.getId());
        return total!=null ? total : BigDecimal.ZERO;
    }

    //filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<IncomeEntity> incomeEntityList =incomeRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(currentUser.getId(),
                startDate,endDate,keyword,sort
        );
        return incomeEntityList.stream().map(this::toDTO).toList();
    }

    //helper methods
    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profileEntity, CategoryEntity categoryEntity){
        return IncomeEntity.builder()
                .id(incomeDTO.getId())
                .name(incomeDTO.getName())
                .amount(incomeDTO.getAmount())
                .icon(incomeDTO.getIcon())
                .date(incomeDTO.getDate())
                .profileEntity(profileEntity)
                .categoryEntity(categoryEntity)
                .build();
    }
    private IncomeDTO toDTO(IncomeEntity incomeEntity){
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .categoryName(incomeEntity.getCategoryEntity() !=null ? incomeEntity.getCategoryEntity().getName() : "N/A")
                .categoryId(incomeEntity.getCategoryEntity() !=null ? incomeEntity.getCategoryEntity().getId() : null)
                .date(incomeEntity.getDate())
                .amount(incomeEntity.getAmount())
                .updatedAt(incomeEntity.getCreatedAt())
                .createdAt(incomeEntity.getCreatedAt())
                .build();

    }
}
