package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final Environment environment;

    @Value("${team.code:ms3}")
    private String teamCode;

    @Value("${app.frontend.url.dev:http://localhost:5173}")
    private String devFrontendUrl;

    @Value("${app.frontend.url.prod:http://intproj24.sit.kmutt.ac.th}")
    private String prodFrontendUrl;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, Environment environment) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.environment = environment;
    }

    private String getHostPath() {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevMode = activeProfiles.length > 0 && activeProfiles[0].equals("dev");
        return isDevMode ? devFrontendUrl : prodFrontendUrl;
    }

    private String getCurrentYear() {
        return new SimpleDateFormat("yyyy").format(new Date());
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    public void sendVerificationEmail(String to, String token) throws MessagingException, UnsupportedEncodingException {
        String hostPath = getHostPath();
        String verifyUrl = hostPath + "/" + teamCode + "/verify-email?token=" + token;

        Context context = new Context();
        context.setVariable("verifyUrl", verifyUrl);
        context.setVariable("teamCode", teamCode);
        context.setVariable("recipientEmail", to);
        context.setVariable("currentYear", getCurrentYear());
        context.setVariable("currentDateTime", getCurrentDateTime());

        // ใช้ path folder ถูกต้อง "emails/verification"
        String htmlContent = templateEngine.process("emails/verification", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Verify your ITBMS account");
        helper.setText(htmlContent, true);
        helper.setFrom("itbmshop359@gmail.com", "ITBMS Team");

        System.out.println("Verification URL: " + verifyUrl);
        System.out.println("Running in mode: " + (getHostPath().contains("localhost") ? "DEVELOPMENT" : "PRODUCTION"));

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException, UnsupportedEncodingException {
        String hostPath = getHostPath();
        String resetUrl = hostPath + "/" + teamCode + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("teamCode", teamCode);
        context.setVariable("recipientEmail", to);
        context.setVariable("currentYear", getCurrentYear());
        context.setVariable("currentDateTime", getCurrentDateTime());

        // ใช้ path folder ถูกต้อง "emails/password-reset"
        String htmlContent = templateEngine.process("emails/password-reset", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Reset your ITBMS password");
        helper.setText(htmlContent, true);
        helper.setFrom("itbmshop359@gmail.com", "ITBMS Team");

        System.out.println("Password reset URL: " + resetUrl);
        System.out.println("Running in mode: " + (getHostPath().contains("localhost") ? "DEVELOPMENT" : "PRODUCTION"));

        mailSender.send(message);
    }
}
