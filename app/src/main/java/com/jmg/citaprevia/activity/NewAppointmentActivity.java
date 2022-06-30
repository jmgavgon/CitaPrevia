package com.jmg.citaprevia.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.jmg.citaprevia.R;
import com.jmg.citaprevia.model.Appointment;
import com.jmg.citaprevia.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class NewAppointmentActivity extends AppCompatActivity {

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");
    private DateTimeFormatter serializeSdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private AwesomeValidation awesomeValidation;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private EditText titleEditText;
    private EditText datetimeEditText;
    private EditText hostEditText;
    private EditText descriptionEditText;
    private Button saveButton;

    private LocalDate date;
    private LocalTime time;
    private User host;
    private String attendant;

    public NewAppointmentActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);
        setupUI();
    }

    private void setupUI(){
        loadElements();
        setupActionBar();
        loadBundle();
        setupDate();
        loadFormValidator();
        handleButtonClick();
    }

    private void handleButtonClick(){
        saveButton.setOnClickListener(x -> {
            if(awesomeValidation.validate()){
                saveAppointment();
            }
        });
    }

    private void saveAppointment(){
        UUID appointmentId = registerAppointment();
        registerAttendant(appointmentId);
        Toast.makeText(this, getString(R.string.appointment_saved), Toast.LENGTH_LONG).show();
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    private UUID registerAppointment(){
        UUID id = UUID.randomUUID();
        Map<String, Object> values =new HashMap<>();
        values.put(getString(R.string.firestore_field_id), id.toString());
        values.put(getString(R.string.firestore_field_title), titleEditText.getText().toString());
        values.put(getString(R.string.firestore_field_description), descriptionEditText.getText().toString());
        values.put(getString(R.string.firestore_field_date), serializeSdf.format(date));
        values.put(getString(R.string.firestore_field_duration), 30);
        values.put(getString(R.string.firestore_field_host), host.getEmail());
        values.put(getString(R.string.firestore_field_hour), time.getHour());
        values.put(getString(R.string.firestore_field_minute), time.getMinute());
        firestore.collection(getString(R.string.firestore_collection_appointments)).document(id.toString()).set(values);
        return id;
    }

    private void registerAttendant(UUID appointmentId){
        UUID id = UUID.randomUUID();
        Map<String, Object> values =new HashMap<>();
        values.put(getString(R.string.firestore_field_appointment_id), appointmentId.toString());
        values.put(getString(R.string.firestore_field_attendant_id), attendant);
        firestore.collection(getString(R.string.firestore_collection_attendants)).document(id.toString()).set(values);
    }


    private void setupDate(){
        hostEditText.setText(host.getName());
        datetimeEditText.setText(LocalDateTime.of(date, time).format(sdf));
    }

    private void loadBundle(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            date = (LocalDate) bundle.get(getString(R.string.date_param));
            time = (LocalTime) bundle.get(getString(R.string.hour_event));
            host = (User) bundle.get(getString(R.string.host));
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
            attendant = sharedPreferences.getString(getString(R.string.email_lower_case), null);
        }
    }

    private void loadElements(){
        titleEditText = findViewById(R.id.newAppointmentTitleEditText);
        descriptionEditText = findViewById(R.id.newAppointmentDescriptionEditText);
        datetimeEditText = findViewById(R.id.newAppointmentDatetimeEditText);
        hostEditText = findViewById(R.id.newAppointmentHostEditText);
        saveButton = findViewById(R.id.newAppointmentEmailButton);
    }

    private void setupActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadFormValidator(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.addValidation(this,R.id.newAppointmentTitleEditText,"[a-zA-Z\\s]+",R.string.err_not_empty);
        awesomeValidation.addValidation(this,R.id.newAppointmentDescriptionEditText,"[a-zA-Z\\s]+",R.string.err_not_empty);
    }
}