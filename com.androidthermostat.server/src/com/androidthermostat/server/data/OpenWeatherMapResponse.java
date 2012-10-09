package com.androidthermostat.server.data;

public class OpenWeatherMapResponse {
	
	public String img="";
	public OpenWeatherMapMain main;

	public class OpenWeatherMapMain
	{
		public double temp;
	}
}
