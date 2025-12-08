package com.devika.moneymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
//    private final JavaMailSender javaMailSender;
//    @Value("${spring.mail.properties.mail.smtp.from}")
//    private String fromEmail;
//
//    @Async
//    public void sendEmail(String to,String subject, String body){
//       try{
//           SimpleMailMessage message = new SimpleMailMessage();
//           message.setFrom(fromEmail);
//           message.setTo(to);
//           message.setSubject(subject);
//           message.setText(body);
//           javaMailSender.send(message);
//           log.info("Email send successfully");
//       }catch (Exception e){
//           log.error("Email sending failed for {}: {}", to, e.getMessage());
//       }
//    }

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    @Value("${brevo.from.name}")
    private String fromName;

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String body) {
        try {
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setApiKey(apiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
            SendSmtpEmailSender sender = new SendSmtpEmailSender().email(fromEmail).name(fromName);

            SendSmtpEmailTo receiver = new SendSmtpEmailTo().email(to);
            List<SendSmtpEmailTo> toList = List.of(receiver);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(toList)
                    .subject(subject)
                    .textContent(body);

            apiInstance.sendTransacEmail(email);
            logger.info("Activation email sent to {}", to);

        } catch (Exception ex) {
            logger.error("Email sending failed: {}", ex.getMessage());
            throw new RuntimeException("Unable to send email. Please try again later.");
        }
    }
}
