package com.androidthermostat.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidthermostat.client.data.Servers;
import com.androidthermostat.client.data.Settings;

public class GeneralSettingsFragment extends SherlockFragment {

	EditText nameText;
	EditText locationText;
	EditText passwordText;
	EditText forecastUrlText;
	View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.general_settings, null);
		
		nameText = (EditText) root.findViewById(R.id.nameText);
		locationText = (EditText) root.findViewById(R.id.locationText);
		passwordText = (EditText) root.findViewById(R.id.passwordText);
		forecastUrlText = (EditText) root.findViewById(R.id.forecastUrlText);
		
		Settings s = Settings.getCurrent();
		
		nameText.setText( s.getName() );
		locationText.setText( s.getLocation() );
		passwordText.setText( String.valueOf(s.getPassword()) );
		forecastUrlText.setText( s.getForecastUrl() );
		
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
		s.setLocation( locationText.getText().toString() );
		s.setForecastUrl ( forecastUrlText.getText().toString());
		s.setPassword(passwordText.getText().toString());
		s.save();
		
		Servers.getCurrent().getSelectedServer().setPassword(s.getPassword());
		
	}
	
}
