package com.murach.mail;

import com.murach.model.User;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {

    public static void sendWelcomeEmail(User user)
            throws MessagingException {

        System.out.println("[MAIL] Starting email process...");

        String mailUsername = System.getenv("MAIL_USERNAME");
        String mailPassword = System.getenv("MAIL_PASSWORD");

        if (mailUsername == null || mailUsername.isBlank()) {
            System.out.println("[MAIL ERROR] MAIL_USERNAME is missing.");

            throw new IllegalStateException(
                "MAIL_USERNAME environment variable is not set."
            );
        }

        if (mailPassword == null || mailPassword.isBlank()) {
            System.out.println("[MAIL ERROR] MAIL_PASSWORD is missing.");

            throw new IllegalStateException(
                "MAIL_PASSWORD environment variable is not set."
            );
        }

        System.out.println(
            "[MAIL] Sending from: " + mailUsername
        );

        System.out.println(
            "[MAIL] Sending to: " + user.getEmail()
        );

        Properties properties = new Properties();

        properties.put(
            "mail.smtp.auth",
            "true"
        );

        properties.put(
            "mail.smtp.starttls.enable",
            "true"
        );

        properties.put(
            "mail.smtp.starttls.required",
            "true"
        );

        properties.put(
            "mail.smtp.host",
            "smtp.gmail.com"
        );

        properties.put(
            "mail.smtp.port",
            "587"
        );

        // Prevent the application from hanging indefinitely
        properties.put(
            "mail.smtp.connectiontimeout",
            "10000"
        );

        properties.put(
            "mail.smtp.timeout",
            "10000"
        );

        properties.put(
            "mail.smtp.writetimeout",
            "10000"
        );

        properties.put(
            "mail.smtp.ssl.trust",
            "smtp.gmail.com"
        );

        Session session = Session.getInstance(
            properties,
            new Authenticator() {
                @Override
                protected PasswordAuthentication
                        getPasswordAuthentication() {

                    return new PasswordAuthentication(
                        mailUsername,
                        mailPassword
                    );
                }
            }
        );

        Message message = new MimeMessage(session);

        message.setFrom(
            new InternetAddress(mailUsername)
        );

        message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(user.getEmail())
        );

        message.setSubject(
            "Thanks for joining our email list!"
        );

        String emailBody =
            "Hi " + user.getFirstName()
            + " " + user.getLastName() + ",\n\n"
            + "Thank you for joining our email list.\n\n"
            + "We have received the following information:\n"
            + "First name: " + user.getFirstName() + "\n"
            + "Last name: " + user.getLastName() + "\n"
            + "Email: " + user.getEmail() + "\n\n"
            + "Welcome!";

        message.setText(emailBody);

        System.out.println("[MAIL] Connecting to Gmail SMTP...");

        Transport.send(message);

        System.out.println("[MAIL] Email sent successfully.");
    }
}