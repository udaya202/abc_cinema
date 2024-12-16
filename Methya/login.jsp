<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body class="auth-page">
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="auth-wrapper">
        <div class="auth-container">
            <div class="auth-box login-box">
                <h2>Welcome Back</h2>
                <p class="auth-subtitle">Sign in to continue your cinematic journey</p>
                
                <form id="loginForm" class="auth-form" novalidate>
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
                            <i class="bi bi-lock"></i>
                            <input type="password" id="password" required>
                            <label for="password">Password</label>
                            <button type="button" class="toggle-password">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                        <span class="error-message"></span>
                    </div>
                    
                    <div class="form-options">
                        <label class="remember-me">
                            <input type="checkbox" id="remember">
                            <span class="checkmark"></span>
                            Remember me
                        </label>
                        <a href="#" class="forgot-password">Forgot Password?</a>
                    </div>
                    
                    <button type="submit" class="auth-button">
                        <span>Sign In</span>
                        <i class="bi bi-arrow-right"></i>
                    </button>
                </form>
                
                <p class="auth-redirect">
                    Don't have an account? 
                    <a href="/register">Sign up</a>
                </p>
            </div>
        </div>
    </div>
    
    <script src="/js/script.js"></script>
</body>
</html> 