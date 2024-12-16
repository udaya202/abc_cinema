// State management for reviews
const reviewState = {
    currentFilter: '',
    searchTerm: '',
    reviewToDelete: null
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('reviews')) {
        initializeReviewsPage();
    }
});

function initializeReviewsPage() {
    loadReviews();
    initializeFilters();
}

async function loadReviews() {
    try {
        const response = await fetch('/admin/reviews/list');
        const data = await response.json();
        
        if (data.success) {
            updateReviewsTable(data.reviews);
            updateStatistics(data.statistics);
        } else {
            showNotification('Failed to load reviews', 'error');
        }
    } catch (error) {
        console.error('Error loading reviews:', error);
        showNotification('Failed to load reviews', 'error');
    }
}

function updateReviewsTable(reviews) {
    const tbody = document.getElementById('reviewsTableBody');
    if (!reviews || reviews.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="no-data">
                    <i class="bi bi-chat-square-text"></i>
                    <p>No reviews found</p>
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = reviews
        .filter(review => filterReview(review))
        .map(review => `
            <tr>
                <td>${formatDate(review.createdAt)}</td>
                <td>${review.reviewerName}</td>
                <td>${review.movieTitle || 'General Review'}</td>
                <td>${generateStarRating(review.rating)}</td>
                <td class="review-text">${review.text}</td>
                <td>
                    <span class="status-badge ${review.status}">
                        ${capitalizeFirst(review.status)}
                    </span>
                </td>
                <td>
                    <div class="action-buttons">
                        ${review.status === 'pending' ? `
                            <button onclick="updateReviewStatus(${review.id})" 
                                    class="action-btn approve-btn" title="Approve">
                                <i class="bi bi-check-lg"></i>
                                Approve
                            </button>
                        ` : ''}
                        <button onclick="showDeleteConfirmModal(${review.id})" 
                                class="action-btn delete-btn" title="Delete">
                            <i class="bi bi-trash"></i>
                            Delete
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
}

function updateStatistics(stats) {
    document.getElementById('averageRating').textContent = stats.averageRating.toFixed(1);
    document.getElementById('pendingReviews').textContent = stats.pendingReviews;
    document.getElementById('approvedReviews').textContent = stats.approvedReviews;
    document.getElementById('rejectedReviews').textContent = stats.rejectedReviews;
}

function initializeFilters() {
    const statusFilter = document.getElementById('statusFilter');
    const searchInput = document.getElementById('searchReviews');

    statusFilter.addEventListener('change', () => {
        reviewState.currentFilter = statusFilter.value;
        loadReviews();
    });

    searchInput.addEventListener('input', (e) => {
        reviewState.searchTerm = e.target.value.toLowerCase();
        loadReviews();
    });
}

function filterReview(review) {
    const matchesStatus = !reviewState.currentFilter || 
                         review.status === reviewState.currentFilter;
    
    const matchesSearch = !reviewState.searchTerm || 
                         review.text.toLowerCase().includes(reviewState.searchTerm) ||
                         review.reviewerName.toLowerCase().includes(reviewState.searchTerm);
    
    return matchesStatus && matchesSearch;
}

// Delete Review Functions
function showDeleteConfirmModal(reviewId) {
    reviewState.reviewToDelete = reviewId;
    const modal = document.getElementById('deleteConfirmModal');
    if (modal) {
        modal.style.display = 'block';
        modal.classList.add('show');
    }
}

function closeDeleteModal() {
    const modal = document.getElementById('deleteConfirmModal');
    if (modal) {
        modal.classList.remove('show');
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }
    reviewState.reviewToDelete = null;
}

async function confirmDelete() {
    if (!reviewState.reviewToDelete) return;

    try {
        const response = await fetch(`/admin/reviews?id=${reviewState.reviewToDelete}`, {
            method: 'DELETE'
        });

        const data = await response.json();
        if (data.success) {
            showNotification('Review deleted successfully');
            loadReviews();
        } else {
            throw new Error(data.error || 'Failed to delete review');
        }
    } catch (error) {
        console.error('Delete error:', error);
        showNotification(error.message, 'error');
    } finally {
        closeDeleteModal();
    }
}

// Update Review Status - No Confirmation
async function updateReviewStatus(reviewId) {
    try {
        const response = await fetch('/admin/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 
                reviewId: reviewId, 
                status: 'approved' 
            })
        });

        const data = await response.json();
        if (data.success) {
            showNotification('Review approved successfully');
            loadReviews();
        } else {
            throw new Error(data.error || 'Failed to approve review');
        }
    } catch (error) {
        showNotification(error.message, 'error');
    }
}

// Utility Functions
function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function generateStarRating(rating) {
    return Array(5).fill(0).map((_, i) => 
        `<i class="bi bi-star${i < rating ? '-fill' : ''}"></i>`
    ).join('');
}

function capitalizeFirst(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
} 