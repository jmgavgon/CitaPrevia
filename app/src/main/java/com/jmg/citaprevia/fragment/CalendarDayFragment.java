package com.jmg.citaprevia.fragment;


import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmg.citaprevia.R;
import com.jmg.citaprevia.activity.HomeActivity;
import com.jmg.citaprevia.activity.NewAppointmentActivity;
import com.jmg.citaprevia.adapter.HourAdapter;
import com.jmg.citaprevia.adapter.ObjectAdapter;
import com.jmg.citaprevia.model.Appointment;
import com.jmg.citaprevia.model.HourEvent;
import com.jmg.citaprevia.model.Slot;
import com.jmg.citaprevia.model.User;
import com.jmg.citaprevia.util.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CalendarDayFragment extends Fragment {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    FloatingActionButton fab;
    private ListView listView;
    private TextView dayTextView;
    private TextView monthTextView;
    public static List<Object> objects = new ArrayList<>();
    private Date date;
    public static ObjectAdapter objectAdapter;
    private LocalDate selectedDate;
    private User host;
    HourAdapter hourAdapter;
    private List<HourEvent> hourEvents;
    private List<Slot> slots;
    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public CalendarDayFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_day, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedDate = Instant.ofEpochMilli(bundle.getLong(getString(R.string.date_param)))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            host = (User) bundle.get(getString(R.string.user));
        }
        initializeUI(view);
        return view;
    }

    private void initializeUI(View view) {
        loadFAB();
        loadWidgets(view);
        addViewAdapter();
        setDayView();
        setListElementsClickListener();
    }

    private void setListElementsClickListener(){
        this.listView.setOnItemClickListener((parent, view, position, id) ->{
            HourEvent hourEvent = hourEvents.get(position);
            if(hourEvent.isBlocked()){
                showAlert(getString(R.string.err_blocked_slot));
            }else if (hourEvent.getAppointment() != null){
                showAlert(getString(R.string.err_existing_appointmenr));
            }else{
                Intent newAppointmentIntent = new Intent(getActivity(), NewAppointmentActivity.class);
                newAppointmentIntent.putExtra(getString(R.string.date_param), selectedDate);
                newAppointmentIntent.putExtra(getString(R.string.hour_event), hourEvent.getTime());
                newAppointmentIntent.putExtra(getString(R.string.host), host);
                newAppointmentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newAppointmentIntent);
                getActivity().finish();
            }
        });
    }
    private void showAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.error));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.accept), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setDayView() {
        monthTextView.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayTextView.setText(dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
getSlots();
    }

    private void getSlots(){
        slots = new ArrayList<>();
        firestore.collection(getString(R.string.firestore_collection_slots))
                .whereEqualTo(getString(R.string.firestore_field_host), host.getEmail())
                .get()
                .addOnSuccessListener(x -> {
                    x.getDocuments().forEach(doc ->
                            slots.add(new Slot(DayOfWeek.valueOf(doc.getString(getString(R.string.firestore_field_day))),
                                    LocalTime.parse(doc.getString(getString(R.string.firestore_field_start_time))),
                                    LocalTime.parse(doc.getString(getString(R.string.firestore_field_end_time))))));
                    hourEvents = hourEventList();
                    hourAdapter = new HourAdapter(getApplicationContext(), hourEvents);
                    listView.setAdapter(hourAdapter);
                });
    }

    private boolean blockedSlot(LocalTime time){
        boolean res = true;
        for(Slot s : slots){
            if(selectedDate.getDayOfWeek().equals(s.getDayOfWeek())){
                if((s.getStartTime().equals(time) || s.getStartTime().isBefore(time)) && s.getEndTime().isAfter(time)){
                    res = false;
                }
            }
        }
        return res;
    }

    private List<HourEvent> hourEventList() {
        List<HourEvent> res = new ArrayList<>();
        LocalTime time = LocalTime.of(0, 0);
        for(int hour = 0; hour < 48; hour++)
        {
            boolean shouldBlock = blockedSlot(time);
            HourEvent hourEvent = new HourEvent(time, null, shouldBlock);
            res.add(hourEvent);
            time = time.plusMinutes(30);
        }
        List<Appointment> appointments = new ArrayList<>();


        firestore.collection(getString(R.string.firestore_collection_appointments))
                .whereEqualTo(getString(R.string.firestore_field_host), host.getEmail())
                .whereEqualTo(getString(R.string.firestore_field_date), sdf.format(selectedDate))
                .get()
                .addOnSuccessListener(x -> {
                    x.getDocuments().forEach(doc ->
                            appointments.add(appointmentFromDocument(doc)));
                            appointments.forEach(appointment -> updateHourEvent(res,appointment));
                            hourAdapter.notifyDataSetChanged();
                });
        firestore.collection(getString(R.string.firestore_collection_attendants))
                .whereEqualTo(getString(R.string.firestore_field_attendant_id), host.getEmail())
                .get()
                .addOnSuccessListener(x -> {
                    List<Object> appointmentIds = x.getDocuments().stream()
                            .map(doc -> doc.get(getString(R.string.firestore_field_appointment_id)))
                            .collect(Collectors.toList());
                    if (!appointmentIds.isEmpty()) {
                        firestore.collection(getString(R.string.firestore_collection_appointments))
                                .whereIn(getString(R.string.firestore_field_id), appointmentIds)
                                .whereEqualTo(getString(R.string.firestore_field_date), sdf.format(selectedDate))
                                .get()
                                .addOnSuccessListener(y -> {
                                    y.getDocuments().forEach(doc -> appointments.add(appointmentFromDocument(doc)));
                                    appointments.forEach(appointment -> updateHourEvent(res,appointment));
                                    hourAdapter.notifyDataSetChanged();
                                });
                    } else {
                        objectAdapter.notifyDataSetChanged();
                    }
                });
        return res;
    }

    private void updateHourEvent(List<HourEvent> hourEvents, Appointment appointment){
        LocalTime time = appointment.getStartTime().toLocalTime();
        HourEvent event =hourEvents.stream().filter(x -> x.getTime().equals(time)).findFirst().get();
        event.setAppointment(appointment);
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

    private void loadWidgets(View view) {
        listView = view.findViewById(R.id.listViewCalendarDay);
        dayTextView = view.findViewById(R.id.weekDayTextView);
        monthTextView = view.findViewById(R.id.monthTextView);
    }


    private void addViewAdapter() {
        this.objectAdapter = new ObjectAdapter(getContext(), objects, R.layout.day_item);
        this.listView.setAdapter(objectAdapter);
    }

    private void loadFAB() {
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
