package com.androidthermostat.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidthermostat.client.data.Schedule;
import com.androidthermostat.client.data.Schedules;

public class ScheduleListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    
    
    public ScheduleListAdapter(Context context) { mInflater = LayoutInflater.from(context); }
    public int getCount() { return Schedules.getCurrent().size(); }

    public Object getItem(int position) { return position; }
    public long getItemId(int position) { return position; }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.schedule_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        Schedule s = Schedules.getCurrent().get(position);
        holder.name.setText(s.getName());
        
        return convertView;
    }
    
    
    

    static class ViewHolder {
        TextView name;
    }
}
