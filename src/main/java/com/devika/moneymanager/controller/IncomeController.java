package com.devika.moneymanager.controller;

import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.dto.IncomeDTO;
import com.devika.moneymanager.service.ExcelExportService;
import com.devika.moneymanager.service.ExpenseService;
import com.devika.moneymanager.service.IncomeService;
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
    @GetMapping("/excel/download")
    public ResponseEntity<InputStreamResource> downloadIncomeExcel() throws IOException {
        ByteArrayInputStream stream = incomeService.downloadIncome();
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=income_details.xlsx"
        );
        return ResponseEntity.ok()
                .headers(headers)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/excel/email")
    public ResponseEntity<String> emailIncomeExcel() throws IOException {
        incomeService.emailIncomeExcel();
        return ResponseEntity.status(200).body("Email Sent Success");
    }




}
