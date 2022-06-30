package com.jmg.citaprevia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.jmg.citaprevia.R;

import java.util.List;

public class ObjectAdapter extends BaseAdapter {

    private Context context;
    private List<Object> list;
    private int layout;

    public ObjectAdapter(Context context, List<Object> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            vh.text = convertView.findViewById(R.id.textViewItemDay);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Object obj = list.get(position);
        return convertView;
    }


    private class ViewHolder{
        TextView text;
    }
}
