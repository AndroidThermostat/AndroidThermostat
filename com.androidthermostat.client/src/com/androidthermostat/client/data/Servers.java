package com.androidthermostat.client.data;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Servers extends ArrayList<Server> {

	private static Servers current;
	private Server selectedServer;
	
	public Server getSelectedServer() {
		if (selectedServer==null) selectedServer = this.get(0);
		return selectedServer; 
	}
	
	public static Servers getCurrent() {
		if (current==null) current=new Servers();
		if (current.size()==0) current.add(new Server());
		return current; 
	};
	
	
	public void setSelectedServer(Server selectedServer) { this.selectedServer = selectedServer; }
	public static void setCurrent(Servers current) { Servers.current = current; }

	

	
	public static String getBaseUrl() { return Servers.getCurrent().getSelectedServer().getBaseUrl(); }
	
	public String[] toStringArray()
	{
		String[] result = new String[this.size()];
		for (int i=0; i<this.size(); i++)
		{
			result[i] = this.get(i).getDisplayName();
		}
		return result;
	}

	
	
	public void save(Activity activity)
	{
		Gson gson = new Gson();
		String json = gson.toJson(this);
		
		SharedPreferences prefs = activity.getSharedPreferences("servers", 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString("json", json);
	    editor.commit();
	}
	
	public static void load(Activity activity)
	{
		SharedPreferences prefs = activity.getSharedPreferences("servers", 0);
	    String json = prefs.getString("json","");
	    if (!json.equals(""))
	    {
	    	Gson gson = new Gson();
	    	current = gson.fromJson(json, Servers.class);
	    }
	}

	
}
