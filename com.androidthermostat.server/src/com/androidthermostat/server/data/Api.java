package com.androidthermostat.server.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;


public class Api {
	
	@Expose private boolean requirePassword = false;
	private static Api current;
	
	public boolean getRequirePassword() { return requirePassword; }
	
	public static Api getCurrent()
	{
		if (current==null) current = new Api();
		current.requirePassword = Settings.getCurrent().getPassword()!="";
		return current;
	}
	
	
	
}
