package com.jmg.citaprevia.fragment;


import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jmg.citaprevia.R;
import com.jmg.citaprevia.activity.AppActivity;
import com.jmg.citaprevia.adapter.PagerAdapter;

import com.jmg.citaprevia.decorator.EventDecorator;
import com.jmg.citaprevia.model.Appointment;
import com.jmg.citaprevia.model.User;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class CalendarFragment extends Fragment implements OnDateSelectedListener {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    TabLayout tablayout;
    ViewPager viewPager;
    User user;
    FloatingActionButton fab;
    PagerAdapter adapter;
    View view;
    private MaterialCalendarView calendarView;
    private Set<CalendarDay> calendarDays = new HashSet<>();

    public CalendarFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null){
            user = (User) bundle.get(getString(R.string.user));
        }
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initializeUI(view);
        return view;
    }

    private void initializeUI(View view) {
        loadFAB();
        loadData();
        calendarView =  view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
    }

    private void addDayDecorator(View view){
        EventDecorator decorator = new EventDecorator(Color.RED,calendarDays);

        calendarView.addDecorator(decorator);
    }

    private void loadData(){
        firestore.collection(getString(R.string.firestore_collection_appointments))
                .whereEqualTo(getString(R.string.firestore_field_host), user.getEmail())
                .get()
                .addOnSuccessListener(x ->{
                    calendarDays.clear();
                    x.getDocuments().forEach( doc -> {
                        String[] parts = doc.getString(getString(R.string.firestore_field_date)).split("-");
                        calendarDays.add(
                                CalendarDay.from(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) -1, Integer.parseInt(parts[2])));
                    });
                });
        firestore.collection(getString(R.string.firestore_collection_attendants))
                .whereEqualTo(getString(R.string.firestore_field_attendant_id), user.getEmail())
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
                                    y.getDocuments().forEach(doc -> {
                                        String[] parts = doc.getString(getString(R.string.firestore_field_date)).split("-");
                                            calendarDays.add(
                                                    CalendarDay.from(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) -1, Integer.parseInt(parts[2])));
                                    });
                                    addDayDecorator(view);
                                });
                    }else{
                        addDayDecorator(view);

                    }
                });

    }

    private void loadFAB(){
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        CalendarDayFragment fragment = new CalendarDayFragment();
        fragment.setDate(date.getDate());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle arguments = new Bundle();
        arguments.putSerializable(getString(R.string.user),user);
        arguments.putLong(getString(R.string.date_param), date.getDate().getTime());
        fragment.setArguments(arguments);
        transaction.replace(R.id.content_frame, fragment );
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
