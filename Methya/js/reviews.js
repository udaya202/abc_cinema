document.addEventListener('DOMContentLoaded', () => {
    initializeReviewCarousel();
    initializeReviewForm();
});

// Add at the beginning of the file
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : 'x-circle'}"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(notification);
    setTimeout(() => notification.classList.add('show'), 10);
    
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Review Carousel
function initializeReviewCarousel() {
    const track = document.querySelector('.review-track');
    const prevBtn = document.getElementById('prevReview');
    const nextBtn = document.getElementById('nextReview');
    const indicators = document.querySelector('.review-indicators');
    
    if (!track) return;
    
    let currentIndex = 0;
    let reviews = window.initialReviews || [];
    
    console.log('Initializing carousel with reviews:', reviews);
    
    if (reviews && reviews.length > 0) {
        renderReviews();
        createIndicators();
        updateCarousel();
    }
    
    function renderReviews() {
        if (!reviews || reviews.length === 0) {
            track.innerHTML = `
                <div class="review-card active">
                    <div class="review-content">
                        <p class="review-text">No reviews yet. Be the first to share your experience!</p>
                    </div>
                </div>
            `;
            return;
        }

        track.innerHTML = reviews.map((review, index) => `
            <div class="review-card ${index === currentIndex ? 'active' : ''}">
                <div class="review-content">
                    <div class="review-quote">"</div>
                    <p class="review-text">${review.text}</p>
                    <div class="reviewer-info">
                        <div class="reviewer-avatar">
                            <img src="${review.avatar || '/images/default-avatar.png'}" 
                                 alt="${review.name}">
                        </div>
                        <div class="reviewer-details">
                            <h4>${review.name}</h4>
                            ${review.movieTitle ? `
                                <span class="movie-name">
                                    <i class="bi bi-film"></i>
                                    ${review.movieTitle}
                                </span>
                            ` : ''}
                            <div class="review-rating">
                                ${Array(review.rating).fill('<i class="bi bi-star-fill star"></i>').join('')}
                                ${Array(5 - review.rating).fill('<i class="bi bi-star star"></i>').join('')}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    }
    
    function createIndicators() {
        indicators.innerHTML = reviews.map((_, index) => `
            <div class="indicator ${index === currentIndex ? 'active' : ''}" 
                 data-index="${index}"></div>
        `).join('');
        
        // Add click handlers to indicators
        document.querySelectorAll('.indicator').forEach(indicator => {
            indicator.addEventListener('click', () => {
                currentIndex = parseInt(indicator.dataset.index);
                updateCarousel();
            });
        });
    }
    
    function updateCarousel() {
        const cards = document.querySelectorAll('.review-card');
        const offset = currentIndex * -(100 / 3); // Show 3 cards at a time
        track.style.transform = `translateX(${offset}%)`;
        
        // Update active states
        cards.forEach((card, index) => {
            card.classList.toggle('active', index === currentIndex);
        });
        
        // Update indicators
        document.querySelectorAll('.indicator').forEach((indicator, index) => {
            indicator.classList.toggle('active', index === currentIndex);
        });
        
        // Update button states
        if (prevBtn) prevBtn.disabled = currentIndex === 0;
        if (nextBtn) nextBtn.disabled = currentIndex >= cards.length - 1;
    }
    
    // Event Listeners
    prevBtn?.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--;
            updateCarousel();
        }
    });
    
    nextBtn?.addEventListener('click', () => {
        if (currentIndex < reviews.length - 1) {
            currentIndex++;
            updateCarousel();
        }
    });
}

// Review Form
function initializeReviewForm() {
    const form = document.getElementById('reviewForm');
    if (!form) return;
    
    console.log('Initializing review form...'); // Debug log
    
    // Load movies for dropdown
    loadMovies().then(movies => {
        console.log('Loaded movies:', movies); // Debug log
        const select = document.getElementById('movieSelect');
        if (select && movies && movies.length > 0) {
            movies.forEach(movie => {
                const option = document.createElement('option');
                option.value = movie.id;
                option.textContent = movie.title;
                select.appendChild(option);
            });
        }
    }).catch(error => {
        console.error('Error loading movies:', error);
        showNotification('Failed to load movies', 'error');
    });
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        
        try {
            const rating = document.querySelector('input[name="rating"]:checked')?.value;
            if (!rating) {
                throw new Error('Please select a rating');
            }
            
            const formData = {
                movieId: document.getElementById('movieSelect').value || null,
                rating: parseInt(rating, 10),
                title: document.getElementById('reviewTitle').value.trim(),
                text: document.getElementById('reviewText').value.trim()
            };
            
            // Validate data
            if (!formData.title) throw new Error('Please enter a review title');
            if (!formData.text) throw new Error('Please enter your review');
            if (formData.rating < 1 || formData.rating > 5) throw new Error('Invalid rating');
            
            console.log('Submitting review:', formData);
            
            const response = await submitReview(formData);
            
            if (response.success) {
                showNotification('Review submitted successfully!', 'success');
                form.reset();
            } else {
                throw new Error(response.error || 'Failed to submit review');
            }
        } catch (error) {
            console.error('Error submitting review:', error);
            showNotification(error.message, 'error');
        } finally {
            submitBtn.disabled = false;
        }
    });
}

// API Functions
async function fetchReviews() {
    try {
        console.log('Fetching reviews...'); // Debug log
        const response = await fetch('/api/reviews');
        console.log('Response status:', response.status); // Debug log
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log('Received data:', data); // Debug log
        
        if (!data.success) {
            throw new Error(data.error || 'Failed to fetch reviews');
        }
        
        return data.reviews || [];
    } catch (error) {
        console.error('Error fetching reviews:', error);
        return [];
    }
}

async function loadMovies() {
    try {
        const response = await fetch('/api/movies/list'); // Updated endpoint
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        return data.movies || [];
    } catch (error) {
        console.error('Error loading movies:', error);
        return [];
    }
}

async function submitReview(formData) {
    try {
        const response = await fetch('/api/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        return await response.json();
    } catch (error) {
        throw new Error('Failed to submit review');
    }
} 