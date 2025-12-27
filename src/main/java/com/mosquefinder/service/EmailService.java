//package com.mosquefinder.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.MailException;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Async
//public class EmailService {
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendOtpEmail(String to, String otp) {
//        String body = String.format("""
//            Hello,
//
//            Your verification code is: %s.
//
//            Your OTP is: %s
//
//            This OTP will expire in 10 minutes.
//
//            If you didn't request this, please ignore this email.
//
//            Best regards,
//            Mosque Finder Team
//            """, otp);
//
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(otp);
//            message.setText(body);
//
//            mailSender.send(message);
//
//        } catch (MailException e) {
//            log.error("Failed to send email to: {}", to, e);
//            throw e;
//        }
//    }
//
//
//    // ✅ ADD this method in your existing EmailService
//    public void sendPasswordResetOtp(String to, String otp) {
//        String subject = "Password Reset OTP - Mosque Finder";
//        String body = String.format("""
//            Hello,
//
//            You requested to reset your password.
//
//            Your OTP is: %s
//
//            This OTP will expire in 10 minutes.
//
//            If you didn't request this, please ignore this email.
//
//            Best regards,
//            Mosque Finder Team
//            """, otp);
//
////        sendOtpEmail(to, subject, body);
//
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(body);
//
//            mailSender.send(message);
//
//        } catch (MailException e) {
//            log.error("Failed to send email to: {}", to, e);
//            throw e;
//        }
//    }
//}



package com.mosquefinder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
@Async
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.from.address}")
    private String fromEmail;

    @Value("${email.from.name}")
    private String fromName;

    public void sendOtpEmail(String to, String otp) {
        String subject = "Your Verification Code - Mosque Finder";
        String htmlBody = String.format("""
            <div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2c5f2d;">🕌 Mosque Finder</h2>
                <p>Assalamu Alaikum,</p>
                <p>Your verification code is:</p>
                <div style="background-color: #f4f4f4; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 5px; margin: 20px 0; border-radius: 8px;">
                    %s
                </div>
                <p>This OTP will expire in <strong>10 minutes</strong>.</p>
                <p style="color: #666;">If you didn't request this, please ignore this email.</p>
                <br>
                <p>Best regards,<br><strong>Mosque Finder Team</strong></p>
            </div>
            """, otp);

        sendEmail(to, subject, htmlBody);
    }

    public void sendPasswordResetOtp(String to, String otp) {
        String subject = "Password Reset OTP - Mosque Finder";
        String htmlBody = String.format("""
            <div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2c5f2d;">🕌 Mosque Finder</h2>
                <p>Assalamu Alaikum,</p>
                <p>You requested to reset your password.</p>
                <p>Your password reset OTP is:</p>
                <div style="background-color: #fff3cd; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 5px; margin: 20px 0; border-radius: 8px; border: 2px solid #ffc107;">
                    %s
                </div>
                <p>This OTP will expire in <strong>10 minutes</strong>.</p>
                <p style="color: #dc3545;"><strong>⚠️ Important:</strong> If you didn't request a password reset, please ignore this email.</p>
                <br>
                <p>Best regards,<br><strong>Mosque Finder Team</strong></p>
            </div>
            """, otp);

        sendEmail(to, subject, htmlBody);
    }

    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to create email message for: {}", to, e);
            throw new RuntimeException("Failed to create email message", e);
        } catch (MailException | UnsupportedEncodingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}