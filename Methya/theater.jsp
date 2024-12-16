<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Seats - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
    <link rel="stylesheet" href="/css/theater.css">
</head>
<body>
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <%
        Map<String, Object> theater = (Map<String, Object>) request.getAttribute("theater");
        if (theater != null) {
            Map<String, Object> movie = (Map<String, Object>) theater.get("movie");
    %>
    
    <!-- Theater Layout Section -->
    <section class="theater-section">
        <div class="container">
            <!-- Movie Info Bar -->
            <div class="movie-info-bar">
                <div class="movie-basic-info">
                    <h2><%= movie.get("title") %></h2>
                    <div class="showtime-info">
                        <span><i class="bi bi-calendar3"></i> Today</span>
                        <span><i class="bi bi-clock"></i> <%= theater.get("showtime") %></span>
                    </div>
                </div>
                <div class="seat-legend">
                    <div class="legend-item">
                        <span class="seat-demo available"></span>
                        <span>Available</span>
                    </div>
                    <div class="legend-item">
                        <span class="seat-demo selected"></span>
                        <span>Selected</span>
                    </div>
                    <div class="legend-item">
                        <span class="seat-demo booked"></span>
                        <span>Booked</span>
                    </div>
                </div>
            </div>
            
            <!-- Screen -->
            <div class="screen-container">
                <div class="screen"></div>
                <div class="screen-text">SCREEN</div>
            </div>
            
            <!-- Theater Layout -->
            <div class="theater-layout">
                <!-- Normal Section (Front) -->
                <div class="section normal">
                    <h3>NORMAL - LKR <%= theater.get("normalPrice") %></h3>
                    <div class="seats-container" id="normalSeats">
                        <!-- Seats will be dynamically loaded -->
                    </div>
                </div>
                
                <!-- Balcony Section (Back) -->
                <div class="section balcony">
                    <h3>BALCONY - LKR <%= theater.get("balconyPrice") %></h3>
                    <div class="seats-container" id="balconySeats">
                        <!-- Seats will be dynamically loaded -->
                    </div>
                </div>
            </div>
            
            <!-- Booking Summary -->
            <div class="booking-summary">
                <div class="summary-content">
                    <!-- Initial Summary View -->
                    <div class="initial-view">
                        <div class="selected-seats">
                            <h4>Selected Seats</h4>
                            <div id="selectedSeatsList"></div>
                        </div>
                        <div class="price-summary">
                            <div class="total-amount">
                                <span>Total Amount</span>
                                <span id="totalPrice">LKR 0.00</span>
                            </div>
                        </div>
                        <button id="bookNowBtn" class="book-now-btn" disabled>
                            Book Now
                        </button>
                    </div>
                    
                    <!-- Confirmation View (Initially Hidden) -->
                    <div class="confirmation-view" style="display: none;">
                        <div class="booking-details">
                            <h3>Confirm Your Booking</h3>
                            <div class="booking-info">
                                <!-- Will be populated dynamically -->
                            </div>
                        </div>
                        <div class="action-buttons">
                            <button class="back-btn">
                                <i class="bi bi-arrow-left"></i> Back
                            </button>
                            <button class="pay-now-btn">
                                <i class="bi bi-credit-card"></i> Pay Now
                            </button>
                            <button class="pay-later-btn">
                                <i class="bi bi-clock-history"></i> Pay Later
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </section>
    
    
    <%
        } else {
    %>
    <div class="error-message">
        <i class="bi bi-exclamation-circle"></i>
        <p>Theater information not available</p>
        <a href="/movies" class="back-btn">Back to Movies</a>
    </div>
    <%
        }
    %>
    
    <!-- Footer -->
    <%@ include file="includes/footer.jsp" %>
    
    <script>
        // Pass server data to JavaScript
        const theaterData = {
            showtimeId: <%= theater.get("showtimeId") %>,
            movieId: <%= ((Map<String, Object>)theater.get("movie")).get("id") %>,
            movieTitle: "<%= ((Map<String, Object>)theater.get("movie")).get("title").toString().replace("\"", "\\'") %>",
            showtime: "<%= theater.get("showtime") %>",
            userId: <%= theater.get("userId") %>,
            balconyPrice: <%= theater.get("balconyPrice") %>,
            normalPrice: <%= theater.get("normalPrice") %>,
            bookedSeats: <%= theater.get("bookedSeats") %>
        };
    </script>
    <script src="/js/theater.js"></script>
    <script src="/js/script.js"></script>
</body>
</html> 