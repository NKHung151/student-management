# Hệ thống Quản lý Học sinh - THPT Nguyễn Trãi

Cổng thông tin quản lý học sinh toàn diện dành cho **Trường THPT Nguyễn Trãi**. Dự án được xây dựng bằng **Spring Boot 3.5.14**, **Spring Security (JWT)**, và **MySQL**, cung cấp hệ thống RESTful API bảo mật cao và giao diện người dùng (Single Page Application) hiện đại, trực quan.

---

## 🚀 Các Tính Năng & Phân Quyền Nổi Bật (Context-Aware RBAC)

Hệ thống triển khai mô hình phân quyền bảo mật dữ liệu theo ngữ cảnh cực kỳ chặt chẽ (Context-Aware Data Isolation), bảo đảm người dùng chỉ nhìn thấy và thao tác trên dữ liệu thuộc phạm vi thẩm quyền của mình.

### 1. Phân Quyền Xem Dữ Liệu (Read-Side Security)
- **ADMIN / EDUCATION_OFFICER:** Có toàn quyền xem toàn bộ học sinh, lớp học, sổ điểm, sổ đầu bài của toàn trường.
- **TEACHER:**
  - Chỉ xem thông tin các học sinh học lớp mình chủ nhiệm hoặc lớp mình trực tiếp giảng dạy.
  - Chỉ xem các sổ đầu bài (`class_journals`) và bảng điểm (`score_records` / `grade_slips`) thuộc các lớp và môn học được phân công giảng dạy.
- **PARENT:** Chỉ xem thông tin cá nhân, bảng điểm, sổ điểm chi tiết, và học bạ của con mình liên kết.

### 2. Bảo Mật Nghiệp Vụ Ghi Dữ Liệu (Write-Side Security)
- **Điểm số (`GradeSlips` & `ScoreRecords`):** Chỉ giáo viên phụ trách bộ môn tương ứng mới có quyền thêm/sửa/xóa điểm của môn đó cho học sinh lớp được phân công.
- **Sổ đầu bài (`ClassJournals`):** Chỉ giáo viên đứng lớp tiết học đó mới có quyền sửa/xóa nội dung hoặc nhận xét tiết học.
- **Học bạ (`ReportCards`):**
  - Chỉ Giáo viên chủ nhiệm lớp đó mới có quyền cập nhật nhận xét giáo viên chủ nhiệm, xếp loại hạnh kiểm, và quyết định lên lớp.
  - Phụ huynh chỉ có quyền cập nhật ý kiến phụ huynh trong học bạ của con mình, không được phép can thiệp vào điểm số hay đánh giá của giáo viên.

---

## 🛠️ Công Nghệ Sử Dụng

- **Backend Framework:** Spring Boot 3.5.14
- **Security:** Spring Security & JWT (Json Web Token)
- **Database Access:** Spring Data JPA / Hibernate (Hỗ trợ truy vấn lọc dữ liệu bảo mật từ tầng Repository)
- **Database:** MySQL 8.0+
- **API Documentation:** Springdoc OpenAPI (Swagger UI)
- **Giao diện Frontend:** HTML5, CSS3 (Thiết kế Dark Theme / Glassmorphic cao cấp, hiện đại), Vanilla JS (SPA)

---

## 💻 Hướng Dẫn Cài Đặt & Chạy Dự Án

### Yêu cầu hệ thống
- Java Development Kit (JDK) 17 trở lên.
- MySQL Server 8.0 trở lên.
- Maven 3.8+.

### Bước 1: Cấu hình Cơ sở dữ liệu
1. Tạo database mới trong MySQL:
   ```sql
   CREATE DATABASE student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Cập nhật cấu hình kết nối trong file `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/student_management?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8
   spring.datasource.username=tên_đăng_nhập_mysql
   spring.datasource.password=mật_khẩu_mysql
   ```

### Bước 2: Khởi chạy ứng dụng
Biên dịch và chạy ứng dụng bằng Maven:
```bash
mvn clean install
mvn spring-boot:run
```
Ứng dụng sẽ chạy tại địa chỉ: [http://localhost:8080/](http://localhost:8080/)

---

## 🗃️ Hướng Dẫn Khởi Tạo Dữ Liệu Giả Lập (Mock Data)

Hệ thống đã cấu hình Database Seeder mặc định tự động chạy khi khởi động nếu cơ sở dữ liệu trống.
Ngoài ra, để thực hiện stress-test hoặc demo giao diện với tập dữ liệu phong phú (60 học sinh, 40 phụ huynh, 240 đầu điểm tổng kết, 300+ phiếu điểm chi tiết và sổ sách đầy đủ), hãy import file [seed_data.sql](file:///d:/student-management/seed_data.sql) vào MySQL.

### Danh sách tài khoản dùng thử mẫu (Mật khẩu mặc định: `123456`):
- **Quản trị viên (ADMIN):** `admin.hung`
- **Cán bộ giáo vụ (EDUCATION_OFFICER):** `gv.vananh` hoặc `gv.trung`
- **Giáo viên bộ môn / Chủ nhiệm (TEACHER):**
  - `gv.bichngoc` (Chủ nhiệm 10 Toán 1)
  - `gv.dung` (Chủ nhiệm 11 Lý 1)
  - `gv.quan` (Chủ nhiệm 10 Tin 1)
  - `gv.thuy` (Chủ nhiệm 12 Văn 1)
- **Phụ huynh học sinh (PARENT):** `ph.minh` hoặc `ph.huong`
