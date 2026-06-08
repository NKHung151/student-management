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

Hệ thống đã cấu hình Database Seeder tự động chạy khi khởi động nếu cơ sở dữ liệu trống.
Ngoài ra, để trải nghiệm giao diện với dữ liệu lớn thực tế (60 học sinh, 40 phụ huynh, 240 đầu điểm tổng kết, hàng trăm phiếu điểm và sổ đầu bài), bạn có thể tự import file `seed_data.sql` đính kèm trong dự án.

### Bước 1: Import file SQL vào cơ sở dữ liệu

Hãy mở Terminal/Command Prompt, di chuyển đến thư mục gốc của dự án và chạy một trong các lệnh sau (tùy thuộc vào môi trường chạy MySQL của bạn):

**Trường hợp 1: Bạn cài đặt MySQL Server trực tiếp trên máy tính**
```bash
# Câu lệnh chuẩn (sẽ yêu cầu nhập mật khẩu MySQL của bạn):
mysql -u root -p student_management < seed_data.sql
```

**Trường hợp 2: Bạn chạy MySQL thông qua Docker (ví dụ container tên là `mysql-container`)**
```bash
# Lệnh kết nối trực tiếp vào container và import (thay 123456 bằng mật khẩu thật):
docker exec -i mysql-container mysql -u root -p123456 student_management < seed_data.sql
```

### Bước 2: Đăng nhập bằng các tài khoản dùng thử mẫu

Sau khi import thành công, dữ liệu đã được gán sẵn các tài khoản sau (Mật khẩu mặc định cho tất cả là: `123456`):

- **Quản trị viên (ADMIN):** `admin.hung`
- **Cán bộ giáo vụ (EDUCATION_OFFICER):** `gv.anh` hoặc `gv.trung`
- **Giáo viên bộ môn / Chủ nhiệm (TEACHER):**
  - `gv.ngoc` (Lê Thị Bích Ngọc - Chủ nhiệm 10 Toán 1)
  - `gv.dung` (Trần Văn Dũng - Chủ nhiệm 11 Lý 1)
  - `gv.quan` (Hoàng Minh Quân - Chủ nhiệm 10 Tin 1)
  - `gv.thuy` (Ngô Thị Phương Thùy - Chủ nhiệm 12 Văn 1)
  - Hoặc thử các tài khoản giáo viên khác: `gv.lien`, `gv.hoan`, `gv.tam`, `gv.son`, `gv.xuan`...
- **Phụ huynh học sinh (PARENT):** `ph.quan1`, `ph.hai1`, `ph.vy1`, `ph.mai1`... (có tới 40 tài khoản theo quy tắc `ph.tên_phụ_huynh` kèm số)
