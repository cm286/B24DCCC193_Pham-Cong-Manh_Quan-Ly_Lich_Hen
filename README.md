# Quản lý lịch hẹn (Android – Java + XML + SQLite)

Bài thực hành lập trình Android: ứng dụng ghi lại lịch hẹn cá nhân, lưu trữ bằng SQLite thông qua `SQLiteOpenHelper`
(không dùng Room / Firebase / API).

## Chạy dự án
1. Mở thư mục này bằng **Android Studio** (Hedgehog trở lên), đợi Gradle sync.
2. Chạy trên máy ảo / thiết bị Android 6.0 (API 23) trở lên.
3. Tài khoản mẫu: **admin / 123456**.

## Cấu trúc
```
app/src/main/java/com/example/appointmentmanager/
├── Appointment.java               # Lớp lịch hẹn (id, title, location, date, time, reminder)
├── DatabaseHelper.java            # SQLiteOpenHelper – appointments.db v1, bảng users + appointments
├── LoginActivity.java             # Màn hình 1: đăng nhập (checkLogin trên bảng users)
├── MainActivity.java              # Màn hình 2: danh sách + thống kê
├── AppointmentAdapter.java        # Adapter RecyclerView
├── AddAppointmentActivity.java    # Màn hình 3: thêm lịch hẹn
└── AppointmentDetailActivity.java # Màn hình 4: chi tiết / bật-tắt nhắc nhở / xóa
app/src/main/res/layout/           # activity_login, activity_main, item_appointment,
                                   # activity_add_appointment, activity_appointment_detail
```

## Luồng màn hình
`LoginActivity → MainActivity ⇄ AddAppointmentActivity`
`MainActivity → AppointmentDetailActivity` (truyền `appointment_id` qua Intent)

## Đối chiếu yêu cầu đề bài
| Yêu cầu | Cách thực hiện |
| --- | --- |
| CSDL `appointments.db` v1, tạo bảng + dữ liệu mẫu trong `onCreate()` | `DatabaseHelper.onCreate()` |
| Bảng `users` (id, username UNIQUE, password) + tài khoản admin/123456 | `DatabaseHelper` |
| Bảng `appointments` (id, title, location, date yyyy-MM-dd, time HH:mm, reminder 0/1) + 3 lịch hẹn mẫu | `DatabaseHelper` |
| Đủ 9 phương thức: `checkLogin`, `insertUser`, `insertAppointment`, `getAllAppointments`, `getAppointmentById`, `updateReminder`, `deleteAppointment`, `countAll`, `countReminders` | `DatabaseHelper` |
| Đăng nhập: chặn bỏ trống, sai → báo lỗi và ở lại, đúng → `MainActivity` + `finish()` | `LoginActivity` |
| Danh sách `ORDER BY date ASC, time ASC`, nhãn Có nhắc / Không nhắc, thống kê tổng + nhắc nhở | `MainActivity`, `AppointmentAdapter` |
| Thêm: chặn trống tên/địa điểm, thời điểm phải sau hiện tại, DatePicker + TimePicker spinner + Switch | `AddAppointmentActivity` |
| Chi tiết: nhận id qua Intent, truy vấn lại theo id, bật/tắt nhắc nhở ghi DB, xóa, quay lại | `AppointmentDetailActivity` |
| Truy vấn có tham số `?` + `selectionArgs`, đóng `Cursor`, xử lý danh sách rỗng | `DatabaseHelper`, `MainActivity` |
| Tải lại danh sách + thống kê trong `onResume()` | `MainActivity` |
| `ArrayList` chỉ chứa kết quả truy vấn để hiển thị | `AppointmentAdapter` |

> Lưu ý: dữ liệu mẫu có ngày 25–27/10/2026. Nếu đã cài app trước đó, hãy gỡ cài đặt (hoặc Clear storage)
> để `onCreate()` chạy lại khi muốn nạp lại dữ liệu mẫu.
