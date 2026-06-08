// DOM Elements
const loginScreen = document.getElementById('login-screen');
const appScreen = document.getElementById('app-screen');
const loginForm = document.getElementById('login-form');
const btnLogout = document.getElementById('btn-logout');
const sidebarNav = document.querySelector('.sidebar-nav');
const tabTitle = document.getElementById('tab-title');
const globalSearch = document.getElementById('global-search');
const btnAddRecord = document.getElementById('btn-add-record');
const modalContainer = document.getElementById('modal-container');
const dynamicForm = document.getElementById('dynamic-form');
const formFields = document.getElementById('form-fields');
const modalTitle = document.getElementById('modal-title');
const closeModalBtns = document.querySelectorAll('.close-modal, .close-modal-btn');
const toastContainer = document.getElementById('toast-container');

// Render Pagination controls helper function
function renderPaginationControls(containerId, pageData, changePageFuncName) {
    const container = document.getElementById(containerId);
    if (!container) return;
    
    if (!pageData || pageData.totalPages <= 1) {
        container.innerHTML = '';
        return;
    }
    
    const currentPage = pageData.number; // 0-indexed
    const totalPages = pageData.totalPages;
    
    let html = '';
    
    // Previous button
    html += `<button class="pagination-btn" ${currentPage === 0 ? 'disabled' : ''} onclick="${changePageFuncName}(${currentPage - 1})">
        <i class="fa-solid fa-chevron-left"></i>
    </button>`;
    
    // Page buttons
    for (let i = 0; i < totalPages; i++) {
        html += `<button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="${changePageFuncName}(${i})">
            ${i + 1}
        </button>`;
    }
    
    // Next button
    html += `<button class="pagination-btn" ${currentPage === totalPages - 1 ? 'disabled' : ''} onclick="${changePageFuncName}(${currentPage + 1})">
        <i class="fa-solid fa-chevron-right"></i>
    </button>`;
    
    container.innerHTML = html;
}

// Render Tables list helper functions
function renderSchools(schools) {
    const list = document.getElementById('schools-list');
    list.innerHTML = schools.map(s => `
        <tr data-row-search="${s.name} ${s.address} ${s.schoolType}">
            <td>${s.id}</td>
            <td><strong>${s.name}</strong></td>
            <td>${s.address || ''}</td>
            <td>${s.phone || ''}</td>
            <td>${s.email || ''}</td>
            <td><span class="badge">${s.schoolType}</span></td>
            <td>${renderActionButtons(s.id, 'schools')}</td>
        </tr>
    `).join('');
}

function renderClassrooms(classrooms) {
    const list = document.getElementById('classrooms-list');
    list.innerHTML = classrooms.map(c => `
        <tr data-row-search="${c.name} ${c.schoolYear} ${c.schoolName || ''} ${c.homeroomTeacherName || ''}">
            <td>${c.id}</td>
            <td><strong>${c.name}</strong></td>
            <td>Khối ${c.gradeLevel || ''}</td>
            <td>${c.schoolYear || ''}</td>
            <td>${c.schoolName || ''}</td>
            <td>${c.homeroomTeacherName || 'Chưa phân công'}</td>
            <td>${renderActionButtons(c.id, 'classrooms')}</td>
        </tr>
    `).join('');
}

function renderStaffs(staffs) {
    const list = document.getElementById('staffs-list');
    list.innerHTML = staffs.map(s => `
        <tr data-row-search="${s.fullName} ${s.email} ${s.role}">
            <td>${s.id}</td>
            <td><strong>${s.fullName}</strong></td>
            <td>${s.dateOfBirth || ''}</td>
            <td>${s.phone || ''}</td>
            <td>${s.email || ''}</td>
            <td><span class="badge">${getRoleLabel(s.role)}</span></td>
            <td>${s.schoolName || ''}</td>
            <td>${renderActionButtons(s.id, 'staffs')}</td>
        </tr>
    `).join('');
}

function renderStudents(data) {
    const list = document.getElementById('students-list');
    const students = Array.isArray(data) ? data : (data.content || []);
    list.innerHTML = students.map(s => `
        <tr data-row-search="${s.studentCode} ${s.fullName} ${s.classroomName || ''}">
            <td>${s.id}</td>
            <td><code>${s.studentCode || ''}</code></td>
            <td><strong>${s.fullName}</strong></td>
            <td>${s.dateOfBirth || ''}</td>
            <td>${s.gender === 'MALE' ? 'Nam' : 'Nữ'}</td>
            <td>${s.address || ''}</td>
            <td>${s.classroomName || 'Chưa xếp lớp'}</td>
            <td>${renderActionButtons(s.id, 'students')}</td>
        </tr>
    `).join('');
}

function renderSubjects(subjects) {
    const list = document.getElementById('subjects-list');
    list.innerHTML = subjects.map(s => `
        <tr data-row-search="${s.name} ${s.teacherName || ''}">
            <td>${s.id}</td>
            <td><strong>${s.name}</strong></td>
            <td>${s.description || ''}</td>
            <td>${s.teacherName || 'Chưa xếp giáo viên'}</td>
            <td>${renderActionButtons(s.id, 'subjects')}</td>
        </tr>
    `).join('');
}

function renderScores(scores) {
    const list = document.getElementById('scores-list');
    list.innerHTML = scores.map(s => `
        <tr data-row-search="${s.studentName} ${s.subjectName}">
            <td><strong>${s.studentName}</strong> <br><small>${s.studentCode}</small></td>
            <td>${s.subjectName}</td>
            <td>${s.semester}</td>
            <td>${s.schoolYear}</td>
            <td>${s.score15min || '-'}</td>
            <td>${s.score45min || '-'}</td>
            <td>${s.scoreFinal || '-'}</td>
            <td><strong style="color:var(--success)">${s.averageScore || '-'}</strong></td>
            <td>${renderActionButtons(s.id, 'score-records')}</td>
        </tr>
    `).join('');
}

function renderGradeSlips(slips) {
    const list = document.getElementById('grade-slips-list');
    list.innerHTML = slips.map(g => `
        <tr data-row-search="${g.studentName} ${g.subjectName} ${g.title}">
            <td><strong>${g.studentName}</strong></td>
            <td>${g.subjectName}</td>
            <td>${g.title}</td>
            <td><span class="badge">${g.scoreType === '15MIN' ? '15 Phút' : g.scoreType === '45MIN' ? '1 Tiết' : g.scoreType === 'FINAL' ? 'Thi HK' : g.scoreType === 'ORAL' ? 'Miệng' : g.scoreType}</span></td>
            <td><strong style="color:var(--accent-indigo)">${g.score}</strong></td>
            <td>${g.examDate || ''}</td>
            <td><small>${g.remarks || ''}</small></td>
            <td>${renderActionButtons(g.id, 'grade-slips')}</td>
        </tr>
    `).join('');
}

function renderJournals(journals) {
    const list = document.getElementById('journals-list');
    list.innerHTML = journals.map(j => `
        <tr data-row-search="${j.classroomName} ${j.subjectName} ${j.teacherName}">
            <td><strong>Lớp ${j.classroomName}</strong></td>
            <td>${j.teachingDate}</td>
            <td>Tiết ${j.lessonPeriod}</td>
            <td>${j.subjectName}</td>
            <td>${j.teacherName}</td>
            <td>${j.lessonContent || ''}</td>
            <td>${j.studentAttendance || 'Không'}</td>
            <td><small>${j.remarks || ''}</small></td>
            <td><strong style="color:var(--accent-blue)">${j.score} / 10</strong></td>
            <td>${renderActionButtons(j.id, 'class-journals')}</td>
        </tr>
    `).join('');
}

function getConductBadge(conduct) {
    if (!conduct) return '-';
    
    let text = conduct;
    let style = '';
    
    if (conduct === 'EXCELLENT' || conduct === 'Tốt') {
        text = 'Tốt';
        style = 'background: var(--success);';
    } else if (conduct === 'GOOD' || conduct === 'Khá') {
        text = 'Khá';
        style = 'background: var(--accent-blue);';
    } else if (conduct === 'AVERAGE' || conduct === 'Trung bình') {
        text = 'Trung bình';
        style = 'background: var(--warning); color: #000;';
    } else if (conduct === 'WEAK' || conduct === 'Yếu') {
        text = 'Yếu';
        style = 'background: var(--danger);';
    }
    
    return `<span class="badge" style="${style}">${text}</span>`;
}

function renderReportCards(cards) {
    const list = document.getElementById('report-cards-list');
    list.innerHTML = cards.map(c => `
        <tr data-row-search="${c.studentName} ${c.studentCode} ${c.classroomName || ''}">
            <td><code>${c.studentCode}</code></td>
            <td><strong>${c.studentName}</strong></td>
            <td>${c.schoolYear}</td>
            <td>Khối ${c.gradeLevel}</td>
            <td><strong style="color:var(--accent-blue)">${c.gpa || '-'}</strong></td>
            <td>${getConductBadge(c.conduct)}</td>
            <td><small>${c.teacherRemarks || ''}</small></td>
            <td><small style="color:var(--text-muted)">${c.parentRemarks || 'Chưa có ý kiến'}</small></td>
            <td><span class="badge" style="background:${c.promoted ? 'var(--success)' : 'var(--danger)'}">${c.promoted ? 'Lên lớp' : 'Ở lại lớp'}</span></td>
            <td>${renderActionButtons(c.id, 'report-cards')}</td>
        </tr>
    `).join('');
}

function renderCards(cards) {
    const list = document.getElementById('cards-list');
    list.innerHTML = cards.map(c => `
        <tr data-row-search="${c.studentName} ${c.studentCode} ${c.cardNumber}">
            <td><code>${c.studentCode}</code></td>
            <td><strong>${c.studentName}</strong></td>
            <td><code>${c.cardNumber}</code></td>
            <td>${c.issueDate}</td>
            <td>${c.expiryDate}</td>
            <td><span class="badge" style="background:${c.status === 'ACTIVE' ? 'var(--success)' : 'var(--text-muted)'}">${c.status === 'ACTIVE' ? 'Kích hoạt' : 'Khóa'}</span></td>
            <td>${renderActionButtons(c.id, 'student-cards')}</td>
        </tr>
    `).join('');
}

function renderParents(parents) {
    const list = document.getElementById('parents-list');
    list.innerHTML = parents.map(p => `
        <tr data-row-search="${p.fullName} ${p.email} ${p.occupation}">
            <td>${p.id}</td>
            <td><strong>${p.fullName}</strong></td>
            <td>${p.phone || ''}</td>
            <td>${p.email || ''}</td>
            <td>${p.occupation || ''}</td>
            <td>${p.address || ''}</td>
            <td>${p.studentNames && p.studentNames.length > 0 ? p.studentNames.join(', ') : 'Chưa có'}</td>
            <td>${renderActionButtons(p.id, 'parents')}</td>
        </tr>
    `).join('');
}

// Action buttons renderer with role mapping checks
function renderActionButtons(id, type) {
    if (!currentUser) return '';
    const role = currentUser.role;

    // Check specific role editing/deleting constraints
    let canEdit = false;
    let canDelete = false;

    if (role === 'ADMIN') {
        canEdit = true;
        canDelete = true;
    } else if (role === 'EDUCATION_OFFICER') {
        canEdit = true;
        canDelete = ['schools', 'staffs'].includes(type) ? false : true;
    } else if (role === 'TEACHER') {
        canEdit = ['score-records', 'grade-slips', 'class-journals'].includes(type);
        canDelete = false; // Teachers cannot delete
    } else if (role === 'PARENT') {
        // Parents can edit parentComments on report-cards only
        canEdit = type === 'report-cards';
        canDelete = false;
    }

    let buttons = '';
    if (canEdit) {
        buttons += `<button class="btn-icon" onclick="openFormModal(${id}, '${type}')"><i class="fa-solid fa-pen-to-square"></i></button>`;
    }
    if (canDelete) {
        buttons += `<button class="btn-icon delete" onclick="deleteRecord(${id}, '${type}')"><i class="fa-solid fa-trash"></i></button>`;
    }
    return `<div class="action-buttons">${buttons}</div>`;
}

// Modal Form Opener
async function openFormModal(id = null, type = null) {
    if (!type) {
        type = activeTab.replace('-tab', '');
        if (type === 'schools') type = 'schools';
        if (type === 'classrooms') type = 'classrooms';
        if (type === 'staffs') type = 'staffs';
        if (type === 'students') type = 'students';
        if (type === 'subjects') type = 'subjects';
        if (type === 'journals') type = 'class-journals';
        if (type === 'report-cards') type = 'report-cards';
        if (type === 'cards') type = 'student-cards';
        if (type === 'parents') type = 'parents';
    }

    modalTitle.innerText = id ? 'Cập nhật bản ghi' : 'Thêm bản ghi mới';
    dynamicForm.setAttribute('data-id', id || '');
    dynamicForm.setAttribute('data-type', type);

    // Fetch details if edit mode
    let record = null;
    if (id) {
        let endpoint = `/api/${type}/${id}`;
        if (type === 'score-records') endpoint = `/api/score-records/${id}`;
        if (type === 'grade-slips') endpoint = `/api/grade-slips/${id}`;
        if (type === 'class-journals') endpoint = `/api/class-journals/${id}`;
        if (type === 'report-cards') endpoint = `/api/report-cards/${id}`;
        if (type === 'student-cards') endpoint = `/api/student-cards/${id}`;

        record = await apiRequest(endpoint);
    }

    // Load dynamic options if needed for dropdowns
    let dropdowns = {};
    if (['classrooms', 'staffs', 'students', 'subjects', 'score-records', 'grade-slips', 'class-journals', 'report-cards', 'student-cards', 'parents'].includes(type)) {
        const schools = await apiRequest('/api/schools');
        const teachers = await apiRequest('/api/staffs');
        const students = await apiRequest('/api/students');
        const classrooms = await apiRequest('/api/classrooms');
        const subjects = await apiRequest('/api/subjects');
        
        dropdowns = {
            schools: schools || [],
            teachers: (teachers || []).filter(t => t.role === 'TEACHER'),
            students: students || [],
            classrooms: classrooms || [],
            subjects: subjects || []
        };
    }

    let fieldsHTML = '';
    
    if (type === 'schools') {
        fieldsHTML = `
            <div class="form-group">
                <label>Tên trường</label>
                <input type="text" name="name" required value="${record?.name || ''}">
            </div>
            <div class="form-group">
                <label>Địa chỉ</label>
                <input type="text" name="address" value="${record?.address || ''}">
            </div>
            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text" name="phone" value="${record?.phone || ''}">
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${record?.email || ''}">
            </div>
            <div class="form-group">
                <label>Loại trường</label>
                <select name="schoolType">
                    <option value="PRIMARY" ${record?.schoolType === 'PRIMARY' ? 'selected' : ''}>Tiểu học</option>
                    <option value="SECONDARY" ${record?.schoolType === 'SECONDARY' ? 'selected' : ''}>THCS</option>
                    <option value="HIGH" ${record?.schoolType === 'HIGH' ? 'selected' : ''}>THPT</option>
                </select>
            </div>
        `;
    } else if (type === 'classrooms') {
        fieldsHTML = `
            <div class="form-group">
                <label>Tên lớp học</label>
                <input type="text" name="name" required value="${record?.name || ''}">
            </div>
            <div class="form-group">
                <label>Khối lớp</label>
                <input type="number" name="gradeLevel" required value="${record?.gradeLevel || ''}">
            </div>
            <div class="form-group">
                <label>Niên khóa</label>
                <input type="text" name="schoolYear" placeholder="e.g. 2025-2026" required value="${record?.schoolYear || ''}">
            </div>
            <div class="form-group">
                <label>Trường liên kết</label>
                <select name="schoolId" required>
                    ${dropdowns.schools?.map(s => `<option value="${s.id}" ${record?.schoolId === s.id ? 'selected' : ''}>${s.name}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Giáo viên chủ nhiệm</label>
                <select name="homeroomTeacherId">
                    <option value="">Chưa phân công</option>
                    ${dropdowns.teachers?.map(t => `<option value="${t.id}" ${record?.homeroomTeacherId === t.id ? 'selected' : ''}>${t.fullName}</option>`).join('')}
                </select>
            </div>
        `;
    } else if (type === 'staffs') {
        fieldsHTML = `
            <div class="form-group">
                <label>Họ và tên nhân viên</label>
                <input type="text" name="fullName" required value="${record?.fullName || ''}">
            </div>
            <div class="form-group">
                <label>Ngày sinh (YYYY-MM-DD)</label>
                <input type="text" name="dateOfBirth" placeholder="YYYY-MM-DD" value="${record?.dateOfBirth || ''}">
            </div>
            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text" name="phone" value="${record?.phone || ''}">
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${record?.email || ''}">
            </div>
            <div class="form-group">
                <label>Vai trò</label>
                <select name="role" required>
                    <option value="TEACHER" ${record?.role === 'TEACHER' ? 'selected' : ''}>Giáo viên</option>
                    <option value="EDUCATION_OFFICER" ${record?.role === 'EDUCATION_OFFICER' ? 'selected' : ''}>Giáo vụ</option>
                    <option value="ADMIN" ${record?.role === 'ADMIN' ? 'selected' : ''}>Quản trị viên</option>
                </select>
            </div>
            <div class="form-group">
                <label>Trường làm việc</label>
                <select name="schoolId" required>
                    ${dropdowns.schools?.map(s => `<option value="${s.id}" ${record?.schoolId === s.id ? 'selected' : ''}>${s.name}</option>`).join('')}
                </select>
            </div>
            ${!record ? `
            <div class="form-group">
                <label>Tạo Tài khoản đăng nhập (Tên đăng nhập)</label>
                <input type="text" name="username" placeholder="Để trống nếu không muốn tạo tài khoản">
            </div>
            <div class="form-group">
                <label>Mật khẩu tài khoản</label>
                <input type="password" name="password" placeholder="Mặc định: 123456">
            </div>
            ` : ''}
        `;
    } else if (type === 'students') {
        fieldsHTML = `
            <div class="form-group">
                <label>Mã học sinh</label>
                <input type="text" name="studentCode" required value="${record?.studentCode || ''}">
            </div>
            <div class="form-group">
                <label>Họ và tên học sinh</label>
                <input type="text" name="fullName" required value="${record?.fullName || ''}">
            </div>
            <div class="form-group">
                <label>Ngày sinh (YYYY-MM-DD)</label>
                <input type="text" name="dateOfBirth" placeholder="YYYY-MM-DD" value="${record?.dateOfBirth || ''}">
            </div>
            <div class="form-group">
                <label>Giới tính</label>
                <select name="gender">
                    <option value="MALE" ${record?.gender === 'MALE' ? 'selected' : ''}>Nam</option>
                    <option value="FEMALE" ${record?.gender === 'FEMALE' ? 'selected' : ''}>Nữ</option>
                </select>
            </div>
            <div class="form-group">
                <label>Địa chỉ</label>
                <input type="text" name="address" value="${record?.address || ''}">
            </div>
            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text" name="phone" value="${record?.phone || ''}">
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${record?.email || ''}">
            </div>
            <div class="form-group">
                <label>Lớp học</label>
                <select name="classroomId">
                    <option value="">Chưa xếp lớp</option>
                    ${dropdowns.classrooms?.map(c => `<option value="${c.id}" ${record?.classroomId === c.id ? 'selected' : ''}>${c.name} (${c.schoolYear})</option>`).join('')}
                </select>
            </div>
        `;
    } else if (type === 'subjects') {
        fieldsHTML = `
            <div class="form-group">
                <label>Tên môn học</label>
                <input type="text" name="name" required value="${record?.name || ''}">
            </div>
            <div class="form-group">
                <label>Mô tả môn học</label>
                <input type="text" name="description" value="${record?.description || ''}">
            </div>
            <div class="form-group">
                <label>Giáo viên phụ trách</label>
                <select name="teacherId">
                    <option value="">Chưa xếp giáo viên</option>
                    ${dropdowns.teachers?.map(t => `<option value="${t.id}" ${record?.teacherId === t.id ? 'selected' : ''}>${t.fullName}</option>`).join('')}
                </select>
            </div>
        `;
    } else if (type === 'score-records') {
        fieldsHTML = `
            <div class="form-group">
                <label>Học sinh</label>
                <select name="studentId" required ${record ? 'disabled' : ''}>
                    ${dropdowns.students?.map(s => `<option value="${s.id}" ${record?.studentId === s.id ? 'selected' : ''}>${s.fullName} (${s.studentCode})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Môn học</label>
                <select name="subjectId" required ${record ? 'disabled' : ''}>
                    ${dropdowns.subjects?.map(sb => `<option value="${sb.id}" ${record?.subjectId === sb.id ? 'selected' : ''}>${sb.name}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Kỳ học</label>
                <select name="semester">
                    <option value="Học kỳ I" ${record?.semester === 'Học kỳ I' ? 'selected' : ''}>Học kỳ I</option>
                    <option value="Học kỳ II" ${record?.semester === 'Học kỳ II' ? 'selected' : ''}>Học kỳ II</option>
                </select>
            </div>
            <div class="form-group">
                <label>Năm học</label>
                <input type="text" name="schoolYear" required value="${record?.schoolYear || ''}">
            </div>
            <div class="form-group">
                <label>Điểm 15 phút</label>
                <input type="number" step="0.1" name="score15min" value="${record?.score15min || ''}">
            </div>
            <div class="form-group">
                <label>Điểm 1 tiết (45 phút)</label>
                <input type="number" step="0.1" name="score45min" value="${record?.score45min || ''}">
            </div>
            <div class="form-group">
                <label>Điểm thi học kỳ</label>
                <input type="number" step="0.1" name="scoreFinal" value="${record?.scoreFinal || ''}">
            </div>
        `;
    } else if (type === 'grade-slips') {
        fieldsHTML = `
            <div class="form-group">
                <label>Học sinh</label>
                <select name="studentId" required>
                    ${dropdowns.students?.map(s => `<option value="${s.id}" ${record?.studentId === s.id ? 'selected' : ''}>${s.fullName} (${s.studentCode})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Môn học</label>
                <select name="subjectId" required>
                    ${dropdowns.subjects?.map(sb => `<option value="${sb.id}" ${record?.subjectId === sb.id ? 'selected' : ''}>${sb.name}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Tên bài kiểm tra</label>
                <input type="text" name="title" required placeholder="e.g. Kiểm tra miệng, Giữa kỳ..." value="${record?.title || ''}">
            </div>
            <div class="form-group">
                <label>Loại điểm</label>
                <select name="scoreType">
                    <option value="ORAL" ${record?.scoreType === 'ORAL' ? 'selected' : ''}>Điểm Miệng</option>
                    <option value="15MIN" ${record?.scoreType === '15MIN' ? 'selected' : ''}>Điểm 15p</option>
                    <option value="45MIN" ${record?.scoreType === '45MIN' ? 'selected' : ''}>Điểm 45p</option>
                    <option value="FINAL" ${record?.scoreType === 'FINAL' ? 'selected' : ''}>Điểm Học Kỳ</option>
                </select>
            </div>
            <div class="form-group">
                <label>Điểm số</label>
                <input type="number" step="0.1" name="score" required value="${record?.score || ''}">
            </div>
            <div class="form-group">
                <label>Ngày kiểm tra (YYYY-MM-DD)</label>
                <input type="text" name="examDate" placeholder="YYYY-MM-DD" value="${record?.examDate || ''}">
            </div>
            <div class="form-group">
                <label>Ghi chú</label>
                <input type="text" name="remarks" value="${record?.remarks || ''}">
            </div>
        `;
    } else if (type === 'class-journals') {
        fieldsHTML = `
            <div class="form-group">
                <label>Lớp học</label>
                <select name="classroomId" required>
                    ${dropdowns.classrooms?.map(c => `<option value="${c.id}" ${record?.classroomId === c.id ? 'selected' : ''}>Lớp: ${c.name}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Môn học giảng dạy</label>
                <select name="subjectId" required>
                    ${dropdowns.subjects?.map(sb => `<option value="${sb.id}" ${record?.subjectId === sb.id ? 'selected' : ''}>${sb.name}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Giáo viên giảng dạy</label>
                <select name="teacherId" required>
                    ${dropdowns.teachers?.map(t => `<option value="${t.id}" ${record?.teacherId === t.id ? 'selected' : ''}>${t.fullName}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Ngày học (YYYY-MM-DD)</label>
                <input type="text" name="teachingDate" required placeholder="YYYY-MM-DD" value="${record?.teachingDate || ''}">
            </div>
            <div class="form-group">
                <label>Tiết dạy</label>
                <input type="number" name="lessonPeriod" required value="${record?.lessonPeriod || ''}">
            </div>
            <div class="form-group">
                <label>Nội dung tiết học</label>
                <textarea name="lessonContent">${record?.lessonContent || ''}</textarea>
            </div>
            <div class="form-group">
                <label>Học sinh vắng mặt</label>
                <input type="text" name="studentAttendance" value="${record?.studentAttendance || ''}">
            </div>
            <div class="form-group">
                <label>Đánh giá của giáo viên</label>
                <input type="text" name="remarks" value="${record?.remarks || ''}">
            </div>
            <div class="form-group">
                <label>Điểm đánh giá tiết (0 - 10)</label>
                <input type="number" name="score" min="0" max="10" required value="${record?.score || '10'}">
            </div>
        `;
    } else if (type === 'report-cards') {
        const isParent = currentUser?.role === 'PARENT';
        fieldsHTML = `
            <div class="form-group">
                <label>Học sinh</label>
                <select name="studentId" required ${record || isParent ? 'disabled' : ''}>
                    ${dropdowns.students?.map(s => `<option value="${s.id}" ${record?.studentId === s.id ? 'selected' : ''}>${s.fullName} (${s.studentCode})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Năm học</label>
                <input type="text" name="schoolYear" required ${isParent ? 'readonly' : ''} value="${record?.schoolYear || ''}">
            </div>
            <div class="form-group">
                <label>Khối lớp</label>
                <input type="number" name="gradeLevel" required ${isParent ? 'readonly' : ''} value="${record?.gradeLevel || ''}">
            </div>
            <div class="form-group">
                <label>Điểm trung bình năm học (GPA)</label>
                <input type="number" step="0.01" name="gpa" ${isParent ? 'readonly' : ''} value="${record?.gpa || ''}">
            </div>
            <div class="form-group">
                <label>Hạnh kiểm</label>
                <select name="conduct" ${isParent ? 'disabled' : ''}>
                    <option value="Tốt" ${record?.conduct === 'Tốt' || record?.conduct === 'EXCELLENT' ? 'selected' : ''}>Tốt</option>
                    <option value="Khá" ${record?.conduct === 'Khá' || record?.conduct === 'GOOD' ? 'selected' : ''}>Khá</option>
                    <option value="Trung bình" ${record?.conduct === 'Trung bình' || record?.conduct === 'AVERAGE' ? 'selected' : ''}>Trung bình</option>
                    <option value="Yếu" ${record?.conduct === 'Yếu' || record?.conduct === 'WEAK' ? 'selected' : ''}>Yếu</option>
                </select>
            </div>
            <div class="form-group">
                <label>Ý kiến/Nhận xét của Giáo viên</label>
                <textarea name="teacherRemarks" ${isParent ? 'readonly' : ''}>${record?.teacherRemarks || ''}</textarea>
            </div>
            <div class="form-group">
                <label>Ý kiến của Phụ huynh</label>
                <textarea name="parentRemarks" ${!isParent ? 'readonly' : ''} placeholder="Dành riêng cho phụ huynh phản hồi...">${record?.parentRemarks || ''}</textarea>
            </div>
            <div class="form-group">
                <label style="display:flex; align-items:center; gap:8px;">
                    <input type="checkbox" name="promoted" style="width:auto;" ${record?.promoted ? 'checked' : ''} ${isParent ? 'disabled' : ''}> Lên lớp thẳng
                </label>
            </div>
        `;
    } else if (type === 'student-cards') {
        fieldsHTML = `
            <div class="form-group">
                <label>Học sinh</label>
                <select name="studentId" required ${record ? 'disabled' : ''}>
                    ${dropdowns.students?.map(s => `<option value="${s.id}" ${record?.studentId === s.id ? 'selected' : ''}>${s.fullName} (${s.studentCode})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Mã số thẻ</label>
                <input type="text" name="cardNumber" required value="${record?.cardNumber || ''}">
            </div>
            <div class="form-group">
                <label>Ngày cấp (YYYY-MM-DD)</label>
                <input type="text" name="issueDate" required placeholder="YYYY-MM-DD" value="${record?.issueDate || ''}">
            </div>
            <div class="form-group">
                <label>Ngày hết hạn (YYYY-MM-DD)</label>
                <input type="text" name="expiryDate" required placeholder="YYYY-MM-DD" value="${record?.expiryDate || ''}">
            </div>
            <div class="form-group">
                <label>Trạng thái thẻ</label>
                <select name="status">
                    <option value="ACTIVE" ${record?.status === 'ACTIVE' ? 'selected' : ''}>Hoạt động (ACTIVE)</option>
                    <option value="LOCKED" ${record?.status === 'LOCKED' ? 'selected' : ''}>Bị khóa (LOCKED)</option>
                    <option value="EXPIRED" ${record?.status === 'EXPIRED' ? 'selected' : ''}>Hết hạn (EXPIRED)</option>
                </select>
            </div>
        `;
    } else if (type === 'parents') {
        fieldsHTML = `
            <div class="form-group">
                <label>Họ và tên Phụ huynh</label>
                <input type="text" name="fullName" required value="${record?.fullName || ''}">
            </div>
            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text" name="phone" value="${record?.phone || ''}">
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${record?.email || ''}">
            </div>
            <div class="form-group">
                <label>Nghề nghiệp</label>
                <input type="text" name="occupation" value="${record?.occupation || ''}">
            </div>
            <div class="form-group">
                <label>Địa chỉ</label>
                <input type="text" name="address" value="${record?.address || ''}">
            </div>
            <div class="form-group">
                <label>Học sinh liên kết (chọn HS là con em)</label>
                <select name="studentIds" multiple style="height:120px;">
                    ${dropdowns.students?.map(s => `<option value="${s.id}" ${record?.studentIds?.includes(s.id) ? 'selected' : ''}>${s.fullName} (Lớp: ${s.classroomName || 'Chưa xếp'})</option>`).join('')}
                </select>
                <small style="color:var(--text-muted)">Giữ Ctrl để chọn nhiều học sinh</small>
            </div>
            ${!record ? `
            <div class="form-group">
                <label>Tạo Tài khoản đăng nhập (Tên đăng nhập)</label>
                <input type="text" name="username" placeholder="Để trống nếu không muốn tạo tài khoản">
            </div>
            <div class="form-group">
                <label>Mật khẩu tài khoản</label>
                <input type="password" name="password" placeholder="Mặc định: 123456">
            </div>
            ` : ''}
        `;
    }

    formFields.innerHTML = fieldsHTML;
    modalContainer.classList.add('active');
}

// Delete Record
async function deleteRecord(id, type) {
    if (!confirm('Bạn có chắc chắn muốn xóa bản ghi này?')) return;
    
    let endpoint = `/api/${type}/${id}`;
    if (type === 'score-records') endpoint = `/api/score-records/${id}`;
    if (type === 'grade-slips') endpoint = `/api/grade-slips/${id}`;
    if (type === 'class-journals') endpoint = `/api/class-journals/${id}`;
    if (type === 'report-cards') endpoint = `/api/report-cards/${id}`;
    if (type === 'student-cards') endpoint = `/api/student-cards/${id}`;

    const res = await apiRequest(endpoint, 'DELETE');
    if (res) {
        showToast('Xóa bản ghi thành công!');
        loadTabData(activeTab);
    }
}

// Global Filter for table search fields
function filterTable(query) {
    const term = query.toLowerCase().trim();
    const rows = document.querySelectorAll('.tab-panel.active tbody tr, .tab-panel.active .sub-panel.active tbody tr');
    
    rows.forEach(row => {
        const text = row.getAttribute('data-row-search')?.toLowerCase() || '';
        if (text.includes(term)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Toast Notifications System
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let iconClass = 'fa-circle-check';
    if (type === 'error') iconClass = 'fa-circle-exclamation';
    if (type === 'warning') iconClass = 'fa-triangle-exclamation';

    toast.innerHTML = `
        <i class="fa-solid ${iconClass}"></i>
        <span>${message}</span>
    `;
    
    toastContainer.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
