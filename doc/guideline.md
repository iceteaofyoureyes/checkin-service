# Hướng dẫn Test các API trong hệ thống với Postman

Tài liệu này mô tả mục đích của từng API, luồng chạy test end-to-end bằng Postman, cách sử dụng biến môi trường, cũng như các lưu ý về header như `X-Timezone`.

---

## 1. Chuẩn bị Postman Collection

- Sau khi export collection, bạn sẽ tìm thấy file `cis.postman_collection.json` trong thư mục: `doc/`.

- Mở Postman → Chọn **Import** → Kéo file `cis.postman_collection.json` vào hoặc chọn từ thư mục này.

---

## 2. Biến môi trường (Collection Variables)

Trong Postman, tạo **Environment** với các biến sau:

- `baseURL` → URL gốc của API (ví dụ: `http://localhost:8080/api`).
- `admin_token` → JWT Token sau khi đăng nhập Admin.
- `access_token` → JWT Token sau khi đăng nhập User.

Các biến token sẽ được **set tự động bằng script** sau khi gọi API Login.

![Postman Env](/doc/assets/collection.png)

---

## 3. Luồng gọi API

### 3.1. Đăng nhập Admin (auth/Login Admin Account)
- **Endpoint:**  
  `POST {{baseURL}}/auth/login`
- **Mục đích:** Xác thực admin, lấy `admin_token`.
- **Headers bắt buộc:**
- `Content-Type: application/json`
- **Lưu ý:** Giữ nguyên tài khoản có sẵn trong request (đã được seeding trước đó). Sau khi đăng nhập, script trong Postman sẽ gán giá trị JWT vào `admin_token`.

---

### 3.2. Tạo User (user/Create user)
- **Endpoint:**  
  `POST {{baseURL}}/users`
- **Mục đích:** Admin tạo tài khoản user mới.
- **Headers:**
- `Authorization: Bearer {{admin_token}}`
- **Body (form-data):**

![Postman Env](/doc/assets/create-user.png)

### 3.3. Đăng nhập User (auth/Login)
- **Endpoint:**  
  `POST {{baseURL}}/auth/login`
- **Mục đích:** Lấy `access_token` cho user.
- **Headers bắt buộc:**
- `Content-Type: application/json`
- **Lưu ý:** Nếu không thay đổi trong request create user có thể bấm gọi luôn API.

### 3.4. Lấy profile bản thân (user/Get Self Profile)
- **Endpoint:**  
  `GET {{baseURL}}/users/me`
- **Mục đích:** Lấy thông tin profile của bản thân.
- **Headers bắt buộc:**
- `Authorization: Bearer {{access_token}}`
- **Lưu ý:** Có thể bấm gọi luôn API. Lấy user profile theo access_token.

### 3.5. Lấy profile user (user/Get Self Profile)
- **Endpoint:**  
  `GET {{baseURL}}/users/{userId}/profile`
- **Mục đích:** Lấy thông tin profile của user theo userId.
- **Headers bắt buộc:**
- `Authorization: Bearer {{admin_token}}`
- **Lưu ý:** Có thể bấm gọi luôn API. Chức năng của Admin.

### 3.6. Điểm danh (checkin/User Checkin)
- **Endpoint:**  
  `POST {{baseURL}}/checkin`
- **Mục đích:** Điểm danh nhận điểm.
- **Headers bắt buộc:**
- `Authorization: Bearer {{access_token}}`
- `X-Timezone: Asia/Ho_Chi_Minh` (hoặc timezone khác tùy ý - với yêu cầu bài test đang để mặc định là `Asia/Ho_Chi_Minh`)
- **Lưu ý:** Api có điều kiện check theo khung giờ trong cấu hình, trong khung giờ mới được phép điểm danh nên cần lưu ý - Sửa cấu hình bảng checkin_config cột time_windows về khoảng thời gian phù hợp để test (giờ UTC). Hiện tại là [{"end": "04:00", "start": "02:00"}, {"start":"12:00","end":"14:00"}] => Từ 9:00 - 11:00, từ 19:00 - 21:00 +7. Sửa cấu hình xong hãy gọi API vì có cơ chế cache config (hiện tại chưa có API sửa config phải vào sửa tay trong DB nên cache chỉ cập nhật sau 1h theo cấu hình TTL)

### 3.7. Xem trạng thái điểm danh tháng hiện tại (checkin/Get Month status)
- **Endpoint:**
  `POST {{baseURL}}/checkin/month-status`
- **Mục đích:** Lấy danh sách các trạng thái các ngày điểm danh tháng hiện tại của bản thân.
- **Headers bắt buộc:**
- `Authorization: Bearer {{access_token}}`
- `X-Timezone: Asia/Ho_Chi_Minh` (hoặc timezone khác tùy ý - với yêu cầu bài test đang để mặc định là `Asia/Ho_Chi_Minh`)
- **Lưu ý:** Có thể bấm gọi luôn API.

### 3.8. Xem lịch sử nhận điểm (audit/Get Point reward history)
- **Endpoint:**  
  `GET {{baseURL}}/transactions/point-reward-history?page=0&size=10&sort=asc`
- **Mục đích:** Api lấy lịch sử cộng điểm của bản thân.
- **Headers bắt buộc:**
- `Authorization: Bearer {{access_token}}`
- `X-Timezone: Asia/Ho_Chi_Minh` (hoặc timezone khác tùy ý - với yêu cầu bài test đang để mặc định là `Asia/Ho_Chi_Minh`)
- **Lưu ý:** Có thể bấm gọi luôn API.

### 3.9. Xem lịch sử giao dịch (audit/Get Self transactions)
- **Endpoint:**  
  `GET {{baseURL}}/transactions/me?page=0&size=10&sort=desc`
- **Mục đích:** Api lấy lịch sử giao dịch (cộng, trừ ...) của bản thân.
- **Headers bắt buộc:**
- `Authorization: Bearer {{access_token}}`
- `X-Timezone: Asia/Ho_Chi_Minh` (hoặc timezone khác tùy ý - với yêu cầu bài test đang để mặc định là `Asia/Ho_Chi_Minh`)
- **Lưu ý:** Có thể bấm gọi luôn API.

### 3.10. Giả lập thanh toán (trừ điểm) (payment/Simulate payment)
- **Endpoint:**  
  `POST {{baseURL}}/payment/simulate-payment`
- **Mục đích:** Api giả lập thanh toán trừ điểm.
- **Headers bắt buộc:**
- `Authorization: Bearer {{admin_token}}`
- `X-Timezone: Asia/Ho_Chi_Minh` (hoặc timezone khác tùy ý - với yêu cầu bài test đang để mặc định là `Asia/Ho_Chi_Minh`)
- **Lưu ý:** Chức năng dành cho admin. Có thể bấm gọi luôn API.

## 4 Flow kiểm thử ví dụ
    1. Gọi API Đăng nhập Admin → Lấy admin_token
    2. Gọi API Tạo User → Tạo user mới
    3. Gọi API Đăng nhập User → Lấy access_token
    4. Gọi API Lấy profile bản thân → Xem thông tin user
    5. Gọi API Điểm danh → Nhận điểm
    6. Gọi API Xem trạng thái điểm danh tháng hiện tại → Xem
    7. Gọi API Xem lịch sử nhận điểm → Xem
    8. Gọi API Giả lập thanh toán (trừ điểm)
    9. Gọi API Xem lịch sử giao dịch → Xem
    10. Gọi API Xem profile user → Xem thông tin ví user