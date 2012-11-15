package com.androidthermostat.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.androidthermostat.client.data.Schedule;
import com.androidthermostat.client.data.Schedules;
import com.androidthermostat.client.data.Settings;

public class SchedulesActivity extends SherlockActivity {
	
	ListView scheduleList;
	Button scheduleAddButton;
	ScheduleListAdapter scheduleListAdapter;
	private static final int ACTIVITY_EDITSCHEDULE=110;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.schedules);
		
		
		scheduleList = (ListView) findViewById(R.id.scheduleList);
		scheduleAddButton = (Button) findViewById(R.id.scheduleAddButton);
		scheduleAddButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {addSchedule();}});

		

		
		scheduleList.setOnItemClickListener(new ListView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	        	selectSchedule(position, false);
	        }
	    });
		
		
		scheduleListAdapter = new ScheduleListAdapter(this);
		scheduleList.setAdapter(scheduleListAdapter);
		
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  scheduleListAdapter.notifyDataSetChanged();
	}

	private void addSchedule()
	{
		Schedule s = new Schedule();
		
		int i=1;
		boolean match = true;
		while (match) {
			String name = "New Schedule #" + String.valueOf(i);
			s.setName(name);
			if (Schedules.getCurrent().getByName(name)==null) match=false;
			i++;
		}
		Schedules.getCurrent().add(s);
		//scheduleListAdapter.notifyDataSetChanged();
		selectSchedule(Schedules.getCurrent().size()-1, true);
	}
	

	@Override
	public void onPause()
	{
		new Thread(new Runnable() {
		    public void run() {
		    	saveData();
		    }
		  }).start();
	    super.onPause();
	}
	
	//onPause
	private void saveData()
	{
		Schedules.getCurrent().save();
		Settings.getCurrent().save();
	}
	
	private void selectSchedule(int scheduleIndex, boolean showSettings)
    {
    	Bundle bundle = new Bundle();
    	//bundle.putString("SCHEDULE_NAME", scheduleName);
    	bundle.putInt("SCHEDULE_INDEX", scheduleIndex);
    	bundle.putBoolean("SHOW_SETTINGS", showSettings);
    	Intent intent = new Intent(this, ScheduleActivity.class);
    	//Intent intent = new Intent(this, ScheduleTabsFragment.class);
    	intent.putExtras(bundle);
		startActivityForResult(intent, ACTIVITY_EDITSCHEDULE);
    }
	
}
