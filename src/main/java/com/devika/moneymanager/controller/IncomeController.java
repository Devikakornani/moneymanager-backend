package com.devika.moneymanager.controller;

import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.dto.IncomeDTO;
import com.devika.moneymanager.service.ExpenseService;
import com.devika.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO){
        IncomeDTO saved=incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getCurrentMonthIncomesForCurrentUser(){
        return ResponseEntity.ok(incomeService.getCurrentMonthIncomesForCurrentProfile());
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId){
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.noContent().build();
    }

}
