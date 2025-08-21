package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token, String teamCode) {
        String verifyUrl = "http://intproj24.sit.kmutt.ac.th/" + teamCode + "/verify-email?token=" + token;

        // ================= Logging URL =================
        System.out.println("=== Verification URL ===");
        System.out.println(verifyUrl);
        System.out.println("=======================");
        // =============================================

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your account");
        message.setText("Welcome! Please click the link below to verify your account:\n\n"
                + verifyUrl
                + "\n\nNote: This link will expire in 1 hour.");

        mailSender.send(message);
    }
}
