package com.androidthermostat.client;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidthermostat.client.data.Conditions;
import com.androidthermostat.client.data.Schedules;
import com.androidthermostat.client.data.Servers;
import com.androidthermostat.client.data.Settings;
import com.androidthermostat.utils.SimpleEula;
import com.androidthermostat.utils.Utils;


public class MainActivity extends ActivityBase {

	TextView insideTempText;
	TextView outsideTempText;
	TextView targetTempText;
	TextView debugText;
	ImageView weatherImage;
	LinearLayout screenLayout;
	
	Handler refreshHandler;
	
	Timer conditionsTimer;
	Timer settingsTimer;
	
	String previousConditionsJson = "";
	String previousSettingsJson = "";
	String previousDebugText = "";
	private static final int ACTIVITY_SETTEMP=100;
	
	
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        setContentView(R.layout.main);
        new SimpleEula(this).show();
        
        insideTempText  = (TextView) findViewById(R.id.insideTempText);
        outsideTempText  = (TextView) findViewById(R.id.outsideTempText);
        targetTempText  = (TextView) findViewById(R.id.targetTempText);
        debugText  = (TextView) findViewById(R.id.debugText);
        weatherImage = (ImageView) findViewById(R.id.weatherImage);
        screenLayout = (LinearLayout) findViewById(R.id.screenLayout);
        
        screenLayout.setOnClickListener(new View.OnClickListener() {public void onClick(View view) { openOptionsMenu(); }});
        weatherImage.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {showWeatherDetails();}});
        outsideTempText.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {showWeatherDetails();}});
        insideTempText.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {setTemperature();}});
        targetTempText.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {setTemperature();}});
        
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        
        Servers.load(this);
        
        Utils.debugText = "Loading";
        initScreen();
        
        
        new Thread(new Runnable() {
	        public void run() {
	        	Schedules.load();
	            Settings.load();
	        }
	      }).start();

        
        
        refreshHandler= new Handler();
        refreshHandler.postDelayed(refreshRunnable, 1000);
        
        conditionsTimer = new Timer();
		conditionsTimer.schedule(new ConditionsTimerTask(), 1000, 3000);
        
        settingsTimer = new Timer();
		settingsTimer.schedule(new SettingsTimerTask(), 1200, 5000);

	}
	
	
	public void showWeatherDetails()
	{
		try{
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse( Conditions.getCurrent().getWeatherForecastUrl() ));
			startActivity(i);
		} catch (Exception ex) {}
	}
	
	private void setTemperature()
    {
    	Intent i = new Intent(this, SetTemperatureActivity.class);
		startActivityForResult(i, ACTIVITY_SETTEMP);
    }


	
	
	private void updateSettings()
	{
		Settings.load();
	}
	
	public void initScreen()
	{
		insideTempText.setText( "" );
		outsideTempText.setText( "" );
		targetTempText.setText( "Not Connected to Server" );
		debugText.setText(Utils.debugText);
	}

	public void updateScreen()
	{
		
		Conditions conditions = Conditions.getCurrent();
		Settings settings = Settings.getCurrent();

		//Updating these fields every second creates unnecessary processor usage.  Only update the fields
		//if the values have changed.
		
		if (!conditions.getJson().equals(previousConditionsJson) || !settings.getJson().equals(previousSettingsJson) || !Utils.debugText.equals(previousDebugText))
		{
		
			insideTempText.setText( String.valueOf(conditions.getInsideTemperature()) + "° F" );
			outsideTempText.setText( String.valueOf(conditions.getOutsideTemperature()) + "° F" );
			targetTempText.setText(settings.getSummary());
			if (conditions.getWeatherImage()!=null) weatherImage.setImageBitmap(conditions.getWeatherImage());
			debugText.setText(Utils.debugText);
			
			previousConditionsJson = conditions.getJson();
			previousSettingsJson = settings.getJson();
			previousDebugText = Utils.debugText;
		}
		
	}
	
	class SettingsTimerTask extends TimerTask {
        @Override
        public void run() {
        	updateSettings();
        }
    };
    

	class ConditionsTimerTask extends TimerTask {
        @Override
        public void run() {
        	Conditions.getCurrent().load();
        }
    };
	
	
	private Runnable refreshRunnable = new Runnable() {
	   public void run() {
		   try {
			   updateScreen();
		   } catch (Exception e) {}
		   refreshHandler.postDelayed(this, 1000);
	    }
	};
	

	
}
