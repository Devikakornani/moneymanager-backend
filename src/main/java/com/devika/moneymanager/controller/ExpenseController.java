package com.devika.moneymanager.controller;

import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.dto.IncomeDTO;
import com.devika.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    @GetMapping("/excel/download")
    public ResponseEntity<InputStreamResource> downloadExpenseExcel() throws IOException {
        ByteArrayInputStream stream = expenseService.downloadExpenses();
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=expense_details.xlsx"
        );
        return ResponseEntity.ok()
                .headers(headers)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/excel/email")
    public ResponseEntity<String> emailExpenseExcel() throws IOException {
        expenseService.emailExpenseExcel();
        return ResponseEntity.status(200).body("Email Sent Success");
    }


}
