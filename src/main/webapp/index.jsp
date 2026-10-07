<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Join our email list</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/style.css">
</head>

<body>

<div class="container">

    <h1>Join our email list</h1>

    <p>
        To join our email list, enter your name and email address below.
    </p>

    <%
        String errorMessage =
            (String) request.getAttribute("errorMessage");

        if (errorMessage != null) {
    %>

    <p class="error">
        <%= errorMessage %>
    </p>

    <%
        }
    %>

    <form action="${pageContext.request.contextPath}/join"
          method="post">

        <div class="form-row">
            <label for="email">
                Email:
            </label>

            <input type="email"
                   id="email"
                   name="email"
                   required>
        </div>

        <div class="form-row">
            <label for="firstName">
                First Name:
            </label>

            <input type="text"
                   id="firstName"
                   name="firstName"
                   required>
        </div>

        <div class="form-row">
            <label for="lastName">
                Last Name:
            </label>

            <input type="text"
                   id="lastName"
                   name="lastName"
                   required>
        </div>

        <div class="button-row">
            <input type="submit"
                   value="Join Now">
        </div>

    </form>

</div>

</body>
</html>