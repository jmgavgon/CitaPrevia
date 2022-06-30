package com.jmg.citaprevia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmg.citaprevia.R;
import com.jmg.citaprevia.model.HomeScreenItem;

import java.util.List;

public class ItemGridAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<HomeScreenItem> items;

    public ItemGridAdapter(Context context, int layout, List<HomeScreenItem> items){
        this.context = context;
        this.layout = layout;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(this.layout, null);

            holder = new ViewHolder();
            holder.textViewItem = convertView.findViewById(R.id.textViewItem);
            holder.imageViewItem = convertView.findViewById(R.id.imageViewItem);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        HomeScreenItem currentItem = items.get(position);


        holder.textViewItem.setText(currentItem.getText());
        holder.imageViewItem.setImageResource(currentItem.getIcon());

        return convertView;
    }

    static class ViewHolder{
        private TextView textViewItem;
        private ImageView imageViewItem;

    }
}
