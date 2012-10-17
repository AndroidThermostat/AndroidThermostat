package com.androidthermostat.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiverHandler extends BroadcastReceiver  {
	@Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
        	
        	//Intent i = new Intent(MainService.class);
            //startService(i);
        	
            Intent serviceIntent = new Intent(context, MainService.class);
            //Intent serviceIntent = new Intent("MainService");
            context.startService(serviceIntent);
        }
    }
}
