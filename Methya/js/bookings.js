document.addEventListener('DOMContentLoaded', () => {
    initializeSelections();
});

function initializeSelections() {
    const checkboxes = document.querySelectorAll('.booking-checkbox');
    const cancelSelectedBtn = document.getElementById('cancelSelectedBtn');
    const paySelectedBtn = document.getElementById('paySelectedBtn');
    
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            const selectedCount = document.querySelectorAll('.booking-checkbox:checked').length;
            cancelSelectedBtn.disabled = selectedCount === 0;
            paySelectedBtn.disabled = selectedCount === 0;
            
            // Update parent card styling
            const card = checkbox.closest('.booking-card');
            if (checkbox.checked) {
                card.classList.add('selected');
            } else {
                card.classList.remove('selected');
            }
        });
    });
    
    // Initialize button states
    const selectedCount = document.querySelectorAll('.booking-checkbox:checked').length;
    cancelSelectedBtn.disabled = selectedCount === 0;
    paySelectedBtn.disabled = selectedCount === 0;
    
    // Initialize cancel selected button
    if (cancelSelectedBtn) {
        cancelSelectedBtn.addEventListener('click', cancelSelectedBookings);
    }
}

async function payBookings(ticketNos) {
    try {
        // Store selected tickets in session storage for payment page
        sessionStorage.setItem('paymentTickets', JSON.stringify(ticketNos));
        
        // Redirect to payment page with source parameter
        window.location.href = '/payment?source=bookings';
        
    } catch (error) {
        console.error('Error initiating payment:', error);
        showNotification(error.message || 'Failed to initiate payment', 'error');
    }
}

// Update the pay selected button handler
document.getElementById('paySelectedBtn').addEventListener('click', () => {
    const selectedSeats = Array.from(document.querySelectorAll('.booking-checkbox:checked'))
        .map(cb => parseInt(cb.value));
    
    if (selectedSeats.length === 0) {
        showNotification('Please select bookings to pay', 'error');
        return;
    }
    
    payBookings(selectedSeats);
});

let selectedSeatsForDeletion = [];

function showConfirmModal(seats) {
    selectedSeatsForDeletion = seats;
    const modal = document.getElementById('confirmModal');
    const countText = modal.querySelector('.selected-count');
    
    countText.textContent = seats.length > 1 
        ? `This will delete ${seats.length} bookings.` 
        : 'This will delete 1 booking.';
    
    modal.classList.add('show');
}

function closeConfirmModal() {
    const modal = document.getElementById('confirmModal');
    modal.classList.remove('show');
    selectedSeatsForDeletion = [];
}

async function confirmDelete() {
    if (selectedSeatsForDeletion.length === 0) return;
    
    try {
        const response = await fetch('/api/bookings/cancel', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ seatNumbers: selectedSeatsForDeletion })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Booking(s) deleted successfully', 'success');
            setTimeout(() => window.location.reload(), 1500);
        } else {
            throw new Error(data.error || 'Failed to delete booking(s)');
        }
    } catch (error) {
        showNotification(error.message, 'error');
    } finally {
        closeConfirmModal();
    }
}

function cancelBooking(seatNumber) {
    showConfirmModal([seatNumber]);
}

function cancelSelectedBookings() {
    const selectedSeats = Array.from(document.querySelectorAll('.booking-checkbox:checked'))
        .map(cb => cb.value);
    
    if (selectedSeats.length === 0) {
        showNotification('Please select bookings to delete', 'error');
        return;
    }
    
    showConfirmModal(selectedSeats);
}

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

function paySelectedBookings() {
    try {
        // Get checked checkboxes and log them
        const checkedBoxes = document.querySelectorAll('.booking-checkbox:checked');
        console.log('Checked checkboxes:', checkedBoxes);
        
        // Map and log each step of ticket number processing
        const selectedTickets = Array.from(checkedBoxes)
            .map(cb => {
                const ticketNo = parseInt(cb.value);
                console.log('Processing checkbox:', {
                    element: cb,
                    value: cb.value,
                    parsedValue: ticketNo,
                    dataset: cb.dataset
                });
                return ticketNo;
            })
            .filter(ticketNo => {
                const isValid = !isNaN(ticketNo) && ticketNo > 0;
                console.log('Validating ticket:', ticketNo, 'isValid:', isValid);
                return isValid;
            });
        
        console.log('Final selected tickets:', selectedTickets);
        
        if (selectedTickets.length === 0) {
            showNotification('Please select bookings to pay', 'error');
            return;
        }
        
        // Store in session storage
        const ticketsData = JSON.stringify(selectedTickets);
        console.log('Storing in session storage:', ticketsData);
        sessionStorage.setItem('paymentTickets', ticketsData);
        
        // Verify storage
        const storedData = sessionStorage.getItem('paymentTickets');
        console.log('Verified stored data:', storedData);
        
        // Add loading state
        const payBtn = document.getElementById('paySelectedBtn');
        payBtn.disabled = true;
        payBtn.innerHTML = '<i class="bi bi-hourglass"></i> Processing...';
        
        // Redirect to payment page
        window.location.href = '/payment?source=bookings';
        
    } catch (error) {
        console.error('Error in paySelectedBookings:', error);
        showNotification('Failed to initiate payment', 'error');
        
        // Reset button state
        const payBtn = document.getElementById('paySelectedBtn');
        payBtn.disabled = false;
        payBtn.innerHTML = '<i class="bi bi-credit-card"></i> Pay Selected';
    }
}

document.removeEventListener('DOMContentLoaded', () => {
    document.getElementById('paySelectedBtn').addEventListener('click', paySelectedBookings);
});