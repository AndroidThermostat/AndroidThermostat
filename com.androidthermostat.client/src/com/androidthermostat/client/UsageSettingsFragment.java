package com.androidthermostat.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.actionbarsherlock.app.SherlockFragment;
import com.androidthermostat.client.data.Settings;

public class UsageSettingsFragment extends SherlockFragment {


	EditText baseUrl;
	EditText cycleCompleteParams;
	EditText insideTempParams;
	EditText outsideTempParams;
	EditText viewStatsParams;
	
	View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.usage_settings, null);
		
		baseUrl = (EditText) root.findViewById(R.id.baseUrl);
		cycleCompleteParams = (EditText) root.findViewById(R.id.cycleCompleteParams);
		insideTempParams = (EditText) root.findViewById(R.id.insideTempParams);
		outsideTempParams = (EditText) root.findViewById(R.id.outsideTempParams);
		viewStatsParams = (EditText) root.findViewById(R.id.viewStatsParams);
		
		Settings s = Settings.getCurrent();
		
		baseUrl.setText( s.getPingOutUrl() );
		cycleCompleteParams.setText( s.getCycleCompleteParams() );
		insideTempParams.setText( s.getInsideTempChangeParams() );
		outsideTempParams.setText( s.getOutsideTempChangeParams() );
		viewStatsParams.setText( s.getViewStatsParams() );
		
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
		
		s.setCycleCompleteParams(cycleCompleteParams.getText().toString());
		s.setInsideTempChangeParams(insideTempParams.getText().toString());
		s.setOutsideTempChangeParams(outsideTempParams.getText().toString());
		s.setViewStatsParams(viewStatsParams.getText().toString());
		
		s.setPingOutUrl(baseUrl.getText().toString());

		s.save();
	}
	
}
