package com.devika.moneymanager.service;

import com.devika.moneymanager.dto.ExpenseDTO;
import com.devika.moneymanager.entity.ExpenseEntity;
import com.devika.moneymanager.entity.ProfileEntity;
import com.devika.moneymanager.repository.ExpenseRepository;
import com.devika.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // logs
public class NotificationService {
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    // scheduled - allows u to run particular method at specific time
    // sending emails, triggers notifs, generates reports
    // sec,min,hr,day(0-31),month(1-12),week(MON-FRI)
    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
    public void sendDailyIncomeExpenseReminder(){
      log.info("Job Started: sendDailyIncomeExpenseReminder()");
      List<ProfileEntity> profiles=profileRepository.findAll();
      for(ProfileEntity profile: profiles){
          String body =
                  "Hi " + profile.getFullName() + ",<br/><br/>"
                          + "This is a friendly reminder to add your expenses and incomes for today in Money Manager.<br/><br/>"
                          + "<a href='" + frontendUrl + "' "
                          + "style='display:inline-block; padding:10px 20px; background-color:#4CAF50; "
                          + "color:#fff; text-decoration:none; border-radius:5px; font-weight:bold;'>"
                          + "Go To Money Manager</a><br/><br/>"
                          + "Best Regards,<br/>Money Manager Team";

          emailService.sendEmail(profile.getEmail(),"Daily Reminder: Add your Incomes/Expenses",body);
      }
    }
    private String buildExpenseTable(List<ExpenseDTO> expenses) {
        StringBuilder table = new StringBuilder();
        table.append("""
        <table style='width:100%; border-collapse:collapse; font-family:Arial, sans-serif; font-size:14px;'>
            <thead>
                <tr style='background-color:#4CAF50; color:white; text-align:left;'>
                    <th style='padding:8px; border:1px solid #ddd;'>Name</th>
                    <th style='padding:8px; border:1px solid #ddd;'>Category</th>
                    <th style='padding:8px; border:1px solid #ddd;'>Amount (₹)</th>
                </tr>
            </thead>
            <tbody>
        """);
        for (ExpenseDTO expense : expenses) {
            table.append("<tr style='background-color:#f9f9f9;'>")
                    .append("<td style='padding:8px; border:1px solid #ddd;'>").append(expense.getName()).append("</td>")
                    .append("<td style='padding:8px; border:1px solid #ddd;'>").append(expense.getCategoryId() != null ? expense.getCategoryName(): "N/A").append("</td>")
                    .append("<td style='padding:8px; border:1px solid #ddd;'>").append(expense.getAmount()).append("</td>")
                    .append("</tr>");
        }
        table.append("""
            </tbody>
        </table>
        """);
        return table.toString();
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job Started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> totalExpenses =
                    expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));
            if (!totalExpenses.isEmpty()) {
                String tableHtml = buildExpenseTable(totalExpenses);
                String body =
                        "Hi " + profile.getFullName() + ",<br/><br/>"
                                + "Here is the summary of your expenses for today:<br/><br/>"
                                + tableHtml
                                + "<br/><br/>Best Regards,<br/>Money Manager Team";
                emailService.sendEmail(
                        profile.getEmail(),
                        "Your Daily Expense Summary",
                        body
                );
            }
        }

        log.info("Job Completed: sendDailyExpenseSummary()");
    }

}
