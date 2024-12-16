<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body class="auth-page">
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="auth-wrapper">
        <div class="auth-container">
            <div class="auth-box register-box">
                <h2>Create Account</h2>
                <p class="auth-subtitle">Join us for the ultimate movie experience</p>
                
                <form id="registerForm" class="auth-form" novalidate>
                    <div class="form-group">
                        <div class="input-group">
                            <i class="bi bi-person"></i>
                            <input type="text" id="name" required>
                            <label for="name">Full Name</label>
                        </div>
                        <span class="error-message"></span>
                    </div>
                    
                    <div class="form-group">
                        <div class="input-group">
                            <i class="bi bi-envelope"></i>
                            <input type="email" id="email" required>
                            <label for="email">Email Address</label>
                        </div>
                        <span class="error-message"></span>
                    </div>
                    
                    <div class="form-group">
                        <div class="input-group">
                            <i class="bi bi-phone"></i>
                            <input type="tel" id="phone" required>
                            <label for="phone">Phone Number</label>
                        </div>
                        <span class="error-message"></span>
                    </div>
                    
                    <div class="form-group">
                        <div class="input-group">
                            <i class="bi bi-lock"></i>
                            <input type="password" id="password" required>
                            <label for="password">Password</label>
                            <button type="button" class="toggle-password">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                        <span class="error-message"></span>
                    </div>
                    
                    <div class="form-group">
                        <div class="input-group">
                            <i class="bi bi-shield-lock-fill"></i>
                            <input type="password" id="confirmPassword" required>
                            <label for="confirmPassword">Confirm Password</label>
                            <button type="button" class="toggle-password">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                        <span class="error-message"></span>
                    </div>
                    
                    <button type="submit" class="auth-button">
                        <span>Create Account</span>
                        <i class="bi bi-arrow-right"></i>
                    </button>
                </form>
                
                <p class="auth-redirect">
                    Already have an account? 
                    <a href="/login">Sign in</a>
                </p>
            </div>
        </div>
    </div>
    
    <script src="/js/script.js"></script>
</body>
</html> 