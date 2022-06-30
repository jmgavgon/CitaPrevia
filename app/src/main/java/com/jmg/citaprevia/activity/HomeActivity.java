package com.jmg.citaprevia.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmg.citaprevia.ProviderType;
import com.jmg.citaprevia.R;
import com.jmg.citaprevia.adapter.ItemGridAdapter;
import com.jmg.citaprevia.model.Appointment;
import com.jmg.citaprevia.model.HomeScreenItem;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomeActivity extends AppCompatActivity {

    private GridView gridView;
    private ItemGridAdapter itemGridAdapter;
    private String provider;
    private String email;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd-MM");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private TextView homeTextView;

    private List<HomeScreenItem> homeScreenItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        saveSession();
        loadItems();
        loadLayout();
        addViewClickListener();
        addViewAdapter();
        loadLatestEvent();
    }
    private void saveSession(){
        Bundle bundle = getIntent().getExtras();
        SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        if(bundle != null){
            email = bundle.getString(getString(R.string.email_lower_case));
            provider = bundle.getString(getString(R.string.provider_lower_case));
            if (email != null && provider != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.email_lower_case), email);
                editor.putString(getString(R.string.provider_lower_case), provider);
                editor.apply();
                setup(email, provider);
            }
        }else{
            email = preferences.getString(getString(R.string.email_lower_case), null);
            provider = preferences.getString(getString(R.string.provider_lower_case), null);
        }

    }
    private void loadLayout(){
        this.gridView = findViewById(R.id.shortcuts);
    }

    private void loadItems(){
        this.homeScreenItems = new ArrayList<>();
        HomeScreenItem myAppointments = new HomeScreenItem(getString(R.string.my_appointments),R.drawable.user_white);
        HomeScreenItem exploreCalendars = new HomeScreenItem(getString(R.string.calendars),R.drawable.calendar);
        HomeScreenItem settingsItem = new HomeScreenItem(getString(R.string.settings),R.drawable.settings);
        this.homeScreenItems.add(myAppointments);
        this.homeScreenItems.add(exploreCalendars);
        this.homeScreenItems.add(settingsItem);
        this.homeTextView = findViewById(R.id.homeTextView);

    }
    private void addViewClickListener(){
        this.gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(HomeActivity.this,AppActivity.class);
            switch(position){
                case 0:
                    intent.putExtra(getString(R.string.fragment_to_load), getString(R.string.my_events));
                    break;
                case 1:
                    intent.putExtra(getString(R.string.fragment_to_load), getString(R.string.explore_calendars));
                    break;
                case 2:
                    intent.putExtra(getString(R.string.fragment_to_load), getString(R.string.settings_view));
                    break;
            }
            if(intent.hasExtra(getString(R.string.fragment_to_load))){
                startActivity(intent);
            }
        });
    }

    private void closeSession(){
        try{
            SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        }catch (Exception e ){
            e.printStackTrace();
        }
            if(provider.equals(ProviderType.FACEBOOK.name())){
                LoginManager.getInstance().logOut();
            }

            FirebaseAuth.getInstance().signOut();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
    }

    private void addViewAdapter(){
        this.itemGridAdapter = new ItemGridAdapter(HomeActivity.this, R.layout.grid_item, homeScreenItems);
        this.gridView.setAdapter(itemGridAdapter);
    }

    private void setup(String email, String providerType){
        setTitle("Home");
    }

    private void loadLatestEvent(){
        List<Appointment> appointments = new ArrayList<>();
        firestore.collection(getString(R.string.firestore_collection_appointments))
                .whereEqualTo(getString(R.string.firestore_field_host), email)
                .get()
                .addOnSuccessListener(x ->{
                    x.getDocuments().forEach( doc -> {
                        appointments.add(appointmentFromDocument(doc));
                    });
                });
        firestore.collection(getString(R.string.firestore_collection_attendants))
                .whereEqualTo(getString(R.string.firestore_field_attendant_id), email)
                .get()
                .addOnSuccessListener(x -> {
                    List<Object> appointmentIds = x.getDocuments().stream()
                            .map(doc -> doc.get(getString(R.string.firestore_field_appointment_id)))
                            .collect(Collectors.toList());
                    if (!appointmentIds.isEmpty()) {
                        firestore.collection(getString(R.string.firestore_collection_appointments))
                                .whereIn(getString(R.string.firestore_field_id), appointmentIds)
                                .get()
                                .addOnSuccessListener(y -> {
                                    y.getDocuments().forEach(doc -> appointments.add(appointmentFromDocument(doc)));
                                    Optional<Appointment> nextAppointment = appointments.stream()
                                            .filter(app -> app.getStartTime().isAfter(LocalDateTime.now()))
                                            .sorted(Comparator.comparing(Appointment::getStartTime))
                                            .findFirst();
                                    if(nextAppointment.isPresent()){
                                        Appointment appointment = nextAppointment.get();
                                        homeTextView.setText("Próximo evento:" + appointment.getTitle() + "\n El día " + sdf.format(appointment.getStartTime()));
                                    }
                                });
                    }
                });
    }

    private Appointment appointmentFromDocument(DocumentSnapshot document) {
        Date date = null;
        try {
            date = format.parse(document.getString(getString(R.string.firestore_field_date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Appointment(
                UUID.fromString(document.getString(getString(R.string.firestore_field_id))),
                document.getString(getString(R.string.firestore_field_title)),
                document.getString(getString(R.string.firestore_field_description)),
                date,
                document.getLong(getString(R.string.firestore_field_duration)).intValue(),
                document.getString(getString(R.string.firestore_field_host)),
                document.getLong(getString(R.string.firestore_field_hour)).intValue(),
                document.getLong(getString(R.string.firestore_field_minute)).intValue());
    }
}