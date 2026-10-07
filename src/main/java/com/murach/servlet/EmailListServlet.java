package com.murach.servlet;

import com.murach.mail.MailUtil;
import com.murach.model.User;

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

            // Send confirmation email through Brevo API
            MailUtil.sendWelcomeEmail(user);

            // Save user temporarily in session
            HttpSession session = request.getSession(true);

            session.setAttribute("user", user);

            // Redirect to thank-you page
            String redirectUrl = response.encodeRedirectURL(
                    request.getContextPath() + "/thanks"
            );

            response.sendRedirect(redirectUrl);

        } catch (IllegalStateException e) {

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

        if (!"/thanks".equals(request.getServletPath())) {

            response.sendRedirect(
                    request.getContextPath() + "/"
            );

            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("user") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/"
            );

            return;
        }

        User user = (User) session.getAttribute("user");

        request.setAttribute("user", user);

        session.removeAttribute("user");

        request.getRequestDispatcher("/thanks.jsp")
                .forward(request, response);
    }
}