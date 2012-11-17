package com.androidthermostat.server;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Settings;
import com.androidthermostat.server.utils.FurnaceController;
import com.androidthermostat.server.utils.MulticastListener;
import com.androidthermostat.server.utils.SimpleEula;
import com.androidthermostat.server.utils.Utils;


public class MainActivity extends Activity {

	
	Handler refreshHandler;
	TextView debugText;
	TextView heatText;
	TextView coolText;
	TextView fanText;
	TextView nameText;
	TextView ipText;
	TextView webText;
	TextView multicastText;
	TextView tempText;
	Button clientButton;
	Button stopButton;


	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        new SimpleEula(this).show();
        
        debugText  = (TextView) findViewById(R.id.debugText);
        heatText  = (TextView) findViewById(R.id.heatText);
        coolText  = (TextView) findViewById(R.id.coolText);
        fanText  = (TextView) findViewById(R.id.fanText);
        nameText  = (TextView) findViewById(R.id.nameText);
        ipText  = (TextView) findViewById(R.id.ipText);
        webText  = (TextView) findViewById(R.id.webText);
        multicastText  = (TextView) findViewById(R.id.multicastText);
        tempText  = (TextView) findViewById(R.id.tempText);
        clientButton = (Button) findViewById(R.id.clientButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        
        refreshHandler= new Handler();
        refreshHandler.postDelayed(refreshRunnable, 1000);
     
        Intent i = new Intent(this, MainService.class);
        startService(i);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        clientButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {launchClient();}});
        stopButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {stop();}});

        
	}
	

	private void stop()
	{
		Intent i = new Intent(this, MainService.class);
		this.stopService(i);
		finish();
	}
	
	private void launchClient()
	{
		try{
			Intent nextIntent = new Intent(Intent.ACTION_MAIN);
			nextIntent.setComponent(new ComponentName("com.androidthermostat.client","com.androidthermostat.client.MainActivity"));
			startActivity(nextIntent);
		} catch (Exception e)
		{
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.androidthermostat.client"));
	    	startActivityForResult(i, 100);
		}
		
	}
	
	
	public void updateScreen()
	{
		FurnaceController fc =  FurnaceController.getCurrent();

		if (fc.heatOn) heatText.setText("On"); else heatText.setText("Off");
		if (fc.coolOn) coolText.setText("On"); else coolText.setText("Off");
		if (fc.fanOn) fanText.setText("On"); else fanText.setText("Off");
		tempText.setText(String.valueOf(Conditions.getCurrent().insideTemperature));
		
		nameText.setText(Settings.getCurrent().getName());
		
		
		if (MulticastListener.ipAddress.equals(""))
		{
			multicastText.setText("Off");
			webText.setText("Off");
			ipText.setText("");
		} else {
			multicastText.setText("On");
			webText.setText("On");
			ipText.setText(MulticastListener.ipAddress);
		}
		
		debugText.setText(Utils.debugText);
	}
	
	private Runnable refreshRunnable = new Runnable() {
	   public void run() {
		   updateScreen();
		   refreshHandler.postDelayed(this, 3000);
	    }
	};
	
}
