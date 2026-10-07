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

        // Create User object
        User user = new User(
                firstName.trim(),
                lastName.trim(),
                email.trim()
        );

        try {

            // Send confirmation email
            MailUtil.sendWelcomeEmail(user);

            // Create session and store user information
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            // Redirect to /thanks
            String redirectUrl = response.encodeRedirectURL(
                    request.getContextPath() + "/thanks"
            );

            response.sendRedirect(redirectUrl);

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

        // Only handle the /thanks URL
        if (!"/thanks".equals(request.getServletPath())) {

            response.sendRedirect(
                    request.getContextPath() + "/"
            );

            return;
        }

        // Get the existing session
        HttpSession session = request.getSession(false);

        // If there is no session or no user,
        // return to the registration form
        if (session == null || session.getAttribute("user") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/"
            );

            return;
        }

        // Get user information from session
        User user = (User) session.getAttribute("user");

        // Pass user information to thanks.jsp
        request.setAttribute("user", user);

        // Remove user from session after retrieving it
        session.removeAttribute("user");

        // Display thank-you page
        request.getRequestDispatcher("/thanks.jsp")
                .forward(request, response);
    }
}