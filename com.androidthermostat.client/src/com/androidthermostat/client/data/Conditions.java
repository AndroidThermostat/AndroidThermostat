package com.androidthermostat.client.data;

import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.androidthermostat.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Conditions {

	
	
	private static Conditions current;
	
	@Expose private double insideTemperature = 0;
	@Expose private double outsideTemperature = 65;
	@Expose private String weatherImageUrl = "";
	@Expose private String weatherForecastUrl = "";
	@Expose private String message = "";
	@Expose private String state = "";
	private String json = "";
	
	private Bitmap weatherImage = null;
	Timer timer;


	
	public String getJson()
	{
		return json;
	}
	
	public double getInsideTemperature() { return insideTemperature; }
	public double getOutsideTemperature() { return outsideTemperature; }
	public String getWeatherImageUrl() { return weatherImageUrl; }
	public Bitmap getWeatherImage() { return weatherImage; }
	public String getWeatherForecastUrl() { return weatherForecastUrl; }
	public String getMessage() { return message; }
	public String getState() { return state; }
	
	public String getDisplayInsideTemperature() {
		if (Settings.getCurrent().displayCelsius)
		{
			if (insideTemperature==0) return "0° C";
			else return String.valueOf( Math.round(Utils.fahrenheitToCelsius(insideTemperature)) ) + "° C";
		} else {
			return String.valueOf(insideTemperature) + "° F";
		}
	}
	
	public String getDisplayOutsideTemperature() {
		if (Settings.getCurrent().displayCelsius)
		{
			return String.valueOf( Utils.fahrenheitToCelsius(outsideTemperature) ) + "° C";
		} else {
			return String.valueOf(outsideTemperature) + "° F";
		}
	}
	

	public void setInsideTemperature(double insideTemperature) { this.insideTemperature = insideTemperature; }
	public void setOutsideTemperature(double outsideTemperature) { this.outsideTemperature = outsideTemperature; }
	public void setMessage(String message) { 
		this.message=message;
		if (this.message!=null && this.message!="") Utils.debugText = message;
	}
	
	public static Conditions getCurrent()
	{
		if (current==null) current = new Conditions();
		return current;
	}
	
	public static void setCurrent(Conditions value)
	{
		current = value;
	}
	
	public boolean load()
	{
		
		
		try {
	        String json = Utils.getUrlContents(Servers.getBaseUrl() + "/api/conditions" + Servers.getBaseParams());
	        if (json!=null && !"".equals(json) && !json.contains("\"error\":"))
	        {
	        	if (!json.equals(this.json))
	        	{
	        		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		        	this.json = json;
					Conditions result = gson.fromJson(json, Conditions.class);
					
					this.insideTemperature = result.insideTemperature;
					this.outsideTemperature = result.outsideTemperature;
					if (!this.weatherImageUrl.equals(result.weatherImageUrl))
					{
						this.weatherImageUrl = result.weatherImageUrl;
						if (this.weatherImageUrl!=null && this.weatherImageUrl!="") this.weatherImage = BitmapFactory.decodeStream((InputStream)new URL(weatherImageUrl).getContent());
					}
					this.weatherForecastUrl = result.weatherForecastUrl;
					this.setMessage(result.message);
	        	}
				return true;
	        }
		} catch (Exception e) {
			Utils.debugText = "Conditions.load - " + e.toString();
		}
		return false;
	}
	
	/*
	public void save()
	{
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(this);
		Utils.jsonPost(Utils.getRemoteServer() + "/api/conditions", json);
	}
	*/
	
	class ConditionsTimerTask extends TimerTask {
        @Override
        public void run() {
        	Conditions.getCurrent().load();
        }
    };
	
}

