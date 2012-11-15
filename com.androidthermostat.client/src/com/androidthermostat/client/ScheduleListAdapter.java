package com.androidthermostat.client;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidthermostat.client.data.Schedule;
import com.androidthermostat.client.data.Schedules;
import com.androidthermostat.client.data.Settings;

public class ScheduleListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    
    Context context=null;
    
    public ScheduleListAdapter(Context context) { this.context = context;  mInflater = LayoutInflater.from(context); }
    public int getCount() { return Schedules.getCurrent().size(); }

    public Object getItem(int position) { return position; }
    public long getItemId(int position) { return position; }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        int currentScheduleIdx = Settings.getCurrent().getSchedule();
        
        
       
         
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.schedule_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.selectButton = (ImageView) convertView.findViewById(R.id.selectButton);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        Schedule s = Schedules.getCurrent().get(position);
        holder.name.setText(s.getName());
        if (holder.selectButton!=null)
        {
        	if (position==currentScheduleIdx) holder.selectButton.setImageResource(R.drawable.btn_check_on_holo_dark);
        	else holder.selectButton.setImageResource(R.drawable.btn_check_on_disabled_holo_dark);
        }
        
        final int currentPosition = position;
        
        holder.selectButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {selectSchedule(currentPosition);}});
        
        return convertView;
    }
    
    private void selectSchedule(int idx)
    {
    	if (Settings.getCurrent().getSchedule()==idx) Settings.getCurrent().setSchedule(-1);
    	else Settings.getCurrent().setSchedule(idx);
    	this.notifyDataSetChanged();
    }
    

    static class ViewHolder {
        TextView name;
        ImageView selectButton;
    }
}
