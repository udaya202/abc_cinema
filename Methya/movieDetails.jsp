<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movie Details - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
    <link rel="stylesheet" href="/css/movieDetails.css">
</head>
<body>
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <%
        Map<String, Object> movie = (Map<String, Object>) request.getAttribute("movie");
        if (movie != null) {
    %>
    
    <!-- Movie Hero Section -->
    <section class="movie-details-hero">
        <div class="movie-backdrop" style="background-image: url('/<%= movie.get("poster") %>')"></div>
        <div class="backdrop-overlay"></div>
        <div class="container">
            <div class="movie-details-content">
                <div class="movie-details-info">
                    <h1 class="movie-title"><%= movie.get("title") %></h1>
                    
                    <div class="movie-meta">
                        <span class="rating">
                            <i class="bi bi-star-fill"></i>
                            <strong>4.5</strong>/5
                        </span>
                        <span class="duration">
                            <i class="bi bi-clock"></i>
                            2h 30m
                        </span>
                        <span class="genre">
                            <i class="bi bi-film"></i>
                            Action, Adventure
                        </span>
                    </div>
                    
                    <div class="movie-synopsis">
                        <h3>Synopsis</h3>
                        <p><%= movie.get("description") %></p>
                    </div>
                    
                    <div class="movie-additional-info">
                        <div class="info-item">
                            <span class="label">Director</span>
                            <span class="value">Christopher Nolan</span>
                        </div>
                        <div class="info-item">
                            <span class="label">Cast</span>
                            <span class="value">Tom Cruise, Margot Robbie, Robert Downey Jr.</span>
                        </div>
                        <div class="info-item">
                            <span class="label">Release Date</span>
                            <span class="value">March 15, 2024</span>
                        </div>
                    </div>
                </div>
                
                <div class="movie-poster-container">
                    <div class="movie-poster">
                        <img src="/<%= movie.get("poster") %>" alt="<%= movie.get("title") %>">
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Showtimes Section -->
    <section class="showtimes-section">
        <div class="container">
            <div class="section-header">
                <h2>Select Showtime</h2>
                <p class="date">Today, March 12</p>
            </div>
            
            <div class="showtimes-container">
                <%
                    List<Map<String, String>> showtimes = (List<Map<String, String>>) movie.get("showtimes");
                    if (showtimes != null && !showtimes.isEmpty()) {
                        for (Map<String, String> showtime : showtimes) {
                %>
                    <button class="showtime-btn" onclick="selectShowtime('<%= showtime.get("id") %>')">
                        <div class="time">
                            <i class="bi bi-clock"></i>
                            <span><%= showtime.get("time") %></span>
                        </div>
                        <div class="availability">
                            <i class="bi bi-circle-fill"></i>
                            Available
                        </div>
                    </button>
                <%
                        }
                    } else {
                %>
                    <div class="no-showtimes">
                        <i class="bi bi-calendar-x"></i>
                        <p>No showtimes available for today</p>
                    </div>
                <%
                    }
                %>
            </div>
        </div>
    </section>
    
    <%
        } else {
    %>
    <div class="error-message">
        <i class="bi bi-exclamation-circle"></i>
        <p>Movie not found</p>
        <a href="/movies" class="back-btn">Back to Movies</a>
    </div>
    <%
        }
    %>
    
    <!-- Footer -->
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/movieDetails.js"></script>
    <script src="/js/script.js"></script>
</body>
</html> 