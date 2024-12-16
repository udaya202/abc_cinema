<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
    <link rel="stylesheet" href="/css/payment.css">
</head>
<body>
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="payment-wrapper">
        <div class="container">
            <div class="payment-grid">
                <!-- Billing Information Form -->
                <div class="billing-section">
                    <h2><i class="bi bi-credit-card"></i> Billing Information</h2>
                    <form id="billingForm" class="billing-form">
                        <div class="form-row">
                            <div class="form-group">
                                <label style="position: static"><i class="bi bi-person"></i> First Name</label>
                                <input type="text" id="firstName" required>
                            </div>
                            <div class="form-group">
                                <label style="position: static"><i class="bi bi-person"></i> Last Name</label>
                                <input type="text" id="lastName" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label style="position: static"><i class="bi bi-envelope"></i> Email</label>
                            <input type="email" id="email" required readonly>
                        </div>
                        
                        <div class="form-group">
                            <label style="position: static"><i class="bi bi-phone"></i> Phone</label>
                            <input type="tel" id="phone" required readonly>
                        </div>
                        
                        <div class="form-group">
                            <label style="position: static"><i class="bi bi-geo-alt"></i> Address</label>
                            <input type="text" id="address" required>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label style="position: static"><i class="bi bi-building"></i> City</label>
                                <input type="text" id="city" required>
                            </div>
                            <div class="form-group">
                                <label style="position: static"><i class="bi bi-globe"></i> Country</label>
                                <select id="country" required>
                                    <option value="Sri Lanka">Sri Lanka</option>
                                </select>
                            </div>
                        </div>
                    </form>
                </div>
                
                <!-- Order Summary -->
                <div class="summary-section">
                    <div class="summary-content">
                        <h2>Order Summary</h2>
                        <div id="ticketsList" class="tickets-list">
                            <!-- Tickets will be loaded dynamically -->
                        </div>
                        
                        <div class="summary-totals">
                            <div class="subtotal">
                                <span>Subtotal</span>
                                <span id="subtotalAmount">LKR 0.00</span>
                            </div>
                            <div class="total">
                                <span>Total</span>
                                <span id="totalAmount">LKR 0.00</span>
                            </div>
                        </div>
                        
                        <div class="payment-actions">
                            <button type="button" class="pay-button" onclick="initiatePayment()">
                                <i class="bi bi-credit-card"></i>
                                Pay Now
                            </button>
                            
                            <div class="payment-methods">
                                <a href="https://www.payhere.lk" target="_blank">
                                    <img src="https://www.payhere.lk/downloads/images/payhere_short_banner_dark.png" 
                                         alt="PayHere" 
                                         width="250"/>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <%@ include file="includes/footer.jsp" %>
    
    <!-- PayHere SDK -->
    <script type="text/javascript" src="https://www.payhere.lk/lib/payhere.js"></script>
    <script src="/js/payment.js"></script>
    <script src="/js/script.js"></script>
</body>
</html> 