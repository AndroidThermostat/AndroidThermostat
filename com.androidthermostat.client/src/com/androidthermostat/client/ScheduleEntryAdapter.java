package com.androidthermostat.client;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidthermostat.client.data.ScheduleEntry;
import com.androidthermostat.client.data.Schedules;

public class ScheduleEntryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;


    private int dayOfWeek = 1;
	private int scheduleIndex;
	ArrayList<ScheduleEntry> entries;
	
    public void update()
    {
    	entries = Schedules.getCurrent().get(this.scheduleIndex).getEntriesByDayOfWeek(this.dayOfWeek);
    	this.notifyDataSetChanged();
    }
    
    public ScheduleEntryAdapter(Context context, int scheduleIndex, int dayOfWeek) 
    { 
    	this.dayOfWeek = dayOfWeek;
    	this.scheduleIndex = scheduleIndex;
    	entries = Schedules.getCurrent().get(this.scheduleIndex).getEntriesByDayOfWeek(this.dayOfWeek);
    	
    	mInflater = LayoutInflater.from(context); 
    }
    public int getCount() { return entries.size(); }

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

        ScheduleEntry e = entries.get(position);
        
        
        holder.name.setText(e.getDisplayName());
        
        return convertView;
    }
    
    
    

    static class ViewHolder {
        TextView name;
    }
}


