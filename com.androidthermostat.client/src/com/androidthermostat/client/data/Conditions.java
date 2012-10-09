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
	
	private Bitmap weatherImage = null;
	Timer timer;


	
	public double getInsideTemperature() { return insideTemperature; }
	public double getOutsideTemperature() { return outsideTemperature; }
	public String getWeatherImageUrl() { return weatherImageUrl; }
	public Bitmap getWeatherImage() { return weatherImage; }
	public String getWeatherForecastUrl() { return weatherForecastUrl; }
	public String getMessage() { return message; }

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
	
	public void load()
	{
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		try {
	        String json = Utils.getUrlContents(Servers.getBaseUrl() + "/api/conditions");
	        if (json!=null && json!="")
	        {
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

		} catch (Exception e) {
			Utils.debugText = "Conditions.load - " + e.toString();
		}
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

