package com.androidthermostat.client.data;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;


import com.androidthermostat.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Schedules extends ArrayList<Schedule> {

	private static Schedules current;
	
	
	
	
	public static Schedules getCurrent() {
		if (current==null) current=new Schedules();
		return current; 
	};

	
	public String[] getNames(boolean includeNone)
	{
		ArrayList<String> result=new ArrayList<String>();
		if (includeNone) result.add("(None)");

		for (Schedule s : this) result.add(s.getName());
		return result.toArray(new String[result.size()]);
	}
	
	public Schedule getByName(String name)
	{
		for (Schedule s : this)
		{
			if (s.getName()!=null && s.getName().equals(name)) return s;
		}
		return null;
	}
	

	public void remove(String scheduleName)
	{
		for (int i=this.size()-1;i>=0;i--)
		{
			if (this.get(i).getName().equals(scheduleName))
			{
				remove(i);
			}
		}
	}

	public void save()
	{
		Gson gson = new Gson();
		String json = gson.toJson(this);
		Utils.jsonPost(Servers.getBaseUrl() + "/api/schedules", json);
	}
	
	public static void load()
	{
		
		//Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		Gson gson = new Gson();
		try {
	        String json = Utils.getUrlContents(Servers.getBaseUrl() + "/api/schedules");
			Schedules result = gson.fromJson(json, Schedules.class);
			current = result;
		} catch (Exception e) {
			Utils.debugText = "Schedules.load - " + e.toString();
		}
		
	}
	
	
	
}
