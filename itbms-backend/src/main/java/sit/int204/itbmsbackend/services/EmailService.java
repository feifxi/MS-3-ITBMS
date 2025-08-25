package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
//    private final String HOST_PATH = "http://intproj24.sit.kmutt.ac.th";
    private final String HOST_PATH = "http://localhost:5173";

    @Value("${team.code:ms3}")
    private String teamCode;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String verifyUrl = HOST_PATH + "/" + teamCode + "/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your account");
        message.setText("Welcome to ITBMS-MS3! Please go to the link below to verify your account:\n\n"
                + verifyUrl
                + "\n\nNote: This link will expire in 24 hour.");

//        System.out.println("Verification URL : " + verifyUrl);
        mailSender.send(message);
    }
}
