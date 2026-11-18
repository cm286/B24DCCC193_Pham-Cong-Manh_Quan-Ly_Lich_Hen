package com.example.appointmentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý cơ sở dữ liệu SQLite appointments.db (phiên bản 1).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "appointments.db";
    public static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // Bảng appointments
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_LOCATION = "location";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_APPOINTMENTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT NOT NULL, "
                + COL_LOCATION + " TEXT NOT NULL, "
                + COL_DATE + " TEXT NOT NULL, "
                + COL_TIME + " TEXT NOT NULL, "
                + COL_REMINDER + " INTEGER NOT NULL)");

        // Dữ liệu mẫu: tài khoản
        insertUser(db, "admin", "123456");

        // Dữ liệu mẫu: 3 lịch hẹn
        insertAppointment(db, new Appointment("Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", true));
        insertAppointment(db, new Appointment("Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", true));
        insertAppointment(db, new Appointment("Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", false));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ===================== USERS =====================

    /** Trả về true nếu có dòng khớp trong bảng users. */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null);
        boolean ok = false;
        try {
            ok = cursor.moveToFirst();
        } finally {
            cursor.close();
        }
        return ok;
    }

    /** Thêm tài khoản mới, trả về id (hoặc -1 nếu lỗi / trùng username). */
    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    // Dùng trong onCreate() (không được gọi getWritableDatabase() lồng nhau)
    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // ===================== APPOINTMENTS =====================

    /** Thêm lịch hẹn, trả về id vừa thêm (-1 nếu lỗi). */
    public long insertAppointment(Appointment a) {
        return insertAppointment(getWritableDatabase(), a);
    }

    private long insertAppointment(SQLiteDatabase db, Appointment a) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, a.getTitle());
        values.put(COL_LOCATION, a.getLocation());
        values.put(COL_DATE, a.getDate());
        values.put(COL_TIME, a.getTime());
        values.put(COL_REMINDER, a.isReminder() ? 1 : 0);
        return db.insert(TABLE_APPOINTMENTS, null, values);
    }

    /** Lấy toàn bộ lịch hẹn, sắp xếp theo ngày, giờ tăng dần. */
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, null, null,
                null, null, COL_DATE + " ASC, " + COL_TIME + " ASC");
        try {
            while (cursor.moveToNext()) {
                list.add(cursorToAppointment(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /** Lấy một lịch hẹn theo id, trả về null nếu không tìm thấy. */
    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null,
                COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        Appointment a = null;
        try {
            if (cursor.moveToFirst()) {
                a = cursorToAppointment(cursor);
            }
        } finally {
            cursor.close();
        }
        return a;
    }

    /** Cập nhật trạng thái nhắc nhở. Trả về số dòng bị ảnh hưởng. */
    public int updateReminder(int id, boolean on) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REMINDER, on ? 1 : 0);
        return db.update(TABLE_APPOINTMENTS, values,
                COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /** Xóa lịch hẹn theo id. Trả về số dòng bị xóa. */
    public int deleteAppointment(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_APPOINTMENTS,
                COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /** Tổng số lịch hẹn: COUNT(*). */
    public int countAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS, null);
        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return count;
    }

    /** Số lịch hẹn đang bật nhắc nhở: COUNT với reminder = 1. */
    public int countReminders() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS + " WHERE " + COL_REMINDER + " = ?",
                new String[]{"1"});
        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return count;
    }

    private Appointment cursorToAppointment(Cursor c) {
        return new Appointment(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                c.getString(c.getColumnIndexOrThrow(COL_LOCATION)),
                c.getString(c.getColumnIndexOrThrow(COL_DATE)),
                c.getString(c.getColumnIndexOrThrow(COL_TIME)),
                c.getInt(c.getColumnIndexOrThrow(COL_REMINDER)) == 1);
    }
}
