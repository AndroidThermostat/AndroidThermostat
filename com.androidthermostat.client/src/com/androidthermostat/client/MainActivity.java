package com.androidthermostat.client;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
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

import com.androidthermostat.client.controls.HomeSchedule;
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
	//TextView debugText;
	TextView currentTime;
	ImageView weatherImage;
	ImageView settingsButton;
	ImageView serverButton;
	LinearLayout screenLayout;
	HomeSchedule homeSchedule;
	
	Handler refreshHandler;
	
	Timer conditionsTimer;
	Timer settingsTimer;
	
	String previousConditionsJson = "";
	String previousSettingsJson = "";
	//String previousDebugText = "";
	String previousDisplayTime = "";
	private static final int ACTIVITY_SETTEMP=100;
	private static final int ACTIVITY_SCHEDULE=101;
	private static final int ACTIVITY_SETTINGS=102;
	private static final int ACTIVITY_SELECT_SERVER=103;
	
	SimpleDateFormat formatter = new SimpleDateFormat("h:mma");
	
	//<TextView android:id="@+id/debugText" android:layout_width="wrap_content" android:layout_height="wrap_content" android:textStyle="bold" android:textSize="14sp"  android:gravity="center" android:text="debug" />
	//<solid android:color="@android:color/transparent" />
    
	@Override
	protected void onResume() {
		super.onResume();
		updateScreen(true);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        setContentView(R.layout.main);
        
        
        
        new SimpleEula(this).show();
        
        currentTime = (TextView) findViewById(R.id.currentTime);
        insideTempText  = (TextView) findViewById(R.id.insideTempText);
        outsideTempText  = (TextView) findViewById(R.id.outsideTempText);
        targetTempText  = (TextView) findViewById(R.id.targetTempText);
        //debugText  = (TextView) findViewById(R.id.debugText);
        weatherImage = (ImageView) findViewById(R.id.weatherImage);
        settingsButton = (ImageView) findViewById(R.id.settingsButton);
        serverButton = (ImageView) findViewById(R.id.serverButton);
        screenLayout = (LinearLayout) findViewById(R.id.screenLayout);
        homeSchedule = (HomeSchedule) findViewById(R.id.homeSchedule);
        
        //screenLayout.setOnClickListener(new View.OnClickListener() {public void onClick(View view) { openOptionsMenu(); }});
        weatherImage.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {showWeatherDetails();}});
        outsideTempText.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {showWeatherDetails();}});
        insideTempText.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {setTemperature();}});
        targetTempText.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {setTemperature();}});
        settingsButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {showSettings();}});
        serverButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {showServer();}});
        homeSchedule.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {showSchedule();}});
        
        
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
	
	private void showServer()
    {
		Intent i = new Intent(this, ServerSelectActivity.class);
		startActivityForResult(i, ACTIVITY_SELECT_SERVER);
    }
	
	private void showSettings()
    {
    	Intent i = new Intent(this, SettingsActivity.class);
		startActivityForResult(i, ACTIVITY_SETTINGS);
    }
	
	private void showSchedule()
    {
		final Activity activity = this;
		new Thread(new Runnable() {
	        public void run() {
	        	Schedules.load();
	        	Intent i = new Intent(activity, SchedulesActivity.class);
	    		startActivityForResult(i, ACTIVITY_SCHEDULE);
	        }
	      }).start();
		
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
		//debugText.setText(Utils.debugText);
	}

	public void updateScreen(boolean forceRefresh)
	{
		
		Conditions conditions = Conditions.getCurrent();
		Settings settings = Settings.getCurrent();

		//Updating these fields every second creates unnecessary processor usage.  Only update the fields
		//if the values have changed.
		
		if (forceRefresh || !conditions.getJson().equals(previousConditionsJson) || !settings.getJson().equals(previousSettingsJson))
		{
		
			//insideTempText.setText( String.valueOf(conditions.getInsideTemperature()) + "° F" );
			//outsideTempText.setText( String.valueOf(conditions.getOutsideTemperature()) + "° F" );
			insideTempText.setText( conditions.getDisplayInsideTemperature() );
			outsideTempText.setText( conditions.getDisplayOutsideTemperature() );
			
			targetTempText.setText(settings.getSummary());
			if (conditions.getWeatherImage()!=null) weatherImage.setImageBitmap(conditions.getWeatherImage());
			
			previousConditionsJson = conditions.getJson();
			previousSettingsJson = settings.getJson();
			//previousDebugText = Utils.debugText;
			homeSchedule.refresh();
			
			if (conditions.getState().equals("Cool"))
			{
				screenLayout.setBackgroundResource(R.drawable.background_blue);
			} else if (conditions.getState().equals("Heat"))
			{
				screenLayout.setBackgroundResource(R.drawable.background_red);
			} else
			{
				screenLayout.setBackgroundResource(R.drawable.background_black);
			}

			
		}
		
		
		String displayTime = formatter.format(new Date()).toLowerCase().replace("m", "");
		if (!displayTime.equals(previousDisplayTime))
		{
			currentTime.setText(displayTime);
			previousDisplayTime=displayTime;
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
			   updateScreen(false);
		   } catch (Exception e) {}
		   refreshHandler.postDelayed(this, 1000);
	    }
	};
	

	
}
