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
import com.androidthermostat.client.controls.HelpControl;
import com.androidthermostat.client.data.Settings;

public class FurnaceSettingsFragment extends SherlockFragment {


	EditText minCoolInterval;
	EditText minHeatInterval;
	EditText temperatureCalibration;
	EditText temperatureCalibrationRunning;
	EditText calibrationSeconds;
	RadioGroup swingRadio;
	RadioGroup hardwareRadio;
	RadioGroup fanOnCoolRadio;
	
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
		hardwareRadio = (RadioGroup) root.findViewById(R.id.hardwareRadio);
		fanOnCoolRadio = (RadioGroup) root.findViewById(R.id.fanOnCoolRadio);
		temperatureCalibration = (EditText) root.findViewById(R.id.temperatureCalibration);
		temperatureCalibrationRunning = (EditText) root.findViewById(R.id.temperatureCalibrationRunning);
		calibrationSeconds = (EditText) root.findViewById(R.id.calibrationSeconds);
		
		
		
		Settings s = Settings.getCurrent();
		minCoolInterval.setText( String.valueOf(s.getMinCoolInterval()) );
		minHeatInterval.setText( String.valueOf(s.getMinHeatInterval()) );
		temperatureCalibration.setText( String.valueOf(s.getTemperatureCalibration() ) );
		temperatureCalibrationRunning.setText( String.valueOf(s.getTemperatureCalibrationRunning() ) );
		calibrationSeconds.setText( String.valueOf(s.getCalibrationSeconds() ) );
		if (s.getSwing()==1) swingRadio.check(R.id.swing1);
		else if (s.getSwing()==2) swingRadio.check(R.id.swing2);
		else if (s.getSwing()==3) swingRadio.check(R.id.swing3);
		
		if (s.getHardwareRevision().equals("B")) hardwareRadio.check(R.id.hardware2); else hardwareRadio.check(R.id.hardware1);
		if (s.getFanOnCool()==true) fanOnCoolRadio.check(R.id.fanOnCoolYes); else fanOnCoolRadio.check(R.id.fanOnCoolNo);
		
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
		s.setTemperatureCalibration( Double.parseDouble(temperatureCalibration.getText().toString()) );
		s.setTemperatureCalibrationRunning( Double.parseDouble(temperatureCalibrationRunning.getText().toString()) );
		s.setCalibrationSeconds( Integer.parseInt(calibrationSeconds.getText().toString()) );
		RadioButton b = (RadioButton) root.findViewById(swingRadio.getCheckedRadioButtonId());
		try{
			s.setSwing( Double.parseDouble(b.getText().toString()) );
		} catch (Exception ex)
		{
			s.setSwing( 1 );
		}
		
		b = (RadioButton) root.findViewById(hardwareRadio.getCheckedRadioButtonId());
		try{
			s.setHardwareRevision( b.getText().toString() );
		} catch (Exception ex)
		{
			s.setHardwareRevision( "A" );
		}
		
		b = (RadioButton) root.findViewById(fanOnCoolRadio.getCheckedRadioButtonId());
		try{
			if (b.getText().equals("Yes")) s.setFanOnCool(true); else s.setFanOnCool(true);
		} catch (Exception ex)
		{
			s.setFanOnCool(true);
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
