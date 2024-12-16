// State management for bookings
const bookingsState = {
    currentPage: 1,
    itemsPerPage: 10,
    totalPages: 1,
    filters: {
        movie: '',
        showtime: '',
        status: ''
    },
    selectedBookings: new Set()
};

// Initialize bookings functionality
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('bookings')) {
        initializeBookingsPage();
    }
});

function initializeBookingsPage() {
    loadMoviesForFilter();
    loadShowtimesForFilter();
    initializeSelectAll();
    loadBookings();
}

// Filter Handling
async function loadMoviesForFilter() {
    try {
        const response = await fetch('/api/admin/movies');
        const data = await response.json();
        
        const movieSelect = document.getElementById('movieFilter');
        data.forEach(movie => {
            const option = document.createElement('option');
            option.value = movie.id;
            option.textContent = movie.title;
            movieSelect.appendChild(option);
        });
    } catch (error) {
        showNotification('Failed to load movies for filter', 'error');
    }
}

async function loadShowtimesForFilter() {
    try {
        const response = await fetch('/api/admin/showtimes');
        const data = await response.json();
        
        const showtimeSelect = document.getElementById('showtimeFilter');
        data.forEach(showtime => {
            const option = document.createElement('option');
            option.value = showtime.id;
            option.textContent = showtime.displayTime;
            showtimeSelect.appendChild(option);
        });
    } catch (error) {
        showNotification('Failed to load showtimes for filter', 'error');
    }
}

function applyFilters() {
    bookingsState.filters = {
        movie: document.getElementById('movieFilter').value,
        showtime: document.getElementById('showtimeFilter').value,
        status: document.getElementById('statusFilter').value
    };
    bookingsState.currentPage = 1;
    loadBookings();
}

// Bookings Data Loading
async function loadBookings() {
    try {
        const queryParams = new URLSearchParams({
            page: bookingsState.currentPage,
            limit: bookingsState.itemsPerPage,
            ...bookingsState.filters
        });
        
        const response = await fetch(`/api/admin/bookings?${queryParams}`);
        const data = await response.json();
        
        if (data.success) {
            renderBookings(data.bookings);
            updatePagination(data.totalPages);
        } else {
            throw new Error(data.error);
        }
    } catch (error) {
        showNotification('Failed to load bookings', 'error');
    }
}

function renderBookings(bookings) {
    const tbody = document.getElementById('bookingsTableBody');
    tbody.innerHTML = '';
    
    bookings.forEach(booking => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <input type="checkbox" 
                       class="booking-checkbox" 
                       value="${booking.ticketNo}"
                       ${bookingsState.selectedBookings.has(booking.ticketNo) ? 'checked' : ''}>
            </td>
            <td>ABC-${String(booking.ticketNo).padStart(6, '0')}</td>
            <td>${booking.movieTitle}</td>
            <td>${booking.customerName}</td>
            <td>${booking.showtime}</td>
            <td>${booking.seatNumber}</td>
            <td>LKR ${booking.amount.toFixed(2)}</td>
            <td><span class="status-badge ${booking.status}">${booking.status}</span></td>
            <td>${new Date(booking.bookingDate).toLocaleDateString()}</td>
            <td>
                <button class="action-btn danger" onclick="deleteBooking(${booking.ticketNo})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
    
    initializeCheckboxes();
}

// Pagination
function updatePagination(totalPages) {
    bookingsState.totalPages = totalPages;
    document.getElementById('pageInfo').textContent = 
        `Page ${bookingsState.currentPage} of ${totalPages}`;
}

function previousPage() {
    if (bookingsState.currentPage > 1) {
        bookingsState.currentPage--;
        loadBookings();
    }
}

function nextPage() {
    if (bookingsState.currentPage < bookingsState.totalPages) {
        bookingsState.currentPage++;
        loadBookings();
    }
}

// Selection Handling
function initializeSelectAll() {
    const selectAllCheckbox = document.getElementById('selectAllBookings');
    selectAllCheckbox.addEventListener('change', (e) => {
        const checkboxes = document.querySelectorAll('.booking-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = e.target.checked;
            if (e.target.checked) {
                bookingsState.selectedBookings.add(checkbox.value);
            } else {
                bookingsState.selectedBookings.delete(checkbox.value);
            }
        });
        updateDeleteButtonState();
    });
}

function initializeCheckboxes() {
    const checkboxes = document.querySelectorAll('.booking-checkbox');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', (e) => {
            if (e.target.checked) {
                bookingsState.selectedBookings.add(e.target.value);
            } else {
                bookingsState.selectedBookings.delete(e.target.value);
            }
            updateDeleteButtonState();
        });
    });
}

function updateDeleteButtonState() {
    const deleteBtn = document.getElementById('deleteSelectedBookings');
    deleteBtn.disabled = bookingsState.selectedBookings.size === 0;
}

// Delete Operations
async function deleteSelectedBookings() {
    if (bookingsState.selectedBookings.size === 0) return;
    
    if (!confirm(`Are you sure you want to delete ${bookingsState.selectedBookings.size} booking(s)?`)) {
        return;
    }
    
    try {
        const response = await fetch('/admin/bookings/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                ticketNos: Array.from(bookingsState.selectedBookings)
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Bookings deleted successfully', 'success');
            bookingsState.selectedBookings.clear();
            loadBookings();
        } else {
            throw new Error(data.error);
        }
    } catch (error) {
        showNotification('Failed to delete bookings', 'error');
    }
}

async function deleteBooking(ticketNo) {
    if (!confirm('Are you sure you want to delete this booking?')) {
        return;
    }
    
    try {
        const response = await fetch('/api/admin/bookings/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                ticketNos: [ticketNo]
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Booking deleted successfully', 'success');
            loadBookings();
        } else {
            throw new Error(data.error);
        }
    } catch (error) {
        showNotification('Failed to delete booking', 'error');
    }
}

// Refresh functionality
function refreshBookings() {
    loadBookings();
} 