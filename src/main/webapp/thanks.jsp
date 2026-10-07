<%@ page import="com.murach.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%
    User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thanks for joining</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/style.css">
</head>

<body>

<div class="container">

    <h1>Thanks for joining our email list!</h1>

    <p>
        We have added the following information to our email list:
    </p>

    <%
        if (user != null) {
    %>

    <div class="user-info">

        <p>
            <strong>Email:</strong>
            <%= user.getEmail() %>
        </p>

        <p>
            <strong>First Name:</strong>
            <%= user.getFirstName() %>
        </p>

        <p>
            <strong>Last Name:</strong>
            <%= user.getLastName() %>
        </p>

    </div>

    <p>
        A confirmation email has been sent to
        <strong><%= user.getEmail() %></strong>.
    </p>

    <%
        } else {
    %>

    <p>
        No user information was found.
    </p>

    <%
        }
    %>

    <form action="${pageContext.request.contextPath}/"
          method="get">

        <input type="submit"
               value="Return">
    </form>

</div>

</body>
</html>