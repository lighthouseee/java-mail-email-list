package com.murach.servlet;

import com.murach.mail.MailUtil;
import com.murach.model.User;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet({"/join", "/thanks"})
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

        // Validate input
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
            // Send real email
            MailUtil.sendWelcomeEmail(user);

            // Store user temporarily in session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // Redirect to thank-you page
            response.sendRedirect(
                request.getContextPath() + "/thanks"
            );

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

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Get existing session without creating a new one
        HttpSession session = request.getSession(false);

        // If user did not come from a successful registration,
        // send them back to the form.
        if (session == null || session.getAttribute("user") == null) {

            response.sendRedirect(
                request.getContextPath() + "/"
            );

            return;
        }

        // Get user from session
        User user = (User) session.getAttribute("user");

        // Pass user to thanks.jsp
        request.setAttribute("user", user);

        // Remove it from session so it doesn't remain stale
        session.removeAttribute("user");

        // Show thank-you page
        request.getRequestDispatcher("/thanks.jsp")
               .forward(request, response);
    }
}