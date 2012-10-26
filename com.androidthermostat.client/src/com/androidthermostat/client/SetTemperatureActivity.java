package com.androidthermostat.client;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;
import com.androidthermostat.client.data.Schedules;
import com.androidthermostat.client.data.Settings;

public class SetTemperatureActivity extends SherlockActivity {

	String[] temperatures = new String[] { "50", "51", "52", "53", "54", "55",
			"56", "57", "58", "59", "60", "61", "62", "63", "64", "65",
			"66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76",
			"77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87",
			"88", "89" };
	String[] modes = new String[] {"Off", "Fan", "Heat", "Cool", "Auto"};

	public int newAwayHigh;
	public int newAwayLow;
	public int newHigh;
	public int newLow;
	public int newSchedule;
	public String newMode;
	public boolean newIsAway;
	Spinner temperatureList;
	Spinner minTemperatureList;
	Spinner maxTemperatureList;
	Spinner minAwayList;
	Spinner maxAwayList;
	Spinner modeList;
	LinearLayout singleTemperatureHolder;
	LinearLayout temperatureRangeHolder;
	LinearLayout homeHolder;
	LinearLayout awayHolder;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.set_temperature);

		//RadioGroup modeRadio = (RadioGroup) findViewById(R.id.modeRadio);
		RadioGroup locationRadio = (RadioGroup) findViewById(R.id.locationRadio);
		temperatureList = (Spinner) findViewById(R.id.temperatureList);
		minTemperatureList = (Spinner) findViewById(R.id.minTemperatureList);
		maxTemperatureList = (Spinner) findViewById(R.id.maxTemperatureList);
		minAwayList = (Spinner) findViewById(R.id.minAwayList);
		maxAwayList = (Spinner) findViewById(R.id.maxAwayList);
		modeList = (Spinner) findViewById(R.id.modeList);
		singleTemperatureHolder = (LinearLayout) findViewById(R.id.singleTemperatureHolder);
		temperatureRangeHolder = (LinearLayout) findViewById(R.id.temperatureRangeHolder);
		homeHolder = (LinearLayout) findViewById(R.id.homeHolder);
		awayHolder = (LinearLayout) findViewById(R.id.awayHolder);
		
		
		Spinner scheduleList = (Spinner) findViewById(R.id.scheduleList);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, temperatures);
		temperatureList.setAdapter(adapter);
		minTemperatureList.setAdapter(adapter);
		maxTemperatureList.setAdapter(adapter);
		minAwayList.setAdapter(adapter);
		maxAwayList.setAdapter(adapter);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, modes);
		modeList.setAdapter(adapter);

		String[] scheduleNames = Schedules.getCurrent().getNames(true);

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, scheduleNames);
		scheduleList.setAdapter(adapter);

		Settings s = Settings.getCurrent();
		newHigh = s.getTargetHigh();
		newLow = s.getTargetLow();
		newSchedule = s.getSchedule();
		newMode = s.getMode();
		newIsAway = s.getIsAway();
		newAwayLow = s.getAwayLow();
		newAwayHigh = s.getAwayHigh();
		

		temperatureList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						if (newMode.equals("Heat")) { newLow = 50 + pos; if (newLow>newHigh) newHigh=newLow; }
						if (newMode.equals("Cool")) { newHigh = 50 + pos; if (newLow>newHigh) newLow=newHigh; }
					}
					public void onNothingSelected(AdapterView<?> parent) {}
				});
		
		minTemperatureList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				newLow = 50 + pos;
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		maxTemperatureList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				newHigh = 50 + pos;
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		minAwayList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				newAwayLow = 50 + pos;
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		maxAwayList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				newAwayHigh = 50 + pos;
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		modeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				newMode = modes[pos];
				toggleMode();
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		scheduleList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						newSchedule = pos - 1;
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
/*
		modeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton b = (RadioButton) findViewById(checkedId);
						newMode = b.getText().toString();
						toggleMode();
					}
				});
*/

		locationRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				newIsAway = checkedId == R.id.locationAway;
				toggleLocation();
			}
		});
		
		scheduleList.setSelection(Settings.getCurrent().getSchedule() + 1);

		if (newIsAway) locationRadio.check(R.id.locationAway); else locationRadio.check(R.id.locationHome); 
		
		String mode = Settings.getCurrent().getMode();
		/*
		if (mode.equals("Off"))
			modeRadio.check(R.id.modeOff);
		else if (mode.equals("Fan"))
			modeRadio.check(R.id.modeFan);
		else if (mode.equals("Heat"))
			modeRadio.check(R.id.modeHeat);
		else if (mode.equals("Cool"))
			modeRadio.check(R.id.modeCool);
		else if (mode.equals("Auto"))
			modeRadio.check(R.id.modeAuto);
		*/
		int modeIdx=0;
		for (int i=0;i<modes.length;i++) if (modes[i].equals(mode)) modeIdx = i;
		modeList.setSelection(modeIdx);
		
		toggleMode();
		toggleLocation();
		
		minAwayList.setSelection(newAwayLow - 50);
		maxAwayList.setSelection(newAwayHigh - 50);
	}
	
	private void toggleMode()
	{
		if (newMode.equals("Cool")) temperatureList.setSelection(newHigh - 50); else temperatureList.setSelection(newLow - 50);
		
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
	
	private void toggleLocation()
	{
		if (newIsAway)
		{
			homeHolder.setVisibility(View.GONE);
			awayHolder.setVisibility(View.VISIBLE);
		} else {
			homeHolder.setVisibility(View.VISIBLE);
			awayHolder.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		new Thread(new Runnable() {
			public void run() {
				if (newLow>newHigh) newLow=newHigh;
				Settings s = Settings.getCurrent();
				s.setTargetHigh(newHigh);
				s.setTargetLow(newLow);
				s.setSchedule(newSchedule);
				s.setMode(newMode);
				s.setAwayHigh(newAwayHigh);
				s.setAwayLow(newAwayLow);
				s.setIsAway(newIsAway);
				s.save();
			}
		}).start();

		super.onPause();
	}

}
