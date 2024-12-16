<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bookings - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
    <link rel="stylesheet" href="/css/bookings.css">
</head>
<body>
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="bookings-wrapper">
        <!-- Pending Bookings Section -->
        <section class="bookings-section pending-bookings">
            <div class="container">
                <div class="section-header">
                    <h2>My Bookings</h2>
                    <div class="header-actions">
                        <button class="action-btn pay-btn" id="paySelectedBtn" onclick="paySelectedBookings()">
                            <i class="bi bi-credit-card"></i>
                            Pay Selected
                        </button>
                        <button class="action-btn cancel-btn" id="cancelSelectedBtn" disabled>
                            <i class="bi bi-x-circle"></i>
                            Cancel Selected
                        </button>
                    </div>
                </div>
                
                <div class="bookings-grid">
                    <% if (request.getAttribute("pendingBookings") != null) { 
                        List<Map<String, Object>> bookings = (List<Map<String, Object>>) request.getAttribute("pendingBookings");
                        for (Map<String, Object> booking : bookings) {
                    %>
                        <div class="booking-card">
                            <div class="booking-header">
                                <div class="booking-status <%= booking.get("status") %>">
                                    <%
                                        Integer ticketNo = (Integer) booking.get("ticketNo");
                                        System.out.println("Rendering booking with ticket number: " + ticketNo);
                                    %>
                                    <input type="checkbox" 
                                           class="booking-checkbox" 
                                           value="<%= ticketNo %>"
                                           data-ticket-no="<%= ticketNo %>"
                                           id="ticket-<%= ticketNo %>">
                                    <label for="ticket-<%= ticketNo %>">
                                        <%= booking.get("status").toString().replace("_", " ").toUpperCase() %>
                                    </label>
                                </div>
                                <div class="booking-date">
                                    <%= booking.get("bookingDate") %>
                                </div>
                            </div>
                            
                            <div class="booking-content">
                                <div class="movie-poster">
                                    <img src="<%= booking.get("moviePoster") %>" alt="<%= booking.get("movieTitle") %>">
                                </div>
                                
                                <div class="booking-details">
                                    <h3 class="movie-title"><%= booking.get("movieTitle") %></h3>
                                    <div class="booking-meta">
                                        <div class="meta-item">
                                            <i class="bi bi-clock"></i>
                                            <span><%= booking.get("showtime") %></span>
                                        </div>
                                        <div class="meta-item">
                                            <i class="bi bi-chair"></i>
                                            <span>Seat <%= booking.get("seatNumber") %></span>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="booking-price">
                                    <div class="price-amount">
                                        LKR <%= String.format("%.2f", booking.get("price")) %>
                                    </div>
                                    <button class="action-btn cancel-btn" onclick="cancelBooking('<%= booking.get("seatNumber") %>')">
                                        <i class="bi bi-x-circle"></i>
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        </div>
                    <% } 
                    } else { %>
                        <div class="no-bookings">
                            <i class="bi bi-ticket-perforated"></i>
                            <p>No pending bookings found</p>
                            <a href="/movies" class="browse-btn">Browse Movies</a>
                        </div>
                    <% } %>
                </div>
            </div>
        </section>
        
        <!-- Tickets Section -->
        <section class="bookings-section my-tickets">
            <div class="container">
                <div class="section-header">
                    <h2>My Tickets</h2>
                </div>
                
                <div class="tickets-grid">
                    <% if (request.getAttribute("tickets") != null) { 
                        List<Map<String, Object>> tickets = (List<Map<String, Object>>) request.getAttribute("tickets");
                        for (Map<String, Object> ticket : tickets) {
                    %>
                        <div class="ticket">
                            <div class="ticket-header">
                                <img src="/images/logo.png" alt="ABC Cinema" class="cinema-logo">
                                <div class="ticket-type">PREMIUM</div>
                            </div>
                            
                            <div class="ticket-content">
                                <h3 class="ticket-movie"><%= ticket.get("movieTitle") %></h3>
                                <div class="ticket-info">
                                    <div class="info-item">
                                        <i class="bi bi-calendar3"></i>
                                        <span><%= ticket.get("bookingDate") %></span>
                                    </div>
                                    <div class="info-item">
                                        <i class="bi bi-clock"></i>
                                        <span><%= ticket.get("showtime") %></span>
                                    </div>
                                    <div class="info-item">
                                        <i class="bi bi-chair"></i>
                                        <span><%= String.join(", ", (List<String>)ticket.get("seats")) %></span>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="ticket-footer">
                                <img src="data:image/png;base64,<%= ticket.get("qrCode") %>" 
                                     alt="QR Code" 
                                     class="qr-code"
                                     title="<%= ticket.get("ticketId") %>">
                                <div class="ticket-number">
                                    <span>Ticket #</span>
                                    <strong><%= ticket.get("ticketId") %></strong>
                                </div>
                            </div>
                        </div>
                    <% }
                    } else { %>
                        <div class="no-tickets">
                            <i class="bi bi-ticket-detailed"></i>
                            <p>No tickets found</p>
                        </div>
                    <% } %>
                </div>
            </div>
        </section>
    </div>
    
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/bookings.js"></script>
    
    <!-- Confirmation Modal -->
    <div class="confirm-modal" id="confirmModal">
        <div class="confirm-modal-content">
            <div class="confirm-modal-header">
                <i class="bi bi-exclamation-triangle"></i>
                <h3>Confirm Deletion</h3>
            </div>
            <div class="confirm-modal-body">
                <p>Are you sure you want to delete the selected booking(s)?</p>
                <p class="selected-count"></p>
            </div>
            <div class="confirm-modal-actions">
                <button class="modal-btn modal-btn-cancel" onclick="closeConfirmModal()">
                    Cancel
                </button>
                <button class="modal-btn modal-btn-confirm" onclick="confirmDelete()">
                    Delete
                </button>
            </div>
        </div>
    </div>
    <script src="/js/script.js"></script>
</body>
</html> 