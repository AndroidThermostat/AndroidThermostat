package com.androidthermostat.client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import com.androidthermostat.client.data.Settings;


public class ActivityBase extends Activity {
	public static final int FIRST_MENU_ITEM = Menu.FIRST + 4;
	
	//private static final int SETTEMP_ID = Menu.FIRST;
	//private static final int SCHEDULE_ID = Menu.FIRST + 1;
	//private static final int SETTINGS_ID = Menu.FIRST + 2;
	//private static final int SELECT_SERVER_ID = Menu.FIRST + 3;
	//private static final int VIEW_STATS_ID = Menu.FIRST + 4;
	
	
	//private static final int ACTIVITY_SETTEMP=100;
	//private static final int ACTIVITY_SCHEDULE=101;
	//private static final int ACTIVITY_SETTINGS=102;
	//private static final int ACTIVITY_SELECT_SERVER=103;
	//private static final int ACTIVITY_VIEW_STATS=104;
	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        //String viewStatsParams = Settings.getCurrent().getViewStatsParams();
        
        //menu.add(0, SETTEMP_ID, 0, "Set Temperature");
        //menu.add(0, SCHEDULE_ID, 0, "Schedule");
        //menu.add(0, SETTINGS_ID, 0, "Settings");
        //menu.add(0, SELECT_SERVER_ID, 0, "Select Server");
        //if (viewStatsParams!=null && viewStatsParams != "") 
        //menu.add(0, VIEW_STATS_ID, 0, "View Usage");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()) {
    	/*
    		case SETTEMP_ID:
	        	setTemperature();
	        	break;*/
    	/*
	        case SETTINGS_ID:
	        	editSettings();
	        	break;*/
    	/*
	        case SCHEDULE_ID:
	        	editSchedule();
	        	break;
	        	*/
    	/*
	        case SELECT_SERVER_ID:
	        	selectServer();
	        	break;*/
	        	/*
	        case VIEW_STATS_ID:
	        	viewStats();
	        	break;*/
	    }
        return super.onMenuItemSelected(featureId, item);
    }
    
    /*
    private void editSettings()
    {
    	Intent i = new Intent(this, SettingsActivity.class);
		startActivityForResult(i, ACTIVITY_SETTINGS);
    }*/
    /*
    private void editSchedule()
    {
    	Intent i = new Intent(this, SchedulesActivity.class);
		startActivityForResult(i, ACTIVITY_SCHEDULE);
    }
    */
    /*
    private void setTemperature()
    {
    	Intent i = new Intent(this, SetTemperatureActivity.class);
		startActivityForResult(i, ACTIVITY_SETTEMP);
    }*/
    
    /*
    private void selectServer()
    {
    	Intent i = new Intent(this, ServerSelectActivity.class);
		startActivityForResult(i, ACTIVITY_SELECT_SERVER);
		
    	//Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.androidthermostat.remote"));
    	//startActivityForResult(i, ACTIVITY_RATE_APP);
    }*/
    
    /*
    private void viewStats()
    {
    	try{
    		String url = Settings.getCurrent().getPingOutUrl() + Settings.getCurrent().getViewStatsParams();
    		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    		startActivityForResult(i, ACTIVITY_VIEW_STATS);
    	} catch (Exception e) {}
    }
	*/
}