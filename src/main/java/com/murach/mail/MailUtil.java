package com.murach.mail;

import com.murach.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MailUtil {

    private static final String BREVO_API_URL =
            "https://api.brevo.com/v3/smtp/email";

    public static void sendWelcomeEmail(User user) {

        System.out.println("[MAIL] Starting email process...");

        String apiKey = System.getenv("BREVO_API_KEY");
        String mailFrom = System.getenv("MAIL_FROM");

        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("[MAIL ERROR] BREVO_API_KEY is missing.");

            throw new IllegalStateException(
                    "BREVO_API_KEY environment variable is not set."
            );
        }

        if (mailFrom == null || mailFrom.isBlank()) {
            System.out.println("[MAIL ERROR] MAIL_FROM is missing.");

            throw new IllegalStateException(
                    "MAIL_FROM environment variable is not set."
            );
        }

        System.out.println(
                "[MAIL] Sending from: " + mailFrom
        );

        System.out.println(
                "[MAIL] Sending to: " + user.getEmail()
        );

        String subject =
                "Thanks for joining our email list!";

        String textContent =
                "Hi " + user.getFirstName()
                + " " + user.getLastName() + ",\n\n"
                + "Thank you for joining our email list.\n\n"
                + "We have received the following information:\n"
                + "First name: " + user.getFirstName() + "\n"
                + "Last name: " + user.getLastName() + "\n"
                + "Email: " + user.getEmail() + "\n\n"
                + "Welcome!";

        String jsonBody =
                "{"
                + "\"sender\":{"
                + "\"email\":\"" + escapeJson(mailFrom) + "\","
                + "\"name\":\"Email List\""
                + "},"
                + "\"to\":[{"
                + "\"email\":\"" + escapeJson(user.getEmail()) + "\","
                + "\"name\":\""
                + escapeJson(
                        user.getFirstName()
                        + " "
                        + user.getLastName()
                )
                + "\""
                + "}],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"textContent\":\""
                + escapeJson(textContent)
                + "\""
                + "}";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BREVO_API_URL))
                .timeout(Duration.ofSeconds(15))
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(jsonBody)
                )
                .build();

        try {

            System.out.println(
                    "[MAIL] Connecting to Brevo API..."
            );

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            int statusCode = response.statusCode();

            System.out.println(
                    "[MAIL] Brevo response status: "
                    + statusCode
            );

            if (statusCode >= 200 && statusCode < 300) {

                System.out.println(
                        "[MAIL] Email sent successfully."
                );

                return;
            }

            System.out.println(
                    "[MAIL ERROR] Brevo API response: "
                    + response.body()
            );

            throw new IllegalStateException(
                    "Brevo API returned HTTP "
                    + statusCode
            );

        } catch (IOException e) {

            System.out.println(
                    "[MAIL ERROR] Network error: "
                    + e.getMessage()
            );

            throw new IllegalStateException(
                    "Unable to connect to Brevo API.",
                    e
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "[MAIL ERROR] Request was interrupted."
            );

            throw new IllegalStateException(
                    "Email request was interrupted.",
                    e
            );
        }
    }

    private static String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}