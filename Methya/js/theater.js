// Theater state and configuration
const state = {
    selectedSeats: new Set()
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    initializeTheater();
});

// Theater Configuration - must be defined before use
const THEATER_CONFIG = {
    balcony: {
        rows: ['U', 'T', 'S', 'R', 'Q', 'P', 'O'],
        seatsPerRow: 15,
        price: theaterData.balconyPrice
    },
    normal: {
        rows: Array.from({length: 10}, (_, i) => String.fromCharCode(65 + i)),
        seatsPerRow: 15,
        price: theaterData.normalPrice
    }
};

function initializeTheater() {
    try {
        createSeats();
        
        // Initialize booking summary buttons
        const bookNowBtn = document.getElementById('bookNowBtn');
        const backBtn = document.querySelector('.back-btn');
        const payNowBtn = document.querySelector('.pay-now-btn');
        const payLaterBtn = document.querySelector('.pay-later-btn');
        
        if (bookNowBtn) bookNowBtn.onclick = showBookingConfirmation;
        if (backBtn) {
            backBtn.onclick = () => {
                document.querySelector('.initial-view').style.display = 'flex';
                document.querySelector('.confirmation-view').style.display = 'none';
            };
        }
        if (payNowBtn) payNowBtn.onclick = () => processBooking('pay_now');
        if (payLaterBtn) payLaterBtn.onclick = () => processBooking('pay_later');
        
        initializeBookingSummary();
    } catch (error) {
        console.error('Error initializing theater:', error);
        showNotification('Error loading theater layout', 'error');
    }
}

function createSeats() {
    // Create balcony seats
    createSeatSection('balconySeats', THEATER_CONFIG.balcony.rows, THEATER_CONFIG.balcony.price);
    
    // Create normal seats
    createSeatSection('normalSeats', THEATER_CONFIG.normal.rows, THEATER_CONFIG.normal.price);
}

function createSeatSection(containerId, rows, price) {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error(`Container ${containerId} not found`);
        return;
    }

    // Clear existing content
    container.innerHTML = '';
    
    // Parse booked seats if it's a string
    const bookedSeats = new Set(
        typeof theaterData.bookedSeats === 'string' 
            ? JSON.parse(theaterData.bookedSeats) 
            : theaterData.bookedSeats
    );

    rows.forEach(row => {
        // Create row label
        const rowLabel = document.createElement('div');
        rowLabel.className = 'row-label';
        rowLabel.textContent = row;
        container.appendChild(rowLabel);

        // Create seats for this row
        const seatsPerRow = containerId === 'balconySeats' ? 
            THEATER_CONFIG.balcony.seatsPerRow : 
            THEATER_CONFIG.normal.seatsPerRow;

        for (let i = 1; i <= seatsPerRow; i++) {
            const seatNumber = `${row}${i.toString().padStart(2, '0')}`;
            const seat = document.createElement('div');
            
            // Set class based on booking status
            const isBooked = bookedSeats.has(seatNumber);
            seat.className = `seat ${isBooked ? 'booked' : 'available'}`;
            seat.dataset.seat = seatNumber;
            seat.dataset.price = price;
            seat.textContent = i;
            
            if (!isBooked) {
                seat.addEventListener('click', () => toggleSeat(seat));
            }
            
            container.appendChild(seat);
        }
    });
}

function toggleSeat(seatElement) {
    const seatNumber = seatElement.dataset.seat;
    
    if (seatElement.classList.contains('selected')) {
        seatElement.classList.remove('selected');
        state.selectedSeats.delete(seatNumber);
    } else {
        seatElement.classList.add('selected');
        state.selectedSeats.add(seatNumber);
    }
    
    updateBookingSummary();
}

function updateBookingSummary() {
    const selectedSeatsList = document.getElementById('selectedSeatsList');
    const totalPriceElement = document.getElementById('totalPrice');
    const bookNowBtn = document.getElementById('bookNowBtn');
    
    // Clear previous selections
    selectedSeatsList.innerHTML = '';
    
    // Group seats by type (Balcony/Normal)
    const seatsByType = {
        balcony: [],
        normal: []
    };
    
    let totalPrice = 0;
    
    state.selectedSeats.forEach(seatNumber => {
        const seatElement = document.querySelector(`[data-seat="${seatNumber}"]`);
        const price = parseFloat(seatElement.dataset.price);
        const isBalcony = seatNumber.match(/^[O-U]/); // Matches balcony rows O to U
        
        if (isBalcony) {
            seatsByType.balcony.push({ number: seatNumber, price });
        } else {
            seatsByType.normal.push({ number: seatNumber, price });
        }
        
        totalPrice += price;
    });
    
    // Create summary HTML
    if (seatsByType.balcony.length > 0) {
        const balconyDiv = document.createElement('div');
        balconyDiv.className = 'seat-type-group';
        balconyDiv.innerHTML = `
            <h5>Balcony (LKR ${theaterData.balconyPrice})</h5>
            <div class="seats-list">
                ${seatsByType.balcony.map(seat => `
                    <span class="selected-seat-tag">
                        ${seat.number}
                        <button class="remove-seat" onclick="removeSeat('${seat.number}')">×</button>
                    </span>
                `).join('')}
            </div>
            <div class="subtotal">
                Subtotal: LKR ${(seatsByType.balcony.length * theaterData.balconyPrice).toFixed(2)}
            </div>
        `;
        selectedSeatsList.appendChild(balconyDiv);
    }
    
    if (seatsByType.normal.length > 0) {
        const normalDiv = document.createElement('div');
        normalDiv.className = 'seat-type-group';
        normalDiv.innerHTML = `
            <h5>Normal (LKR ${theaterData.normalPrice})</h5>
            <div class="seats-list">
                ${seatsByType.normal.map(seat => `
                    <span class="selected-seat-tag">
                        ${seat.number}
                        <button class="remove-seat" onclick="removeSeat('${seat.number}')">×</button>
                    </span>
                `).join('')}
            </div>
            <div class="subtotal">
                Subtotal: LKR ${(seatsByType.normal.length * theaterData.normalPrice).toFixed(2)}
            </div>
        `;
        selectedSeatsList.appendChild(normalDiv);
    }
    
    // Update total price
    totalPriceElement.textContent = `LKR ${totalPrice.toFixed(2)}`;
    
    // Show/hide booking summary and enable/disable book now button
    const bookingSummary = document.querySelector('.booking-summary');
    bookingSummary.classList.toggle('active', state.selectedSeats.size > 0);
    bookNowBtn.disabled = state.selectedSeats.size === 0;
}

function removeSeat(seatNumber) {
    const seatElement = document.querySelector(`[data-seat="${seatNumber}"]`);
    if (seatElement) {
        seatElement.classList.remove('selected');
        state.selectedSeats.delete(seatNumber);
        updateBookingSummary();
    }
}

function initializeModal() {
    const modal = document.getElementById('bookingModal');
    if (!modal) {
        console.error('Booking modal not found');
        return;
    }

    const closeBtn = modal.querySelector('.close-btn');
    const payNowBtn = modal.querySelector('.pay-now-btn');
    const payLaterBtn = modal.querySelector('.pay-later-btn');
    const bookNowBtn = document.getElementById('bookNowBtn');
    
    // Close modal when clicking X
    if (closeBtn) {
        closeBtn.onclick = closeModal;
    }
    
    // Close modal when clicking outside
    window.onclick = (event) => {
        if (event.target === modal) {
            closeModal();
        }
    };
    
    // Handle payment buttons
    if (payNowBtn) {
        payNowBtn.onclick = () => processBooking('pay_now');
    }
    if (payLaterBtn) {
        payLaterBtn.onclick = () => processBooking('pay_later');
    }
    
    // Initialize book now button
    if (bookNowBtn) {
        bookNowBtn.onclick = showBookingModal;
    }
}

function showBookingModal() {
    console.log('Showing booking modal');
    const modal = document.getElementById('bookingModal');
    const modalBody = modal.querySelector('.booking-details');
    
    if (!modal || !modalBody) {
        console.error('Modal elements not found');
        return;
    }
    
    try {
        // Create detailed summary
        const summary = createBookingSummary();
        
        // Update modal content
        modalBody.innerHTML = `
            <h3>${summary.movieTitle}</h3>
            <div class="booking-info">
                <p><i class="bi bi-calendar3"></i> ${summary.date}</p>
                <p><i class="bi bi-clock"></i> ${summary.time}</p>
            </div>
            <div class="seats-summary">
                ${summary.seatsSummary}
            </div>
            <div class="price-breakdown">
                ${summary.priceBreakdown}
                <div class="total-price">
                    <strong>Total Amount:</strong>
                    <span>LKR ${summary.totalPrice.toFixed(2)}</span>
                </div>
            </div>
            <div class="confirmation-text">
                <p>Please confirm your booking and select a payment option:</p>
            </div>
        `;
        
        // Show modal and add body class
        modal.style.display = 'block';
        document.body.classList.add('modal-open');
        
    } catch (error) {
        console.error('Error showing booking modal:', error);
        showNotification('Error showing booking details', 'error');
    }
}

function closeModal() {
    const modal = document.getElementById('bookingModal');
    modal.style.display = 'none';
    document.body.classList.remove('modal-open');
}

function createBookingSummary() {
    if (!theaterData || !theaterData.movieTitle) {
        throw new Error('Theater data not properly initialized');
    }

    const balconySeats = [];
    const normalSeats = [];
    let totalPrice = 0;
    
    state.selectedSeats.forEach(seatNumber => {
        const isBalcony = seatNumber.match(/^[O-U]/);
        if (isBalcony) {
            balconySeats.push(seatNumber);
            totalPrice += theaterData.balconyPrice;
        } else {
            normalSeats.push(seatNumber);
            totalPrice += theaterData.normalPrice;
        }
    });
    
    // Sort seats for better presentation
    balconySeats.sort();
    normalSeats.sort();
    
    const seatsSummary = `
        <div class="seats-category">
            ${balconySeats.length > 0 ? `
                <div class="category">
                    <h4>Balcony Seats</h4>
                    <p>${balconySeats.join(', ')}</p>
                    <span>${balconySeats.length} × LKR ${theaterData.balconyPrice}</span>
                </div>
            ` : ''}
            ${normalSeats.length > 0 ? `
                <div class="category">
                    <h4>Normal Seats</h4>
                    <p>${normalSeats.join(', ')}</p>
                    <span>${normalSeats.length} × LKR ${theaterData.normalPrice}</span>
                </div>
            ` : ''}
        </div>
    `;
    
    const priceBreakdown = `
        ${balconySeats.length > 0 ? `
            <div class="price-row">
                <span>Balcony Seats (${balconySeats.length})</span>
                <span>LKR ${(balconySeats.length * theaterData.balconyPrice).toFixed(2)}</span>
            </div>
        ` : ''}
        ${normalSeats.length > 0 ? `
            <div class="price-row">
                <span>Normal Seats (${normalSeats.length})</span>
                <span>LKR ${(normalSeats.length * theaterData.normalPrice).toFixed(2)}</span>
            </div>
        ` : ''}
    `;
    
    return {
        movieTitle: theaterData.movieTitle,
        date: 'Today',
        time: theaterData.showtime,
        seatsSummary,
        priceBreakdown,
        totalPrice
    };
}

async function processBooking(paymentType) {
    try {
        const response = await fetch('/api/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                seats: Array.from(state.selectedSeats),
                movieId: theaterData.movieId,
                showtimeId: theaterData.showtimeId,
                paymentType: paymentType
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Booking successful!', 'success');
            if (paymentType === 'pay_now') {
                // Redirect to payment page (to be implemented)
                setTimeout(() => window.location.href = '/mybookings', 1500);
            } else {
                // Redirect to bookings page
                setTimeout(() => window.location.href = '/', 1500);
            }
        } else {
            throw new Error(data.error || 'Booking failed');
        }
    } catch (error) {
        showNotification(error.message, 'error');
    }
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

function initializeBookingSummary() {
    const bookingSummary = document.querySelector('.booking-summary');
    if (bookingSummary) {
        updateBookingSummary(); // Initial update
    }
}

function showBookingConfirmation() {
    const initialView = document.querySelector('.initial-view');
    const confirmationView = document.querySelector('.confirmation-view');
    const bookingInfo = confirmationView.querySelector('.booking-info');
    
    try {
        const summary = createBookingSummary();
        
        // Update booking info content
        bookingInfo.innerHTML = `
            <h4>${summary.movieTitle}</h4>
            <div class="show-details">
                <p><i class="bi bi-calendar3"></i> ${summary.date}</p>
                <p><i class="bi bi-clock"></i> ${summary.time}</p>
            </div>
            <div class="seats-summary">
                ${summary.seatsSummary}
            </div>
            <div class="price-breakdown">
                ${summary.priceBreakdown}
                <div class="total-price">
                    <strong>Total Amount:</strong>
                    <span>LKR ${summary.totalPrice.toFixed(2)}</span>
                </div>
            </div>
        `;
        
        // Hide initial view and show confirmation
        initialView.style.display = 'none';
        confirmationView.style.display = 'block';
        
    } catch (error) {
        console.error('Error showing booking confirmation:', error);
        showNotification('Error showing booking details', 'error');
    }
}