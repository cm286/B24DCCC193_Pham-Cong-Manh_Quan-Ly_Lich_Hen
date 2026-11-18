package com.example.appointmentmanager;

/**
 * Lớp biểu diễn một lịch hẹn.
 * date lưu dạng yyyy-MM-dd, time lưu dạng HH:mm (giống cột trong CSDL).
 */
public class Appointment {
    private int id;
    private String title;
    private String location;
    private String date;   // yyyy-MM-dd
    private String time;   // HH:mm
    private boolean reminder;

    public Appointment() {
    }

    public Appointment(int id, String title, String location, String date, String time, boolean reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public Appointment(String title, String location, String date, String time, boolean reminder) {
        this(0, title, location, date, time, reminder);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isReminder() { return reminder; }
    public void setReminder(boolean reminder) { this.reminder = reminder; }

    /** Trả về ngày giờ theo định dạng dd/MM/yyyy · HH:mm để hiển thị. */
    public String getDisplayDateTime() {
        String displayDate = date;
        if (date != null) {
            String[] p = date.split("-");
            if (p.length == 3) {
                displayDate = p[2] + "/" + p[1] + "/" + p[0];
            }
        }
        return displayDate + " · " + time;
    }
}
