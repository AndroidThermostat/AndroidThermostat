package com.androidthermostat.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Schedules;
import com.androidthermostat.server.data.Settings;
import com.androidthermostat.server.utils.FurnaceController;
import com.androidthermostat.server.utils.MulticastListener;
import com.androidthermostat.server.utils.Utils;
import com.androidthermostat.server.web.WebServer;

public class MainService extends Service {
	private NotificationManager mNM;
	WebServer webServer;
	private static Context context;
	
	public static Context getContext() { return context; }
	
	
	
	
	private int NOTIFICATION = 1;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting.  We put an icon in the status bar.
		showNotification();
		init();
	}
	
	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//code to execute when the service is starting up
		return START_STICKY;
	}
	
	private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Android Thermsotat Server Started";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "Android Thermostat Server", text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
	
	private void init()
	{
		context = getApplicationContext();
		Utils.logInfo("Initializing", "MainService.init");
		
        Settings.load(context);
        Schedules.load(context);
        FurnaceController.getCurrent().init();
        Conditions.getCurrent().init(context);
        webServer = new WebServer();
        webServer.init(context);
        
        
        
        new Thread(new Runnable() {
		    public void run() {
		      MulticastListener.listen(context);
		    }
		  }).start();
     
        Intent i = new Intent(this, IOIOServiceHelper.class);
        startService(i);
	}
	
	
}
