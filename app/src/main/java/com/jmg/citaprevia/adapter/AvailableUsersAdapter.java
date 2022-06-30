package com.jmg.citaprevia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmg.citaprevia.R;
import com.jmg.citaprevia.model.Appointment;
import com.jmg.citaprevia.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AvailableUsersAdapter extends BaseAdapter {

    private Context context;
    private List<User> userList;
    private int layout;
    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");

    public AvailableUsersAdapter(Context context, List<User> list, int layout) {
        this.context = context;
        this.userList = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return userList == null? 0 :userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
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
            vh.name = convertView.findViewById(R.id.nameListItemUserTextView);
            vh.email = convertView.findViewById(R.id.emailListItemUserTextView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        User user = userList.get(position);
        vh.name.setText(user.getName());
        vh.email.setText(user.getEmail());

        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView email;
    }
}
