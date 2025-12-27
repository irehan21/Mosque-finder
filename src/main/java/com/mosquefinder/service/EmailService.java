//package com.mosquefinder.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Async
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${email.from.address}")
//    private String fromEmail;
//
//    @Value("${email.from.name}")
//    private String fromName;
//
//    public void sendOtpEmail(String to, String otp) {
//        String subject = "Your Verification Code - Mosque Finder";
//        String htmlBody = String.format("""
//            <div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
//                <h2 style="color: #2c5f2d;">🕌 Mosque Finder</h2>
//                <p>Assalamu Alaikum,</p>
//                <p>Your verification code is:</p>
//                <div style="background-color: #f4f4f4; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 5px; margin: 20px 0; border-radius: 8px;">
//                    %s
//                </div>
//                <p>This OTP will expire in <strong>10 minutes</strong>.</p>
//                <p style="color: #666;">If you didn't request this, please ignore this email.</p>
//                <br>
//                <p>Best regards,<br><strong>Mosque Finder Team</strong></p>
//            </div>
//            """, otp);
//
//        sendEmail(to, subject, htmlBody);
//    }
//
//    public void sendPasswordResetOtp(String to, String otp) {
//        String subject = "Password Reset OTP - Mosque Finder";
//        String htmlBody = String.format("""
//            <div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
//                <h2 style="color: #2c5f2d;">🕌 Mosque Finder</h2>
//                <p>Assalamu Alaikum,</p>
//                <p>You requested to reset your password.</p>
//                <p>Your password reset OTP is:</p>
//                <div style="background-color: #fff3cd; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 5px; margin: 20px 0; border-radius: 8px; border: 2px solid #ffc107;">
//                    %s
//                </div>
//                <p>This OTP will expire in <strong>10 minutes</strong>.</p>
//                <p style="color: #dc3545;"><strong>⚠️ Important:</strong> If you didn't request a password reset, please ignore this email.</p>
//                <br>
//                <p>Best regards,<br><strong>Mosque Finder Team</strong></p>
//            </div>
//            """, otp);
//
//        sendEmail(to, subject, htmlBody);
//    }
//
//    private void sendEmail(String to, String subject, String htmlBody) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail, fromName);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(htmlBody, true);
//
//            mailSender.send(message);
//            log.info("Email sent successfully to: {}", to);
//
//        } catch (MessagingException e) {
//            log.error("Failed to create email message for: {}", to, e);
//            throw new RuntimeException("Failed to create email message", e);
//        } catch (MailException | UnsupportedEncodingException e) {
//            log.error("Failed to send email to: {}", to, e);
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
//}

package com.mosquefinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {


    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.brevo.com/v3")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // ================= OTP EMAIL =================

    @Async
    public void sendOtpEmail(String to, String otp) {
        String subject = "Your OTP - Mosque Finder";

        String html = """
            <div style="font-family:Arial;padding:20px">
                <h2>🕌 Mosque Finder</h2>
                <p>Your OTP is:</p>
                <h1 style="letter-spacing:6px">%s</h1>
                <p>This OTP is valid for 10 minutes.</p>
            </div>
        """.formatted(otp);

        sendEmail(to, subject, html);
    }

    @Async
    public void sendPasswordResetOtp(String to, String otp) {
        String subject = "Password Reset OTP - Mosque Finder";

        String html = """
            <div style="font-family:Arial;padding:20px">
                <h2>🔐 Password Reset</h2>
                <p>Your reset OTP:</p>
                <h1 style="letter-spacing:6px">%s</h1>
                <p>Expires in 10 minutes.</p>
            </div>
        """.formatted(otp);

        sendEmail(to, subject, html);
    }

    // ================= CORE SENDER =================

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of(
                            "email", senderEmail,
                            "name", senderName
                    ),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", htmlContent
            );

            webClient.post()
                    .uri("/smtp/email")
                    .header("api-key", brevoApiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("✅ Email sent to {}", to);

        } catch (Exception e) {
            log.error("❌ Email sending failed", e);
            throw new RuntimeException("Email sending failed");
        }
    }
}
