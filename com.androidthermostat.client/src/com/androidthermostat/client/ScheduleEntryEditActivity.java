package com.androidthermostat.client;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
	Spinner minTemperatureList;
	Spinner maxTemperatureList;
	LinearLayout singleTemperatureHolder;
	LinearLayout temperatureRangeHolder;
	
	RadioGroup modeRadio;
	RadioGroup periodRadio;
	Button saveButton;
	Button deleteButton;
	Button cancelButton;
	
	int selectedHourIndex=-1;
	int selectedMinuteIndex=-1;
	
	int dayOfWeek;
	int scheduleIndex;
	int entryIndex;
	
	public int newHigh = 75;
	public int newLow = 75;
	public String newMode = "Off";
	
	ArrayList<ScheduleEntry> entries;
	String[] temperatures = new String[] {  "50", "51", "52", "53", "54", "55", "56", "57", "58", "59","60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89" };
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
		minTemperatureList = (Spinner) findViewById(R.id.minTemperatureList);
		maxTemperatureList = (Spinner) findViewById(R.id.maxTemperatureList);
		singleTemperatureHolder = (LinearLayout) findViewById(R.id.singleTemperatureHolder);
		temperatureRangeHolder = (LinearLayout) findViewById(R.id.temperatureRangeHolder);
		modeRadio = (RadioGroup) findViewById(R.id.modeRadio);
		periodRadio = (RadioGroup) findViewById(R.id.periodRadio);
		saveButton = (Button) findViewById(R.id.saveButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, temperatures);
		tempList.setAdapter(adapter);
		minTemperatureList.setAdapter(adapter);
		maxTemperatureList.setAdapter(adapter);
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
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { 
        		newHigh = 50 + pos;
        		newLow = 50 + pos;
        	}
        	public void onNothingSelected(AdapterView<?> parent) {}
        });
		minTemperatureList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { newLow = 50 + pos; }
        	public void onNothingSelected(AdapterView<?> parent) {}
        });
		maxTemperatureList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { newHigh = 50 + pos; }
        	public void onNothingSelected(AdapterView<?> parent) {}
        });
		modeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton b = (RadioButton) findViewById(checkedId);
				newMode = b.getText().toString();
				toggleMode();
			}
		});
		
		
		saveButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {save();}});
		deleteButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {deleteEntry();}});
		cancelButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {cancel();}});
		
		
		populateFields();
		toggleMode();
		
	}
	
	private void populateFields()
	{
		if (entryIndex>-1) 
		{
			ScheduleEntry entry = entries.get(entryIndex);
		
			newHigh = entry.getTargetHigh();
			newLow = entry.getTargetLow();
			newMode = entry.getMode();
		
			selectedHourIndex=entry.getHour() - 1;
			selectedMinuteIndex = entry.getMinute();
		}
		
		if (selectedHourIndex>=11) {
			selectedHourIndex=selectedHourIndex-12;
			periodRadio.check(R.id.periodPM);
		} else {
			periodRadio.check(R.id.periodAM);
		}
		if (selectedHourIndex<0) selectedHourIndex=11;
		
		hourList.setSelection(selectedHourIndex);
		minuteList.setSelection(selectedMinuteIndex);
				
		
		if (newMode.equals("Off")) modeRadio.check(R.id.modeOff);
		else if (newMode.equals("Fan")) modeRadio.check(R.id.modeFan);
		else if (newMode.equals("Heat")) modeRadio.check(R.id.modeHeat);
		else if (newMode.equals("Cool")) modeRadio.check(R.id.modeCool);
		else if (newMode.equals("Auto")) modeRadio.check(R.id.modeAuto);
		
	}
	
	private void toggleMode()
	{
		tempList.setSelection(newHigh - 50);
		minTemperatureList.setSelection(newLow - 50);
		maxTemperatureList.setSelection(newHigh - 50);
		
		if (newMode.equals("Auto"))
		{
			singleTemperatureHolder.setVisibility(View.GONE);
			temperatureRangeHolder.setVisibility(View.VISIBLE);
		} else if (newMode.equals("Fan") || newMode.equals("Off"))
		{
			singleTemperatureHolder.setVisibility(View.GONE);
			temperatureRangeHolder.setVisibility(View.GONE);
		} else {
			singleTemperatureHolder.setVisibility(View.VISIBLE);
			temperatureRangeHolder.setVisibility(View.GONE);
		}
		
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
		if (newLow>newHigh) newLow=newHigh;
		
		ScheduleEntry entry = new ScheduleEntry();
		if (entryIndex>-1) entry = entries.get(entryIndex); entry.setDayOfWeek(dayOfWeek);
		
		int hour = selectedHourIndex + 1;
		if (hour==12) hour = 0;
		if (periodRadio.getCheckedRadioButtonId() == R.id.periodPM) hour += 12;
		
		entry.setHour(hour);
		entry.setMinute(selectedMinuteIndex);
		entry.setTargetHigh(newHigh);
		entry.setTargetLow(newLow);
		RadioButton b = (RadioButton) this.findViewById(modeRadio.getCheckedRadioButtonId());
		entry.setMode(b.getText().toString());
		
		if (entryIndex==-1) Schedules.getCurrent().get(scheduleIndex).getEntries().add(entry);
		finish();
		
		
	}
	
}
