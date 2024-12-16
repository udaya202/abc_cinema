document.addEventListener('DOMContentLoaded', () => {
    initAccountPage();
});

function initAccountPage() {
    // Initialize edit buttons
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', () => toggleEditMode(btn.dataset.section));
    });

    // Initialize cancel buttons
    document.querySelectorAll('.cancel-btn').forEach(btn => {
        btn.addEventListener('click', () => toggleEditMode(btn.dataset.section));
    });

    // Initialize forms
    initProfileForm();
    initPasswordForm();
    initPreferences();
}

function toggleEditMode(section) {
    const sectionElement = document.getElementById(`${section}Section`);
    const form = sectionElement.querySelector('.edit-form');
    const info = sectionElement.querySelector(`.${section}-info`);
    
    if (form.style.display === 'none') {
        // Switch to edit mode
        form.style.display = 'block';
        info.style.display = 'none';
        
        // Pre-fill form if needed
        if (section === 'profile') {
            prefillProfileForm();
        }
    } else {
        // Switch back to view mode
        form.style.display = 'none';
        info.style.display = 'block';
    }
}

function prefillProfileForm() {
    const form = document.getElementById('profileForm');
    const infoGroups = document.querySelectorAll('.info-group');
    
    try {
        form.querySelector('#name').value = infoGroups[0].querySelector('p').textContent.trim();
        form.querySelector('#email').value = infoGroups[1].querySelector('p').textContent.trim();
        form.querySelector('#phone').value = infoGroups[2].querySelector('p').textContent.trim();
    } catch (error) {
        console.error('Error prefilling form:', error);
        showNotification('Error loading profile data', 'error');
    }
}

function initProfileForm() {
    const form = document.getElementById('profileForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = form.querySelector('.save-btn');
        submitBtn.disabled = true;
        
        try {
            const response = await fetch('/api/account/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: form.querySelector('#name').value,
                    email: form.querySelector('#email').value,
                    phone: form.querySelector('#phone').value
                })
            });

            const data = await response.json();
            
            if (response.ok) {
                showNotification('Profile updated successfully', 'success');
                updateProfileDisplay(data);
                toggleEditMode('profile');
            } else {
                throw new Error(data.error || 'Failed to update profile');
            }
        } catch (error) {
            showNotification(error.message, 'error');
        } finally {
            submitBtn.disabled = false;
        }
    });
}

function initPasswordForm() {
    const form = document.getElementById('passwordForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = form.querySelector('.save-btn');
        submitBtn.disabled = true;

        try {
            // Validate passwords
            const currentPassword = form.querySelector('#currentPassword').value;
            const newPassword = form.querySelector('#newPassword').value;
            const confirmPassword = form.querySelector('#confirmNewPassword').value;

            if (newPassword !== confirmPassword) {
                throw new Error('New passwords do not match');
            }

            if (newPassword.length < 8) {
                throw new Error('New password must be at least 8 characters long');
            }

            const response = await fetch('/api/account/password', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    currentPassword,
                    newPassword,
                    confirmPassword
                })
            });

            const data = await response.json();
            
            if (response.ok) {
                showNotification('Password updated successfully', 'success');
                toggleEditMode('security');
                form.reset();
            } else {
                throw new Error(data.error || 'Failed to update password');
            }
        } catch (error) {
            showNotification(error.message, 'error');
        } finally {
            submitBtn.disabled = false;
        }
    });
}

function initPreferences() {
    const emailToggle = document.getElementById('emailNotif');
    const smsToggle = document.getElementById('smsNotif');

    [emailToggle, smsToggle].forEach(toggle => {
        if (!toggle) return;
        
        toggle.addEventListener('change', async () => {
            try {
                const response = await fetch('/api/account/preferences', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        emailNotifications: emailToggle.checked,
                        smsNotifications: smsToggle.checked
                    })
                });

                const data = await response.json();
                
                if (response.ok) {
                    showNotification('Preferences updated', 'success');
                } else {
                    throw new Error(data.error || 'Failed to update preferences');
                }
            } catch (error) {
                showNotification(error.message, 'error');
                // Revert toggle if update failed
                toggle.checked = !toggle.checked;
            }
        });
    });
}

function updateProfileDisplay(data) {
    const infoGroups = document.querySelectorAll('.info-group p');
    infoGroups[0].textContent = data.name;
    infoGroups[1].textContent = data.email;
    infoGroups[2].textContent = data.phone;
}

function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Add password toggle functionality
document.querySelectorAll('.toggle-password').forEach(button => {
    button.addEventListener('click', (e) => {
        const input = e.target.closest('.input-group').querySelector('input');
        const icon = e.target.querySelector('i');
        
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.replace('bi-eye', 'bi-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.replace('bi-eye-slash', 'bi-eye');
        }
    });
}); 