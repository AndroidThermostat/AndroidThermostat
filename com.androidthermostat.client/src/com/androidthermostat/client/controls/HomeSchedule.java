package com.androidthermostat.client.controls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.androidthermostat.client.data.Conditions;
import com.androidthermostat.client.data.Schedule;
import com.androidthermostat.client.data.ScheduleEntry;
import com.androidthermostat.client.data.Schedules;
import com.androidthermostat.client.data.Settings;
import com.androidthermostat.utils.Utils;

public class HomeSchedule extends View {

	int width = 0;
	int height = 0;
	double minTemp = 100;
	double maxTemp = 0;
	Schedule currentSchedule = null;
	List<HomeScheduleData> data = new ArrayList<HomeScheduleData>();
	Canvas canvas = null;

    public HomeSchedule(Context context, AttributeSet attrs) 
    {
    	super(context, attrs);
    	init();
    }
    
    public HomeSchedule(Context context) {
        super(context);
        init();
    }
    
    private void init()
    {
    	currentSchedule = null;
    	Schedules schedules = Schedules.getCurrent();
    	Settings settings = Settings.getCurrent();
    	if (settings.getSchedule()>-1 && settings.getSchedule()<schedules.size())
    	{
    		currentSchedule = schedules.get(settings.getSchedule());
    	}
    	
    	populateData();
    	setMinMax();
    }
    
    private void setMinMax()
    {
    	minTemp = 100;
    	maxTemp = 0;
    	
    	for (HomeScheduleData dataPoint : data)
    	{
    		if (dataPoint.minDegrees>0 && dataPoint.minDegrees < minTemp) minTemp = dataPoint.minDegrees;
    		if (dataPoint.maxDegrees>0 && dataPoint.maxDegrees > maxTemp) maxTemp = dataPoint.maxDegrees;
    	}
    	if (minTemp>maxTemp) {
    		if (maxTemp==0) maxTemp = minTemp; else minTemp = maxTemp;
    	}
    	

    	minTemp = Math.round(minTemp) - 1;
    	maxTemp = Math.round(maxTemp) + 1;
    }
        
    private void populateData()
    {
    	Settings s = Settings.getCurrent();
    	data = new ArrayList<HomeScheduleData>();
    	
    	boolean nowDataAdded = false;
    	HomeScheduleData nowData = null;
    	if (s.getIsAway()) nowData = new HomeScheduleData(new Date().getHours(), new Date().getMinutes(), "Auto", s.getAwayLow(), s.getAwayHigh());
    	else nowData = new HomeScheduleData(new Date().getHours(), new Date().getMinutes(), s.getMode(), s.getTargetLow(), s.getTargetHigh());
    	
    	
    	if (currentSchedule!=null)
    	{
    		int currentDayOfWeek = Utils.getDayOfWeek(new Date());
    		int previousDayOfWeek = currentDayOfWeek-1;
    		if (previousDayOfWeek < 0) previousDayOfWeek = 6;
    		
    		List<ScheduleEntry> previousEntries = currentSchedule.getEntriesByDayOfWeek(previousDayOfWeek);
    		if (previousEntries.size()>0) {
    			ScheduleEntry previousEntry = previousEntries.get(previousEntries.size()-1);
    			data.add(new HomeScheduleData(0, 0, previousEntry.getMode(), previousEntry.getTargetLow(), previousEntry.getTargetHigh()));
    		}
    		
    		for (ScheduleEntry entry : currentSchedule.getEntriesByDayOfWeek(currentDayOfWeek))
    		{
    			HomeScheduleData scheduleData = new HomeScheduleData(entry.getHour(), entry.getMinute(), entry.getMode(), entry.getTargetLow(), entry.getTargetHigh());
    			if (!nowDataAdded && scheduleData.minutes>nowData.minutes)
    			{
    				data.add(nowData);
    				nowDataAdded = true;
    			}
    			data.add(scheduleData);
    		}
    		
    	}
    		    		
    	if (!nowDataAdded) data.add(nowData);

    }
    
    
    
    
    
    
    private void drawData()
    {
    	
    	
    	for (int i=0;i<data.size();i++)
    	{
    		HomeScheduleData dataPoint = data.get(i);
    		if (i==0)
    		{
    			drawBlock(0, dataPoint.minutes, dataPoint.minDegrees, dataPoint.maxDegrees);
    		}
    		if (i==data.size()-1)
    		{
    			drawBlock(dataPoint.minutes, 1440, dataPoint.minDegrees, dataPoint.maxDegrees);
    		} else {
    			drawBlock(dataPoint.minutes, data.get(i+1).minutes, dataPoint.minDegrees, dataPoint.maxDegrees);
    		}
    	}
    }
    
    private void drawBlock(int startMinutes, int endMinutes, double minDegrees, double maxDegrees)
    {
    	Paint heatPaint = new Paint();
    	heatPaint.setARGB(100, 255, 0, 0);
    	Paint coolPaint = new Paint();
    	coolPaint.setARGB(100, 0, 0, 255);
    	
    	int left = (int) ((double)startMinutes / 1440.0 * width);
    	int right = (int) ((double)endMinutes / 1440.0 * width);
    	
    	if (minDegrees > 0)
    	{
    		int mid = height - (int) ((minDegrees - minTemp) / (maxTemp - minTemp) * height);
    		canvas.drawRect(left, mid, right, height, heatPaint);    		
    	}
    	if (maxDegrees > 0)
    	{
    		int mid = height - (int) ((maxDegrees - minTemp) / (maxTemp - minTemp) * height);
    		canvas.drawRect(left, 0, right, mid, coolPaint);
    	}
    	
    }
    

    private void drawLabels()
    {
    	int halfX = width / 2;
    	int quarterX = width / 4;
    	int threeQuarterX = halfX + quarterX;
    	
    	Paint linePaint = new Paint();
    	linePaint.setARGB(100, 255, 255, 255);
    	canvas.drawLine(0, 0, 0, height, linePaint);
    	
    	canvas.drawLine(width-2, 0, width-2, height, linePaint);
    	canvas.drawLine(quarterX, 0, quarterX, height, linePaint);
    	canvas.drawLine(halfX, 0, halfX, height, linePaint);
    	canvas.drawLine(threeQuarterX, 0, threeQuarterX, height, linePaint);
    	
    	Paint textPaint = new Paint();
    	textPaint.setARGB(100, 255, 255, 255);
    	textPaint.setTextSize(20);
    	canvas.drawText("12a", 0, 18, textPaint);
    	canvas.drawText("6a", quarterX, 18, textPaint);
    	canvas.drawText("12p", halfX, 18, textPaint);
    	canvas.drawText("6p", threeQuarterX, 18, textPaint);
    }
    
    private void drawNow()
    {
    	int minutes = new Date().getHours() * 60 + new Date().getMinutes();
    	double percent = minutes / 1440.0;
    	int x = (int) (percent * width);
    	int y = (int) height / 2;
    	
    	if (Conditions.getCurrent().getInsideTemperature()>0)
    	{
    		y = (int) ((Conditions.getCurrent().getInsideTemperature() - minTemp) / (maxTemp - minTemp) * height);
    	}
    	
    	Paint p = new Paint();
    	p.setARGB(200, 255, 255, 255);
    	canvas.drawCircle(x, y, 5, p);
    	
    }
    
    public void refresh()
    {
    	init();
    	this.invalidate();
    }
    
    public void draw()
    {
		width = canvas.getClipBounds().right;
		height = canvas.getClipBounds().bottom;
    	canvas.drawColor(Color.BLACK);
    	drawData();
    	drawLabels();
    	drawNow();
    }
    
    @Override
    public void draw(Canvas canvas) 
    {
    	this.canvas = canvas;
    	draw();
    }
    
    
    class HomeScheduleData
    {
    	public int minutes = 0;
    	public double minDegrees = 0;
    	public double maxDegrees = 0;
    	
    	private HomeScheduleData(int hour, int minute, String mode, int targetLow, int targetHigh)
        {
        	this.minutes = hour * 60 + minute;
        	if (mode.equals("Heat"))
        	{
        		this.minDegrees = targetLow;
        	} else if (mode.equals("Cool"))
        	{
        		this.maxDegrees = targetHigh;
        	} else if (mode.equals("Auto"))
        	{
        		this.minDegrees = targetLow;
        		this.maxDegrees = targetHigh;
        	}
        }
    }
    
    
}