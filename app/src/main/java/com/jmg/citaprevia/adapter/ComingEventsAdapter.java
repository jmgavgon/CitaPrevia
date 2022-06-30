package com.jmg.citaprevia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmg.citaprevia.R;
import com.jmg.citaprevia.model.Appointment;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ComingEventsAdapter extends BaseAdapter {

    private Context context;
    private List<Appointment> appointmentList;
    private int layout;
    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");

    public ComingEventsAdapter(Context context, List<Appointment> list, int layout) {
        this.context = context;
        this.appointmentList = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return appointmentList == null? 0 :appointmentList.size();
    }

    @Override
    public Appointment getItem(int position) {
        return appointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout,null);
            vh = new ViewHolder();
            vh.title = convertView.findViewById(R.id.titleListItemAppointmentTextView);
            vh.description = convertView.findViewById(R.id.descriptionListItemAppointmentTextView);
            vh.date = convertView.findViewById(R.id.dateListItemAppointmentTextView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        Appointment appointment = appointmentList.get(position);
        vh.title.setText(appointment.getTitle());
        vh.description.setText(appointment.getDescription());
        vh.date.setText(sdf.format(appointment.getStartTime()));

        return convertView;
    }

    private class ViewHolder{
        TextView title;
        TextView description;
        TextView date;
    }
}
