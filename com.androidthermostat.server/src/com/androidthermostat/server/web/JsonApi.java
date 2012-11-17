package com.androidthermostat.server.web;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

import android.content.Context;

import com.androidthermostat.server.data.Api;
import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Schedules;
import com.androidthermostat.server.data.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonApi {

	public static void handleGet(String url, String[] params, HttpResponse response, Context context) throws HttpException, IOException
    {
		try {
	    	response.setStatusCode(HttpStatus.SC_OK);
	        
	    	Gson gson = new Gson();
	    	
	    	String json="";
	    	if (Utils.verifyPassword(params)) {
	        	if (url.equals("/api/settings")) {
	        		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	        		json = gson.toJson(Settings.getCurrent());
	        	} else if (url.equals("/api/schedules")) {
	        		json = gson.toJson(Schedules.getCurrent());
	        	} else if (url.equals("/api/conditions")) {
	        		Conditions.getCurrent().getState(); // update the mode field.
	        		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	        		json = gson.toJson(Conditions.getCurrent());
	        	} else if (url.equals("/api")) {
	        		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	        		json = gson.toJson(Api.getCurrent());
	        	}
	    	} else {
	    		json = "{\"error\":\"Invalid password\"}";
	    	}
	    	
	    	StringEntity body = new StringEntity(json);
	        body.setContentType("application/json");
	        response.setEntity(body); 
	    } catch (Exception ex) {
			com.androidthermostat.server.utils.Utils.debugText = "Error handling get to " + url;
		}
    }
    
    public static void handlePost(String url, String[] params, String json, HttpResponse response, Context context) throws HttpException, IOException
    {
    	try{
	    	response.setStatusCode(HttpStatus.SC_OK);
	    	String output = "[]";
	    	
	    	if (Utils.verifyPassword(params)) {
	        	if (url.equals("/api/settings")) {
	        		Settings.load(json);
	        		Settings.getCurrent().save(context);
	        	} else if (url.equals("/api/conditions")) {
	        		//can't change conditions remotely
	        	} else if (url.equals("/api/schedules")) {
	        		Schedules.load(json);
	        		Schedules.getCurrent().save(context);
	        	}
	        } else {
	    		output = "{\"error\":\"Invalid password\"}";
	    	}
	    	
	    	StringEntity body = new StringEntity("output");
	        body.setContentType("application/json");
	        response.setEntity(body); 
    	} catch (Exception ex) {
    		com.androidthermostat.server.utils.Utils.debugText = "Error handling post to " + url;
    	}
    }
    

	
}
