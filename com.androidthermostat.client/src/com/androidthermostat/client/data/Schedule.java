package com.androidthermostat.client.data;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;

import com.androidthermostat.utils.Utils;

public class Schedule {
	
	private String name;
	private ArrayList<ScheduleEntry> entries;
	
	public String getName() { return name; }
	public ArrayList<ScheduleEntry> getEntries() {
		if (entries==null) entries=new ArrayList<ScheduleEntry>();
		return entries; 
	}
	
	public ArrayList<ScheduleEntry> getEntriesByDayOfWeek(int dayOfWeek) {
		if (entries==null) entries=new ArrayList<ScheduleEntry>();
		ArrayList<ScheduleEntry> result = new ArrayList<ScheduleEntry>();
		for (ScheduleEntry entry : this.entries)
		{
			if (entry.getDayOfWeek()==dayOfWeek) result.add(entry);
		}
		return result;
	}
	
	public void setName(String name) { this.name = name; }
	public void setEntries(ArrayList<ScheduleEntry> entries) { this.entries = entries; }

	public void deleteEntry(int dayOfWeek, int position)
	{
		int delPos=-1;
		int matches=-1;
		
		for (int i=0;i<entries.size();i++)
		{
			ScheduleEntry entry = entries.get(i);
			if (entry.getDayOfWeek() == dayOfWeek)
			{
				matches++;
				if (matches==position) delPos = i;
			}
		}
		
		if (delPos>-1) entries.remove(delPos);
	}
	
	
	public void copyDay(int fromDay, int toDay)
	{
		//remove existing
		for (int i=entries.size() - 1;i>=0;i--)
		{
			if (entries.get(i).getDayOfWeek() == toDay) entries.remove(i);
		}
		
		//copy new
		int size=entries.size();
		for (int i=0;i<size;i++)
		{
			ScheduleEntry existing = entries.get(i);
			if (existing.getDayOfWeek() == fromDay)
			{
				ScheduleEntry newEntry = new ScheduleEntry();
				newEntry.setDayOfWeek(toDay);
				newEntry.setHour(existing.getHour());
				newEntry.setMinute(existing.getMinute());
				newEntry.setMode(existing.getMode());
				newEntry.setTargetHigh(existing.getTargetHigh());
				newEntry.setTargetLow(existing.getTargetLow());
				entries.add(newEntry);
			}
		}
		
	}

}

