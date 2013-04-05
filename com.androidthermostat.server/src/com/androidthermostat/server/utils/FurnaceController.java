package com.androidthermostat.server.utils;

import java.util.Calendar;
import java.util.Date;

import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Settings;



public class FurnaceController {
	public boolean coolOn = false;
	public boolean heatOn = false;
	public boolean fanOn = false;
	private Date cycleStartTime;
	private Calendar lastCoolTime;
	private Calendar lastHeatTime;
	private Calendar offTime;
	private Calendar forcedFanTime;
	private boolean forcingFan = false;
	
	private int tempMaxSamples = 30;
	private int tempSamples = 0;
	private int tempIndex = 0;
	double[] temps = new double[tempMaxSamples];
	
	//private ArduinoHelper arduinoHelper;
	//private IOIOHelper ioioHelper;
	
	private static FurnaceController current;
	
	public FurnaceController()
	{
		lastCoolTime = Calendar.getInstance();
		lastCoolTime.add(Calendar.YEAR, -1);
		
		lastHeatTime = Calendar.getInstance();
		lastHeatTime.add(Calendar.YEAR, -1);
		
		offTime = Calendar.getInstance();
		
		forcedFanTime = Calendar.getInstance();
		forcedFanTime.add(Calendar.YEAR, -1);
		
		
		
	}
	
	public static FurnaceController getCurrent()
	{
		if (current==null) {
			current = new FurnaceController();
			//current.arduinoHelper = new ArduinoHelper();
			//current.ioioHelper = new IOIOHelper();
			//Utils.debugText = "Created IOIOHelper";
			
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
	
	private boolean forceFanOn()
	{
		
		Settings s = Settings.getCurrent();
		//Exit if the fan is still on or the fan cycling is disabled
		if (s.getCycleFan())
		{
			if (!fanOn)
			{
				int minutes = (int)(new Date().getTime() - offTime.getTime().getTime()) / 1000 / 60;
				if (minutes>=s.getCycleFanOffMinutes()) {
					Utils.logInfo("Turning on fan per cycle settings", "utils.FurnaceController.forceFanOn");
					forcedFanTime = Calendar.getInstance();
					return true;
				}
			} else {
				int minutes = (int)(new Date().getTime() - forcedFanTime.getTime().getTime()) / 1000 / 60;
				if (minutes>=s.getCycleFanOnMinutes()) {
					Utils.logInfo("Turning off fan per cycle settings", "utils.FurnaceController.forceFanOn");
					return false;
				} else return true;
			}
		}
		return false;
	}

	public void setMode(String mode)
	{
		if (!mode.equals(Conditions.getCurrent().getState())) Utils.logInfo("Setting mode to " + mode, "utils.FurnaceController.setMode");

		
		if (mode.equals("Off")) {
			
			if (forceFanOn())
			{
				forcingFan = true;
				toggleFan(true);
				int minutes = (int)(new Date().getTime() - forcedFanTime.getTime().getTime()) / 1000 / 60;
				int remaining = Settings.getCurrent().getCycleFanOnMinutes() - minutes;
				String message = "Cycling fan - " + String.valueOf(remaining) + " minute(s) remaining.";
				Conditions.getCurrent().setMessage(message);
			} else {
				forcingFan = false;
				//Utils.debugText = "Setting Mode to Off";
				Conditions.getCurrent().setMessage("Off");
				if (fanOn) toggleFan(false);
				if (heatOn) toggleHeat(false);
				if (coolOn) toggleCool(false);
			}
		} else if (mode.equals("Heat")) {
			int minutes = (int)(new Date().getTime() - lastHeatTime.getTime().getTime()) / 1000 / 60;
			if (minutes > Settings.getCurrent().getMinHeatInterval())
			{
				//Utils.debugText = "Setting Mode to Heat";
				Conditions.getCurrent().setMessage("Heating");
				forcingFan = false;
				//if (fanOn) toggleFan(true);
				if (!heatOn) toggleHeat(true);
				if (coolOn) toggleCool(false);
			} else {
				int remaining = Settings.getCurrent().getMinHeatInterval() - minutes;
				String message = "Waiting to heat - " + String.valueOf(remaining) + " minute(s) remaining.";
				Utils.logInfo(message, "utils.FurnaceController.setMode");
				Conditions.getCurrent().setMessage(message);
			}
			
		} else if (mode.equals("Cool")) {
			int minutes = (int)(new Date().getTime() - lastCoolTime.getTime().getTime()) / 1000 / 60;
			if (minutes > Settings.getCurrent().getMinCoolInterval())
			{
				//Utils.debugText = "Setting Mode to Cool";
				Conditions.getCurrent().setMessage("Cooling");
				forcingFan = false;
				if (!fanOn) toggleFan(true);
				if (heatOn) toggleHeat(false);
				if (!coolOn) toggleCool(true);
			} else {
				int remaining = Settings.getCurrent().getMinCoolInterval() - minutes;
				String message = "Waiting to cool - " + String.valueOf(remaining) + " minute(s) remaining.";
				Utils.logInfo(message, "utils.FurnaceController.setMode");
				Conditions.getCurrent().setMessage(message);
			}
		} else if (mode.equals("Fan")) {
			//Utils.debugText = "Setting Mode to Fan";
			Conditions.getCurrent().setMessage("Running fan");
			forcingFan = false;
			if (!fanOn) toggleFan(true);
			if (heatOn) toggleHeat(false);
			if (coolOn) toggleCool(false);
		}
		cycleStartTime = new Date();
	}
	
	private void toggleFan(boolean on)
	{
		//arduinoHelper.toggleFan(on);
		if (!on && fanOn) this.offTime = Calendar.getInstance();
		IOIOHelper.getCurrent().toggleFan(on);
		this.fanOn=on;
	}
	
	public void toggleHeat(boolean on)
	{
		//arduinoHelper.toggleHeat(on);
		if (!on && heatOn) this.lastHeatTime = Calendar.getInstance();
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
		double tempSample = IOIOHelper.getCurrent().getTemperature();
		if (tempSample!=-99)
		{
//			Utils.logInfo(String.valueOf(tempSample), "getTemperature");
			temps[tempIndex] = tempSample;
			if (tempSamples<tempMaxSamples) tempSamples++;
			tempIndex ++;
			if (tempIndex>=tempMaxSamples) tempIndex = 0;
		}
		
		double result = 0;
		if (tempSamples>0)
		{
			double totalTemp = 0;
			for (int i=0;i<tempSamples;i++) { totalTemp+=temps[i]; }
			result = (double)Math.round(totalTemp / (double)tempSamples * 10) / 10;
		}
		return result;
		
		//return IOIOHelper.getCurrent().getTemperature();
	}

	public void init()
	{
		//IOIOHelper.getCurrent().setup();
	}
	

}
