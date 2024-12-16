<%--
  Created by IntelliJ IDEA.
  User: Adithya Sandew
  Date: 12/8/2024
  Time: 11:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page session="true" %>
<%@ page import="java.util.List" %>
<%@ page import="service.Admin" %>
<%@ page import="java.util.Map" %>

<%
    // Check if the user is logged in
    Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");

    // If the user is not logged in, redirect to the login page
    if (isLoggedIn == null || !isLoggedIn) {
        response.sendRedirect("adminlogin.jsp"); // Redirect to login page
        return; // Stop further processing of the page
    }
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Admin adminService = new Admin();
    List<String[]> showtimeOptions = adminService.getShowtimes(); // For dropdown options
%>
<html>
<head>
    <title>Theater Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/adminPanel.css">
</head>
<body>
<div class="container">
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="logo">
            <i class="bi bi-film"></i>
            <span>Theater Admin</span>
        </div>
        <nav>
            <a href="/admin/manageMovies" class="nav-item" onclick="refreshPage()" data-page="movies">
                <i class="bi bi-film"></i>
                <span>Manage Movies</span>
            </a>
            <a href="#" class="nav-item" data-page="bookings">
                <i class="bi bi-ticket-perforated"></i>
                <span>Bookings</span>
            </a>

            <%--            <script>onclick="navigateToManageMovies()--%>
            <%--                function navigateToManageMovies() {--%>
            <%--                    window.location.href = '/admin/manageMovies'; // Redirects to the manageMovies endpoint--%>
            <%--                }--%>
            <%--            </script>--%>
            <a href="//admin/manageTheater" class="nav-item" data-page="theater">
                <i class="bi bi-building"></i>
                <span>Manage Theater</span>
            </a>
            <a href="/admin/messages/" class="nav-item" data-page="messages">
                <i class="bi bi-envelope"></i>
                <span>Contact Messages</span>
            </a>
            <a href="/admin/reviews/" class="nav-item" data-page="reviews">
                <i class="bi bi-star"></i>
                <span>Reviews</span>
            </a>
<%--            <div class="tab-button" data-tab="reviews">--%>
<%--                <i class="bi bi-star"></i>--%>
<%--                Reviews--%>
<%--            </div>--%>

        </nav>
        <a href="/logout" class="nav-item logout">
            <i class="bi bi-door-open"></i>
            <span>Logout</span>
        </a>
    </div>
    <style>
        .logout {
            position:absolute;
            bottom: 0;
            margin-bottom: 10px;
            background-color: var(--primary-color);
            color: var(--box-background-color);
            align-self: center;
        }

        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid transparent;
            border-radius: 4px;
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            min-width: 300px;
            animation: slideIn 0.5s ease-out;
        }

        .alert-success {
            color: #155724;
            background-color: #d4edda;
            border-color: #c3e6cb;
        }

        .alert-error {
            color: #721c24;
            background-color: #f8d7da;
            border-color: #f5c6cb;
        }

        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
    </style>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Content pages -->
        <div id="bookings" class="page">
            <div class="page-header">
                <h2>Bookings Management</h2>
                <div class="header-actions">
                    <button class="delete-selected-btn" id="deleteSelectedBookings" disabled>
                        <i class="bi bi-trash"></i>
                        Delete Selected
                    </button>
                </div>
            </div>

            <div class="filter-section">
                <div class="filter-group">
                    <select id="movieFilter" class="filter-select">
                        <option value="">All Movies</option>
                        <!-- Movies will be loaded dynamically -->
                    </select>
                    
                    <select id="showtimeFilter" class="filter-select">
                        <option value="">All Showtimes</option>
                        <!-- Showtimes will be loaded dynamically -->
                    </select>
                    
                    <select id="statusFilter" class="filter-select">
                        <option value="">All Status</option>
                        <option value="pending_payment">Pending Payment</option>
                        <option value="paid">Paid</option>
                    </select>
                    
                    <button class="apply-filter-btn" onclick="applyBookingFilters()">
                        <i class="bi bi-funnel"></i>
                        Apply Filters
                    </button>
                </div>
            </div>

            <div class="bookings-table-container">
                <div class="table-header">
                    <div class="select-all-wrapper">
                        <input type="checkbox" id="selectAllBookings">
                        <label for="selectAllBookings">Select All</label>
                    </div>
                    <div class="table-actions">
                        <button class="refresh-btn" onclick="loadBookings()">
                            <i class="bi bi-arrow-clockwise"></i>
                            Refresh
                        </button>
                    </div>
                </div>

                <div class="bookings-table">
                    <table>
                        <thead>
                            <tr>
                                <th width="40px"></th>
                                <th>Ticket #</th>
                                <th>Movie</th>
                                <th>Customer</th>
                                <th>Showtime</th>
                                <th>Seat</th>
                                <th>Amount</th>
                                <th>Status</th>
                                <th>Booking Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="bookingsTableBody">
                            <!-- Bookings will be loaded dynamically -->
                        </tbody>
                    </table>
                </div>

                <div class="table-footer">
                    <div class="pagination">
                        <button class="page-btn" onclick="previousBookingPage()">
                            <i class="bi bi-chevron-left"></i>
                        </button>
                        <span id="pageInfo">Page 1 of 1</span>
                        <button class="page-btn" onclick="nextBookingPage()">
                            <i class="bi bi-chevron-right"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div id="movies" class="page active">
            <div class="header">
                <h1>Manage Movies</h1>
                <button class="add-movie-btn" onclick="showAddMovieModal()">
                    <i class="bi bi-plus-lg"></i> Add New Movie
                </button>
            </div>

            <%--            <div class="movies-grid">--%>
            <%--                <!-- Movie cards will be dynamically added here -->--%>
            <%--                <!-- Example card structure -->--%>
            <%--                <div class="movie-card">--%>
            <%--                    <img src="" alt="Movie Poster">--%>
            <%--                    <div class="movie-info">--%>
            <%--                        <h3>Movie Title</h3>--%>
            <%--                        <p class="description">Movie description goes here...</p>--%>
            <%--                        <div class="showtimes">--%>
            <%--                            <span class="time-badge">10:00 AM</span>--%>
            <%--                            <span class="time-badge">2:30 PM</span>--%>
            <%--                        </div>--%>
            <%--                    </div>--%>
            <%--                    <div class="actions">--%>
            <%--                        <button onclick="editMovie(1)" class="edit-btn">--%>
            <%--                            <i class="bi bi-pencil"></i>--%>
            <%--                        </button>--%>
            <%--                        <button onclick="deleteMovie(1)" class="delete-btn">--%>
            <%--                            <i class="bi bi-trash"></i>--%>
            <%--                        </button>--%>
            <%--                    </div>--%>
            <%--                </div>--%>
            <%--            </div>--%>

            <div class="movies-grid">
                <%
                    List<Map<String, Object>> movies = (List<Map<String, Object>>) request.getAttribute("movies");
                    if (movies != null && !movies.isEmpty()) {
                        for (Map<String, Object> movie : movies) {
                %>
                <div class="movie-card" data-movie-id="<%= movie.get("id") %>">
                    <img src="/<%= movie.get("poster") %>" alt="Movie Poster">
                    <div class="movie-info">
                        <h3><%= movie.get("title") %></h3>
                        <p class="description"><%= movie.get("description") %></p>
                        <div class="showtimes">
                            <%
                                String showtime = (String) movie.get("showtimes");
                                if (showtime != null) {
                                    for (String time : showtime.split(", ")) {
                            %>
                            <span class="time-badge"><%= time %></span>
                            <%
                                    }
                                }
                            %>
                        </div>
                    </div>
                    <div class="actions">
                        <button onclick="editMovie('<%= movie.get("id") %>')" class="edit-btn">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button onclick="deleteMovie('<%= movie.get("id") %>')" class="delete-btn">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                <%
                    }
                } else {
                %>
                <p>No movies found!</p>
                <%
                    }
                %>
            </div>
        </div>

        <div id="theater" class="page">
            <div class="theater-container">
                <!-- Showtimes Management Section -->
                <div class="management-section">
                    <div class="section-header">
                        <h2><i class="bi bi-clock"></i> Manage Showtimes</h2>
                        <button class="action-btn" onclick="showShowtimeModal()">
                            <i class="bi bi-plus-lg"></i> Add Showtime
                        </button>
                    </div>
                    <div class="time-slots-grid">
                        <% 
                            List<Map<String, Object>> theaterShowtimes = (List<Map<String, Object>>) request.getAttribute("showtimes");
                            if (theaterShowtimes != null && !theaterShowtimes.isEmpty()) {
                                for (Map<String, Object> showtime : theaterShowtimes) {
                        %>
                        <div class="time-slot-card" data-showtime-id="<%= showtime.get("id") %>">
                            <div class="time-header">
                                <h3><%= showtime.get("displayTime") %></h3>
                                <div class="slot-actions">
                                    <button class="edit-btn" onclick="showShowtimeModal('<%= showtime.get("id") %>')">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    <button class="delete-btn" onclick="deleteShowtime('<%= showtime.get("id") %>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="slot-info">
                                <div class="time-details">
                                    <span class="time-badge">
                                        <i class="bi bi-hourglass-start"></i>
                                        Start: <%= showtime.get("startTime") %>
                                    </span>
                                    <span class="time-badge">
                                        <i class="bi bi-hourglass-end"></i>
                                        End: <%= showtime.get("endTime") %>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <% 
                                }
                            } else {
                        %>
                        <p>No showtimes found!</p>
                        <% } %>
                    </div>
                </div>

                <!-- Pricing Management Section -->
                <div class="management-section">
                    <div class="section-header">
                        <h2><i class="bi bi-tag"></i> Movie Pricing</h2>
                        <button class="action-btn" onclick="showPricingModal()">
                            <i class="bi bi-plus-lg"></i> Add Pricing Rule
                        </button>
                    </div>
                    <div class="pricing-grid">
                        <% 
                            List<Map<String, Object>> moviePricing = (List<Map<String, Object>>) request.getAttribute("moviePricing");
                            if (moviePricing != null && !moviePricing.isEmpty()) {
                                for (Map<String, Object> pricing : moviePricing) {
                        %>
                        <div class="pricing-card" data-pricing-id="<%= pricing.get("movieId") %>">
                            <div class="pricing-header">
                                <h3><%= pricing.get("movieTitle") %></h3>
                                <div class="pricing-actions">
                                    <button class="edit-btn" onclick="editPricing('<%= pricing.get("movieId") %>')">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    <button class="delete-btn" onclick="deletePricing('<%= pricing.get("movieId") %>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="pricing-details">
                                <div class="price-row">
                                    <span>Standard Seats:</span>
                                    <span class="price">LKR <%= String.format("%.2f", pricing.get("normalPrice")) %></span>
                                </div>
                                <div class="price-row">
                                    <span>Premium Seats:</span>
                                    <span class="price">LKR <%= String.format("%.2f", pricing.get("balconyPrice")) %></span>
                                </div>
                            </div>
                            <div class="pricing-footer">
                                <span class="status-badge active">Active</span>
                                <% 
                                    String showtimes = (String) pricing.get("showtimes");
                                    boolean isAllShowtimes = showtimes != null && showtimes.split(",").length >= 3;
                                %>
                                <span class="time-badge"><%= isAllShowtimes ? "All Showtimes" : showtimes.split(",").length + " Showtimes" %></span>
                            </div>
                        </div>
                        <%
                                }
                            } else {
                        %>
                        <p>No pricing rules found!</p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

        <div id="messages" class="page">
            <div class="messages-container">
                <div class="section-header">
                    <h2><i class="bi bi-envelope"></i> Contact Messages</h2>
                    <button class="action-btn danger" onclick="clearAllMessages()">
                        <i class="bi bi-trash"></i> Clear All Messages
                    </button>
                </div>
                
                <div class="messages-grid">
                    <% 
                        List<Map<String, Object>> messages = (List<Map<String, Object>>) request.getAttribute("messages");
                        if (messages != null && !messages.isEmpty()) {
                            for (Map<String, Object> message : messages) {
                    %>
                    <div class="message-card" data-message-id="<%= message.get("id") %>">
                        <div class="message-header">
                            <h3><%= message.get("subject") %></h3>
                            <span class="timestamp"><%= message.get("created_at") %></span>
                        </div>
                        <div class="sender-info">
                            <span class="sender-name"><i class="bi bi-person"></i> <%= message.get("name") %></span>
                            <span class="sender-email"><i class="bi bi-envelope"></i> <%= message.get("email") %></span>
                        </div>
                        <div class="message-content">
                            <p><%= message.get("message") %></p>
                        </div>
                    </div>
                    <%
                            }
                        } else {
                    %>
                    <div class="no-messages">
                        <i class="bi bi-inbox"></i>
                        <p>No messages found!</p>
                    </div>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>

        <div id="reviews" class="page">
            <div class="section-header">
                <h2>Review Management</h2>
                <div class="review-stats">
                    <div class="stat-card">
                        <i class="bi bi-star-fill"></i>
                        <span id="averageRating">0.0</span>
                        <span>Average Rating</span>
                    </div>
                    <div class="stat-card">
                        <i class="bi bi-clock"></i>
                        <span id="pendingReviews">0</span>
                        <span>Pending</span>
                    </div>
                    <div class="stat-card">
                        <i class="bi bi-check-circle"></i>
                        <span id="approvedReviews">0</span>
                        <span>Approved</span>
                    </div>
                    <div class="stat-card">
                        <i class="bi bi-x-circle"></i>
                        <span id="rejectedReviews">0</span>
                        <span>Rejected</span>
                    </div>
                </div>
            </div>

            <div class="reviews-table-wrapper">
                <div class="table-filters">
                    <select id="statusFilter">
                        <option value="">All Status</option>
                        <option value="pending">Pending</option>
                        <option value="approved">Approved</option>
                        <option value="rejected">Rejected</option>
                    </select>
                    <input type="text" id="searchReviews" placeholder="Search reviews...">
                </div>

                <table class="reviews-table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>User</th>
                            <th>Movie</th>
                            <th>Rating</th>
                            <th>Review</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="reviewsTableBody">
                        <!-- Reviews will be loaded dynamically -->
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</div>

<!-- Add/Edit Movie Modal -->
<div id="movieModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Add New Movie</h2>
            <button class="close-btn" onclick="closeMovieModal()">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>
        <%--        action="/manageMovies"--%>
        <form id="movieForm" onsubmit="handleMovieSubmit(event)" enctype="multipart/form-data" method="post">
            <div class="form-group">
                <label for="movieName">Movie Name</label>
                <input type="text" id="movieName" name="title" required>
            </div>

            <div class="form-group">
                <label for="moviePoster">Movie Poster</label>
                <div class="file-upload">
                    <input type="file" id="moviePoster" name="poster" accept="image/*" required>
                    <label for="moviePoster" class="file-label">
                        <i class="bi bi-cloud-upload"></i>
                        <span>Choose File</span>
                    </label>
                </div>
            </div>

            <div class="form-group">
                <label for="movieDescription">Description</label>
                <textarea id="movieDescription" name="description" required></textarea>
            </div>

            <div class="form-group">
                <label for="showTimes">Show Times</label>
                <select id="showTimes" name="showtimeIds" multiple required>
                    <% for (String[] showtime : showtimeOptions) { %>
                    <option value="<%= showtime[0] %>"><%= showtime[1] %></option>
                    <% } %>
                </select>
            </div>
            <% String result = (String) request.getAttribute("result"); %>
            <% if (result != null) { %>
            <div class="alert <%= result.equals("success") ? "alert-success" : "alert-error" %>">
                <%= result.equals("success") ? "Movie saved successfully!" : "Failed to save the movie. Please try again." %>
            </div>
            <% } %>
            <button type="submit" class="submit-btn">Save Movie</button>
        </form>

    </div>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteConfirmModal" class="modal">
    <div class="modal-content delete-confirm-content">
        <div class="delete-icon">
            <i class="bi bi-exclamation-triangle"></i>
        </div>
        <h2>Confirm Delete</h2>
        <p>Are you sure you want to delete this movie? This action cannot be undone.</p>
        <div class="modal-actions">
            <button class="cancel-btn" onclick="closeDeleteModal()">Cancel</button>
            <button class="confirm-delete-btn" onclick="confirmDelete()">Delete</button>
        </div>
    </div>
</div>

<%--show time modal--%>
<div id="showtimeModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Add Showtime</h2>
            <button class="close-btn" onclick="closeShowtimeModal()">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>
        <form id="showtimeForm" onsubmit="handleShowtimeSubmit(event)">
            <div class="form-group">
                <label for="displayTime">Display Time (e.g., 2:30 PM IST)</label>
                <input type="text" id="displayTime" required placeholder="2:30 PM IST">
            </div>
            <div class="form-group">
                <label for="startTime">Start Time (24h format)</label>
                <input type="time" id="startTime" required>
            </div>
            <div class="form-group">
                <label for="endTime">End Time (24h format)</label>
                <input type="time" id="endTime" required>
            </div>
            <button type="submit" class="submit-btn">Save Showtime</button>
        </form>
    </div>
</div>

<!-- Movie Pricing Modal -->
<div id="pricingModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Set Movie Pricing</h2>
            <button class="close-btn" onclick="closePricingModal()">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>
        <form id="pricingForm" onsubmit="handlePricingSubmit(event)">
            <div class="form-group">
                <label for="movieSelect">Select Movie</label>
                <select id="movieSelect" required onchange="loadShowtimesForMovie(this.value)">
                    <option value="">Choose a movie...</option>
                </select>
            </div>
            <div class="form-group">
                <label for="normalPricing">Normal Seating Price (LKR)</label>
                <input type="number" id="normalPricing" min="0" step="0.01" required>
            </div>
            <div class="form-group">
                <label for="balconyPricing">Balcony Seating Price (LKR)</label>
                <input type="number" id="balconyPricing" min="0" step="0.01" required>
            </div>
            <div class="form-group">
                <label for="showtimeSelect">Apply to Showtimes</label>
                <select id="showtimeSelect" multiple required>
                    <!-- Showtimes will be loaded dynamically -->
                </select>
            </div>
            <button type="submit" class="submit-btn">Save Pricing</button>
        </form>
    </div>
</div>

<!-- Add this right before the closing body tag, alongside other modals -->
<div id="deleteConfirmModal" class="modal">
    <div class="modal-content">
        <h2>Confirm Delete</h2>
        <p>Are you sure you want to delete this review? This action cannot be undone.</p>
        <div class="modal-actions">
            <button class="cancel-btn" onclick="closeDeleteModal()">Cancel</button>
            <button class="confirm-delete-btn" onclick="confirmDelete()">Delete</button>
        </div>
    </div>
</div>

<script src="/js/adminPanel.js"></script>
<script src="/js/adminReviews.js"></script>

</body>
</html>