<%--
  Created by IntelliJ IDEA.
  User: Adithya Sandew
  Date: 12/8/2024
  Time: 9:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Login</title>
<%--    <link rel="stylesheet" href="/css/adminlogin.css">--%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>

<style>
    :root {
        --primary-color: #e50909;
        --primary-hover: #f40d19;
        --secondary-color: #7a3195;
        --background-dark: #0a0a0a;
        --background-light: #141414;
        --text-primary: #ffffff;
        --text-secondary: #b3b3b3;
        --gradient-dark: linear-gradient(to bottom, rgba(10, 10, 10, 0.8), rgba(10, 10, 10, 0.95));
        --transition: all 0.3s ease;
    }
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Poppins', sans-serif;
        background-color: var(--background-dark);
        color: var(--text-primary);
        line-height: 1.6;
    }
    /* Admin Login Styles */
    .login-wrapper{
        width: 100%;
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .login-container {
        width: 100%;
        max-width: 500px;
        padding: 20px;
        height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .login-box {
        width: 100%;
        background-color: var(--background-light);
        border-radius: 15px;
        padding: 40px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
        animation: fadeInUp 0.5s ease;
    }

    .login-box h2 {
        color: var(--text-primary);
        text-align: center;
        margin-bottom: 30px;
        font-size: 28px;
    }

    .input-group {
        margin-bottom: 25px;
    }

    .input-group label {
        display: block;
        color: var(--text-secondary);
        margin-bottom: 10px;
        font-size: 14px;
    }

    .input-group input {
        width: 100%;
        padding: 12px 15px;
        background-color: var(--background-dark);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 8px;
        color: var(--text-primary);
        font-size: 16px;
        transition: var(--transition);
    }

    .input-group input:focus {
        outline: none;
        border-color: var(--primary-color);
        box-shadow: 0 0 0 2px rgba(229, 9, 20, 0.2);
    }

    .input-group input::placeholder {
        color: rgba(255, 255, 255, 0.5);
    }

    .login-button {
        width: 100%;
        padding: 14px;
        background-color: var(--primary-color);
        color: var(--text-primary);
        border: none;
        border-radius: 8px;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        transition: var(--transition);
    }

    .login-button:hover {
        background-color: var(--primary-hover);
        transform: translateY(-2px);
    }
    /* Login Error Modal */
    #loginErrorModal {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.8);
        justify-content: center;
        align-items: center;
        z-index: 9999;
        backdrop-filter: blur(5px);
    }

    .modal-content {
        background-color: var(--background-light);
        padding: 30px;
        border-radius: 15px;
        text-align: center;
        max-width: 400px;
        width: 90%;
        position: relative;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
    }

    .modal-content p {
        color: #ff4d4d;
        font-size: 16px;
        margin: 20px 0;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }

    .modal-content i {
        font-size: 24px;
    }

    .modal-content button {
        background-color: var(--primary-color);
        color: white;
        border: none;
        padding: 12px 30px;
        border-radius: 8px;
        cursor: pointer;
        font-size: 16px;
        transition: var(--transition);
    }

    .modal-content button:hover {
        background-color: var(--primary-hover);
        transform: scale(1.05);
    }
</style>

<div class="login-wrapper">
    <div class="login-container">
        <div class="login-box">
        <h2>Admin Login</h2>
        <form id="adminLoginForm" action="adminlogin" method="post">
            <div class="input-group">
                <label for="adminID">Admin ID</label>
                <input type="text" id="adminID" name="id" placeholder="Enter your Admin ID" required>
            </div>
            <div class="input-group">
                <label for="adminPassword">Password</label>
                <input type="password" id="adminPassword" name="password" placeholder="Enter your Password" required>
            </div>
            <button type="submit" class="login-button">Login</button>
        </form>

        <div id="loginErrorModal" class="modal">
            <div class="modal-content">
                <!-- <span class="close-btn" onclick="closeModal()">&times;</span> -->
                <p><i class="bi bi-exclamation-circle"></i> Invalid username or password. Please try again.</p>
                <button onclick="closeModal()">OK</button>
            </div>
        </div>

            </div>
    </div>

</div>

<script>
    // Show the modal on login failure (this will be triggered by servlet response)
    function showModal() {
        document.getElementById('loginErrorModal').style.display = 'flex';
    }

    // Close the modal
    function closeModal() {
        document.getElementById('loginErrorModal').style.display = 'none';
    }

    // Check if login failed via the request parameter
    window.onload = function() {
        var loginFailed = '<%= request.getAttribute("loginFailed") %>';
        if (loginFailed === 'true') {
            showModal();
        }
    }
</script>

</body>
</html>
