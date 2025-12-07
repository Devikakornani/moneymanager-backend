package com.devika.moneymanager.controller;

import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.dto.FilterDTO;
import com.devika.moneymanager.dto.IncomeDTO;
import com.devika.moneymanager.service.ExpenseService;
import com.devika.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO) {
        LocalDate startDate = filterDTO.getStartDate() != null ? filterDTO.getStartDate() : LocalDate.of(1970, 1, 1);
        LocalDate endDate = filterDTO.getEndDate() != null ? filterDTO.getEndDate() : LocalDate.now();
        String keyword = filterDTO.getKeyword() != null ? filterDTO.getKeyword() : "";
        String sortField= filterDTO.getSortField() !=null ? filterDTO.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filterDTO.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort= Sort.by(direction,sortField);
        if("income".equals(filterDTO.getType())){
            List<IncomeDTO> incomes =incomeService.filterIncomes(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(incomes);
        }else if("expense".equals(filterDTO.getType())){
            List<ExpenseDTO> expenses =expenseService.filterExpenses(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(expenses);
        }else {
            return ResponseEntity.badRequest().body("Invalid Type. It must be income/expense");
        }

    }
}
