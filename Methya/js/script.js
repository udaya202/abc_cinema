document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const profileBtn = document.getElementById('profileBtn');
    const profileDropdown = document.getElementById('profileDropdown');
    const mobileMenu = document.querySelector('.mobile-menu');
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const navbar = document.querySelector('.navbar');
    
    // State
    let isProfileMenuOpen = false;

    // Initialize all components
    initNavbar();
    initAuthForms();
    initMobileMenu();
    initProfileMenu();

    // Navbar functionality
    function initNavbar() {
        // Navbar scroll effect
        window.addEventListener('scroll', () => {
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });

        // Active link highlighting
        const navLinks = document.querySelectorAll('.nav-link');
        const currentPath = window.location.pathname;

        navLinks.forEach(link => {
            if (link.getAttribute('href') === currentPath) {
                link.classList.add('active');
            }
        });
    }

    // Auth Forms Initialization
    function initAuthForms() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');

        // Password toggle functionality
        document.querySelectorAll('.toggle-password').forEach(button => {
            button.addEventListener('click', (e) => {
                const input = e.target.closest('.input-group').querySelector('input');
                const icon = e.target.closest('.toggle-password').querySelector('i');
                
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.classList.replace('bi-eye', 'bi-eye-slash');
                } else {
                    input.type = 'password';
                    icon.classList.replace('bi-eye-slash', 'bi-eye');
                }
            });
        });

        // Validation rules
        const validationRules = {
            name: [
                { validate: value => value.length >= 2, message: 'Name must be at least 2 characters' }
            ],
            email: [
                { validate: value => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value), message: 'Invalid email address' }
            ],
            phone: [
                { validate: value => /^\+?[\d\s-]{10,}$/.test(value), message: 'Invalid phone number' }
            ],
            password: [
                { validate: value => value.length >= 8, message: 'Password must be at least 8 characters' }
            ]
        };

        // Login form handling
        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                let isValid = true;
                
                const emailInput = loginForm.querySelector('#email');
                const passwordInput = loginForm.querySelector('#password');
                
                if (!validateField(emailInput, validationRules.email)) isValid = false;
                if (!validateField(passwordInput, [{ validate: value => value.length > 0, message: 'Password is required' }])) isValid = false;
                
                if (isValid) {
                    const submitButton = loginForm.querySelector('button[type="submit"]');
                    submitButton.disabled = true;
                    submitButton.innerHTML = '<i class="bi bi-hourglass-split"></i> Signing In...';
                    
                    try {
                        const response = await fetch('/api/login', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                email: emailInput.value,
                                password: passwordInput.value
                            })
                        });
                        
                        const data = await response.json();
                        
                        if (response.ok) {
                            showNotification('Login successful!', 'success');
                            setTimeout(() => {
                                window.location.href = '/';
                            }, 1500);
                        } else {
                            throw new Error(data.error || 'Invalid email or password');
                        }
                    } catch (error) {
                        showNotification(error.message, 'error');
                    } finally {
                        submitButton.disabled = false;
                        submitButton.innerHTML = '<span>Sign In</span><i class="bi bi-arrow-right"></i>';
                    }
                }
            });
        }

        // Register form handling
        if (registerForm) {
            registerForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                let isValid = true;
                
                // Validate all fields
                for (const [fieldName, rules] of Object.entries(validationRules)) {
                    const input = registerForm.querySelector(`#${fieldName}`);
                    if (input && !validateField(input, rules)) {
                        isValid = false;
                    }
                }
                
                // Validate password confirmation
                const passwordInput = registerForm.querySelector('#password');
                const confirmPasswordInput = registerForm.querySelector('#confirmPassword');
                if (passwordInput.value !== confirmPasswordInput.value) {
                    const errorElement = confirmPasswordInput.closest('.form-group').querySelector('.error-message');
                    errorElement.textContent = 'Passwords do not match';
                    errorElement.classList.add('show');
                    isValid = false;
                }
                
                if (isValid) {
                    const submitButton = registerForm.querySelector('button[type="submit"]');
                    submitButton.disabled = true;
                    submitButton.innerHTML = '<i class="bi bi-hourglass-split"></i> Creating Account...';
                    
                    try {
                        const response = await fetch('/api/register', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                name: registerForm.querySelector('#name').value,
                                email: registerForm.querySelector('#email').value,
                                phone: registerForm.querySelector('#phone').value,
                                password: registerForm.querySelector('#password').value
                            })
                        });
                        
                        const data = await response.json();
                        
                        if (response.ok) {
                            showNotification('Account created successfully!', 'success');
                            setTimeout(() => {
                                window.location.href = '/login';
                            }, 1500);
                        } else {
                            throw new Error(data.error || 'Failed to create account');
                        }
                    } catch (error) {
                        showNotification(error.message, 'error');
                    } finally {
                        submitButton.disabled = false;
                        submitButton.innerHTML = '<span>Create Account</span><i class="bi bi-arrow-right"></i>';
                    }
                }
            });
        }
    }

    // Field validation helper
    function validateField(input, rules) {
        const errorElement = input.closest('.form-group').querySelector('.error-message');
        let isValid = true;
        
        for (const rule of rules) {
            if (!rule.validate(input.value)) {
                errorElement.textContent = rule.message;
                errorElement.classList.add('show');
                isValid = false;
                break;
            } else {
                errorElement.classList.remove('show');
            }
        }
        
        return isValid;
    }

    // Mobile Menu Initialization
    function initMobileMenu() {
        if (mobileMenuBtn && mobileMenu) {
            mobileMenuBtn.addEventListener('click', toggleMobileMenu);
            
            // Close mobile menu when clicking links
            document.querySelectorAll('.mobile-link, .mobile-profile-link').forEach(link => {
                link.addEventListener('click', closeMobileMenu);
            });
        }
    }

    // Profile Menu Initialization
    function initProfileMenu() {
        if (profileBtn && profileDropdown) {
            profileBtn.addEventListener('click', handleProfileClick);
            profileDropdown.addEventListener('click', handleProfileDropdownClick);
            document.addEventListener('click', handleOutsideClick);
            document.addEventListener('keydown', handleEscapeKey);
        }
    }

    // Event Handlers
    function handleProfileClick(e) {
        e.stopPropagation();
        isProfileMenuOpen = !isProfileMenuOpen;
        
        if (isProfileMenuOpen) {
            openProfileMenu();
        } else {
            closeProfileMenu();
        }
    }

    function handleProfileDropdownClick(e) {
        const link = e.target.closest('a');
        if (link?.classList.contains('logout-btn')) {
            e.preventDefault();
            handleLogout();
        }
    }

    function handleOutsideClick(e) {
        if (!e.target.closest('.profile-menu') && isProfileMenuOpen) {
            closeProfileMenu();
        }
    }

    function handleEscapeKey(e) {
        if (e.key === 'Escape' && isProfileMenuOpen) {
            closeProfileMenu();
        }
    }

    // UI Actions
    function openProfileMenu() {
        profileDropdown.classList.add('show');
        if (window.innerWidth <= 768) {
            addBackdrop();
        }
    }

    function closeProfileMenu() {
        isProfileMenuOpen = false;
        profileDropdown?.classList.remove('show');
        removeBackdrop();
    }

    function toggleMobileMenu() {
        mobileMenu.classList.toggle('active');
        document.body.style.overflow = mobileMenu.classList.contains('active') ? 'hidden' : '';
    }

    function closeMobileMenu() {
        mobileMenu.classList.remove('active');
        document.body.style.overflow = '';
    }

    // Backdrop Management
    function addBackdrop() {
        const backdrop = document.createElement('div');
        backdrop.classList.add('modal-backdrop');
        document.body.appendChild(backdrop);
        backdrop.addEventListener('click', closeProfileMenu);
    }

    function removeBackdrop() {
        const backdrop = document.querySelector('.modal-backdrop');
        if (backdrop) {
            backdrop.remove();
        }
    }

    // Notification System
    function showNotification(message, type = 'success') {
        // Remove existing notification if any
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }

        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        
        // Add icon based on type
        let icon = '';
        switch(type) {
            case 'success':
                icon = 'bi-check-circle-fill';
                break;
            case 'error':
                icon = 'bi-exclamation-circle-fill';
                break;
            case 'info':
                icon = 'bi-info-circle-fill';
                break;
        }
        
        notification.innerHTML = `
            <i class="bi ${icon}"></i>
            <span>${message}</span>
        `;
        
        // Add to DOM
        document.body.appendChild(notification);
        
        // Trigger animation
        setTimeout(() => notification.classList.add('show'), 10);
        
        // Remove after delay
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 300);
        }, 5000);
    }

    // API Interactions
    async function handleLogout() {
        try {
            const response = await fetch('/api/logout');
            if (response.ok) {
                showNotification('Logged out successfully', 'success');
                setTimeout(() => {
                    window.location.href = '/';
                }, 1000);
            } else {
                throw new Error('Logout failed');
            }
        } catch (error) {
            showNotification('Failed to logout. Please try again.', 'error');
        }
    }

    // Contact Form Handling
    const contactForm = document.getElementById('contactForm');
    if (contactForm) {
        contactForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            // Get form elements
            const nameInput = contactForm.querySelector('#name');
            const emailInput = contactForm.querySelector('#email');
            const subjectInput = contactForm.querySelector('#subject');
            const messageInput = contactForm.querySelector('#message');
            
            // Remove existing success message if any
            const existingSuccess = contactForm.querySelector('.contact-success');
            if (existingSuccess) {
                existingSuccess.remove();
            }
            
            // Disable submit button and show loading state
            const submitBtn = contactForm.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Sending...';
            
            try {
                // Client-side validation
                if (!nameInput.value.trim() || !emailInput.value.trim() || 
                    !subjectInput.value.trim() || !messageInput.value.trim()) {
                    throw new Error('All fields are required');
                }
                
                // Email validation
                const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
                if (!emailRegex.test(emailInput.value)) {
                    throw new Error('Please enter a valid email address');
                }
                
                // Send form data
                const response = await fetch('/api/contact', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        name: nameInput.value.trim(),
                        email: emailInput.value.trim(),
                        subject: subjectInput.value.trim(),
                        message: messageInput.value.trim()
                    })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    // Show success notification
                    showNotification('Message sent successfully!', 'success');
                    
                    // Show success message in form
                    const successMessage = document.createElement('div');
                    successMessage.className = 'contact-success';
                    successMessage.innerHTML = `
                        <i class="bi bi-check-circle-fill"></i>
                        <div>
                            <h4>Thank you for your message!</h4>
                            <p>We'll get back to you as soon as possible.</p>
                        </div>
                    `;
                    contactForm.insertBefore(successMessage, contactForm.firstChild);
                    
                    // Reset form
                    contactForm.reset();
                    
                    // Scroll to top of form
                    contactForm.scrollIntoView({ behavior: 'smooth' });
                } else {
                    throw new Error(data.error || 'Failed to send message');
                }
            } catch (error) {
                showNotification(error.message, 'error');
            } finally {
                // Restore submit button
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        });
    }

    // Add these styles to your CSS
    function addContactStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .success-message {
                background-color: rgba(40, 167, 69, 0.1);
                border: 1px solid #28a745;
                color: #28a745;
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
                animation: fadeIn 0.3s ease;
            }
            
            @keyframes fadeIn {
                from { opacity: 0; transform: translateY(-10px); }
                to { opacity: 1; transform: translateY(0); }
            }
        `;
        document.head.appendChild(style);
    }

    // Call this when the page loads
    document.addEventListener('DOMContentLoaded', addContactStyles);


}); 