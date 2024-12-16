<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="service.Review" %>
<%@ page import="java.util.*" %>
<%
    Review reviewService = new Review();
    List<Map<String, Object>> recentReviews = reviewService.getLatestReviews(5); // Get 5 recent reviews
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Share Your Experience - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
    <link rel="stylesheet" href="/css/reviews.css">
</head>
<body>
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="reviews-wrapper">
        <div class="container">
            <div class="reviews-grid">
                <!-- Review Form Section -->
                <div class="review-form-section">
                    <h2>Share Your Experience</h2>
                    <p class="subtitle">Let us know what you think about ABC Cinema</p>
                    
                    <form id="reviewForm" class="review-form">
                        <div class="form-group">
                            <label for="movieSelect" style="position:static;">
                                <i class="bi bi-film"></i>
                                Which movie did you watch?
                            </label>
                            <select id="movieSelect">
                                <option value="">General Review</option>
                                <!-- Movies will be loaded dynamically -->
                            </select>
                        </div>
                        
                        <div class="rating-group">
                            <label style="position:static;">
                                <i class="bi bi-star"></i>
                                How would you rate your experience?
                            </label>
                            <div class="star-rating">
                                <input type="radio" name="rating" value="5" id="star5">
                                <label for="star5" title="5 stars"><i class="bi bi-star-fill"></i></label>
                                
                                <input type="radio" name="rating" value="4" id="star4">
                                <label for="star4" title="4 stars"><i class="bi bi-star-fill"></i></label>
                                
                                <input type="radio" name="rating" value="3" id="star3">
                                <label for="star3" title="3 stars"><i class="bi bi-star-fill"></i></label>
                                
                                <input type="radio" name="rating" value="2" id="star2">
                                <label for="star2" title="2 stars"><i class="bi bi-star-fill"></i></label>
                                
                                <input type="radio" name="rating" value="1" id="star1">
                                <label for="star1" title="1 star"><i class="bi bi-star-fill"></i></label>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="reviewTitle" style="position:static;">
                                <i class="bi bi-chat-square-quote"></i>
                                Give your review a title
                            </label>
                            <input type="text" id="reviewTitle" required 
                                   placeholder="Sum up your experience in a few words">
                        </div>
                        
                        <div class="form-group">
                            <label for="reviewText" style="position:static;">
                                <i class="bi bi-pencil-square"></i>
                                Write your review
                            </label>
                            <textarea id="reviewText" required 
                                    placeholder="Tell us about your experience at ABC Cinema"></textarea>
                        </div>
                        
                        <button type="submit" class="submit-btn">
                            <i class="bi bi-send"></i>
                            Submit Review
                        </button>
                    </form>
                </div>
                
                <!-- Recent Reviews Section -->
                <div class="recent-reviews-section">
                    <h3>
                        <i class="bi bi-clock-history"></i>
                        Recent Reviews
                    </h3>
                    <div class="recent-reviews">
                        <% if (recentReviews != null && !recentReviews.isEmpty()) { %>
                            <% for (Map<String, Object> review : recentReviews) { %>
                                <div class="review-item">
                                    <div class="review-header">
                                        <div class="reviewer-info">
                                            <div class="reviewer-avatar">
                                                <img src="/images/default-avatar.png" alt="<%= review.get("name") %>">
                                            </div>
                                            <div class="reviewer-details">
                                                <h4><%= review.get("name") %></h4>
                                                <span class="review-date">
                                                    <i class="bi bi-calendar3"></i>
                                                    <%= review.get("createdAt").toString().split(" ")[0] %>
                                                </span>
                                            </div>
                                        </div>
                                        <div class="review-rating">
                                            <% int rating = ((Number) review.get("rating")).intValue(); %>
                                            <% for (int i = 0; i < rating; i++) { %>
                                                <i class="bi bi-star-fill"></i>
                                            <% } %>
                                            <% for (int i = rating; i < 5; i++) { %>
                                                <i class="bi bi-star"></i>
                                            <% } %>
                                        </div>
                                    </div>
                                    
                                    <div class="review-content" style="height:auto;">
                                        <h5 class="review-title"><%= review.get("title") %></h5>
                                        <p class="review-text"><%= review.get("text") %></p>
                                        <% if (review.get("movieTitle") != null) { %>
                                            <div class="movie-tag">
                                                <i class="bi bi-film"></i>
                                                <%= review.get("movieTitle") %>
                                            </div>
                                        <% } %>
                                    </div>
                                </div>
                            <% } %>
                        <% } else { %>
                            <div class="no-reviews">
                                <i class="bi bi-chat-square-text"></i>
                                <p>No reviews yet. Be the first to share your experience!</p>
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/reviews.js"></script>
    <script src="/js/script.js"></script>
</body>
</html> 