package com.example.appointmentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_APPOINTMENT_ID = "appointment_id";

    private TextView tvStats;
    private TextView tvEmpty;
    private RecyclerView rvAppointments;
    private Button btnAdd;
    private Button btnLogout;

    private DatabaseHelper db;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        tvStats = findViewById(R.id.tvStats);
        tvEmpty = findViewById(R.id.tvEmpty);
        rvAppointments = findViewById(R.id.rvAppointments);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);

        adapter = new AppointmentAdapter(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Appointment appointment) {
                Intent intent = new Intent(MainActivity.this, AppointmentDetailActivity.class);
                intent.putExtra(EXTRA_APPOINTMENT_ID, appointment.getId()); // chỉ truyền id
                startActivity(intent);
            }
        });
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvAppointments.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddAppointmentActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // tải lại danh sách + thống kê sau khi thêm / sửa / xóa
    }

    private void loadData() {
        List<Appointment> list = db.getAllAppointments();
        adapter.setData(list);

        boolean empty = list.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvAppointments.setVisibility(empty ? View.GONE : View.VISIBLE);

        tvStats.setText(getString(R.string.stats_format, db.countAll(), db.countReminders()));
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
