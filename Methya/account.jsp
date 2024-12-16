<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Settings - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body class="account-page">
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="account-wrapper">
        <div class="account-container">
            <div class="account-header">
                <h1>Account Settings</h1>
                <p>Manage your ABC Cinema profile and preferences</p>
            </div>
            
            <div class="account-grid">
                <!-- Profile Section -->
                <div class="account-section">
                    <div class="section-header">
                        <h2>Profile Information</h2>
                        <button class="edit-btn" data-section="profile">
                            <i class="bi bi-pencil"></i>
                            Edit
                        </button>
                    </div>
                    <div class="section-content" id="profileSection">
                        <div class="profile-info">
                            <div class="info-group">
                                <label>Name</label>
                                <p><%= session.getAttribute("userName") %></p>
                            </div>
                            <div class="info-group">
                                <label>Email</label>
                                <p><%= session.getAttribute("userEmail") %></p>
                            </div>
                            <div class="info-group">
                                <label>Phone</label>
                                <p><%= session.getAttribute("userPhone") %></p>
                            </div>
                        </div>
                        <form class="edit-form" id="profileForm" style="display: none;">
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
                            <div class="form-actions">
                                <button type="button" class="cancel-btn" data-section="profile">Cancel</button>
                                <button type="submit" class="save-btn">Save Changes</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Security Section -->
                <div class="account-section">
                    <div class="section-header">
                        <h2>Security</h2>
                        <button class="edit-btn" data-section="security">
                            <i class="bi bi-shield-lock"></i>
                            Change Password
                        </button>
                    </div>
                    <div class="section-content" id="securitySection">
                        <div class="security-info">
                            <p class="password-status">
                                <i class="bi bi-shield-check"></i>
                                Your password was last changed on <span class="highlight">January 1, 2024</span>
                            </p>
                        </div>
                        <form class="edit-form" id="passwordForm" style="display: none;">
                            <div class="form-group">
                                <div class="input-group">
                                    <i class="bi bi-lock"></i>
                                    <input type="password" id="currentPassword" required>
                                    <label for="currentPassword">Current Password</label>
                                    <button type="button" class="toggle-password">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <span class="error-message"></span>
                            </div>
                            <div class="form-group">
                                <div class="input-group">
                                    <i class="bi bi-lock"></i>
                                    <input type="password" id="newPassword" required>
                                    <label for="newPassword">New Password</label>
                                    <button type="button" class="toggle-password">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <span class="error-message"></span>
                            </div>
                            <div class="form-group">
                                <div class="input-group">
                                    <i class="bi bi-shield-lock"></i>
                                    <input type="password" id="confirmNewPassword" required>
                                    <label for="confirmNewPassword">Confirm New Password</label>
                                    <button type="button" class="toggle-password">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <span class="error-message"></span>
                            </div>
                            <div class="form-actions">
                                <button type="button" class="cancel-btn" data-section="security">Cancel</button>
                                <button type="submit" class="save-btn">Update Password</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Preferences Section -->
                <div class="account-section">
                    <div class="section-header">
                        <h2>Preferences</h2>
                        <button class="edit-btn" data-section="preferences">
                            <i class="bi bi-gear"></i>
                            Edit
                        </button>
                    </div>
                    <div class="section-content" id="preferencesSection">
                        <div class="preferences-info">
                            <div class="preference-item">
                                <label>Email Notifications</label>
                                <div class="toggle-switch">
                                    <input type="checkbox" id="emailNotif" checked>
                                    <span class="toggle-slider"></span>
                                </div>
                            </div>
                            <div class="preference-item">
                                <label>SMS Notifications</label>
                                <div class="toggle-switch">
                                    <input type="checkbox" id="smsNotif">
                                    <span class="toggle-slider"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Footer -->
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/account.js"></script>
    <script src="/js/script.js"></script>
</body>
</html> 