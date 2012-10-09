package com.androidthermostat.client;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidthermostat.client.data.ScheduleEntry;
import com.androidthermostat.client.data.Schedules;

public class ScheduleDayFragment extends SherlockFragment {
	
	private String[] daysOfWeek = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
	
	private int dayOfWeek;
	private int scheduleIndex;
	
	Button addButton;
	Button copyButton;
	TextView dayOfWeekText;
	ArrayList<ScheduleEntry> entries;
	ListView entryList;
	ScheduleEntryAdapter scheduleEntryAdapter;
	
	
	public ScheduleDayFragment()
	{
		
	}
	
	public ScheduleDayFragment(int scheduleIndex, int dayOfWeek)
	{
		this.scheduleIndex = scheduleIndex;
		this.dayOfWeek = dayOfWeek;
		entries = Schedules.getCurrent().get(scheduleIndex).getEntriesByDayOfWeek(dayOfWeek);
	}


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//setTheme(R.style.Theme_Sherlock); //Used for theme switching in samples
		View root = inflater.inflate(R.layout.schedule_day_tab, null);
		
		entryList = (ListView)root.findViewById(R.id.entryList);
		dayOfWeekText = (TextView)root.findViewById(R.id.dayOfWeekText);
		addButton = (Button) root.findViewById(R.id.addButton);
		copyButton = (Button) root.findViewById(R.id.copyButton);
		
		addButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {add();}});
		copyButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {copy();}});
		
		dayOfWeekText.setText(daysOfWeek[dayOfWeek-1]);
		entryList.setOnItemClickListener(new ListView.OnItemClickListener() { @Override public void onItemClick(AdapterView<?> a, View v, int position, long id) { editEntry(position); } });

		scheduleEntryAdapter = new ScheduleEntryAdapter(this.getActivity(), scheduleIndex, dayOfWeek);
		entryList.setAdapter(scheduleEntryAdapter);
	
		
		
		return root;
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  scheduleEntryAdapter.update(); 
	}
	
	private void editEntry(int position)
	{
		Bundle bundle = new Bundle();
    	bundle.putInt("DAY_OF_WEEK", dayOfWeek);
    	bundle.putInt("SCHEDULE_INDEX", scheduleIndex);
    	bundle.putInt("ENTRY_INDEX", position);
		Intent i = new Intent(this.getActivity(), ScheduleEntryEditActivity.class);
		i.putExtras(bundle);
		startActivityForResult(i, 112);
	}
	

	private void add()
	{
		Bundle bundle = new Bundle();
    	bundle.putInt("DAY_OF_WEEK", dayOfWeek);
    	bundle.putInt("SCHEDULE_INDEX", scheduleIndex);
    	bundle.putInt("ENTRY_INDEX", -1);
		Intent i = new Intent(this.getActivity(), ScheduleEntryEditActivity.class);
		i.putExtras(bundle);
		startActivityForResult(i, 112);
	}
	
	private void copy()
	{
		Bundle bundle = new Bundle();
    	bundle.putInt("DAY_OF_WEEK", dayOfWeek);
    	bundle.putInt("SCHEDULE_INDEX", scheduleIndex);
		Intent i = new Intent(this.getActivity(), CopyScheduleActivity.class);
		i.putExtras(bundle);
		startActivityForResult(i, 111);
	}
	
	
	
}
