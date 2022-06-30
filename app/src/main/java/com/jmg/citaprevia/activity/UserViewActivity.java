package com.jmg.citaprevia.activity;


import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class UserViewActivity extends AppCompatActivity {

    private int medId;
    private String name;
    private String date;
    private String time;
    private String location;
    private String notes;
    MenuItem itemEditItem;
    MenuItem itemDeleteItem;
    MenuItem itemSaveItem;


    private TextView textViewName;
    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewLocation;
    private TextView textViewNotes;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_appointment_view);
//
//        loadData();
//
//        setToolbar();
//
//        initializeRealm();

    }


/*
    private void loadData(){
        textViewName = findViewById(R.id.textView_medicalAppointmentView_name);
        textViewDate = findViewById(R.id.textView_medicalAppointmentView_date);
        textViewTime = findViewById(R.id.textView_medicalAppointmentView_time);
        textViewLocation = findViewById(R.id.textView_medicalAppointmentView_place);
        textViewNotes = findViewById(R.id.textView_medicalAppointmentView_notes);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            medId = bundle.getInt("id");
            name = bundle.getString("name");
            date = bundle.getString("date");
            time = bundle.getString("time");
            location = bundle.getString("location");
            notes = bundle.getString("notes");
        }

        textViewName.setText(name);
        textViewDate.setText(date);
        textViewTime.setText(time);
        textViewLocation.setText(location);
        textViewNotes.setText(notes);

    }

    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cita médica");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 16908332:
                Intent inten = new Intent(AppointmentViewActivity.this, AppActivity.class);
                inten.putExtra("frgToLoad",getResources().getString(R.string.citas));
                startActivity(inten);
                break;
            case R.id.action_bar_menu_edit_item:
                Intent intent = new Intent(AppointmentViewActivity.this, NewAppoinmentActivity.class);
                intent.putExtra("id",medId);
                startActivity(intent);
                break;
            case R.id.action_bar_menu_delete_item:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AppointmentViewActivity.this);
                builder1.setMessage("¿Borrar la cita?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                realm.beginTransaction();
                                MedicalAppointment medicalAppointment = realm.where(MedicalAppointment.class).equalTo("id",medId).findFirst();
                                medicalAppointment.deleteFromRealm();
                                realm.commitTransaction();
                                dialog.cancel();
                                AppointmentViewActivity.super.onBackPressed();
                            }
                        });

                builder1.setNegativeButton(
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        this.itemEditItem = menu.findItem(R.id.action_bar_menu_edit_item);
        this.itemDeleteItem = menu.findItem(R.id.action_bar_menu_delete_item);
        this.itemSaveItem = menu.findItem(R.id.action_bar_menu_save_item);
        this.itemEditItem.setVisible(true);
        this.itemDeleteItem.setVisible(true);
        this.itemSaveItem.setVisible(false);
        return true;
    }*/
}
