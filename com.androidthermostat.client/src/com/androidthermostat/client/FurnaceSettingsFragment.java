package com.androidthermostat.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidthermostat.client.data.Settings;

public class FurnaceSettingsFragment extends SherlockFragment {


	EditText minCoolInterval;
	EditText minHeatInterval;
	EditText temperatureCalibration;
	RadioGroup swingRadio;
	
	EditText cycleFanOnText;
	EditText cycleFanOffText;
	ToggleButton cycleFanSwitch;;

	View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.furnace_settings, null);
		minCoolInterval = (EditText) root.findViewById(R.id.minCoolInterval);
		minHeatInterval = (EditText) root.findViewById(R.id.minHeatInterval);
		cycleFanOnText = (EditText) root.findViewById(R.id.cycleFanOnText);
		cycleFanOffText = (EditText) root.findViewById(R.id.cycleFanOffText);
		cycleFanSwitch = (ToggleButton) root.findViewById(R.id.cycleFanSwitch);
		swingRadio = (RadioGroup) root.findViewById(R.id.swingRadio);
		temperatureCalibration = (EditText) root.findViewById(R.id.temperatureCalibration);
		
		
		Settings s = Settings.getCurrent();
		minCoolInterval.setText( String.valueOf(s.getMinCoolInterval()) );
		minHeatInterval.setText( String.valueOf(s.getMinHeatInterval()) );
		temperatureCalibration.setText( String.valueOf(s.getTemperatureCalibration() ) );
		if (s.getSwing()==1) swingRadio.check(R.id.swing1);
		else if (s.getSwing()==2) swingRadio.check(R.id.swing2);
		else if (s.getSwing()==3) swingRadio.check(R.id.swing3);
		
		cycleFanSwitch.setChecked(s.getCycleFan());
		cycleFanOnText.setText( String.valueOf(s.getCycleFanOnMinutes()) );
		cycleFanOffText.setText( String.valueOf(s.getCycleFanOffMinutes()) );
		
		return root;
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
	
	
	private void saveData()
	{
		Settings s = Settings.getCurrent();
		int coolInterval = Integer.parseInt(minCoolInterval.getText().toString());
		int heatInterval = Integer.parseInt(minHeatInterval.getText().toString());
		s.setMinCoolInterval(coolInterval);
		s.setMinHeatInterval(heatInterval);
		s.setTemperatureCalibration( Integer.parseInt(temperatureCalibration.getText().toString()) );
		RadioButton b = (RadioButton) root.findViewById(swingRadio.getCheckedRadioButtonId());
		try{
			s.setSwing( Double.parseDouble(b.getText().toString()) );
		} catch (Exception ex)
		{
			s.setSwing( 1 );
		}
		
		int fanOnMin = Integer.parseInt(cycleFanOnText.getText().toString() );
		int fanOffMin = Integer.parseInt(cycleFanOffText.getText().toString() );
		if (fanOnMin < 1) fanOnMin = 1;
		if (fanOffMin < 1) fanOffMin = 1;
		s.setCycleFan(cycleFanSwitch.isChecked());
		s.setCycleFanOnMinutes(fanOnMin);
		s.setCycleFanOffMinutes(fanOffMin);
		s.save();
	}
	
}
