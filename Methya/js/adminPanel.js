// Navigation with URL handling
document.addEventListener('DOMContentLoaded', () => {
    // Set active page based on URL
    const path = window.location.pathname;
    let activePage = 'bookings'; // default

    if (path.includes('manageMovies')) {
        activePage = 'movies';
    } else if (path.includes('manageTheater')) {
        activePage = 'theater';
    } else if (path.includes('bookings')) {
        activePage = 'bookings';
    } else if (path.includes('messages')) {
        activePage = 'messages';
    }else if (path.includes('reviews')) {
        activePage = 'reviews';
    }

    // Update UI to show active page
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.page === activePage) {
            item.classList.add('active');
        }
    });

    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
        if (page.id === activePage) {
            page.classList.add('active');
        }
    });
});

// Update navigation click handlers
document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        const targetPage = item.dataset.page;

        // Handle navigation based on page
        switch(targetPage) {
            case 'movies':
                window.location.href = '/admin/manageMovies';
                break;
            case 'theater':
                window.location.href = '/admin/manageTheater';
                break;
            case 'bookings':
                window.location.href = '/admin/bookings';
                break;
            case 'messages':
                window.location.href = '/admin/messages';
                break;
            case 'reviews':
                window.location.href = '/admin/reviews';
                break;
        }
    });
});

function refreshPage() {
    window.location.reload(); // This reloads the current page
}

// Fetch movie data for editing
function fetchMovieDetails(movieId) {
    const url = `/Movies/${movieId}`;
    
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error: ${response.status} - ${response.statusText}`);
            }
            return response.json();
        })
        .then(movie => {
            // Populate modal fields with the movie data
            document.getElementById('movieName').value = movie.title;
            document.getElementById('movieDescription').value = movie.description;
            
            // Handle showtimes selection
            const showTimesSelect = document.getElementById('showTimes');
            if (movie.showtimes) {
                const selectedShowtimes = movie.showtimes.split(', ');
                Array.from(showTimesSelect.options).forEach(option => {
                    option.selected = selectedShowtimes.includes(option.text);
                });
            }
            
            // Store movieId for submission
            document.getElementById('movieModal').dataset.movieId = movie.id;
            
            // Show current poster filename if exists
            const posterInput = document.getElementById('moviePoster');
            const posterLabel = posterInput.nextElementSibling.querySelector('span');
            if (movie.poster) {
                posterLabel.textContent = movie.poster.split('/').pop();
                // Make poster field optional for updates
                posterInput.removeAttribute('required');
            }
        })
        .catch(error => {
            console.error('Error fetching movie details:', error);
        });
}

// Modal Functions
function showAddMovieModal(movieId = null) {
    const modalTitle = document.querySelector('.modal-header h2');
    const moviePosterInput = document.getElementById('moviePoster');

    if (movieId) {
        modalTitle.textContent = "Update Movie";
        fetchMovieDetails(movieId); // Fetch existing movie data
    } else {
        modalTitle.textContent = "Add New Movie";
        document.getElementById('movieForm').reset(); // Clear fields
        document.getElementById('movieModal').dataset.movieId = ""; // Clear movieId
    }

    // Reset the poster field
    moviePosterInput.value = '';
    document.getElementById('movieModal').style.display = 'block';
}

function closeMovieModal() {
    document.getElementById('movieModal').style.display = 'none';
}

// Handle movie form submission
function handleMovieSubmit(event) {
    event.preventDefault();
    const form = document.getElementById('movieForm');
    const formData = new FormData(form);
    const movieId = document.getElementById('movieModal').dataset.movieId;
    
    // Determine if this is an update or create
    const method = movieId ? 'PUT' : 'POST';
    const url = movieId ? `/admin/manageMovies/${movieId}` : '/admin/manageMovies';
    
    // If updating and no new poster is selected, remove the poster field
    if (movieId && !formData.get('poster').size) {
        formData.delete('poster');
    }

    // Show loading state
    const submitButton = form.querySelector('button[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Saving...';

    fetch(url, {
        method: method,
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            closeMovieModal();
            // Show success message
            showNotification('Movie saved successfully!', 'success');
            refreshPage();
        } else {
            throw new Error(data.error || 'Failed to save movie');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Failed to save movie: ' + error.message, 'error');
    })
    .finally(() => {
        // Reset button state
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    });
}

// Add this helper function for notifications
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.textContent = message;
    
    // Add to page
    const container = document.querySelector('.main-content');
    container.insertBefore(notification, container.firstChild);
    
    // Remove after 5 seconds
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

// Example functions for edit and delete
function editMovie(movieId) {
    console.log('Editing movie:', movieId);
    showAddMovieModal(movieId);
    // You can also fetch and populate the movie data here if needed
}

let movieToDelete = null;

function deleteMovie(id) {
    movieToDelete = id;
    console.log('Movie ID to delete:', movieToDelete);
    const deleteModal = document.getElementById('deleteConfirmModal');
    deleteModal.style.display = 'block';
    // Add show class for animation
    setTimeout(() => {
        deleteModal.classList.add('show');
    }, 10);
}

function closeDeleteModal() {
    const deleteModal = document.getElementById('deleteConfirmModal');
    deleteModal.classList.remove('show');
    setTimeout(() => {
        deleteModal.style.display = 'none';
        movieToDelete = null;
        showtimeToDelete = null;
        pricingToDelete = null;
        bookingsToDelete = null; // Reset bookingsToDelete
    }, 300);
}
//
// async function confirmDelete() {
//     if (!movieToDelete) {
//         showNotification('Error: Movie ID is missing', 'error');
//         return;
//     }
//
//     try {
//         const response = await fetch(`/admin/manageMovies?id=${movieToDelete}`, {
//             method: 'DELETE',
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         });
//
//         if (!response.ok) {
//             throw new Error(`HTTP error! status: ${response.status}`);
//         }
//
//         const data = await response.json();
//
//         if (data.success) {
//             showNotification('Movie deleted successfully', 'success');
//             // Remove the movie element from DOM
//             const movieElement = document.querySelector(`[data-movie-id="${movieToDelete}"]`);
//             if (movieElement) {
//                 movieElement.remove();
//             }
//             closeDeleteModal();
//         } else {
//             throw new Error(data.error || 'Failed to delete movie');
//         }
//     } catch (error) {
//         console.error('Delete error:', error);
//         showNotification(error.message, 'error');
//     } finally {
//         closeDeleteModal();
//     }
// }



// Close modal if clicking outside
window.onclick = function(event) {
    const deleteModal = document.getElementById('deleteConfirmModal');
    if (event.target === deleteModal) {
        closeDeleteModal();
    }
}

// Helper function for confirmation dialog
function showConfirmDialog(message) {
    return new Promise((resolve) => {
        const confirmed = window.confirm(message);
        resolve(confirmed);
    });
}

// Helper function for notifications
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// File upload preview
document.getElementById('moviePoster').addEventListener('change', function(e) {
    const fileName = e.target.files[0]?.name || 'Choose File';
    e.target.nextElementSibling.querySelector('span').textContent = fileName;
});

// Theater Management Functions
let currentShowtime = null;
let currentPricing = null;

// Showtime Modal Functions
function showShowtimeModal(showtimeId = null) {
    const modal = document.getElementById('showtimeModal');
    const form = document.getElementById('showtimeForm');
    const title = modal.querySelector('.modal-header h2');
    
    if (showtimeId) {
        title.textContent = 'Edit Showtime';
        form.dataset.showtimeId = showtimeId;
        fetchShowtimeDetails(showtimeId);
    } else {
        title.textContent = 'Add Showtime';
        form.reset();
        delete form.dataset.showtimeId;
    }
    
    modal.style.display = 'block';
    setTimeout(() => modal.classList.add('show'), 10);
}

function closeShowtimeModal() {
    const modal = document.getElementById('showtimeModal');
    modal.classList.remove('show');
    setTimeout(() => {
        modal.style.display = 'none';
        document.getElementById('showtimeForm').reset();
    }, 300);
}

function handleShowtimeSubmit(event) {
    event.preventDefault();
    const form = document.getElementById('showtimeForm');
    const showtimeId = form.dataset.showtimeId;
    
    const formData = new URLSearchParams();
    formData.append('displayTime', document.getElementById('displayTime').value);
    formData.append('startTime', document.getElementById('startTime').value);
    formData.append('endTime', document.getElementById('endTime').value);

    // Show loading state
    const submitButton = form.querySelector('button[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Saving...';

    const method = showtimeId ? 'PUT' : 'POST';
    const url = showtimeId ? 
        `/admin/manageShowtimes/${showtimeId}` : 
        '/admin/manageShowtimes';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData.toString() // Convert URLSearchParams to string
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            closeShowtimeModal();
            showNotification(
                showtimeId ? 'Showtime updated successfully!' : 'Showtime added successfully!', 
                'success'
            );
            // Reload the page to show updated data
            window.location.href = '/admin/manageTheater';
        } else {
            throw new Error(data.error || 'Failed to save showtime');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Failed to save showtime: ' + error.message, 'error');
    })
    .finally(() => {
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    });
}

let showtimeToDelete = null;

function deleteShowtime(showtimeId) {
    showtimeToDelete = showtimeId;
    const deleteModal = document.getElementById('deleteConfirmModal');
    const modalTitle = deleteModal.querySelector('h2');
    const modalText = deleteModal.querySelector('p');
    
    modalTitle.textContent = 'Delete Showtime';
    modalText.textContent = 'Are you sure you want to delete this showtime? This action cannot be undone.';
    
    // Store the ID for the confirm action
    deleteModal.dataset.itemId = showtimeId;
    deleteModal.dataset.itemType = 'showtime';
    
    deleteModal.style.display = 'block';
    setTimeout(() => deleteModal.classList.add('show'), 10);
}

function editShowtime(showtimeId) {
    fetch(`/admin/manageShowtimes/${showtimeId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(showtime => {
            const form = document.getElementById('showtimeForm');
            form.dataset.showtimeId = showtime.id;
            document.getElementById('displayTime').value = showtime.displayTime;
            document.getElementById('startTime').value = showtime.startTime;
            document.getElementById('endTime').value = showtime.endTime;
            showShowtimeModal(showtimeId);
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Failed to load showtime details: ' + error.message, 'error');
        });
}

// Pricing Modal Functions
function showPricingModal(movieId = null) {
    const modal = document.getElementById('pricingModal');
    const form = document.getElementById('pricingForm');
    const title = modal.querySelector('.modal-header h2');
    
    // Reset form
    form.reset();
    
    // Load movies for dropdown
    fetchMoviesForPricing().then(() => {
        if (movieId) {
            title.textContent = 'Edit Movie Pricing';
            document.getElementById('movieSelect').value = movieId;
            loadShowtimesForMovie(movieId);
            loadExistingPricing(movieId);
        } else {
            title.textContent = 'Add Movie Pricing';
        }
    });
    
    modal.style.display = 'block';
    setTimeout(() => modal.classList.add('show'), 10);
}

function closePricingModal() {
    const modal = document.getElementById('pricingModal');
    modal.classList.remove('show');
    setTimeout(() => {
        modal.style.display = 'none';
        document.getElementById('pricingForm').reset();
    }, 300);
}

async function fetchMoviesForPricing() {
    try {
        const response = await fetch('/admin/managePricing/');
        if (!response.ok) throw new Error('Failed to fetch movies');
        
        const movies = await response.json();
        const select = document.getElementById('movieSelect');
        select.innerHTML = '<option value="">Choose a movie...</option>';
        
        movies.forEach(movie => {
            const option = document.createElement('option');
            option.value = movie.id;
            option.textContent = movie.title;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error:', error);
        showNotification('Failed to load movies: ' + error.message, 'error');
    }
}

async function loadShowtimesForMovie(movieId) {
    if (!movieId) return;
    
    try {
        const response = await fetch(`/admin/managePricing/movie/${movieId}`);
        if (!response.ok) throw new Error('Failed to fetch showtimes');
        
        const showtimes = await response.json();
        const select = document.getElementById('showtimeSelect');
        select.innerHTML = '';
        
        showtimes.forEach(showtime => {
            const option = document.createElement('option');
            option.value = showtime.id;
            option.textContent = showtime.displayTime;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error:', error);
        showNotification('Failed to load showtimes: ' + error.message, 'error');
    }
}

async function loadExistingPricing(movieId) {
    try {
        const response = await fetch(`/admin/managePricing/price/${movieId}`);
        if (!response.ok) throw new Error('Failed to fetch pricing');
        
        const pricing = await response.json();
        if (pricing) {
            document.getElementById('normalPricing').value = pricing.normalPrice;
            document.getElementById('balconyPricing').value = pricing.balconyPrice;
            
            // Select showtimes
            const showtimeSelect = document.getElementById('showtimeSelect');
            pricing.showtimeIds.forEach(id => {
                const option = showtimeSelect.querySelector(`option[value="${id}"]`);
                if (option) option.selected = true;
            });
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Failed to load pricing details: ' + error.message, 'error');
    }
}

// Add this function for editing pricing
function editPricing(movieId) {
    const modal = document.getElementById('pricingModal');
    const form = document.getElementById('pricingForm');
    const title = modal.querySelector('.modal-header h2');
    
    // Reset form
    form.reset();
    form.dataset.movieId = movieId; // Store movieId for update operation
    
    title.textContent = 'Edit Movie Pricing';
    
    // Load movies and select the current movie
    fetchMoviesForPricing().then(() => {
        document.getElementById('movieSelect').value = movieId;
        loadShowtimesForMovie(movieId);
        loadExistingPricing(movieId);
    });
    
    modal.style.display = 'block';
    setTimeout(() => modal.classList.add('show'), 10);
}

// Update handlePricingSubmit to handle both add and edit
async function handlePricingSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const movieId = document.getElementById('movieSelect').value;
    const isEdit = form.dataset.movieId; // Check if we're editing
    
    const formData = {
        movieId: parseInt(movieId),
        normalPrice: parseFloat(document.getElementById('normalPricing').value),
        balconyPrice: parseFloat(document.getElementById('balconyPricing').value),
        showtimeIds: Array.from(document.getElementById('showtimeSelect').selectedOptions)
            .map(option => parseInt(option.value))
    };

    const submitButton = form.querySelector('button[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Saving...';

    try {
        const response = await fetch('/admin/managePricing', {
            method: isEdit ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to save pricing');
        }
        
        const result = await response.json();
        if (result.success) {
            closePricingModal();
            showNotification(
                isEdit ? 'Pricing updated successfully!' : 'Pricing added successfully!', 
                'success'
            );
            window.location.href = '/admin/manageTheater';
        } else {
            throw new Error(result.error || 'Failed to save pricing');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Failed to save pricing: ' + error.message, 'error');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    }
}

let pricingToDelete = null;
function deletePricing(movie_Id) {
    pricingToDelete = movie_Id;
    const deleteModal = document.getElementById('deleteConfirmModal');
    const modalTitle = deleteModal.querySelector('h2');
    const modalText = deleteModal.querySelector('p');
    
    modalTitle.textContent = 'Delete Pricing Rule 2';
    modalText.textContent = 'Are you sure you want to delete this pricing rule? This action cannot be undone.';
    
    deleteModal.dataset.itemId = movie_Id;
    deleteModal.dataset.itemType = 'pricing';
    
    deleteModal.style.display = 'block';
    setTimeout(() => deleteModal.classList.add('show'), 10);
}



// Close modals when clicking outside
window.onclick = function(event) {
    const modals = [
        document.getElementById('deleteConfirmModal'),
        document.getElementById('showtimeModal'),
        document.getElementById('pricingModal'),
        document.getElementById('movieModal')
    ];

    modals.forEach(modal => {
        if (event.target === modal) {
            if (modal === document.getElementById('deleteConfirmModal')) {
                closeDeleteModal();
            } else if (modal === document.getElementById('showtimeModal')) {
                closeShowtimeModal();
            } else if (modal === document.getElementById('pricingModal')) {
                closePricingModal();
            } else if (modal === document.getElementById('movieModal')) {
                closeMovieModal();
            }
        }
    });
};

// Add this function to handle fetching showtime details
function fetchShowtimeDetails(showtimeId) {
    fetch(`/admin/manageShowtimes/${showtimeId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(showtime => {
            const form = document.getElementById('showtimeForm');
            form.dataset.showtimeId = showtime.id;
            document.getElementById('displayTime').value = showtime.displayTime;
            document.getElementById('startTime').value = showtime.startTime;
            document.getElementById('endTime').value = showtime.endTime;
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Failed to load showtime details: ' + error.message, 'error');
        });
}

async function clearAllMessages() {
    if (!confirm('Are you sure you want to clear all messages? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch('/admin/messages', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();

        if (data.success) {
            showNotification('All messages cleared successfully', 'success');
            // Clear the messages grid
            const messagesGrid = document.querySelector('.messages-grid');
            messagesGrid.innerHTML = `
                <div class="no-messages">
                    <i class="bi bi-inbox"></i>
                    <p>No messages found!</p>
                </div>
            `;
        } else {
            throw new Error(data.error || 'Failed to clear messages');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message, 'error');
    }
}

// Add these to your existing state management
const adminState = {
    // ... existing state ...
    bookings: {
        currentPage: 1,
        itemsPerPage: 10,
        totalPages: 1,
        filters: {
            movie: '',
            showtime: '',
            status: ''
        },
        selectedBookings: new Set()
    }
};

// Add these functions to your existing adminPanel.js
function initializeBookingsPage() {
    if (!document.getElementById('bookings')) return;
    
    // Clear existing data
    adminState.bookings.selectedBookings.clear();
    adminState.bookings.currentPage = 1;
    
    // Initialize components
    loadMoviesForFilter();
    loadShowtimesForFilter();
    initializeBookingSelections();
    loadBookings();
    
    // Add event listener for filter changes
    document.querySelectorAll('.filter-select').forEach(select => {
        select.addEventListener('change', () => {
            adminState.bookings.currentPage = 1; // Reset to first page on filter change
            applyBookingFilters();
        });
    });
}

async function loadMoviesForFilter() {
    try {
        const response = await fetch('/admin/bookings/movies/list');  // Updated path
        const data = await response.json();
        
        const movieSelect = document.getElementById('movieFilter');
        if (!movieSelect) return;

        // Clear existing options except the first one (All Movies)
        while (movieSelect.options.length > 1) {
            movieSelect.remove(1);
        }

        if (data.success && data.movies) {
            data.movies.forEach(movie => {
                const option = document.createElement('option');
                option.value = movie.id;
                option.textContent = movie.movie_title;
                movieSelect.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading movies:', error);
        showNotification('Failed to load movies for filter', 'error');
    }
}

async function loadShowtimesForFilter() {
    try {
        const response = await fetch('/admin/bookings/showtimes/list');
        if (!response.ok) {
            throw new Error('Failed to fetch showtimes');
        }
        
        const data = await response.json();
        
        const showtimeSelect = document.getElementById('showtimeFilter');
        if (!showtimeSelect) return;

        // Clear existing options except the first one (All Showtimes)
        while (showtimeSelect.options.length > 1) {
            showtimeSelect.remove(1);
        }

        if (data.success && data.showtimes && data.showtimes.length > 0) {
            data.showtimes.forEach(showtime => {
                const option = document.createElement('option');
                option.value = showtime.id;
                option.textContent = showtime.display_time;
                showtimeSelect.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading showtimes:', error);
        showNotification('Failed to load showtimes', 'error');
    }
}

function applyBookingFilters() {
    const movieId = document.getElementById('movieFilter')?.value;
    const showtimeId = document.getElementById('showtimeFilter')?.value;
    const status = document.getElementById('statusFilter')?.value;

    // Store filter values in state
    adminState.bookings.filters = {
        movie: movieId || '',
        showtime: showtimeId || '',
        status: status || ''
    };

    // Reset to first page when applying filters
    adminState.bookings.currentPage = 1;
    loadBookings();
}

async function loadBookings() {
    try {
        const queryParams = new URLSearchParams({
            page: adminState.bookings.currentPage,
            limit: adminState.bookings.itemsPerPage
        });

        // Add filters only if they have values
        if (adminState.bookings.filters.movie) {
            queryParams.append('movie', adminState.bookings.filters.movie);
        }
        if (adminState.bookings.filters.showtime) {
            queryParams.append('showtime', adminState.bookings.filters.showtime);
        }
        if (adminState.bookings.filters.status) {
            queryParams.append('status', adminState.bookings.filters.status);
        }
        
        const response = await fetch(`/admin/bookings/list?${queryParams}`);
        const data = await response.json();
        
        if (data.success) {
            renderBookings(data.bookings);
            updateBookingsPagination(data.totalPages);
        } else {
            throw new Error(data.error || 'Failed to load bookings');
        }
    } catch (error) {
        console.error('Error loading bookings:', error);
        showNotification('Failed to load bookings', 'error');
    }
}

function renderBookings(bookings) {
    const tbody = document.getElementById('bookingsTableBody');
    if (!tbody) return;

    tbody.innerHTML = '';
    
    bookings.forEach(booking => {
        const tr = document.createElement('tr');
        tr.innerHTML = createBookingRow(booking);
        tbody.appendChild(tr);
    });
    
    initializeBookingCheckboxes();
}

function createBookingRow(booking) {
    return `
        <td>
            <input type="checkbox" 
                   class="booking-checkbox" 
                   value="${booking.ticketNo}"
                   ${adminState.bookings.selectedBookings.has(booking.ticketNo) ? 'checked' : ''}>
        </td>
        <td>ABC-${String(booking.ticketNo).padStart(6, '0')}</td>
        <td>${escapeHtml(booking.movieTitle)}</td>
        <td>${escapeHtml(booking.customerName)}</td>
        <td>${escapeHtml(booking.showtime)}</td>
        <td>${escapeHtml(booking.seatNumber)}</td>
        <td>LKR ${booking.amount.toFixed(2)}</td>
        <td><span class="status-badge ${booking.status}">${booking.status}</span></td>
        <td>${new Date(booking.bookingDate).toLocaleDateString()}</td>
        <td>
            <button class="action-btn danger" onclick="deleteBooking(${booking.ticketNo})">
                <i class="bi bi-trash"></i>
            </button>
        </td>
    `;
}

function initializeBookingSelections() {
    const selectAllCheckbox = document.getElementById('selectAllBookings');
    if (!selectAllCheckbox) return;

    selectAllCheckbox.addEventListener('change', (e) => {
        const checkboxes = document.querySelectorAll('.booking-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = e.target.checked;
            if (e.target.checked) {
                adminState.bookings.selectedBookings.add(checkbox.value);
            } else {
                adminState.bookings.selectedBookings.delete(checkbox.value);
            }
        });
        updateDeleteButtonState();
    });

    // Initialize filter change events
    ['movieFilter', 'showtimeFilter', 'statusFilter'].forEach(id => {
        document.getElementById(id)?.addEventListener('change', applyBookingFilters);
    });

    // Initialize refresh button
    document.querySelector('.refresh-btn')?.addEventListener('click', loadBookings);

    // Initialize delete selected button
    document.getElementById('deleteSelectedBookings')?.addEventListener('click', deleteSelectedBookings);
}

function initializeBookingCheckboxes() {
    const checkboxes = document.querySelectorAll('.booking-checkbox');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', (e) => {
            // Convert value to number when adding to set
            const ticketNo = parseInt(e.target.value, 10);
            if (e.target.checked) {
                adminState.bookings.selectedBookings.add(ticketNo);
            } else {
                adminState.bookings.selectedBookings.delete(ticketNo);
            }
            updateDeleteButtonState();
        });
    });
}

function updateDeleteButtonState() {
    const deleteBtn = document.getElementById('deleteSelectedBookings');
    if (deleteBtn) {
        deleteBtn.disabled = adminState.bookings.selectedBookings.size === 0;
    }
}

function updateBookingsPagination(totalPages) {
    adminState.bookings.totalPages = totalPages;
    const pageInfo = document.getElementById('pageInfo');
    if (!pageInfo) {
        console.warn('Pagination info element not found');
        return;
    }
    pageInfo.textContent = `Page ${adminState.bookings.currentPage} of ${totalPages}`;
    
    // Update pagination buttons state
    const prevBtn = document.querySelector('.page-btn[onclick="previousBookingPage()"]');
    const nextBtn = document.querySelector('.page-btn[onclick="nextBookingPage()"]');
    
    if (prevBtn) prevBtn.disabled = adminState.bookings.currentPage <= 1;
    if (nextBtn) nextBtn.disabled = adminState.bookings.currentPage >= totalPages;
}

let bookingsToDelete = null;

async function deleteSelectedBookings() {
    if (adminState.bookings.selectedBookings.size === 0) {
        showNotification('Please select bookings to delete', 'error');
        return;
    }
    
    // Store selected bookings for deletion
    bookingsToDelete = Array.from(adminState.bookings.selectedBookings)
        .map(ticket => parseInt(ticket, 10));
    
    // Show delete confirmation modal
    const deleteModal = document.getElementById('deleteConfirmModal');
    const modalTitle = deleteModal.querySelector('h2');
    const modalText = deleteModal.querySelector('p');
    
    modalTitle.textContent = 'Delete Selected Bookings';
    modalText.textContent = `Are you sure you want to delete ${bookingsToDelete.length} booking(s)? This action cannot be undone.`;
    
    // Store the type for confirmDelete handler
    deleteModal.dataset.itemType = 'bookings';
    
    deleteModal.style.display = 'block';
    setTimeout(() => deleteModal.classList.add('show'), 10);
}

async function deleteBooking(ticketNo) {
    // Store single booking for deletion
    bookingsToDelete = [parseInt(ticketNo, 10)];
    
    // Show delete confirmation modal
    const deleteModal = document.getElementById('deleteConfirmModal');
    const modalTitle = deleteModal.querySelector('h2');
    const modalText = deleteModal.querySelector('p');
    
    modalTitle.textContent = 'Delete Booking';
    modalText.textContent = 'Are you sure you want to delete this booking? This action cannot be undone.';
    
    // Store the type for confirmDelete handler
    deleteModal.dataset.itemType = 'bookings';
    
    deleteModal.style.display = 'block';
    setTimeout(() => deleteModal.classList.add('show'), 10);
}

// Update the confirmDelete function to handle bookings
function confirmDelete() {
    if (movieToDelete) {
        fetch(`/admin/manageMovies?id=${movieToDelete}`, {
            method: 'DELETE',
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Movie deleted successfully!');
                    // Remove movie card from UI
                    const movieCard = document.querySelector(`[data-movie-id="${movieToDelete}"]`);
                    if (movieCard) {
                        movieCard.remove();
                    }
                } else {
                    console.log('Failed to delete movie.');
                }
            })
            .catch(error => console.error('Error deleting movie:', error))
            .finally(() => {
                closeDeleteModal(); // Close modal
            });
    }else if(showtimeToDelete){
        fetch(`/admin/manageShowtimes?id=${showtimeToDelete}`, {
            method: 'DELETE',
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Showtime deleted successfully!');
                    // Remove showtime card from UI
                    const showtimeCard = document.querySelector(`[data-showtime-id="${showtimeToDelete}"]`);
                    if (showtimeCard) {
                        showtimeCard.remove();
                    }
                }
            })
            .catch(error => console.error('Error deleting showtime:', error))
            .finally(() => {
                closeDeleteModal(); // Close modal
            });
    } else if (showtimeToDelete) {
        fetch(`/admin/manageShowtimes?id=${showtimeToDelete}`, {
            method: 'DELETE',
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Showtime deleted successfully!');
                    // Remove showtime card from UI
                    const showtimeCard = document.querySelector(`[data-showtime-id="${showtimeToDelete}"]`);
                    if (showtimeCard) {
                        showtimeCard.remove();
                    }
                }
            })
            .catch(error => console.error('Error deleting showtime:', error))
            .finally(() => {
                closeDeleteModal(); // Close modal
            });
    } else if (pricingToDelete) {
        console.log(pricingToDelete);
        fetch(`/admin/managePricing?id=${pricingToDelete}`, {
            method: 'DELETE',
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Pricing deleted successfully!');
                    // Remove pricing card from UI
                    const pricingCard = document.querySelector(`[data-pricing-id="${pricingToDelete}"]`);
                    if (pricingCard) {
                        pricingCard.remove();
                    }
                }
            })
            .catch(error => console.error('Error deleting pricing:', error))
            .finally(() => {
                closeDeleteModal(); // Close modal
            });
    } else if (bookingsToDelete) {
        fetch('/admin/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                ticketNos: bookingsToDelete
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification('Booking(s) deleted successfully', 'success');
                // Clear selected bookings
                adminState.bookings.selectedBookings.clear();
                // Reload bookings table
                loadBookings();
            } else {
                throw new Error(data.error || 'Failed to delete booking(s)');
            }
        })
        .catch(error => {
            console.error('Error deleting booking(s):', error);
            showNotification(error.message, 'error');
        })
        .finally(() => {
            closeDeleteModal();
            bookingsToDelete = null; // Reset bookingsToDelete
        });
    }
}

// Add this to your existing initialization
document.addEventListener('DOMContentLoaded', () => {
    // ... existing initialization code ...
    initializeBookingsPage();
});

// Add these functions for pagination
function previousBookingPage() {
    if (adminState.bookings.currentPage > 1) {
        adminState.bookings.currentPage--;
        loadBookings();
    }
}

function nextBookingPage() {
    if (adminState.bookings.currentPage < adminState.bookings.totalPages) {
        adminState.bookings.currentPage++;
        loadBookings();
    }
}

// Update the initialization to handle page switching
document.addEventListener('DOMContentLoaded', () => {
    // Existing initialization code...
    
    // Initialize page navigation
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetPage = link.getAttribute('data-page');
            showPage(targetPage);
            
            // Initialize bookings if switching to bookings page
            if (targetPage === 'bookings') {
                initializeBookingsPage();
            }
        });
    });
    
    // Initialize bookings if it's the active page
    const activePage = document.querySelector('.page.active');
    if (activePage && activePage.id === 'bookings') {
        initializeBookingsPage();
    }
});

// Add this utility function if not already present
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Add this function to show notification if not already present
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}
