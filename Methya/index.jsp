<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ABC Cinema - Experience Movies Like Never Before</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <!-- Hero Section -->
    <section class="hero">
        <div class="hero-content">
            <h1>Experience Cinema at Its Finest</h1>
            <p>Immerse yourself in the world of entertainment with cutting-edge technology and ultimate comfort.</p>
            <div class="hero-buttons">
                <a href="/movies" class="primary-btn">Now Showing</a>
                <a href="/movies#upcoming" class="secondary-btn">Coming Soon</a>
            </div>
        </div>
        <div class="hero-overlay"></div>
    </section>

    
    <!-- Features Section -->
    <section class="features">
        <div class="feature-card">
            <i class="bi bi-display"></i>
            <h3>4K Projection</h3>
            <p>Crystal clear picture quality with state-of-the-art 4K projectors.</p>
        </div>
        <div class="feature-card">
            <i class="bi bi-music-note-beamed"></i>
            <h3>Dolby Atmos</h3>
            <p>Immersive sound experience that puts you in the action.</p>
        </div>
        <div class="feature-card">
            <i class="bi bi-person-wheelchair"></i>
            <h3>Luxury Seating</h3>
            <p>Premium recliner seats for ultimate comfort.</p>
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
    
    <!-- User Reviews Section -->
    <%@ page import="service.Review" %>
    <%@ page import="java.util.*" %>
    <%
        Review reviewService = new Review();
        List<Map<String, Object>> initialReviews = reviewService.getLatestReviews(10);
        request.setAttribute("initialReviews", initialReviews);
    %>
    
    <section class="reviews">
        <div class="section-header">
            <h2>What Our Visitors Say</h2>
            <a href="/reviews" class="view-all">Share Your Experience <i class="bi bi-arrow-right"></i></a>
        </div>
        
        <div class="reviews-container">
            <button class="carousel-btn prev" id="prevReview" disabled>
                <i class="bi bi-chevron-left"></i>
            </button>
            
            <div class="review-carousel">
                <div class="review-track">
                    <% if (initialReviews != null && !initialReviews.isEmpty()) { %>
                        <% for (Map<String, Object> review : initialReviews) { %>
                            <div class="review-card" style="filter: blur(0);opacity: 0.8;;">
                                <div class="review-content">
                                    <div class="review-quote">"</div>
                                    <p class="review-text"><%= review.get("text") %></p>
                                    <div class="reviewer-info">
                                        <div class="reviewer-avatar">
                                            <img src="/images/default-avatar.png" alt="<%= review.get("name") %>">
                                        </div>
                                        <div class="reviewer-details">
                                            <h4><%= review.get("name") %></h4>
                                            <% if (review.get("movieTitle") != null) { %>
                                                <span class="movie-name">
                                                    <i class="bi bi-film"></i>
                                                    <%= review.get("movieTitle") %>
                                                </span>
                                            <% } %>
                                            <div class="review-rating">
                                                <% int rating = ((Number) review.get("rating")).intValue(); %>
                                                <% for (int i = 0; i < rating; i++) { %>
                                                    <i class="bi bi-star-fill star"></i>
                                                <% } %>
                                                <% for (int i = rating; i < 5; i++) { %>
                                                    <i class="bi bi-star star"></i>
                                                <% } %>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <div class="review-card active">
                            <div class="review-content">
                                <p class="review-text">No reviews yet. Be the first to share your experience!</p>
                            </div>
                        </div>
                    <% } %>
                </div>
            </div>
            
            <button class="carousel-btn next" id="nextReview">
                <i class="bi bi-chevron-right"></i>
            </button>
        </div>
        
        <div class="review-indicators"></div>
    </section>
    
    <!-- Footer -->
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/script.js"></script>
    <script>
        window.initialReviews = <%= new com.google.gson.Gson().toJson(initialReviews) %>;
        console.log('Initial reviews:', window.initialReviews);
    </script>
</body>
</html>
