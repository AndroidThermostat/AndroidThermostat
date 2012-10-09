package com.androidthermostat.client.data;

public class ScheduleEntry {

	private int dayOfWeek;
	private int hour;
	private int minute;
	private String mode;
	private int targetHigh;
	private int targetLow;
	
	public int getDayOfWeek() { return dayOfWeek; }
	public int getHour() { return hour; }
	public int getMinute() { return minute; }
	public String getMode() { return mode; }
	public int getTargetHigh() { return targetHigh; }
	public int getTargetLow() { return targetLow; }
	
	public String getDisplayName()
	{
		String period = "am";
		
		int displayHour=hour;
		if (displayHour>11) {
			displayHour=displayHour-12;
			period = "pm";
		}
		if (displayHour==0) displayHour=12;
		
		String result = "At " + String.valueOf(displayHour) + ":" + String.format("%02d", minute) + " " + period + ", ";
		if (mode.equals("Off")) result += "turn the thermostat off.";
		else if (mode.equals("Fan")) result += "turn the thermostat fan on.";
		else if (mode.equals("Heat")) result += "heat to " + String.valueOf(targetLow) + "° F.";
		else if (mode.equals("Cool")) result += "cool to " + String.valueOf(targetHigh) + "° F.";
		return result;
	}
	
	public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
	public void setHour(int hour) { this.hour = hour; }
	public void setMinute(int minute) { this.minute = minute; }
	public void setMode(String mode) { this.mode = mode; }
	public void setTargetHigh(int targetHigh) { this.targetHigh = targetHigh; }
	public void setTargetLow(int targetLow) { this.targetLow = targetLow; }
	
	public ScheduleEntry(){}
	
	public ScheduleEntry(int dayOfWeek, int hour, int minute, String mode, int targetHigh, int targetLow)
	{
		this.dayOfWeek = dayOfWeek;
		this.hour = hour;
		this.minute = minute;
		this.mode = mode;
		this.targetHigh = targetHigh;
		this.targetLow = targetLow;
	}
	
}