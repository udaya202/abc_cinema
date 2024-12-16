<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Us - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
</head>
<body>
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <!-- Contact Hero Section -->
    <section class="contact-hero">
        <div class="contact-hero-content">
            <h1>Get in Touch</h1>
            <p>We'd love to hear from you</p>
        </div>
        <div class="hero-overlay"></div>
    </section>
    
    <!-- Contact Section -->
    <section class="contact-section">
        <div class="container">
            <div class="contact-grid">
                <!-- Contact Information -->
                <div class="contact-info">
                    <h2>Contact Information</h2>
                    <div class="info-items">
                        <div class="info-item">
                            <i class="bi bi-geo-alt"></i>
                            <div>
                                <h3>Location</h3>
                                <p>123 Cinema Street, Colombo</p>
                            </div>
                        </div>
                        <div class="info-item">
                            <i class="bi bi-telephone"></i>
                            <div>
                                <h3>Phone</h3>
                                <p>+94 11 234 5678</p>
                            </div>
                        </div>
                        <div class="info-item">
                            <i class="bi bi-envelope"></i>
                            <div>
                                <h3>Email</h3>
                                <p>info@abccinema.com</p>
                            </div>
                        </div>
                        <div class="info-item">
                            <i class="bi bi-clock"></i>
                            <div>
                                <h3>Working Hours</h3>
                                <p>9:00 AM - 11:00 PM</p>
                            </div>
                        </div>
                    </div>
                    <div class="social-links">
                        <a href="#"><i class="bi bi-facebook"></i></a>
                        <a href="#"><i class="bi bi-instagram"></i></a>
                        <a href="#"><i class="bi bi-twitter"></i></a>
                        <a href="#"><i class="bi bi-youtube"></i></a>
                    </div>
                </div>
                
                <!-- Contact Form -->
                <div class="contact-form-container">
                    <h2>Send us a Message</h2>
                    <form id="contactForm" class="contact-form">
                        <div class="form-group">
                            <input type="text" id="name" required>
                            <label for="name">Your Name</label>
                        </div>
                        <div class="form-group">
                            <input type="email" id="email" required>
                            <label for="email">Your Email</label>
                        </div>
                        <div class="form-group">
                            <select id="subject" required>
                                <option value="">Select Subject</option>
                                <option value="general">General Inquiry</option>
                                <option value="booking">Booking Issue</option>
                                <option value="feedback">Feedback</option>
                                <option value="other">Other</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <textarea id="message" required></textarea>
                            <label for="message">Your Message</label>
                        </div>
                        <button type="submit" class="submit-btn">
                            <i class="bi bi-send"></i>
                            <span>Send Message</span>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Map Section -->
    <section class="map-section">
        <div class="container">
            <div class="map-container">
                <iframe 
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3961.575840369662!2d79.85777147486577!3d6.821493193176254!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3ae25bc5b8555555%3A0xa291a7918c6c8c89!2sColombo%2C%20Sri%20Lanka!5e0!3m2!1sen!2sus!4v1709667547736!5m2!1sen!2sus"
                    width="100%" 
                    height="450" 
                    style="border:0;" 
                    allowfullscreen="" 
                    loading="lazy" 
                    referrerpolicy="no-referrer-when-downgrade">
                </iframe>
            </div>
        </div>
    </section>
    
    <!-- Footer -->
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/script.js"></script>
</body>
</html> 