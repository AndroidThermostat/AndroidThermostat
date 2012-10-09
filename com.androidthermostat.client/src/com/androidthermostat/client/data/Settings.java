package com.androidthermostat.client.data;

import java.util.Timer;

import android.app.Activity;

import com.androidthermostat.client.data.Conditions.ConditionsTimerTask;
import com.androidthermostat.utils.Utils;
import com.google.gson.Gson;

public class Settings {

	private String mode = "Off";
	private int targetLow = 75;
	private int targetHigh = 75;
	private double swing = 0.0;
	private static Settings current;
	private int zipCode = 90210;
	private int schedule = -1;
	private int minCoolInterval = 5;
	private int temperatureCalibration = 0;
	private String name = "";
	private String pingOutUrl = "";
	private String cycleCompleteParams = "";
	private String insideTempChangeParams = "";
	private String outsideTempChangeParams = "";
	private String viewStatsParams = "";

	public int getTargetHigh() { return targetHigh; }
	public int getTargetLow() { return targetLow; }
	public double getSwing() { return swing; }
	public String getMode() { return mode; }
	public int getZipCode() { return zipCode; }
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
	
	public void save()
	{
		//Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Gson gson = new Gson();
		String json = gson.toJson(this);
		Utils.jsonPost(Servers.getBaseUrl() + "/api/settings", json);
	}
	
	public static void load()
	{
		Gson gson = new Gson();
		
		try {
	        String json = Utils.getUrlContents(Servers.getBaseUrl() + "/api/settings");
			Settings result = gson.fromJson(json, Settings.class);
			Settings.current=result;
		} catch (Exception e) {
			Utils.debugText = "Settings.load - " + e.toString();
		}
	}
	
}
