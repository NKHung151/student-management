// API Configuration
const API_BASE_URL = '';

// Check local storage token state on load
let token = localStorage.getItem('token') || '';
let currentUser = null;
let activeTab = 'schools-tab';
let listsData = {}; // Cache for list data

// Generic API Request Helper
async function apiRequest(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const options = { method, headers };
    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        if (response.status === 401 || response.status === 403) {
            handleLogout();
            showToast('Phiên làm việc hết hạn hoặc không có quyền truy cập', 'error');
            return null;
        }

        if (response.status === 204) {
            return true;
        }

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || data.message || 'Lỗi không xác định');
        }
        return data;
    } catch (err) {
        showToast(err.message, 'error');
        console.error(err);
        return null;
    }
}
