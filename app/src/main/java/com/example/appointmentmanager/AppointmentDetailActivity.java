package com.example.appointmentmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AppointmentDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvLocation;
    private TextView tvDateTime;
    private TextView tvReminderState;
    private SwitchCompat switchReminder;
    private Button btnBack;
    private Button btnDelete;

    private DatabaseHelper db;
    private int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        db = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvReminderState = findViewById(R.id.tvReminderState);
        switchReminder = findViewById(R.id.switchReminder);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        // Nhận id qua Intent và truy vấn lại CSDL theo id
        appointmentId = getIntent().getIntExtra(MainActivity.EXTRA_APPOINTMENT_ID, -1);
        Appointment a = db.getAppointmentById(appointmentId);
        if (a == null) {
            Toast.makeText(this, R.string.err_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(a.getTitle());
        tvLocation.setText(a.getLocation());
        tvDateTime.setText(a.getDisplayDateTime());

        // Gán trạng thái ban đầu TRƯỚC khi đặt listener để không ghi DB thừa
        switchReminder.setChecked(a.isReminder());
        updateReminderLabel(a.isReminder());

        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.updateReminder(appointmentId, isChecked);
                updateReminderLabel(isChecked);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteAppointment(appointmentId);
                Toast.makeText(AppointmentDetailActivity.this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateReminderLabel(boolean on) {
        tvReminderState.setText(on ? R.string.reminder_on : R.string.reminder_off);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
