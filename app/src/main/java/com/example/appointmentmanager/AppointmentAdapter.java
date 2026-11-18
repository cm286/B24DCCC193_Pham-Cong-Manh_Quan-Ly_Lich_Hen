package com.example.appointmentmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách lịch hẹn. Danh sách chỉ chứa kết quả truy vấn từ SQLite.
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Appointment appointment);
    }

    private final List<Appointment> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public AppointmentAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Appointment> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Appointment a = items.get(position);
        holder.tvTitle.setText(a.getTitle());
        holder.tvLocation.setText(a.getLocation());
        holder.tvDateTime.setText(a.getDisplayDateTime());

        if (a.isReminder()) {
            holder.tvReminder.setText(R.string.badge_reminder_on);
            holder.tvReminder.setBackgroundResource(R.drawable.bg_badge_on);
            holder.tvReminder.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.badge_on_text));
        } else {
            holder.tvReminder.setText(R.string.badge_reminder_off);
            holder.tvReminder.setBackgroundResource(R.drawable.bg_badge_off);
            holder.tvReminder.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.badge_off_text));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(a);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvLocation;
        final TextView tvDateTime;
        final TextView tvReminder;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvLocation = itemView.findViewById(R.id.tvItemLocation);
            tvDateTime = itemView.findViewById(R.id.tvItemDateTime);
            tvReminder = itemView.findViewById(R.id.tvItemReminder);
        }
    }
}
