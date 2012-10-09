package com.androidthermostat.server;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Schedules;
import com.androidthermostat.server.data.Settings;
import com.androidthermostat.server.utils.FurnaceController;
import com.androidthermostat.server.utils.IOIOHelper;
import com.androidthermostat.server.utils.MulticastListener;
import com.androidthermostat.server.utils.Utils;
import com.androidthermostat.server.utils.WebServer;


public class MainActivity extends Activity {

	WebServer webServer;
	Handler refreshHandler;
	TextView debugText;


	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        debugText  = (TextView) findViewById(R.id.debugText);
        refreshHandler= new Handler();
        refreshHandler.postDelayed(refreshRunnable, 1000);
        
        Settings.load(this);
        Schedules.load(this);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        FurnaceController.getCurrent().init(this);
        
        
        Conditions.getCurrent().init(this);
        webServer = new WebServer();
        webServer.init(this);
        
        new Thread(new Runnable() {
		    public void run() {
		      MulticastListener.listen(MainActivity.this);
		    }
		  }).start();
     
        Intent i = new Intent(this, IOIOServiceHelper.class);
		//startServiceForResult(i, 100);
        startService(i);
        
	}
	
	
	public void updateScreen()
	{
		FurnaceController fc =  FurnaceController.getCurrent();
		String output = "Fan: " + String.valueOf(fc.fanOn) + "\n";
		output += "Cool: " + String.valueOf(fc.coolOn) + "\n";
		output += "Heat: " + String.valueOf(fc.heatOn) + "\n";
		output += "Temp: " + Conditions.getCurrent().insideTempRaw + "\n";
		
		debugText.setText(output + Utils.debugText);
	}
	
	private Runnable refreshRunnable = new Runnable() {
	   public void run() {
		   updateScreen();
		   refreshHandler.postDelayed(this, 1000);
	    }
	};
	
}
