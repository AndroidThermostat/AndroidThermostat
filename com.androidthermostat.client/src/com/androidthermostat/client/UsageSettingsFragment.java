package com.androidthermostat.client;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidthermostat.client.data.Settings;

public class UsageSettingsFragment extends SherlockFragment {


	EditText baseUrl;
	EditText cycleCompleteParams;
	EditText insideTempParams;
	EditText outsideTempParams;
	EditText viewStatsParams;
	EditText thermostatKey;
	Button usageButton;
	
	LinearLayout thermostatMonitorHolder;
	LinearLayout otherHolder;
	RadioGroup serviceRadio;
	
	View root;
	String serviceName = "Other";
	private static final int ACTIVITY_VIEW_USAGE=100;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.usage_settings, null);
		
		baseUrl = (EditText) root.findViewById(R.id.baseUrl);
		cycleCompleteParams = (EditText) root.findViewById(R.id.cycleCompleteParams);
		insideTempParams = (EditText) root.findViewById(R.id.insideTempParams);
		outsideTempParams = (EditText) root.findViewById(R.id.outsideTempParams);
		viewStatsParams = (EditText) root.findViewById(R.id.viewStatsParams);
		thermostatMonitorHolder = (LinearLayout) root.findViewById(R.id.thermostatMonitorHolder);
		otherHolder = (LinearLayout) root.findViewById(R.id.otherHolder);
		serviceRadio = (RadioGroup) root.findViewById(R.id.serviceRadio);
		thermostatKey = (EditText) root.findViewById(R.id.thermostatKey);
		usageButton = (Button) root.findViewById(R.id.usageButton);
		
		Settings s = Settings.getCurrent();
		
		baseUrl.setText( s.getPingOutUrl() );
		cycleCompleteParams.setText( s.getCycleCompleteParams() );
		insideTempParams.setText( s.getInsideTempChangeParams() );
		outsideTempParams.setText( s.getOutsideTempChangeParams() );
		viewStatsParams.setText( s.getViewStatsParams() );
		
		if (s.getPingOutUrl()==null || s.getPingOutUrl().equals("") ||  s.getPingOutUrl().contains("http://api.thermostatmonitor.com/v2/?k=") ) {
			String key = s.getPingOutUrl();
			if (key==null) key="";
			key = key.replace("http://api.thermostatmonitor.com/v2/?k=","");
			thermostatKey.setText(key);
			serviceName = "ThermostatMonitor";
			serviceRadio.check(R.id.serviceThermostatMonitor);
		}
		else {
			serviceName = "Other";
			serviceRadio.check(R.id.serviceOther);
		}
		
		
		serviceRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId)
				{
					case R.id.serviceThermostatMonitor:
						serviceName = "ThermostatMonitor";
						break;
					case R.id.serviceOther:
						serviceName = "Other";
						break;
				}
				toggleService();
			}
		});
		
		usageButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) { viewUsage(); }});
		
		
		toggleService();
		return root;
	}
	
	private void viewUsage()
	{
		saveData();
		String pingOutUrl = Settings.getCurrent().getPingOutUrl();
		String viewStatsParams = Settings.getCurrent().getViewStatsParams();
		if (pingOutUrl==null || pingOutUrl.equals("") || viewStatsParams==null || viewStatsParams.equals(""))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
			.setTitle("Tracking service not configured")
			.setMessage("A tracking service has not been configured yet.  Be sure to enter a thermostat key or base url and view stats parameters.")
			.setPositiveButton("OK", null);
			builder.create().show();
		} else {
    		String url = pingOutUrl + viewStatsParams;
    		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    		startActivityForResult(i, ACTIVITY_VIEW_USAGE);
		}
	
	}
	

	private void toggleService()
	{
		if (serviceName.equals("ThermostatMonitor"))
		{
			otherHolder.setVisibility(View.GONE);
			thermostatMonitorHolder.setVisibility(View.VISIBLE);
		} else {
			otherHolder.setVisibility(View.VISIBLE);
			thermostatMonitorHolder.setVisibility(View.GONE);
		}
	}
	
	

	@Override
	public void onPause()
	{
		new Thread(new Runnable() {
		    public void run() {
		    	saveData();
		    	Settings.getCurrent().save();
		    }
		  }).start();
	    
	    super.onPause();
	}
	
	private void saveData()
	{
		Settings s = Settings.getCurrent();
		
		if (serviceName.equals("ThermostatMonitor"))
		{
			String key = thermostatKey.getText().toString();
			
			if (key.equals("")) s.setPingOutUrl("");
			else s.setPingOutUrl("http://api.thermostatmonitor.com/v2/?k=" + key);
			
			s.setCycleCompleteParams("&a=cycle&m=[mode]&d=[duration]");
			s.setInsideTempChangeParams("&a=temp&t=[insideTemp]");
			s.setOutsideTempChangeParams("&a=conditions&t=[outsideTemp]");
			s.setViewStatsParams("&a=stats");
		} else {
			s.setCycleCompleteParams(cycleCompleteParams.getText().toString());
			s.setInsideTempChangeParams(insideTempParams.getText().toString());
			s.setOutsideTempChangeParams(outsideTempParams.getText().toString());
			s.setViewStatsParams(viewStatsParams.getText().toString());
			
			s.setPingOutUrl(baseUrl.getText().toString());
		}
	}
	
}
