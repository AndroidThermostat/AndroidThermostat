package com.androidthermostat.server.data;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.androidthermostat.server.utils.FurnaceController;
import com.androidthermostat.server.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Conditions {

	
	Timer arduinoTimer;
	Timer weatherTimer;
	Timer scheduleTimer;
	
	private static Conditions current;
	@Expose public double insideTemperature = 0;
	@Expose private double outsideTemperature = 65;
	@Expose private String weatherImageUrl = "";
	@Expose private String weatherForecastUrl = "";
	@Expose private String message = "";
	@Expose private String state = "Off";
	
	private Bitmap weatherImage = null;
	public int insideTempRaw=0;
	public String debugMessage = "";

	private Context context;
	
	
	public double getInsideTemperature() { return insideTemperature; }
	public double getOutsideTemperature() { return outsideTemperature; }
	public String getWeatherImageUrl() { return weatherImageUrl; }
	public Bitmap getWeatherImage() { return weatherImage; }
	public String getWeatherForecastUrl() { return weatherForecastUrl; }
	public String getMessage() { return message; }
	
	
	public void setInsideTemperature(double insideTemperature) { this.insideTemperature = insideTemperature; }
	public void setOutsideTemperature(double outsideTemperature) { this.outsideTemperature = outsideTemperature; }
	public void setMessage(String message) { this.message=message; }
	

	
	public static Conditions getCurrent()
	{
		if (current==null) current = new Conditions();
		return current;
	}
	
	
	public String getState()
	{
		this.state = FurnaceController.getCurrent().getMode(); 
		return state;
	}
	
	
	public void init(Context context)
	{
		this.context=context;
		
		arduinoTimer = new Timer();
		arduinoTimer.schedule(new ConditionsTimerTask(), 5000, 5000);
		//arduinoTimer.schedule(new ConditionsTimerTask(), 1000, 1000);
		
		weatherTimer = new Timer();
		weatherTimer.schedule(new WeatherTimerTask(), 2000, 900000);

		//Start the timer at 1 second after the next minute
		int seconds = 61 - new Date().getSeconds();
		scheduleTimer = new Timer();
		scheduleTimer.schedule(new ScheduleTimerTask(), seconds * 1000, 60000);
	}
	
	public void load(String json)
	{
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		
		Conditions result = gson.fromJson(json, Conditions.class);
		
		this.insideTemperature = result.insideTemperature;
		this.outsideTemperature = result.outsideTemperature;
		if (!this.weatherImageUrl.equals(result.weatherImageUrl))
		{
			this.weatherImageUrl = result.weatherImageUrl;
		}
		this.weatherForecastUrl = result.weatherForecastUrl;
			
	}
	
	public void updateWeather()
	{
		
		double previousTemp= Conditions.getCurrent().getOutsideTemperature();
		boolean success=false;
		
		
		String json = Utils.getUrlContents("http://openweathermap.org/data/2.1/weather/city/" + Settings.getCurrent().getOpenWeatherMapStation() );
		com.google.gson.Gson gson = new com.google.gson.Gson();
		OpenWeatherMapResponse resp = gson.fromJson(json, OpenWeatherMapResponse.class);

		try {
			if (resp.main.temp>0) 
			{
				success = true;
				double celcius = resp.main.temp - 272.15;
				this.outsideTemperature = Math.round(celcius * 9.0 / 5.0 + 32);
				this.weatherForecastUrl = Settings.getCurrent().getForecastUrl().replace("[postalCode]", String.valueOf(Settings.getCurrent().getLocation()) );
				
				this.weatherImageUrl = "/img/" + resp.weather.get(0).icon + ".png";
				//this.weatherImageUrl = "http://openweathermap.org/img/w/" + resp.weather.get(0).icon + ".png";
				//this.weatherImageUrl = resp.img;
				//this.weatherImage = BitmapFactory.decodeStream((InputStream)new URL(weatherImageUrl).getContent());
				//this.weatherForecastUrl = "http://www.weather.com/weather/right-now/" + Settings.getCurrent().getZipCode();
				
			}
		
			if (previousTemp!=Conditions.getCurrent().getOutsideTemperature() && success)
			{
				try {
					Settings s = Settings.getCurrent();
					if (s.getPingOutUrl()!=null && s.getPingOutUrl()!="" && s.getOutsideTempChangeParams()!=null && s.getOutsideTempChangeParams()!="") Utils.pingOut(s.getPingOutUrl() + s.getOutsideTempChangeParams());
				} catch (Exception e) {
					Utils.logError(e.toString(), "data.Conditions.updateWeather");
				}
			}
		} catch (Exception e) { Utils.logError(e.toString(), "data.Conditions.updateWeather"); }

	}
	
	
	public void updateArduino()
	{
		
		Settings s = Settings.getCurrent();
		FurnaceController fc = FurnaceController.getCurrent();
		
		double previousTemp = Conditions.getCurrent().insideTemperature;
		double temp = fc.getTemperature();
		
		int effectiveHigh = s.getTargetHigh();
		int effectiveLow = s.getTargetLow();
		if (s.getIsAway())
		{
			effectiveHigh = s.getAwayHigh();
			effectiveLow = s.getAwayLow();
		}
		

		
		//temp patch due to unreliable arduino communication
		if (temp>0.0)
		{
			temp += Settings.getCurrent().getTemperatureCalibration();
			
			this.setInsideTemperature( temp );
			
			
			if (temp > 45 && temp < 100)
			{
				if (s.getMode().equals("Auto")) setThermostatState(effectiveLow, effectiveHigh, s.getSwing());
				if (s.getMode().equals("Cool")) setThermostatState(0, effectiveHigh, s.getSwing());
				if (s.getMode().equals("Heat")) setThermostatState(effectiveLow, 255, s.getSwing());
				if (s.getMode().equals("Fan")) fc.setMode("Fan");
				if (s.getMode().equals("Off")) fc.setMode("Off");
	
				//Make sure there's a full 1 degree difference in temperature so it isn't too chatty.
				if (previousTemp - temp > 1 || previousTemp - temp < -1)
				{
					try {
						if (s.getPingOutUrl()!=null && s.getPingOutUrl()!="" && s.getInsideTempChangeParams()!=null && s.getInsideTempChangeParams()!="") Utils.pingOut(s.getPingOutUrl() + s.getInsideTempChangeParams());
					} catch (Exception e) {
						Utils.logError(e.toString(), "data.Conditions.updateArduino");
					}
				}
			} else {
				//*** Temperature is outside of safe range.  Ignore all other settings and take action.
				Utils.logInfo("MANUAL TEMPERATURE OVERRIDE ACTIVATED - " + String.valueOf(temp) + "F", "data.Conditions.updateArduino");
				if (temp<=45) fc.setMode("Heat");
				if (temp>=100) fc.setMode("Cool");
			}
			
		}
		
	}
	
	private void setThermostatState(int minTemp, int maxTemp, double swing)
	{
		FurnaceController fc = FurnaceController.getCurrent();
		
		String previousMode=fc.getMode();
		Date cycleStartTime = fc.getCycleStartTime();
		
		if (insideTemperature < minTemp - swing) fc.setMode("Heat");
		else if (insideTemperature > maxTemp + swing) fc.setMode("Cool");
		else {
			//We ignore the swing here in order to leave the Heat/AC on until it is fully within range
			if (insideTemperature >= minTemp && insideTemperature<=maxTemp) fc.setMode("Off");
		}
		
		String newMode = fc.getMode();

		try{
			if (newMode!=previousMode && newMode=="Off" && cycleStartTime!=null)
			{
				//If a cycle just ended, log it
				Settings s = Settings.getCurrent();
				if (s.getPingOutUrl()!=null && s.getPingOutUrl()!="" && s.getCycleCompleteParams()!=null && s.getCycleCompleteParams()!="") {
					String url = s.getPingOutUrl() + s.getCycleCompleteParams();
					if (url.contains("[mode]")) url = url.replace("[mode]", previousMode );
					if (url.contains("[duration]")) {
						int duration = (int)(new Date().getTime() - cycleStartTime.getTime()) / 1000;
						url = url.replace("[duration]", String.valueOf(duration) );
					}
					Utils.pingOut(url);
				}
			}
		} catch (Exception e) {
			Utils.logError(e.toString(), "data.Conditions.setThermostatState");
		}
		
	}

	
	class ConditionsTimerTask extends TimerTask {
        @Override
        public void run() {
        	try {
        		Conditions.getCurrent().updateArduino();
        	} catch (Exception e) {
        		Utils.logError(e.toString(), "data.Conditions.ConditionsTimerTask.run");
    		}
        }
    };
    
    class WeatherTimerTask extends TimerTask {
        @Override
        public void run() {
        	try {
        		Conditions.getCurrent().updateWeather();
        	} catch (Exception e) { 
        		Utils.logError(e.toString(), "data.Conditions.WeatherTimerTask.run");
        	}
        }
    };
    
    class ScheduleTimerTask extends TimerTask {
        @Override
        public void run() {
        	try{
	        	Settings s = Settings.getCurrent();
	            int scheduleId = s.getSchedule();
	            if (scheduleId>-1 && scheduleId<Schedules.getCurrent().size())
	            {
	            	Schedules.getCurrent().get(scheduleId).check(context);
	            }
        	} catch (Exception e) { 
        		Utils.logError(e.toString(), "data.Conditions.WeatherTimerTask.run");
        	}
            		
        }
    };
	
	
	
}