package com.androidthermostat.server.data;

import android.app.Activity;
import android.content.SharedPreferences;


import com.androidthermostat.server.utils.Utils;
import com.google.gson.Gson;

public class Settings {

	private String mode = "Off";
	private int targetLow = 75;
	private int targetHigh = 75;
	private double swing = 0.0;
	private static Settings current;
	private int zipCode = 90210;
	private int openWeatherMapStation = 5328041;
	private int schedule = -1;
	private int minCoolInterval = 5;
	private int temperatureCalibration = 0;
	private String name = "Android Thermostat";
	private String pingOutUrl = "";
	private String cycleCompleteParams = "&a=cycle&m=[mode]&d=[duration]";
	private String insideTempChangeParams = "&a=temp&t=[insideTemp]";
	private String outsideTempChangeParams = "&a=conditions&t=[outsideTemp]";
	private String viewStatsParams = "&a=stats";
	
	public int getTargetHigh() { return targetHigh; }
	public int getTargetLow() { return targetLow; }
	public double getSwing() { return swing; }
	public String getMode() { return mode; }
	public int getZipCode() { return zipCode; }
	public int getOpenWeatherMapStation() { return openWeatherMapStation; }
	public int getSchedule() { return schedule; }
	public int getMinCoolInterval() { return minCoolInterval; }
	public int getTemperatureCalibration() { return temperatureCalibration; }
	public String getName() { return name; }
	public String getPingOutUrl() { return pingOutUrl; }
	public String getCycleCompleteParams() { return cycleCompleteParams; }
	public String getInsideTempChangeParams() { return insideTempChangeParams; }
	public String getOutsideTempChangeParams() { return outsideTempChangeParams; }
	public String getViewStatsParams() { return viewStatsParams; }
	
	public void setTargetHigh(int value) { targetHigh = value; }
	public void setTargetLow(int value) { targetLow = value; }
	public void setSwing(double value) { swing = value; }
	public void setMode(String value) { mode = value; }
	public void setZipCode(int value) { zipCode = value; }
	public void setOpenWeatherMapStation(int value) { openWeatherMapStation = value; }
	public void setSchedule(int value) { schedule = value; }
	public void setMinCoolInterval(int value) { minCoolInterval = value; }
	public void setTemperatureCalibration(int value) { temperatureCalibration = value; }
	public void setName(String value) { this.name = value; }
	public void setPingOutUrl(String pingOutUrl) { this.pingOutUrl = pingOutUrl; }
	public void setCycleCompleteParams(String cycleCompleteParams) { this.cycleCompleteParams = cycleCompleteParams; }
	public void setInsideTempChangeParams(String insideTempChangeParams) { this.insideTempChangeParams = insideTempChangeParams; }
	public void setOutsideTempChangeParams(String outsideTempChangeParams) { this.outsideTempChangeParams = outsideTempChangeParams; }
	public void setViewStatsParams(String viewStatsParams) { this.viewStatsParams = viewStatsParams; }
	
	
	
	public String getSummary()
	{
		String result="";
		result = mode;
		if (mode.equals("Cool")) result += " - " + String.valueOf(targetLow) + "° F";
		if (mode.equals("Heat")) result += " - " + String.valueOf(targetHigh) + "° F";
		return result;
	}
	
	public static Settings getCurrent()
	{
		if (current==null) current = new Settings();
		return current;
	}
	
	public void save(Activity activity)
	{
		Gson gson = new Gson();
		String json = gson.toJson(this);
		
		SharedPreferences prefs = activity.getSharedPreferences("settings", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString("json", json);
	    editor.commit();
	}
	
	public static void load(Activity activity)
	{
		SharedPreferences prefs = activity.getSharedPreferences("settings", 0);
	    String json = prefs.getString("json","");
	    if (!json.equals(""))
	    {
	    	Gson gson = new Gson();
	    	current = gson.fromJson(json, Settings.class);
	    }
	}
	
	public static void load(String json)
	{
		int previousZip = current.zipCode; 
		Gson gson = new Gson();
		Settings result = gson.fromJson(json, Settings.class);
		if (!result.getName().equals(""))
		{
			//prevents a client from posting back blank settings if it failed to load them first
			Settings.current=result;
			if (current.zipCode != previousZip) updateOpenWeatherStation();
		}
	}
	
	private static void updateOpenWeatherStation()
	{
		String result = Utils.getUrlContents("http://api.thermostatmonitor.com/v2/?a=location&z=" + Settings.getCurrent().zipCode );
		Settings.getCurrent().setOpenWeatherMapStation( Integer.parseInt(result) );
		Conditions.getCurrent().updateWeather();
	}
	
}
