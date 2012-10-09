package com.androidthermostat.client;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;
import com.androidthermostat.client.data.Schedules;
import com.androidthermostat.client.data.Settings;

public class SetTemperatureActivity extends SherlockActivity {

	String[] temperatures = new String[] { "60", "61", "62", "63", "64", "65",
			"66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76",
			"77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87",
			"88", "89" };

	public int newTemp;
	public int newSchedule;
	public String newMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.set_temperature);

		RadioGroup modeRadio = (RadioGroup) findViewById(R.id.modeRadio);
		Spinner temperatureList = (Spinner) findViewById(R.id.temperatureList);
		Spinner scheduleList = (Spinner) findViewById(R.id.scheduleList);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				temperatures);
		temperatureList.setAdapter(adapter);

		String[] scheduleNames = Schedules.getCurrent().getNames(true);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				scheduleNames);
		scheduleList.setAdapter(adapter);

		Settings s = Settings.getCurrent();
		newTemp = s.getTargetHigh();
		newSchedule = s.getSchedule();
		newMode = s.getMode();

		temperatureList
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						newTemp = 60 + pos;
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		scheduleList
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						newSchedule = pos - 1;
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		modeRadio
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton b = (RadioButton) findViewById(checkedId);
						newMode = b.getText().toString();
					}
				});

		temperatureList
				.setSelection(Settings.getCurrent().getTargetHigh() - 60);
		scheduleList.setSelection(Settings.getCurrent().getSchedule() + 1);

		String mode = Settings.getCurrent().getMode();

		if (mode.equals("Off"))
			modeRadio.check(R.id.modeOff);
		else if (mode.equals("Fan"))
			modeRadio.check(R.id.modeFan);
		else if (mode.equals("Heat"))
			modeRadio.check(R.id.modeHeat);
		else if (mode.equals("Cool"))
			modeRadio.check(R.id.modeCool);

	}

	@Override
	public void onPause() {
		new Thread(new Runnable() {
			public void run() {
				Settings s = Settings.getCurrent();
				s.setTargetHigh(newTemp);
				s.setTargetLow(newTemp);
				s.setSchedule(newSchedule);
				s.setMode(newMode);
				s.save();
			}
		}).start();

		super.onPause();
	}

}
