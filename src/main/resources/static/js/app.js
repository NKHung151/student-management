// Pagination state
let studentCurrentPage = 0;
const studentPageSize = 5;

window.changeStudentPage = function(pageNo) {
    studentCurrentPage = pageNo;
    loadTabData('students-tab');
};

// Screens Toggling
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(el => el.classList.remove('active'));
    document.getElementById(screenId).classList.add('active');
}

// Switch Tabs
function switchTab(tabId) {
    activeTab = tabId;
    document.querySelectorAll('.tab-panel').forEach(el => el.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');

    // Update Header Title
    const activeBtn = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
    if (activeBtn) {
        tabTitle.innerText = activeBtn.innerText.trim();
    }

    // Reset Filters
    const globalSearch = document.getElementById('global-search');
    if (globalSearch) globalSearch.value = '';

    // Reset pagination
    if (tabId === 'students-tab') {
        studentCurrentPage = 0;
    }

    // Refresh Tab Data
    loadTabData(tabId);

    // Cập nhật hiển thị nút Thêm mới
    if (typeof updateRoleBasedUIPermissions === 'function') {
        updateRoleBasedUIPermissions();
    }
}

// Fetch and Render Data per Tab
async function loadTabData(tabId) {
    let endpoint = '';
    let renderFunc = null;

    switch (tabId) {
        case 'schools-tab':
            endpoint = '/api/schools';
            renderFunc = renderSchools;
            break;
        case 'classrooms-tab':
            endpoint = '/api/classrooms';
            renderFunc = renderClassrooms;
            break;
        case 'staffs-tab':
            endpoint = '/api/staffs';
            renderFunc = renderStaffs;
            break;
        case 'students-tab':
            endpoint = `/api/students?page=${studentCurrentPage}&size=${studentPageSize}`;
            renderFunc = (data) => {
                renderStudents(data);
                renderPaginationControls('students-pagination', data, 'changeStudentPage');
            };
            break;
        case 'subjects-tab':
            endpoint = '/api/subjects';
            renderFunc = renderSubjects;
            break;
        case 'grades-tab':
            await Promise.all([
                loadSubPanelData('/api/score-records', renderScores),
                loadSubPanelData('/api/grade-slips', renderGradeSlips)
            ]);
            return;
        case 'journals-tab':
            endpoint = '/api/class-journals';
            renderFunc = renderJournals;
            break;
        case 'report-cards-tab':
            endpoint = '/api/report-cards';
            renderFunc = renderReportCards;
            break;
        case 'cards-tab':
            endpoint = '/api/student-cards';
            renderFunc = renderCards;
            break;
        case 'parents-tab':
            endpoint = '/api/parents';
            renderFunc = renderParents;
            break;
    }

    if (endpoint) {
        const data = await apiRequest(endpoint);
        if (data) {
            listsData[tabId] = data;
            renderFunc(data);
        }
    }
}

async function loadSubPanelData(endpoint, renderFunc) {
    const data = await apiRequest(endpoint);
    if (data) {
        renderFunc(data);
    }
}

// Handle Form Submission
async function handleFormSubmit(e) {
    e.preventDefault();
    const id = dynamicForm.getAttribute('data-id');
    const type = dynamicForm.getAttribute('data-type');
    
    const formData = new FormData(dynamicForm);
    const body = {};

    formData.forEach((value, key) => {
        if (key === 'studentIds') {
            const select = dynamicForm.querySelector('select[name="studentIds"]');
            body[key] = Array.from(select.selectedOptions).map(opt => parseInt(opt.value));
        } else if (value !== '') {
            if (key === 'schoolId' || key === 'homeroomTeacherId' || key === 'teacherId' || key === 'classroomId' || key === 'studentId' || key === 'subjectId' || key === 'gradeLevel' || key === 'lessonPeriod' || key === 'score') {
                body[key] = parseInt(value);
            } else if (key === 'score15min' || key === 'score45min' || key === 'scoreFinal' || key === 'gpa') {
                body[key] = parseFloat(value);
            } else {
                body[key] = value;
            }
        }
    });

    const promotedCheckbox = dynamicForm.querySelector('input[name="promoted"]');
    if (promotedCheckbox) {
        body['promoted'] = promotedCheckbox.checked;
    }

    let endpoint = `/api/${type}`;
    if (type === 'score-records') endpoint = `/api/score-records`;
    if (type === 'grade-slips') endpoint = `/api/grade-slips`;
    if (type === 'class-journals') endpoint = `/api/class-journals`;
    if (type === 'report-cards') endpoint = `/api/report-cards`;
    if (type === 'student-cards') endpoint = `/api/student-cards`;

    let method = 'POST';
    if (id) {
        method = 'PUT';
        endpoint += `/${id}`;
    }

    const data = await apiRequest(endpoint, method, body);
    if (data) {
        showToast(id ? 'Cập nhật thành công!' : 'Thêm mới thành công!');
        modalContainer.classList.remove('active');
        loadTabData(activeTab);
    }
}

// DOM Events Initialization
document.addEventListener('DOMContentLoaded', () => {
    // System Clock
    const updateClock = () => {
        const el = document.getElementById('current-time');
        if (el) {
            el.innerText = new Date().toLocaleString('vi-VN');
        }
    };
    setInterval(updateClock, 1000);
    updateClock();

    // Check token authentication state
    if (token) {
        loadUserProfile();
    } else {
        showScreen('login-screen');
    }

    // Login and Logout listeners
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
        btnLogout.addEventListener('click', handleLogout);
    }
    
    // Tab switching listener
    const sidebarNav = document.querySelector('.sidebar-nav');
    if (sidebarNav) {
        sidebarNav.addEventListener('click', (e) => {
            const btn = e.target.closest('.nav-item');
            if (!btn) return;
            
            document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
            btn.classList.add('active');
            
            const tabId = btn.getAttribute('data-tab');
            switchTab(tabId);
        });
    }

    // Sub-tab switching for Grades panel
    document.querySelectorAll('.tab-sub-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.tab-sub-btn').forEach(el => el.classList.remove('active'));
            e.target.classList.add('active');
            
            const subId = e.target.getAttribute('data-sub');
            document.querySelectorAll('.sub-panel').forEach(el => el.classList.remove('active'));
            document.getElementById(subId).classList.add('active');
        });
    });

    // Modal forms close listeners
    document.querySelectorAll('.close-modal, .close-modal-btn').forEach(btn => {
        btn.addEventListener('click', () => modalContainer.classList.remove('active'));
    });

    // Add Record Action Button
    if (btnAddRecord) {
        btnAddRecord.addEventListener('click', () => openFormModal());
    }

    // Form submission listener
    if (dynamicForm) {
        dynamicForm.addEventListener('submit', handleFormSubmit);
    }

    // Search input typing filter listener
    const globalSearch = document.getElementById('global-search');
    if (globalSearch) {
        globalSearch.addEventListener('input', (e) => {
            filterTable(e.target.value);
        });
    }
});
