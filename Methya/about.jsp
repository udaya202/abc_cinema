<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - ABC Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        /* Enhanced About Page Styles */
        .about-hero {
            position: relative;
            height: 70vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: url('/images/about-hero.jpg') center/cover;
            overflow: hidden;
        }

        .about-hero::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(
                to bottom,
                rgba(0, 0, 0, 0.7),
                rgba(0, 0, 0, 0.5)
            );
        }

        .about-hero-content {
            position: relative;
            text-align: center;
            color: #fff;
            max-width: 800px;
            padding: 0 20px;
            animation: fadeInUp 1s ease;
        }

        .about-hero-content h1 {
            font-size: 4rem;
            margin-bottom: 20px;
            font-weight: 700;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }

        .about-hero-content p {
            font-size: 1.5rem;
            opacity: 0.9;
            text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
        }

        /* Mission Section */
        .mission-section {
            padding: 100px 0;
            background: linear-gradient(135deg, #1a1a2e, #16213e);
        }

        .mission-content {
            text-align: center;
            max-width: 800px;
            margin: 0 auto 60px;
        }

        .mission-content h2 {
            font-size: 2.5rem;
            margin-bottom: 30px;
            color: var(--primary-color);
        }

        .mission-content p {
            font-size: 1.2rem;
            line-height: 1.8;
            color: #fff;
            opacity: 0.9;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 30px;
            max-width: 1200px;
            margin: 0 auto;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.05);
            padding: 40px 30px;
            border-radius: 20px;
            text-align: center;
            transition: transform 0.3s ease;
            border: 1px solid rgba(255, 255, 255, 0.1);
            cursor: pointer;
        }

        .stat-card:hover {
            transform: translateY(-10px);
            background: rgba(255, 255, 255, 0.1);
        }

        .stat-number {
            font-size: 3rem;
            font-weight: 700;
            color: var(--primary-color);
            margin-bottom: 15px;
            display: block;
        }

        .stat-label {
            font-size: 1.1rem;
            color: #fff;
            opacity: 0.8;
        }

        /* Timeline Section */
        .timeline-section {
            padding: 100px 0;
            background: var(--background-dark);
        }

        .timeline-section h2 {
            text-align: center;
            font-size: 2.5rem;
            margin-bottom: 60px;
            color: #fff;
        }

        .timeline {
            max-width: 1000px;
            margin: 0 auto;
            position: relative;
        }

        .timeline::before {
            content: '';
            position: absolute;
            left: 50%;
            transform: translateX(-50%);
            width: 2px;
            height: 100%;
            background: var(--primary-color);
        }

        .timeline-item {
            margin-bottom: 60px;
            position: relative;
            width: 50%;
            padding: 0 40px;
        }

        .timeline-item:nth-child(odd) {
            left: 0;
        }

        .timeline-item:nth-child(even) {
            left: 50%;
        }

        .timeline-content {
            background: rgba(255, 255, 255, 0.05);
            padding: 30px;
            border-radius: 15px;
            position: relative;
            transition: transform 0.3s ease;
        }

        .timeline-content:hover {
            transform: scale(1.05);
        }

        .timeline-content::before {
            content: '';
            position: absolute;
            width: 20px;
            height: 20px;
            background: var(--primary-color);
            border-radius: 50%;
            top: 50%;
            transform: translateY(-50%);
        }

        .timeline-item:nth-child(odd) .timeline-content::before {
            right: -50px;
        }

        .timeline-item:nth-child(even) .timeline-content::before {
            left: -50px;
        }

        .timeline-content h3 {
            font-size: 1.8rem;
            color: var(--primary-color);
            margin-bottom: 15px;
        }

        .timeline-content p {
            font-size: 1.1rem;
            color: #fff;
            opacity: 0.9;
        }

        /* Team Section */
        .team-section {
            padding: 100px 0;
            background: linear-gradient(135deg, #16213e, #1a1a2e);
        }

        .team-section h2 {
            text-align: center;
            font-size: 2.5rem;
            margin-bottom: 60px;
            color: #fff;
        }

        .team-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 40px;
            max-width: 1200px;
            margin: 0 auto;
        }

        .team-member {
            text-align: center;
            transition: transform 0.3s ease;
        }

        .team-member:hover {
            transform: translateY(-10px);
        }

        .member-image {
            width: 200px;
            height: 200px;
            border-radius: 50%;
            overflow: hidden;
            margin: 0 auto 20px;
            border: 3px solid var(--primary-color);
            transition: transform 0.3s ease;
        }

        .member-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.3s ease;
        }

        .team-member:hover .member-image img {
            transform: scale(1.1);
        }

        .team-member h3 {
            font-size: 1.5rem;
            color: #fff;
            margin-bottom: 10px;
        }

        .team-member p {
            color: var(--primary-color);
            font-size: 1.1rem;
            opacity: 0.9;
        }

        /* Animations */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Responsive Design */
        @media (max-width: 992px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .team-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 768px) {
            .about-hero-content h1 {
                font-size: 3rem;
            }

            .timeline::before {
                left: 30px;
            }

            .timeline-item {
                width: 100%;
                padding-left: 70px;
            }

            .timeline-item:nth-child(even) {
                left: 0;
            }

            .timeline-content::before {
                left: -40px !important;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .team-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <%@ include file="includes/navbar.jsp" %>
    
    <!-- About Hero Section -->
    <section class="about-hero">
        <div class="about-hero-content">
            <h1>Our Story</h1>
            <p>Redefining the cinema experience since 1995</p>
        </div>
        <div class="hero-overlay"></div>
    </section>
    
    <!-- Mission Section -->
    <section class="mission-section">
        <div class="container">
            <div class="mission-content">
                <h2>Our Mission</h2>
                <p>To provide an unparalleled cinematic experience through cutting-edge technology, 
                   exceptional comfort, and outstanding service.</p>
            </div>
            <div class="stats-grid">
                <div class="stat-card">
                    <span class="stat-number" data-value="25">25</span>
                    <span class="stat-label">Years of Excellence</span>
                </div>
                <div class="stat-card">
                    <span class="stat-number" data-value="500000">500000</span>
                    <span class="stat-label">Happy Customers</span>
                </div>
                <div class="stat-card">
                    <span class="stat-number" data-value="1000">1000</span>
                    <span class="stat-label">Movies Screened</span>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Timeline Section -->
    <section class="timeline-section">
        <div class="container">
            <h2>Our Journey</h2>
            <div class="timeline">
                <div class="timeline-item">
                    <div class="timeline-content">
                        <h3>1995</h3>
                        <p>Founded ABC Cinema with a single screen</p>
                    </div>
                </div>
                <div class="timeline-item">
                    <div class="timeline-content">
                        <h3>2005</h3>
                        <p>Expanded to 5 screens with digital projection</p>
                    </div>
                </div>
                <div class="timeline-item">
                    <div class="timeline-content">
                        <h3>2015</h3>
                        <p>Introduced IMAX and 4K projection</p>
                    </div>
                </div>
                <div class="timeline-item">
                    <div class="timeline-content">
                        <h3>2024</h3>
                        <p>Launched mobile app and premium experiences</p>
                    </div>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Team Section -->
    <section class="team-section">
        <div class="container">
            <h2>Leadership Team</h2>
            <div class="team-grid">
                <div class="team-member">
                    <div class="member-image">
                        <img src="/images/ceo.jpg" alt="CEO">
                    </div>
                    <h3>John Smith</h3>
                    <p>Chief Executive Officer</p>
                </div>
                <div class="team-member">
                    <div class="member-image">
                        <img src="/images/cto.jpg" alt="CTO">
                    </div>
                    <h3>Sarah Johnson</h3>
                    <p>Chief Technology Officer</p>
                </div>
                <div class="team-member">
                    <div class="member-image">
                        <img src="/images/coo.jpg" alt="COO">
                    </div>
                    <h3>Michael Chen</h3>
                    <p>Chief Operations Officer</p>
                </div>
            </div>
        </div>
    </section>
    
    
    <!-- Footer -->
    <%@ include file="includes/footer.jsp" %>
    
    <script src="/js/script.js"></script>
</body>
</html> 