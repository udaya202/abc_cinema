// Remove hardcoded configuration
let paymentConfig = null;

let paymentState = {
    tickets: [],
    totalAmount: 0,
    orderId: null
};

document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Fetch payment configuration
        const response = await fetch('/api/payment/config');
        paymentConfig = await response.json();
        console.log('Payment configuration loaded');
        
        // Initialize payment page
        await initializePaymentPage();
    } catch (error) {
        console.error('Failed to load payment configuration:', error);
        showNotification('Failed to initialize payment system', 'error');
    }
});

async function initializePaymentPage() {
    try {
        // Get payment source from URL
        const urlParams = new URLSearchParams(window.location.search);
        const source = urlParams.get('source');
        console.log('Payment source:', source);
        
        if (!source) {
            throw new Error('Payment source not specified');
        }
        
        // Get selected tickets from session storage if coming from bookings
        const selectedTickets = sessionStorage.getItem('paymentTickets');
        console.log('Retrieved from session storage:', selectedTickets);
        
        if (source === 'bookings' && (!selectedTickets || selectedTickets === 'null')) {
            throw new Error('No tickets selected for payment');
        }
        
        // Prepare request options
        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        // Add body only if we have selected tickets
        if (selectedTickets && selectedTickets !== 'null') {
            const parsedTickets = JSON.parse(selectedTickets);
            console.log('Parsed tickets from storage:', parsedTickets);
            
            if (!Array.isArray(parsedTickets) || parsedTickets.length === 0) {
                throw new Error('Invalid ticket selection');
            }
            
            requestOptions.body = JSON.stringify({
                tickets: parsedTickets
            });
            console.log('Request body being sent:', requestOptions.body);
        }
        
        // Log full request details
        console.log('Sending request:', {
            url: `/api/payment/initialize?source=${source}`,
            options: requestOptions
        });
        
        // Make the request
        const response = await fetch(`/api/payment/initialize?source=${source}`, requestOptions);
        
        // Log raw response
        const rawResponse = await response.text();
        console.log('Raw server response:', rawResponse);
        
        // Try to parse the response
        let data;
        try {
            data = JSON.parse(rawResponse);
            console.log('Parsed response data:', data);
        } catch (parseError) {
            console.error('JSON Parse Error:', parseError);
            throw new Error('Invalid server response format');
        }
        
        if (!data.success) {
            throw new Error(data.error || 'Failed to initialize payment');
        }
        
        // Store payment data
        paymentState = {
            ...paymentState,
            tickets: data.tickets,
            totalAmount: data.totalAmount,
            orderId: data.orderId,
            userInfo: data.userInfo
        };
        
        // Clear session storage
        sessionStorage.removeItem('paymentTickets');
        
        // Populate form and render summary
        populateUserData(data.userInfo);
        renderTickets(data.tickets);
        updateSummary(data.totalAmount);
        
    } catch (error) {
        console.error('Payment initialization error:', error);
        showNotification(error.message, 'error');
        // Don't redirect yet - let's see the error
    }
}

function populateUserData(userInfo) {
    // Pre-fill form with user data
    document.getElementById('firstName').value = userInfo.firstName || '';
    document.getElementById('lastName').value = userInfo.lastName || '';
    document.getElementById('email').value = userInfo.email || '';
    document.getElementById('phone').value = userInfo.phone || '';
}

function renderTickets(tickets) {
    const ticketsList = document.getElementById('ticketsList');
    ticketsList.innerHTML = tickets.map(ticket => `
        <div class="ticket-item">
            <div class="ticket-movie">${escapeHtml(ticket.movieTitle)}</div>
            <div class="ticket-details">
                <div class="ticket-info">
                    <div>${ticket.showtime}</div>
                    <div>Seat ${ticket.seatNumber}</div>
                </div>
                <div class="ticket-price">LKR ${ticket.amount.toFixed(2)}</div>
            </div>
        </div>
    `).join('');
}

function updateSummary(totalAmount) {
    document.getElementById('subtotalAmount').textContent = `LKR ${totalAmount.toFixed(2)}`;
    document.getElementById('totalAmount').textContent = `LKR ${totalAmount.toFixed(2)}`;
}

async function initiatePayment() {
    if (!paymentConfig) {
        showNotification('Payment system not initialized', 'error');
        return;
    }
    
    try {
        // Validate form
        const form = document.getElementById('billingForm');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }
        
        // Get form data
        const formData = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            address: document.getElementById('address').value,
            city: document.getElementById('city').value,
            country: document.getElementById('country').value
        };
        
        // Get payment hash from server
        const hashResponse = await fetch('/api/payment/hash', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                orderId: paymentState.orderId,
                amount: paymentState.totalAmount
            })
        });
        
        const hashData = await hashResponse.json();
        if (!hashData.success) {
            throw new Error(hashData.error || 'Failed to generate payment hash');
        }
        
        // Configure PayHere payment
        const payment = {
            sandbox: paymentConfig.sandbox,
            merchant_id: paymentConfig.merchantId,
            return_url: undefined,     // Important
            cancel_url: undefined,     // Important
            notify_url: paymentConfig.notifyUrl,
            order_id: paymentState.orderId,
            items: `Movie Tickets (${paymentState.tickets.length})`,
            amount: paymentState.totalAmount.toFixed(2),
            currency: 'LKR',
            hash: hashData.hash,
            first_name: formData.firstName,
            last_name: formData.lastName,
            email: formData.email,
            phone: formData.phone,
            address: formData.address,
            city: formData.city,
            country: formData.country
        };
        
        // Show PayHere payment popup
        payhere.startPayment(payment);
        
    } catch (error) {
        console.error('Payment initiation error:', error);
        showNotification(error.message, 'error');
    }
}

// PayHere event handlers
payhere.onCompleted = async function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    try {
        // Send request to our success endpoint
        const response = await fetch('/payment/success', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                order_id: orderId,
                payment_id: paymentState.paymentId,
                status_code: 2 // Success status code
            })
        });

        const result = await response.json();
        
        if (result.success) {
            showNotification('Payment successful!', 'success');
            // Redirect after showing success message
            setTimeout(() => window.location.href = '/mybookings', 1500);
        } else {
            throw new Error(result.error || 'Failed to update payment status');
        }
    } catch (error) {
        console.error('Error updating payment status:', error);
        showNotification('Payment completed but status update failed. Please contact support.', 'error');
        // Still redirect to bookings page after error
        setTimeout(() => window.location.href = '/mybookings', 3000);
    }
};

payhere.onDismissed = function onDismissed() {
    console.log("Payment dismissed");
    showNotification('Payment cancelled', 'error');
    setTimeout(() => window.location.href = '/mybookings', 1500);
};

payhere.onError = function onError(error) {
    console.error("Payment error:", error);
    showNotification('Payment failed: ' + error, 'error');
    setTimeout(() => window.location.href = '/mybookings', 1500);
};

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

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
} 