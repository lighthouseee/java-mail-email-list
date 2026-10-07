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

        String mailUsername = System.getenv("MAIL_USERNAME");
        String mailPassword = System.getenv("MAIL_PASSWORD");

        if (mailUsername == null || mailUsername.isBlank()) {
            throw new IllegalStateException(
                "MAIL_USERNAME environment variable is not set."
            );
        }

        if (mailPassword == null || mailPassword.isBlank()) {
            throw new IllegalStateException(
                "MAIL_PASSWORD environment variable is not set."
            );
        }

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
            "mail.smtp.host",
            "smtp.gmail.com"
        );

        properties.put(
            "mail.smtp.port",
            "587"
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

        Transport.send(message);
    }
}