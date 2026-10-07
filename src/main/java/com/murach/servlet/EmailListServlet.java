package com.murach.servlet;

import com.murach.mail.MailUtil;
import com.murach.model.User;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/join")
public class EmailListServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        if (email == null || email.isBlank()
                || firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()) {

            request.setAttribute(
                "errorMessage",
                "Please enter all required information."
            );

            request.getRequestDispatcher("/index.jsp")
                   .forward(request, response);

            return;
        }

        User user = new User(
            firstName.trim(),
            lastName.trim(),
            email.trim()
        );

        try {
            MailUtil.sendWelcomeEmail(user);

            request.setAttribute("user", user);

            request.getRequestDispatcher("/thanks.jsp")
                   .forward(request, response);

        } catch (MessagingException | IllegalStateException e) {

            e.printStackTrace();

            request.setAttribute(
                "errorMessage",
                "Unable to send email. Please try again later."
            );

            request.getRequestDispatcher("/index.jsp")
                   .forward(request, response);
        }
    }
}