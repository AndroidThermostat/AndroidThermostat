package com.androidthermostat.server.utils;

import java.util.Calendar;
import java.util.Date;

import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Settings;



public class FurnaceController {
	public boolean coolOn = false;
	public boolean heatOn = false;
	public boolean fanOn = false;
	private Calendar cycleStartTime;
	private Calendar lastCoolTime;
	private Calendar lastHeatTime;
	private Calendar offTime;
	private Calendar forcedFanTime;
	private boolean forcingFan = false;
	private String lastMode = "";
	
	private int tempMaxSamples = 60;
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
	

	public Calendar getCycleStartTime() { return cycleStartTime; }

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
		Settings s = Settings.getCurrent();
		//if (!mode.equals(Conditions.getCurrent().getState()) && !forcingFan) Utils.logInfo("Setting mode to " + mode, "utils.FurnaceController.setMode");
		if (!mode.equals(lastMode)) {
			Utils.logInfo("Setting mode to " + mode, "utils.FurnaceController.setMode");
			cycleStartTime = Calendar.getInstance();
		}
		
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
				toggleFan(false);
				toggleHeat(false);
				toggleCool(false);
			}
		} else if (mode.equals("Heat")) {
			int minutes = (int)(new Date().getTime() - lastHeatTime.getTime().getTime()) / 1000 / 60;
			if (minutes > s.getMinHeatInterval())
			{
				//Utils.debugText = "Setting Mode to Heat";
				Conditions.getCurrent().setMessage("Heating");
				forcingFan = false;
				//if (fanOn) toggleFan(true);
				toggleHeat(true);
				toggleCool(false);
			} else {
				int remaining = s.getMinHeatInterval() - minutes;
				String message = "Waiting to heat - " + String.valueOf(remaining) + " minute(s) remaining.";
				Utils.logInfo(message, "utils.FurnaceController.setMode");
				Conditions.getCurrent().setMessage(message);
			}
			
		} else if (mode.equals("Cool")) {
			int minutes = (int)(new Date().getTime() - lastCoolTime.getTime().getTime()) / 1000 / 60;
			if (minutes > s.getMinCoolInterval())
			{
				//Utils.debugText = "Setting Mode to Cool";
				Conditions.getCurrent().setMessage("Cooling");
				forcingFan = false;
				//toggleFan(true);
				toggleFan(s.getFanOnCool());
				toggleHeat(false);
				toggleCool(true);
			} else {
				int remaining = s.getMinCoolInterval() - minutes;
				String message = "Waiting to cool - " + String.valueOf(remaining) + " minute(s) remaining.";
				Utils.logInfo(message, "utils.FurnaceController.setMode");
				Conditions.getCurrent().setMessage(message);
			}
		} else if (mode.equals("Fan")) {
			//Utils.debugText = "Setting Mode to Fan";
			Conditions.getCurrent().setMessage("Running fan");
			forcingFan = false;
			toggleFan(true);
			toggleHeat(false);
			toggleCool(false);
		}
		lastMode = mode;
	}
	
	private void toggleFan(boolean on)
	{
		if (this.fanOn!=on)
		{
			if (!on && fanOn) this.offTime = Calendar.getInstance();
			this.fanOn=on;
			IOIOHelper.getCurrent().toggleFan(on);
		}
	}
	
	public void toggleHeat(boolean on)
	{
		if (this.heatOn!=on)
		{
			if (!on && heatOn) this.lastHeatTime = Calendar.getInstance();
			this.heatOn=on;
			IOIOHelper.getCurrent().toggleHeat(on);
		}
	}
	
	public void toggleCool(boolean on)
	{
		if (this.coolOn!=on)
		{
			if (!on && coolOn) this.lastCoolTime = Calendar.getInstance();
			this.coolOn=on;
			IOIOHelper.getCurrent().toggleCool(on);
		}
	}
	
	public double getEffectiveCalibration(double calibrationIdle, double calibrationRunning, int calibrationSeconds)
	{
		double calibration = calibrationIdle;
		if (this.fanOn || this.coolOn || this.heatOn) {
			calibration = calibrationRunning;
			int seconds = (int)(new Date().getTime() - cycleStartTime.getTime().getTime()) / 1000;
			//Utils.logInfo(String.valueOf(seconds) + " - " + new Date().toString() + " - " + cycleStartTime.getTime().toString(), "utils.FurnaceController.getEffectiveCalibration");
			if (seconds<calibrationSeconds)
			{
				int offSeconds = calibrationSeconds - seconds;
				calibration = (calibrationIdle * (double)offSeconds + calibrationRunning * (double)seconds) / (double)calibrationSeconds;
			}
		} else 
		{
			int seconds = (int)(new Date().getTime() - offTime.getTime().getTime()) / 1000;
			if (seconds<calibrationSeconds)
			{
				int onSeconds = calibrationSeconds - seconds;
				calibration = (calibrationIdle * (double)seconds + calibrationRunning * (double)onSeconds) / (double)calibrationSeconds;
			}
		}
		return calibration;
	}
	
	public double getTemperature(double calibration)
	{
		try{
			double tempSample = IOIOHelper.getCurrent().getTemperature();
			if (tempSample!=-99)
			{
				tempSample += calibration;
				temps[tempIndex] = tempSample;
				if (tempSamples<tempMaxSamples) tempSamples++;
				tempIndex ++;
				if (tempIndex>=tempMaxSamples) tempIndex = 0;
			}
		} catch (Exception e) {
			Utils.logError(e.toString(), "utils.FurnaceController.getTemperature");
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
