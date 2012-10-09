package com.androidthermostat.client;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;
import com.androidthermostat.client.data.ScheduleEntry;
import com.androidthermostat.client.data.Schedules;

public class ScheduleEntryEditActivity extends SherlockActivity{

	Spinner hourList;
	Spinner minuteList;
	Spinner tempList;
	RadioGroup modeRadio;
	RadioGroup periodRadio;
	Button saveButton;
	Button deleteButton;
	Button cancelButton;
	
	int selectedHourIndex=-1;
	int selectedMinuteIndex=-1;
	int selectedTempIndex=-1;
	
	int dayOfWeek;
	int scheduleIndex;
	int entryIndex;
	ArrayList<ScheduleEntry> entries;
	String[] temperatures = new String[] {  "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89" };
	//String[] hours = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" };
	String[] hours = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
	String[] minutes = new String[] {  
			"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
			"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
			"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
			"30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
			"40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
			"50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.schedule_entry_edit);
		
		
		
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			scheduleIndex = extras.getInt("SCHEDULE_INDEX");
			dayOfWeek = extras.getInt("DAY_OF_WEEK");
			entryIndex = extras.getInt("ENTRY_INDEX");
		}
		entries = Schedules.getCurrent().get(scheduleIndex).getEntriesByDayOfWeek(dayOfWeek);
		
		hourList = (Spinner) findViewById(R.id.hourList);
		minuteList = (Spinner) findViewById(R.id.minuteList);
		tempList = (Spinner) findViewById(R.id.tempList);
		modeRadio = (RadioGroup) findViewById(R.id.modeRadio);
		periodRadio = (RadioGroup) findViewById(R.id.periodRadio);
		saveButton = (Button) findViewById(R.id.saveButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, temperatures);
		tempList.setAdapter(adapter);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, hours);
		hourList.setAdapter(adapter);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, minutes);
		minuteList.setAdapter(adapter);
		

		hourList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { selectedHourIndex=pos; }
        	public void onNothingSelected(AdapterView<?> parent) {}
        });
		minuteList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { selectedMinuteIndex=pos; }
        	public void onNothingSelected(AdapterView<?> parent) {}
        });
		tempList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { selectedTempIndex=pos; }
        	public void onNothingSelected(AdapterView<?> parent) {}
        });
		
		saveButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {save();}});
		deleteButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {deleteEntry();}});
		cancelButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {cancel();}});
		
		
		if (entryIndex>-1) populateFields();
		
	}
	
	private void populateFields()
	{
		ScheduleEntry entry = entries.get(entryIndex);
		
		selectedHourIndex=entry.getHour() - 1;
		selectedMinuteIndex = entry.getMinute();
		selectedTempIndex = entry.getTargetHigh() - 60;
		
		if (selectedHourIndex>=11) {
			selectedHourIndex=selectedHourIndex-12;
			periodRadio.check(R.id.periodPM);
		} else {
			periodRadio.check(R.id.periodAM);
		}
		if (selectedHourIndex<0) selectedHourIndex=11;
		
		hourList.setSelection(selectedHourIndex);
		minuteList.setSelection(selectedMinuteIndex);
		tempList.setSelection(selectedTempIndex);
		
		if (entry.getMode().equals("Off")) modeRadio.check(R.id.modeOff);
		else if (entry.getMode().equals("Fan")) modeRadio.check(R.id.modeFan);
		else if (entry.getMode().equals("Heat")) modeRadio.check(R.id.modeHeat);
		else if (entry.getMode().equals("Cool")) modeRadio.check(R.id.modeCool);
		
	}
	
	

	private void deleteEntry()
	{
		Schedules.getCurrent().get(scheduleIndex).deleteEntry(dayOfWeek, entryIndex);
		finish();
	}
	
	private void cancel()
	{
		finish();
	}
	

	private void save()
	{
		ScheduleEntry entry = new ScheduleEntry();
		if (entryIndex>-1) entry = entries.get(entryIndex); entry.setDayOfWeek(dayOfWeek);
		
		int hour = selectedHourIndex + 1;
		if (hour==12) hour = 0;
		if (periodRadio.getCheckedRadioButtonId() == R.id.periodPM) hour += 12;
		
		entry.setHour(hour);
		entry.setMinute(selectedMinuteIndex);
		entry.setTargetHigh(selectedTempIndex + 60);
		entry.setTargetLow(selectedTempIndex + 60);
		RadioButton b = (RadioButton) this.findViewById(modeRadio.getCheckedRadioButtonId());
		entry.setMode(b.getText().toString());
		
		if (entryIndex==-1) Schedules.getCurrent().get(scheduleIndex).getEntries().add(entry);
		finish();
		
		
	}
	
}
