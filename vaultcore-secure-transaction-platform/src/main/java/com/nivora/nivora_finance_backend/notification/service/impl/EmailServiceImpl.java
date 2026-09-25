package com.nivora.nivora_finance_backend.notification.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.nivora.nivora_finance_backend.notification.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String email, String otp) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Verify Your Nivora Finance Account");

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; background:#f5f5f5; padding:20px;">

                        <div style="
                            max-width:600px;
                            margin:auto;
                            background:white;
                            padding:30px;
                            border-radius:12px;
                            box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                            <h1 style="color:#2563eb;">
                                Nivora Finance
                            </h1>

                            <h2>Verify Your Account</h2>

                            <p>
                                Thank you for signing up.
                                Use the OTP below to verify your account.
                            </p>

                            <div style="
                                font-size:32px;
                                font-weight:bold;
                                text-align:center;
                                background:#eff6ff;
                                color:#2563eb;
                                padding:20px;
                                border-radius:10px;
                                margin:20px 0;">

                                %s

                            </div>

                            <p>
                                This OTP is valid for 5 minutes.
                            </p>

                            <p>
                                If you didn't request this, please ignore this email.
                            </p>

                            <hr>

                            <p>
                                Team Nivora Finance
                            </p>

                        </div>

                    </body>
                    </html>
                    """.formatted(otp);

            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String email, String name) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Welcome to Nivora Finance");

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; background:#f5f5f5; padding:20px;">

                        <div style="
                            max-width:600px;
                            margin:auto;
                            background:white;
                            padding:30px;
                            border-radius:12px;
                            box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                            <h1 style="color:#2563eb;">
                                Welcome to Nivora Finance 🎉
                            </h1>

                            <p>
                                Hello %s,
                            </p>

                            <p>
                                Your account has been successfully verified.
                            </p>

                            <h3>
                                You can now:
                            </h3>

                            <ul>
                                <li>Add Money</li>
                                <li>Withdraw Money</li>
                                <li>Transfer Funds</li>
                                <li>View Transactions</li>
                                <li>Use QR Payments</li>
                            </ul>

                            <p>
                                Thank you for joining Nivora Finance.
                            </p>

                            <hr>

                            <p>
                                Team Nivora Finance
                            </p>

                        </div>

                    </body>
                    </html>
                    """.formatted(name);

            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }
}