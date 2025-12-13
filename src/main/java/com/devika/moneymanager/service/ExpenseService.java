package com.devika.moneymanager.service;

import com.devika.moneymanager.dto.CategoryDTO;
import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.entity.CategoryEntity;
import com.devika.moneymanager.entity.ExpenseEntity;
import com.devika.moneymanager.entity.IncomeEntity;
import com.devika.moneymanager.entity.ProfileEntity;
import com.devika.moneymanager.repository.CategoryRepository;
import com.devika.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;
    private final ExcelExportService excelExportService;
    private final EmailService emailService;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO){
         ProfileEntity currentUser= profileService.getCurrentProfile();
         CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
                 .orElseThrow(() -> new RuntimeException("Category Not found"));
         ExpenseEntity newExpense = toEntity(expenseDTO,currentUser,category);
         newExpense = expenseRepository.save(newExpense);
         return toDTO(newExpense);
    }

    // retrieve all the expenses for current month based on start and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentProfile() {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> expenses=expenseRepository.findByProfileEntity_IdAndDateBetween(currentProfile.getId(),startDate,endDate );
        return expenses.stream().map(this::toDTO).toList();
    }

    // delete expense by id for current user
    public void deleteExpense(Long expenseId){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        ExpenseEntity expense=expenseRepository.findById(expenseId)
                        .orElseThrow(()-> new RuntimeException("Expense is not found"));
        if(!(expense.getProfileEntity().getId() == currentUser.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(expense);

    }

    // get latest 5 expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<ExpenseEntity> top5Expenses = expenseRepository.findTop5ByProfileEntity_IdOrderByDateDesc(currentUser.getId());
        return top5Expenses.stream().map(this::toDTO).toList();
    }

    //get total expenses for current user
    public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        BigDecimal total =expenseRepository.findTotalExpenseByProfileEntity_id(currentUser.getId());
        return total!=null ? total : BigDecimal.ZERO;
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity currentUser = profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntityList =expenseRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(currentUser.getId(),
                startDate,endDate,keyword,sort
                );
        return expenseEntityList.stream().map(this::toDTO).toList();
    }

    //notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date){
        List<ExpenseEntity> expenseEntities=expenseRepository.findByProfileEntity_IdAndDate(profileId,date);
        return expenseEntities.stream().map(this::toDTO).toList();
    }
    //excel
    public ByteArrayInputStream downloadExpenses() throws IOException {
        String[] EXPENSE_HEADERS = {"S.NO", "Name", "Category", "Amount", "Date"};
        List<ExpenseEntity> expenses = expenseRepository.findAll();
        AtomicInteger counter = new AtomicInteger(1);
        return excelExportService.export(
                expenses,
                "Expense Details",
                EXPENSE_HEADERS,
                expense -> new Object[]{
                        counter.getAndIncrement(),
                        expense.getName(),
                        expense.getCategoryEntity().getName(),
                        expense.getAmount(),
                        expense.getDate(),
                }
        );
    }

    // send mail expense details excel
    public void emailExpenseExcel() throws IOException {
        byte[] excelBytes = downloadExpenses().readAllBytes();
        ProfileEntity profile= profileService.getCurrentProfile();
        emailService.sendEmailWithAttachment(
                profile.getEmail(),
                "Your Expense Report",
                "Please find attached your expense details Excel report.",
                excelBytes,
                "expense_details.xlsx"
        );
    }

    //helper methods
    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profileEntity, CategoryEntity categoryEntity){
        return ExpenseEntity.builder()
                .id(expenseDTO.getId())
                .name(expenseDTO.getName())
                .amount(expenseDTO.getAmount())
                .icon(expenseDTO.getIcon())
                .date(expenseDTO.getDate())
                .profileEntity(profileEntity)
                .categoryEntity(categoryEntity)
                .build();
    }
    private ExpenseDTO toDTO(ExpenseEntity expenseEntity){
        return ExpenseDTO.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .categoryName(expenseEntity.getCategoryEntity() !=null ? expenseEntity.getCategoryEntity().getName() : "N/A")
                .categoryId(expenseEntity.getCategoryEntity() !=null ? expenseEntity.getCategoryEntity().getId() : null)
                .date(expenseEntity.getDate())
                .amount(expenseEntity.getAmount())
                .updatedAt(expenseEntity.getCreatedAt())
                .createdAt(expenseEntity.getCreatedAt())
                .build();

    }
}
