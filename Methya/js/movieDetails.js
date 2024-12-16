function selectShowtime(showtimeId) {
    // Check if user is logged in (we'll handle the redirect server-side)
    window.location.href = `/theater/${showtimeId}`;
}

function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'error' ? 'x-circle' : 'info-circle'}"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(notification);
    setTimeout(() => notification.classList.add('show'), 10);
    
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add this to your existing script.js
function bookMovie(movieId) {
    window.location.href = `/movie/${movieId}`;
}