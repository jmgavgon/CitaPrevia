package com.jmg.citaprevia.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.jmg.citaprevia.ProviderType;
import com.jmg.citaprevia.R;
import com.jmg.citaprevia.fragment.AvailableUsersFragment;
import com.jmg.citaprevia.fragment.CalendarFragment;
import com.jmg.citaprevia.fragment.ComingEventsFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AppActivity extends AppCompatActivity {

    private String MY_APPOINTMENTS;
    private String EXPLORE_CALENDARS;
    private String VIEW_CALENDAR;
    private String NEW_APPOINTMENT;

    public List<String> fragmentTitles;

    SharedPreferences sharedPreferences;
    private String userEmail;
    private ProviderType provider;

    AccountHeader drawerHeader;
    Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        sharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString(getString(R.string.email_lower_case), null);
        provider = ProviderType.valueOf(sharedPreferences.getString(getString(R.string.provider_lower_case), null));
        initializeUI();
    }

    private void initializeUI() {
        loadStrings();
        setToolbar();
        if(this.drawer == null){
            setDrawer();
        }
        loadInitialFragment();
    }

    private void setToolbar() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            //getSupportActionBar().setTitle(fragmentTitles.get(fragmentTitles.size() - 1));
            //fragmentTitles.remove(fragmentTitles.size() - 1);
        } else {
            super.onBackPressed();
        }
    }


    private void loadStrings() {
        fragmentTitles = new ArrayList<>();
        MY_APPOINTMENTS = getString(R.string.my_events);
        EXPLORE_CALENDARS = getString(R.string.explore_calendars);
        NEW_APPOINTMENT = getString(R.string.new_appointment);
        VIEW_CALENDAR = getString(R.string.view_calendar);
    }

    private void loadInitialFragment() {
        String intentFragment = getIntent().getExtras().getString(getString(R.string.fragment_to_load));
        Fragment fragment = null;
        if (intentFragment.equals(MY_APPOINTMENTS)) {
            fragment = new ComingEventsFragment();
            drawer.setSelection(2, false);
        } else if (intentFragment.equals(EXPLORE_CALENDARS)) {
            fragment = new AvailableUsersFragment();
            drawer.setSelection(3, false);
        }else if (intentFragment.equals(VIEW_CALENDAR)) {
            fragment = new CalendarFragment();
            drawer.setSelection(3, false);
        }  else if (intentFragment.equals(NEW_APPOINTMENT)) {
            fragment = new CalendarFragment();
            drawer.setSelection(4, false);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        getSupportActionBar().setTitle(intentFragment);
    }


    private void setDrawer() {
        // Create the AccountHeader
        drawerHeader = new AccountHeaderBuilder()
                .withHeaderBackground(R.drawable.drawerheader)
                .withActivity(this)
                .build();
        PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withIdentifier(1).withName(getString(R.string.home)).withIcon(R.drawable.ic_home_drawer);
        PrimaryDrawerItem citaItem = new PrimaryDrawerItem().withIdentifier(2).withName(getString(R.string.my_appointments)).withIcon(R.drawable.user);
        PrimaryDrawerItem calItem = new PrimaryDrawerItem().withIdentifier(3).withName(getString(R.string.calendars)).withIcon(R.drawable.calendar_black);
        PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withIdentifier(4).withName("Ajustes").withIcon(R.drawable.settingsblack);
        PrimaryDrawerItem logoutItem = new PrimaryDrawerItem().withIdentifier(4).withName("Cerrar sesión").withIcon(R.drawable.logout);
        //Creacion del drawer
        drawer = new DrawerBuilder()
                .withAccountHeader(drawerHeader)
                .withActivity(this)
                .addDrawerItems(
                        homeItem,
                        new DividerDrawerItem(),
                        citaItem,
                        calItem,
                        settingsItem,
                        new DividerDrawerItem(),
                        logoutItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem.getIdentifier() != 4){
                            startFragment(drawerItem);
                        }else{
                            closeSession();
                        }

                        return true;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        getSupportActionBar().show();
                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {
                    }
                })
                .build();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer();
                getSupportActionBar().hide();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startFragment(IDrawerItem drawerItem){
        Fragment fragment = null;
        Intent intent = null;
        int identifier = (int) drawerItem.getIdentifier();
        switch (identifier) {
            case 1:
                intent = new Intent(AppActivity.this, HomeActivity.class);
                break;
            case 2:
                fragment = new CalendarFragment();
                break;
            case 3:
                fragment = new CalendarFragment();
                break;
        }

        if (fragment == null) {
            startActivity(intent);
        } else {
            fragmentTitles.add(getSupportActionBar().getTitle().toString());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
            PrimaryDrawerItem item = (PrimaryDrawerItem) drawerItem;
            getSupportActionBar().setTitle(item.getName().toString());
            drawer.closeDrawer();
        }
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
        if(provider.toString().equals(ProviderType.FACEBOOK.name())){
            LoginManager.getInstance().logOut();
        }

        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }



}
