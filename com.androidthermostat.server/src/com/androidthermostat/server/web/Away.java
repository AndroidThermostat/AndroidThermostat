package com.androidthermostat.server.web;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import com.androidthermostat.server.data.Settings;

import android.content.Context;

public class Away {
	
	private static Hashtable devices;

	public static void handleGet(String url, String[] params, HttpResponse response, Context context) throws HttpException, IOException
    {
		String reply="";
		if (Utils.verifyPassword(params)) {
			if (devices==null) devices=new Hashtable();
			
			String deviceName = Utils.getParamValue(params, "name");
			boolean away = Utils.getParamValue(params, "away").equals("1");
			devices.put(deviceName, away);
			
			boolean allAway = true;
			for (Object obj : devices.values())
			{
				boolean value = (Boolean)obj;
				if (!value) allAway = false;
			}
			
			Settings.getCurrent().setIsAway(allAway);
			
			if (away) reply = deviceName + " is away."; else reply = deviceName + " is not away.";
		} else {
			reply="Invalid password";
		}
		
		StringEntity body = new StringEntity(reply);
        body.setContentType("text/text");
        response.setEntity(body); 
		
    }
	
}
