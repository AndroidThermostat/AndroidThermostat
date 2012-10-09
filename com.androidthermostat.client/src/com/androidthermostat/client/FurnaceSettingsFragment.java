package com.androidthermostat.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.actionbarsherlock.app.SherlockFragment;
import com.androidthermostat.client.data.Settings;

public class FurnaceSettingsFragment extends SherlockFragment {


	EditText minCoolInterval;

	View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.furnace_settings, null);
		minCoolInterval = (EditText) root.findViewById(R.id.minCoolInterval);	
		Settings s = Settings.getCurrent();
		minCoolInterval.setText( String.valueOf(s.getMinCoolInterval()) );
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
		s.setMinCoolInterval(coolInterval);
		s.save();
	}
	
}
