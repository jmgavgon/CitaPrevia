package com.jmg.citaprevia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jmg.citaprevia.R;
import com.jmg.citaprevia.model.Appointment;
import com.jmg.citaprevia.model.HourEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent> {
    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents) {
        super(context, 0, hourEvents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);

        setHour(convertView, event.getTime());
        setAppointment(convertView, event.getAppointment(), event.isBlocked());

        return convertView;
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(DateTimeFormatter.ofPattern("HH:mm").format((time)));
    }

    private void setAppointment(View convertView, Appointment appointment, boolean isBlocked) {
        TextView eventUnblocked = convertView.findViewById(R.id.eventunblocked);
        TextView eventBlocked = convertView.findViewById(R.id.eventblocked);

        if(isBlocked){
            eventBlocked.setVisibility(View.VISIBLE);
            eventUnblocked.setVisibility(View.GONE);
        }else if (appointment == null) {
            eventUnblocked.setVisibility(View.INVISIBLE);
            eventBlocked.setVisibility(View.GONE);
        } else if (appointment != null) {
            eventUnblocked.setText(appointment.getTitle());
            eventUnblocked.setVisibility(View.VISIBLE);
            eventBlocked.setVisibility(View.GONE);

        }
    }

    private void setAppointment(TextView textView, Appointment appointment) {

    }

    private void hideAppointment(TextView tv) {

    }

}






