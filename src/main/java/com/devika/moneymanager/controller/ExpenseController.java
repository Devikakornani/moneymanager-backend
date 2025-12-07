package com.devika.moneymanager.controller;

import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.dto.IncomeDTO;
import com.devika.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO){
        ExpenseDTO saved=expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getCurrentMonthExpensesForCurrentUser(){
        return ResponseEntity.ok(expenseService.getCurrentMonthExpensesForCurrentProfile());
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

}
