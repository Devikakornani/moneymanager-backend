package com.devika.moneymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

//    @Async
    public void sendEmail(String to,String subject, String body){
       try{
           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom(fromEmail);
           message.setTo(to);
           message.setSubject(subject);
           message.setText(body);
           javaMailSender.send(message);
           log.info("Email send successfully");
       }catch (Exception e){
           log.error("Email sending failed for {}: {}", to, e.getMessage());
       }
    }
}
