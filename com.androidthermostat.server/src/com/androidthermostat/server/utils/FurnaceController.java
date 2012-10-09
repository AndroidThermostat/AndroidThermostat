package com.androidthermostat.server.utils;

import java.util.Calendar;
import java.util.Date;

import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Settings;

import android.app.Activity;

public class FurnaceController {
	public boolean coolOn = false;
	public boolean heatOn = false;
	public boolean fanOn = false;
	private Date cycleStartTime;
	private Calendar lastCoolTime;
	//private ArduinoHelper arduinoHelper;
	//private IOIOHelper ioioHelper;
	
	private static FurnaceController current;
	
	public FurnaceController()
	{
		lastCoolTime = Calendar.getInstance();
		lastCoolTime.add(Calendar.YEAR, -1);
	}
	
	public static FurnaceController getCurrent()
	{
		if (current==null) {
			current = new FurnaceController();
			//current.arduinoHelper = new ArduinoHelper();
			//current.ioioHelper = new IOIOHelper();
			Utils.debugText = "Created IOIOHelper";
			
		}
		return current;
	}
	

	public Date getCycleStartTime() { return cycleStartTime; }

	public String getMode()
	{
		String result="Off";
		if (fanOn) result = "Fan";
		if (heatOn) result = "Heat";
		if (coolOn) result = "Cool";
		return result;
	}
	

	public void setMode(String mode)
	{
		if (mode.equals("Off")) {
			//Utils.debugText = "Setting Mode to Off";
			Conditions.getCurrent().setMessage("Off");
			if (fanOn) toggleFan(false);
			if (heatOn) toggleHeat(false);
			if (coolOn) toggleCool(false);
		} else if (mode.equals("Heat")) {
			//Utils.debugText = "Setting Mode to Heat";
			Conditions.getCurrent().setMessage("Heating");
			if (!fanOn) toggleFan(true);
			if (!heatOn) toggleHeat(true);
			if (coolOn) toggleCool(false);
		} else if (mode.equals("Cool")) {
			int minutes = (int)(new Date().getTime() - lastCoolTime.getTime().getTime()) / 1000 / 60;
			if (minutes > Settings.getCurrent().getMinCoolInterval())
			{
				//Utils.debugText = "Setting Mode to Cool";
				Conditions.getCurrent().setMessage("Cooling");
				if (!fanOn) toggleFan(true);
				if (heatOn) toggleHeat(false);
				if (!coolOn) toggleCool(true);
			} else {
				int remaining = Settings.getCurrent().getMinCoolInterval() - minutes;
				String message = "Waiting to cool - " + String.valueOf(remaining) + " minute(s) remaining.";
				Utils.debugText = message;
				Conditions.getCurrent().setMessage(message);
			}
		} else if (mode.equals("Fan")) {
			//Utils.debugText = "Setting Mode to Fan";
			Conditions.getCurrent().setMessage("Running fan");
			if (!fanOn) toggleFan(true);
			if (heatOn) toggleHeat(false);
			if (coolOn) toggleCool(false);
		}
		cycleStartTime = new Date();
	}
	
	private void toggleFan(boolean on)
	{
		//arduinoHelper.toggleFan(on);
		IOIOHelper.getCurrent().toggleFan(on);
		this.fanOn=on;
	}
	
	public void toggleHeat(boolean on)
	{
		//arduinoHelper.toggleHeat(on);
		IOIOHelper.getCurrent().toggleHeat(on);
		this.heatOn=on;
	}
	
	public void toggleCool(boolean on)
	{
		if (!on && coolOn) this.lastCoolTime = Calendar.getInstance();
		//arduinoHelper.toggleCool(on);
		IOIOHelper.getCurrent().toggleCool(on);
		this.coolOn=on;
	}
	
	public double getTemperature()
	{
		//return arduinoHelper.getTemperature();
		return IOIOHelper.getCurrent().getTemperature();
		//return 0;
	}

	public void init(Activity activity)
	{
		//arduinoHelper.init(activity);
	}
	

}
