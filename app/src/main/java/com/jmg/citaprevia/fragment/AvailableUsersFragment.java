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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmg.citaprevia.ProviderType;
import com.jmg.citaprevia.R;
import com.jmg.citaprevia.activity.AppActivity;
import com.jmg.citaprevia.activity.AppointmentViewActivity;
import com.jmg.citaprevia.activity.UserViewActivity;
import com.jmg.citaprevia.adapter.AvailableUsersAdapter;
import com.jmg.citaprevia.adapter.ComingEventsAdapter;
import com.jmg.citaprevia.model.Appointment;
import com.jmg.citaprevia.model.User;

import java.util.ArrayList;
import java.util.List;

public class AvailableUsersFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView listView;
    private List<User> availableUsers;
    private AvailableUsersAdapter availableUsersAdapter;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


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
        this.availableUsersAdapter = new AvailableUsersAdapter(getContext(),availableUsers, R.layout.list_item_user);
        this.listView.setAdapter(availableUsersAdapter);
    }

    private void setListElementsClickListener(View view) {
        this.listView = view.findViewById(R.id.comingAppointmentsListView);
        this.listView.setOnItemClickListener((parent, view1, position, id) -> {
            User user =  availableUsers.get(position);
            Fragment fragment = new CalendarFragment();
            Bundle arguments = new Bundle();
            arguments.putSerializable(getString(R.string.user),user);
            fragment.setArguments(arguments);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        });
    }

    public void loadData(){
        this.availableUsers = new ArrayList<>();
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = preferences.getString(getString(R.string.email_lower_case), null);
        firestore.collection(getString(R.string.firestore_collection_users))
                .whereNotEqualTo(getString(R.string.firestore_field_email), email)
                .get()
                .addOnSuccessListener(x -> {
                    availableUsers.clear();
                    x.getDocuments().forEach(doc ->
                            availableUsers.add(new User(
                                    doc.getString(getString(R.string.firestore_field_email)),
                                    doc.getString(getString(R.string.firestore_field_name)),
                                    ProviderType.valueOf(doc.getString(getString(R.string.firestore_field_provider))))
                            )
                    );
                    availableUsersAdapter.notifyDataSetChanged();
                });
    }

    private void loadFAB(){
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }


}
