package com.androidthermostat.server.data;

import java.util.ArrayList;

public class OpenWeatherMapResponse {
	
	public String img="";
	public OpenWeatherMapMain main;
	public ArrayList<OpenWeatherMapWeather> weather;

	public class OpenWeatherMapMain
	{
		public double temp;
	}
	
	public class OpenWeatherMapWeather
	{
		public String icon;
	}
	
	
}
