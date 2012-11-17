package com.androidthermostat.server.data;

import java.net.URLEncoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.androidthermostat.server.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;


public class Settings {

	@Expose private String mode = "Off";
	@Expose private int targetLow = 74;
	@Expose private int targetHigh = 78;
	@Expose private int awayLow = 70;
	@Expose private int awayHigh = 80;
	@Expose private boolean isAway = false;
	@Expose private double swing = 1.0;
	@Expose private static Settings current;
	@Expose private String location = "90210";
	@Expose private int openWeatherMapStation = 5328041;
	@Expose private int schedule = -1;
	@Expose private int minCoolInterval = 5;
	@Expose private int minHeatInterval = 2;
	@Expose private int temperatureCalibration = 0;
	@Expose private String name = "Android Thermostat";
	@Expose private String pingOutUrl = "";
	@Expose private String cycleCompleteParams = "&a=cycle&m=[mode]&d=[duration]";
	@Expose private String insideTempChangeParams = "&a=temp&t=[insideTemp]";
	@Expose private String outsideTempChangeParams = "&a=conditions&t=[outsideTemp]";
	@Expose private String viewStatsParams = "&a=stats";
	@Expose private String password = "";
	@Expose private String forecastUrl = "http://www.weather.com/weather/right-now/[postalCode]";
	@Expose private boolean cycleFan = false;
	@Expose public int cycleFanOnMinutes = 5;
	@Expose public int cycleFanOffMinutes = 25;
	@Expose public boolean displayCelcius = false;
	
	public int getTargetHigh() { return targetHigh; }
	public int getTargetLow() { return targetLow; }
	public int getAwayHigh() { return awayHigh; }
	public int getAwayLow() { return awayLow; }
	public boolean getIsAway() { return isAway; }
	public double getSwing() { return swing; }
	public String getMode() { return mode; }
	public String getLocation() { return location; }
	public int getOpenWeatherMapStation() { return openWeatherMapStation; }
	public int getSchedule() { return schedule; }
	public int getMinCoolInterval() { return minCoolInterval; }
	public int getMinHeatInterval() { return minHeatInterval; }
	public int getTemperatureCalibration() { return temperatureCalibration; }
	public String getName() { return name; }
	public String getPingOutUrl() { return pingOutUrl; }
	public String getCycleCompleteParams() { return cycleCompleteParams; }
	public String getInsideTempChangeParams() { return insideTempChangeParams; }
	public String getOutsideTempChangeParams() { return outsideTempChangeParams; }
	public String getViewStatsParams() { return viewStatsParams; }
	public String getPassword() { return password; }
	public String getForecastUrl() { return forecastUrl; }
	public boolean getCycleFan() { return cycleFan; }
	public int getCycleFanOnMinutes() { return cycleFanOnMinutes; }
	public int getCycleFanOffMinutes() { return cycleFanOffMinutes; }
	public boolean getDisplayCelcius() { return displayCelcius; }
	
	public void setTargetHigh(int value) { targetHigh = value; }
	public void setTargetLow(int value) { targetLow = value; }
	public void setAwayHigh(int value) { awayHigh = value; }
	public void setAwayLow(int value) { awayLow = value; }
	public void setIsAway(boolean value) { isAway = value; }
	public void setSwing(double value) { swing = value; }
	public void setMode(String value) { mode = value; }
	public void setLocation(String value) { location = value; }
	public void setOpenWeatherMapStation(int value) { openWeatherMapStation = value; }
	public void setSchedule(int value) { schedule = value; }
	public void setMinCoolInterval(int value) { minCoolInterval = value; }
	public void setMinHeatInterval(int value) { minHeatInterval = value; }
	public void setTemperatureCalibration(int value) { temperatureCalibration = value; }
	public void setName(String value) { this.name = value; }
	public void setPingOutUrl(String pingOutUrl) { this.pingOutUrl = pingOutUrl; }
	public void setCycleCompleteParams(String cycleCompleteParams) { this.cycleCompleteParams = cycleCompleteParams; }
	public void setInsideTempChangeParams(String insideTempChangeParams) { this.insideTempChangeParams = insideTempChangeParams; }
	public void setOutsideTempChangeParams(String outsideTempChangeParams) { this.outsideTempChangeParams = outsideTempChangeParams; }
	public void setViewStatsParams(String viewStatsParams) { this.viewStatsParams = viewStatsParams; }
	public void setPassword(String password) { this.password = password; }
	public void setForecastUrl(String forecastUrl) { this.forecastUrl = forecastUrl; }
	public void setCycleFan(boolean cycleFan) { this.cycleFan = cycleFan; }
	public void setCycleFanOnMinutes(int cycleFanOnMinutes) { this.cycleFanOnMinutes = cycleFanOnMinutes; }
	public void setCycleFanOffMinutes(int cycleFanOffMinutes) { this.cycleFanOffMinutes = cycleFanOffMinutes; }
	public void setDisplayCelcius(boolean value) { this.displayCelcius = value; }
	
	public String getSummary()
	{
		String result="";
		result = mode;
		if (isAway)
		{
			result = "Away: " + String.valueOf(awayLow) + " - " + String.valueOf(awayHigh) + "° F";
		} else {
			if (mode.equals("Cool")) result += ": " + String.valueOf(targetLow) + "° F";
			if (mode.equals("Heat")) result += ": " + String.valueOf(targetHigh) + "° F";
			if (mode.equals("Auto")) result += ": " + String.valueOf(targetLow) + " - " + String.valueOf(targetHigh) + "° F";
		}
		return result;
	}
	
	public static Settings getCurrent()
	{
		if (current==null) current = new Settings();
		return current;
	}
	
	public void save(Context context)
	{
		Gson gson = new Gson();
		String json = gson.toJson(this);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		//SharedPreferences prefs = activity.getSharedPreferences("settings", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    //editor.putString("json", json);
	    editor.putString("settings", json);
	    editor.commit();
	}
	
	public static void load(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString("settings","");
	    if (!json.equals(""))
	    {
	    	Gson gson = new Gson();
	    	current = gson.fromJson(json, Settings.class);
	    }
	}
	
	public static void load(String json)
	{
		String previousLocation = current.location; 
		int previousWeatherStation = current.openWeatherMapStation;
		
		Gson gson = new Gson();
		Settings result = gson.fromJson(json, Settings.class);
		
		//prevents a client from posting back blank settings if it failed to load them first
		if (!result.getName().equals(""))
		{
			//the weather station isn't posted back.  Override it.
			result.openWeatherMapStation = previousWeatherStation;

			Settings.current=result;
			if (current.location != previousLocation) updateOpenWeatherStation();
		}
	}
	
	private static void updateOpenWeatherStation()
	{
		String result = Utils.getUrlContents("http://api.thermostatmonitor.com/v2/?a=location&q=" + URLEncoder.encode(Settings.getCurrent().location) );
		Settings.getCurrent().setOpenWeatherMapStation( Integer.parseInt(result) );
		Conditions.getCurrent().updateWeather();
	}
	
}
