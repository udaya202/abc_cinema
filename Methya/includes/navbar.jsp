<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar">
    <div class="nav-container">
        <div class="logo">
            <a href="/">
                <img src="/images/logo.png" alt="ABC Cinema">
                <span>Cinema</span>
            </a>
        </div>
        
        <div class="nav-links">
            <a href="/" class="nav-link">Home</a>
            <a href="/movies" class="nav-link">Movies</a>
            <a href="/contact" class="nav-link">Contact</a>
            <a href="/about" class="nav-link">About Us</a>
        </div>
        
        <div class="auth-buttons">
            <% if (session.getAttribute("isLoggedIn") != null && (Boolean)session.getAttribute("isLoggedIn")) { %>
                <div class="profile-menu">
                    <button class="profile-btn" id="profileBtn">
                        <i class="bi bi-person-circle"></i>
                        <span class="profile-name"><%= session.getAttribute("userName") %></span>
                    </button>
                    <!-- Modal-like dropdown menu -->
                    <div class="profile-modal" id="profileDropdown">
                        <div class="modal-header">
                            <div class="user-info">
                                <i class="bi bi-person-circle"></i>
                                <div class="user-details">
                                    <span class="user-name"><%= session.getAttribute("userName") %></span>
                                    <span class="user-email"><%= session.getAttribute("userEmail") %></span>
                                </div>
                            </div>
                        </div>
                        <div class="modal-body">
                            <a href="/account" class="modal-item">
                                <i class="bi bi-gear"></i>
                                <span>Manage Account</span>
                            </a>
                            <a href="/mybookings" class="modal-item">
                                <i class="bi bi-ticket-perforated"></i>
                                <span>My Bookings</span>
                            </a>
                            <a href="/api/logout" class="modal-item logout-btn">
                                <i class="bi bi-box-arrow-right"></i>
                                <span>Logout</span>
                            </a>
                        </div>
                    </div>
                </div>
            <% } else { %>
                <a href="/login" class="login-btn">Login</a>
                <a href="/register" class="register-btn">Register</a>
            <% } %>
            <button class="mobile-menu-btn">
                <i class="bi bi-list"></i>
            </button>
        </div>
    </div>
</nav>

<!-- Mobile Menu -->
<div class="mobile-menu">
    <div class="mobile-menu-links">
        <a href="/" class="mobile-link">Home</a>
        <a href="/movies" class="mobile-link">Movies</a>
        <a href="/contact" class="mobile-link">Contact</a>
        <a href="/about" class="mobile-link">About Us</a>
    </div>
    <div class="mobile-auth">
        <% if (session.getAttribute("isLoggedIn") != null && (Boolean)session.getAttribute("isLoggedIn")) { %>
            <div class="mobile-profile">
                <a href="/account" class="mobile-profile-link">
                    <i class="bi bi-gear"></i>
                    Manage Account
                </a>
                <a href="/mybookings" class="mobile-profile-link">
                    <i class="bi bi-ticket-perforated"></i>
                    My Bookings
                </a>
                <a href="/api/logout" class="mobile-profile-link logout-btn">
                    <i class="bi bi-box-arrow-right"></i>
                    Logout
                </a>
            </div>
        <% } else { %>
            <a href="/login" class="login-btn">Login</a>
            <a href="/register" class="register-btn">Register</a>
        <% } %>
    </div>
</div> 