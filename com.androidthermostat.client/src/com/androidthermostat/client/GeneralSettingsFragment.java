package com.androidthermostat.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidthermostat.client.data.Settings;

public class GeneralSettingsFragment extends SherlockFragment {

	EditText nameText;
	EditText zipText;
	EditText temperatureCalibration;
	RadioGroup swingRadio;
	View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.general_settings, null);
		
		nameText = (EditText) root.findViewById(R.id.nameText);
		zipText = (EditText) root.findViewById(R.id.zipText);
		swingRadio = (RadioGroup) root.findViewById(R.id.swingRadio);
		temperatureCalibration = (EditText) root.findViewById(R.id.temperatureCalibration);
		
		Settings s = Settings.getCurrent();
		
		nameText.setText( s.getName() );
		zipText.setText( String.valueOf(s.getZipCode()) );
		temperatureCalibration.setText( String.valueOf(s.getTemperatureCalibration() ) );
		
		if (s.getSwing()==0) swingRadio.check(R.id.swing0);
		else if (s.getSwing()==1) swingRadio.check(R.id.swing1);
		else if (s.getSwing()==2) swingRadio.check(R.id.swing2);
		
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
	
	//onPause
	private void saveData()
	{
		Settings s = Settings.getCurrent();
		
		s.setName( nameText.getText().toString() );
		s.setZipCode( Integer.parseInt(zipText.getText().toString()) );
		s.setTemperatureCalibration( Integer.parseInt(temperatureCalibration.getText().toString()) );
		
		RadioButton b = (RadioButton) root.findViewById(swingRadio.getCheckedRadioButtonId());
		try{
			s.setSwing( Double.parseDouble(b.getText().toString()) );
		} catch (Exception ex)
		{
			//for some reason parseDouble throws an exception with 0
			s.setSwing( 0 );
		}
		
		s.save();
		
	}
	
}
