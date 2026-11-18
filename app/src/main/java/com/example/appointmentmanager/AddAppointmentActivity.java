package com.example.appointmentmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText edtTitle;
    private EditText edtLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchCompat switchReminder;
    private Button btnSave;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        db = new DatabaseHelper(this);

        edtTitle = findViewById(R.id.edtTitle);
        edtLocation = findViewById(R.id.edtLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSave = findViewById(R.id.btnSave);

        timePicker.setIs24HourView(true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointment();
            }
        });
    }

    private void saveAppointment() {
        String title = edtTitle.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError(getString(R.string.err_title_empty));
            edtTitle.requestFocus();
            return;
        }
        if (location.isEmpty()) {
            edtLocation.setError(getString(R.string.err_location_empty));
            edtLocation.requestFocus();
            return;
        }

        int year = datePicker.getYear();
        int month = datePicker.getMonth();      // 0-11
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Thời điểm hẹn phải sau thời điểm hiện tại
        Calendar chosen = Calendar.getInstance();
        chosen.set(year, month, day, hour, minute, 0);
        chosen.set(Calendar.MILLISECOND, 0);
        if (chosen.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, R.string.err_time_past, Toast.LENGTH_LONG).show();
            return;
        }

        String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        String time = String.format(Locale.US, "%02d:%02d", hour, minute);

        Appointment a = new Appointment(title, location, date, time, switchReminder.isChecked());
        long id = db.insertAppointment(a);

        if (id == -1) {
            Toast.makeText(this, R.string.err_save_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
        finish(); // quay về danh sách; MainActivity.onResume() sẽ tải lại dữ liệu
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
