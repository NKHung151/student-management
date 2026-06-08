// Handle Login
async function handleLogin(e) {
    e.preventDefault();
    const usernameVal = document.getElementById('username').value;
    const passwordVal = document.getElementById('password').value;

    const res = await apiRequest('/api/auth/login', 'POST', { username: usernameVal, password: passwordVal });
    if (res && res.token) {
        token = res.token;
        localStorage.setItem('token', token);
        showToast('Đăng nhập thành công!');
        await loadUserProfile();
    }
}

// Handle Logout
function handleLogout() {
    token = '';
    currentUser = null;
    localStorage.removeItem('token');
    showScreen('login-screen');
}

// Profile Loader
async function loadUserProfile() {
    const user = await apiRequest('/api/accounts/me');
    if (user) {
        currentUser = user;
        document.getElementById('user-display-name').innerText = user.staffName || user.parentName || user.username;
        
        let roleLabel = getRoleLabel(user.role);
        if (user.role === 'TEACHER') {
            let details = [];
            if (user.teachingSubjectNames && user.teachingSubjectNames.length > 0) {
                details.push('Dạy: ' + user.teachingSubjectNames.join(', '));
            }
            if (user.homeroomClassNames && user.homeroomClassNames.length > 0) {
                details.push('CN: ' + user.homeroomClassNames.join(', '));
            }
            if (details.length > 0) {
                roleLabel += ' (' + details.join(' | ') + ')';
            }
        }
        document.getElementById('user-display-role').innerText = roleLabel;
        
        // Hide add actions based on roles
        updateRoleBasedUIPermissions();

        // Filter navigation tabs dynamically by user role
        const initialTab = renderNavigationByRole(user.role);

        showScreen('app-screen');
        
        if (initialTab) {
            document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
            const activeBtn = document.querySelector(`.nav-item[data-tab="${initialTab}"]`);
            if (activeBtn) activeBtn.classList.add('active');
            switchTab(initialTab);
        }
    } else {
        handleLogout();
    }
}

function renderNavigationByRole(role) {
    const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
    let firstVisibleTab = null;

    navItems.forEach(item => {
        const tabId = item.getAttribute('data-tab');
        let isVisible = false;

        if (role === 'ADMIN') {
            isVisible = true;
        } else if (role === 'EDUCATION_OFFICER') {
            isVisible = ['classrooms-tab', 'students-tab', 'subjects-tab', 'grades-tab', 'journals-tab', 'report-cards-tab', 'cards-tab', 'parents-tab'].includes(tabId);
        } else if (role === 'TEACHER') {
            isVisible = ['classrooms-tab', 'students-tab', 'subjects-tab', 'grades-tab', 'journals-tab', 'report-cards-tab'].includes(tabId);
        } else if (role === 'PARENT') {
            isVisible = ['students-tab', 'grades-tab', 'report-cards-tab', 'cards-tab'].includes(tabId);
        }

        if (isVisible) {
            item.style.display = 'flex';
            if (!firstVisibleTab) {
                firstVisibleTab = tabId;
            }
        } else {
            item.style.display = 'none';
        }
    });

    return firstVisibleTab;
}

function getRoleLabel(role) {
    const labels = {
        'ADMIN': 'Quản trị viên',
        'EDUCATION_OFFICER': 'Giáo vụ',
        'TEACHER': 'Giáo viên',
        'PARENT': 'Phụ huynh'
    };
    return labels[role] || role;
}

function updateRoleBasedUIPermissions() {
    if (!currentUser || !btnAddRecord) return;
    const role = currentUser.role;
    const tab = activeTab || 'schools-tab';
    
    let canAdd = false;
    if (role === 'ADMIN') {
        canAdd = true;
    } else if (role === 'EDUCATION_OFFICER') {
        // Giáo vụ không được thêm trường học và nhân sự
        canAdd = !['schools-tab', 'staffs-tab'].includes(tab);
    } else if (role === 'TEACHER') {
        // Giáo viên chỉ được thêm điểm (grades), sổ đầu bài (journals), và học bạ (report-cards)
        canAdd = ['grades-tab', 'journals-tab', 'report-cards-tab'].includes(tab);
    } else if (role === 'PARENT') {
        canAdd = false;
    }
    
    btnAddRecord.style.display = canAdd ? 'block' : 'none';
}
