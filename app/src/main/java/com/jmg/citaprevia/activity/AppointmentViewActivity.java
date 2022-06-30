package com.jmg.citaprevia.activity;


import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.jmg.citaprevia.R;
import com.jmg.citaprevia.model.Appointment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AppointmentViewActivity extends AppCompatActivity {

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");

    private TextView titleTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_view);
        setupUI();

    }

    private void setupUI(){
        titleTextView = findViewById(R.id.appointmentTitleTextView);
        startTimeTextView = findViewById(R.id.appointmentStartTextView);
        endTimeTextView = findViewById(R.id.appointmentEndTextView);
        descriptionTextView = findViewById(R.id.appointmentDescriptionTextView);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Appointment appointment = (Appointment) bundle.get(getString(R.string.appointment));
            titleTextView.setText(appointment.getTitle());
            startTimeTextView.setText(sdf.format(appointment.getStartTime()));
            endTimeTextView.setText(sdf.format(appointment.getEndTime()));
            descriptionTextView.setText(appointment.getDescription());
        }
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
