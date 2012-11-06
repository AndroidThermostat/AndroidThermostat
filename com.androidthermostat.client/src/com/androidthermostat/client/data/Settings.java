package com.androidthermostat.client.data;

import java.util.Timer;

import android.app.Activity;

import com.androidthermostat.client.data.Conditions.ConditionsTimerTask;
import com.androidthermostat.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class Settings {

	private String mode = "Off";
	private int targetLow = 74;
	private int targetHigh = 78;
	private int awayLow = 70;
	private int awayHigh = 80;
	private boolean isAway = false;
	private double swing = 1.0;
	private static Settings current;
	private int zipCode = 90210;
	private int schedule = -1;
	private int minCoolInterval = 5;
	private int minHeatInterval = 2;
	private int temperatureCalibration = 0;
	private String name = "";
	private String pingOutUrl = "";
	private String cycleCompleteParams = "";
	private String insideTempChangeParams = "";
	private String outsideTempChangeParams = "";
	private String viewStatsParams = "";
	private String password = "";
	private String forecastUrl = "http://www.weather.com/weather/right-now/[postalCode]";
	private boolean cycleFan = false;
	public int cycleFanOnMinutes = 5;
	public int cycleFanOffMinutes = 25;
	private String json;
	
	

	public int getTargetHigh() { return targetHigh; }
	public int getTargetLow() { return targetLow; }
	public int getAwayHigh() { return awayHigh; }
	public int getAwayLow() { return awayLow; }
	public boolean getIsAway() { return isAway; }
	public double getSwing() { return swing; }
	public String getMode() { return mode; }
	public int getZipCode() { return zipCode; }
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
	
	public void setTargetHigh(int value) { targetHigh = value; }
	public void setTargetLow(int value) { targetLow = value; }
	public void setAwayHigh(int value) { awayHigh = value; }
	public void setAwayLow(int value) { awayLow = value; }
	public void setIsAway(boolean value) { isAway = value; }
	public void setSwing(double value) { swing = value; }
	public void setMode(String value) { mode = value; }
	public void setZipCode(int value) { zipCode = value; }
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
	
	public String getJson()
	{
		return json;
	}
	
	public String getSummary()
	{
		String result="";
		result = mode;
		if (isAway)
		{
			result = "Away: " + String.valueOf(awayLow) + " - " + String.valueOf(awayHigh) + "° F";
		} else {
			if ("Cool".equals(mode)) result += ": " + String.valueOf(targetHigh) + "° F";
			if ("Heat".equals(mode)) result += ": " + String.valueOf(targetLow) + "° F";
			if ("Auto".equals(mode)) result += ": " + String.valueOf(targetLow) + " - " + String.valueOf(targetHigh) + "° F";
		}
		return result;
	}
	
	public static Settings getCurrent()
	{
		if (current==null) current = new Settings();
		return current;
	}
	
	public void save()
	{
		//Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Gson gson = new Gson();
		String json = gson.toJson(this);
		Utils.jsonPost(Servers.getBaseUrl() + "/api/settings" + Servers.getBaseParams(), json);
	}
	
	public static void load()
	{
		
		
		try {
	        String json = Utils.getUrlContents(Servers.getBaseUrl() + "/api/settings" + Servers.getBaseParams());
	        if (!json.equals(Settings.getCurrent().getJson()))
	        {
		        Gson gson = new Gson();
		        if (json!=null && !"".equals(json) && !json.contains("\"error\":"))
		        {
		        	Settings result = gson.fromJson(json, Settings.class);
		        	result.json = json;
		        	Settings.current=result;
		        	
		        }
	        }
		} catch (Exception e) {
			Utils.debugText = "Settings.load - " + e.toString();
		}
	}
	
}
