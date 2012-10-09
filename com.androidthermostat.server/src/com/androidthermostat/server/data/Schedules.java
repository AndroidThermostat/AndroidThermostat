package com.androidthermostat.server.data;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

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
			if (s.getName().equals(name)) return s;
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

	public void save(Activity activity)
	{
		Gson gson = new Gson();
		String json = gson.toJson(this);
		
		SharedPreferences prefs = activity.getSharedPreferences("schedules", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString("json", json);
	    editor.commit();
	}
	
	public static void load(Activity activity)
	{
		SharedPreferences prefs = activity.getSharedPreferences("schedules", 0);
	    String json = prefs.getString("json","");
	    if (!json.equals("") && !json.equals("[]"))
	    {
	    	Gson gson = new Gson();
	    	current = gson.fromJson(json, Schedules.class);
	    } else {
	    	createDefault();
	    }
		//createDefault();
	}
	
	public static void load(String json)
	{
		Gson gson = new Gson();
		Schedules result = gson.fromJson(json, Schedules.class);
		Schedules.current=result;
	}
	
	
	//Create sample schedule
	public static void createDefault()
	{
		Schedules result=new Schedules();
		
		Schedule s = new Schedule();
		s.setName("Summer");
		ArrayList<ScheduleEntry> entries = s.getEntries();
		entries.add(new ScheduleEntry(1,9,0,"Cool",79,79));
		entries.add(new ScheduleEntry(1,12,0,"Cool",76,76));
		entries.add(new ScheduleEntry(2,7,30,"Cool",79,79));
		entries.add(new ScheduleEntry(2,17,00,"Cool",76,76));
		entries.add(new ScheduleEntry(3,7,30,"Cool",79,79));
		entries.add(new ScheduleEntry(3,17,00,"Cool",76,76));
		entries.add(new ScheduleEntry(4,7,30,"Cool",79,79));
		entries.add(new ScheduleEntry(4,17,00,"Cool",76,76));
		entries.add(new ScheduleEntry(5,7,30,"Cool",79,79));
		entries.add(new ScheduleEntry(5,17,00,"Cool",76,76));
		entries.add(new ScheduleEntry(6,7,30,"Cool",79,79));
		entries.add(new ScheduleEntry(6,17,00,"Cool",76,76));
		result.add(s);
		
		s = new Schedule();
		s.setName("Winter");
		entries = s.getEntries();
		entries.add(new ScheduleEntry(1,9,0,"Heat",74,74));
		entries.add(new ScheduleEntry(1,12,0,"Heat",77,77));
		entries.add(new ScheduleEntry(2,7,30,"Heat",74,74));
		entries.add(new ScheduleEntry(2,17,00,"Heat",77,77));
		entries.add(new ScheduleEntry(3,7,30,"Heat",74,74));
		entries.add(new ScheduleEntry(3,17,00,"Heat",77,77));
		entries.add(new ScheduleEntry(4,7,30,"Heat",74,74));
		entries.add(new ScheduleEntry(4,17,00,"Heat",77,77));
		entries.add(new ScheduleEntry(5,7,30,"Heat",74,74));
		entries.add(new ScheduleEntry(5,17,00,"Heat",77,77));
		entries.add(new ScheduleEntry(6,7,30,"Heat",74,74));
		entries.add(new ScheduleEntry(6,17,00,"Heat",77,77));
		result.add(s);
		
		s = new Schedule();
		s.setName("Vacation");
		entries = s.getEntries();
		entries.add(new ScheduleEntry(1,0,0,"Cool",80,80));
		entries.add(new ScheduleEntry(2,0,0,"Cool",80,80));
		entries.add(new ScheduleEntry(3,0,0,"Cool",80,80));
		entries.add(new ScheduleEntry(4,0,0,"Cool",80,80));
		entries.add(new ScheduleEntry(5,0,0,"Cool",80,80));
		entries.add(new ScheduleEntry(6,0,0,"Cool",80,80));
		entries.add(new ScheduleEntry(7,0,0,"Cool",80,80));
		result.add(s);
	
		current = result;
	
	}
	
	
}