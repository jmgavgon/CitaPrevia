package com.jmg.citaprevia.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jmg.citaprevia.R;
import com.jmg.citaprevia.activity.AppActivity;
import com.jmg.citaprevia.activity.AppointmentViewActivity;
import com.jmg.citaprevia.adapter.ComingEventsAdapter;
import com.jmg.citaprevia.model.Appointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ComingEventsFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView listView;
    private List<Appointment> comingEvents;
    private ComingEventsAdapter comingEventsAdapter;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coming_events, container, false);
        initializeUI(view);
        return view;
    }
    private void initializeUI(View view){
        setListElementsClickListener(view);
        loadFAB();
        loadData();
        loadAdapter();
    }

    private void loadAdapter(){
        this.comingEventsAdapter = new ComingEventsAdapter(getContext(),comingEvents, R.layout.list_item_appointment);
        this.listView.setAdapter(comingEventsAdapter);
    }

    private void setListElementsClickListener(View view) {
        this.listView = view.findViewById(R.id.comingAppointmentsListView);
        this.listView.setOnItemClickListener((parent, view1, position, id) -> {
            AppActivity activity = (AppActivity) getActivity();
            Appointment appointment =  comingEvents.get(position);
            Intent intent = new Intent(getActivity(), AppointmentViewActivity.class);
            intent.putExtra(getString(R.string.appointment),appointment);
            startActivity(intent);
        });
    }

    public void loadData(){
        this.comingEvents = new ArrayList<>();
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = preferences.getString(getString(R.string.email_lower_case), null);
        firestore.collection(getString(R.string.firestore_collection_appointments))
                .whereEqualTo(getString(R.string.firestore_field_host), email)
                .get()
                .addOnSuccessListener(x -> {
                    x.getDocuments().forEach(doc ->
                            comingEvents.add(appointmentFromDocument(doc)));
                });
        firestore.collection(getString(R.string.firestore_collection_attendants))
                .whereEqualTo(getString(R.string.firestore_field_attendant_id), email)
                .get()
                .addOnSuccessListener(x -> {
                    List<Object> attendants = x.getDocuments().stream()
                            .map(doc -> doc.get(getString(R.string.firestore_field_appointment_id)))
                            .collect(Collectors.toList());
                    if(!attendants.isEmpty()){
                        firestore.collection(getString(R.string.firestore_collection_appointments))
                                .whereIn(getString(R.string.firestore_field_id), attendants)
                                .get()
                                .addOnSuccessListener(y -> {
                                    y.getDocuments().forEach(doc ->
                                            comingEvents.add(appointmentFromDocument(doc)));
                                    comingEvents.sort(Comparator.comparing(Appointment::getStartTime).reversed());
                                    comingEventsAdapter.notifyDataSetChanged();
                                });
                    }else{
                        comingEvents.sort(Comparator.comparing(Appointment::getStartTime).reversed());
                        comingEventsAdapter.notifyDataSetChanged();
                    }


                });
    }

    private Appointment appointmentFromDocument(DocumentSnapshot document){
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

    private void loadFAB(){
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }


}
