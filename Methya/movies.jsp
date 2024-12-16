<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movies - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body>
<script src="/js/movieDetails.js"></script>
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <!-- Movies Hero Section -->
    <section class="movies-hero">
        <div class="movies-hero-content">
            <h1>Now Showing</h1>
            <p>Experience the latest blockbusters in ultimate comfort</p>
        </div>
        <div class="hero-overlay"></div>
    </section>
    
    <!-- Now Showing Section -->
    <section class="movies-grid-section">
        <div class="container">
            <div class="movies-filter">
                <div class="filter-group">
                    <label>Sort By:</label>
                    <select id="sortFilter">
                        <option value="latest">Latest</option>
                        <option value="name">Name</option>
                        <option value="rating">Rating</option>
                    </select>
                </div>
            </div>
            
            <!-- Movies Grid -->
            <div class="movies-grid">
                <%
                    List<Map<String, Object>> movies = (List<Map<String, Object>>) request.getAttribute("movies");
                    if (movies != null && !movies.isEmpty()) {
                        for (Map<String, Object> movie : movies) {
                %>
                    <div class="movie-card" data-movie-id="<%= movie.get("id") %>">
                        <div class="movie-poster">
                            <img src="<%= movie.get("poster") %>" alt="<%= movie.get("title") %>">
                            <div class="movie-overlay">
                                <div class="movie-actions">
                                    <button class="book-btn" onclick="bookMovie(<%= movie.get("id") %>)">
                                        <i class="bi bi-ticket-perforated"></i>
                                        Book Tickets
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="movie-info">
                            <h3><%= movie.get("title") %></h3>
                            <p class="movie-description"><%= movie.get("description") %></p>
                            <div class="movie-showtimes">
                                <%
                                    List<String> showtimes = (List<String>) movie.get("showtimes");
                                    if (showtimes != null) {
                                        for (String showtime : showtimes) {
                                %>
                                    <span class="showtime-badge"><%= showtime %></span>
                                <%
                                        }
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                <%
                        }
                    } else {
                %>
                    <div class="no-movies">
                        <i class="bi bi-film"></i>
                        <p>No movies available at the moment.</p>
                    </div>
                <%
                    }
                %>
            </div>
        </div>
    </section>
    
    <!-- Coming Soon Section -->
    <section class="coming-soon">
        <div class="section-header">
            <h2>Coming Soon</h2>
            <a href="/movies#upcoming" class="view-all">View All <i class="bi bi-arrow-right"></i></a>
        </div>
        <div class="movie-grid">
            <!-- Movie Card 1 -->
            <div class="movie-card">
                <img src="/images/deadpool.jpg" alt="Deadpool 3" class="movie-poster">
                <div class="movie-info">
                    <h3 class="movie-title">Deadpool 3</h3>
                    <div class="movie-details">
                        <span><i class="bi bi-calendar3"></i> July 26, 2024</span>
                        <span><i class="bi bi-clock"></i> 2h 5min</span>
                    </div>
                </div>
            </div>
            
            <!-- Movie Card 2 -->
            <div class="movie-card">
                <img src="/images/joker.jpeg" alt="Joker: Folie à Deux" class="movie-poster">
                <div class="movie-info">
                    <h3 class="movie-title">Joker: Folie à Deux</h3>
                    <div class="movie-details">
                        <span><i class="bi bi-calendar3"></i> Oct 4, 2024</span>
                        <span><i class="bi bi-clock"></i> 2h 15min</span>
                    </div>
                </div>
            </div>
            
            <!-- Movie Card 3 -->
            <div class="movie-card">
                <img src="/images/kungfu-panda-4.png" alt="Kung Fu Panda 4" class="movie-poster">
                <div class="movie-info">
                    <h3 class="movie-title">Kung Fu Panda 4</h3>
                    <div class="movie-details">
                        <span><i class="bi bi-calendar3"></i> March 8, 2024</span>
                        <span><i class="bi bi-clock"></i> 1h 54min</span>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Footer -->
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/script.js"></script>
</body>
</html> 