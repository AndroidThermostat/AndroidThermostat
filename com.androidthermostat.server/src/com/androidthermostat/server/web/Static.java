package com.androidthermostat.server.web;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import android.content.Context;

import com.androidthermostat.server.R;

public class Static {
	

	public static void handleGet(String url, String[] params, HttpResponse response, Context context) throws HttpException, IOException
    {
		String reply="";
		String contentType="text/html";
		
		if (url.equals("") || url.equals("/")) reply = com.androidthermostat.server.utils.Utils.readRawResource(context, R.raw.webserver_html);
		else if (url.equals("/main.js")) {
			reply = com.androidthermostat.server.utils.Utils.readRawResource(context, R.raw.webserver_js);
			contentType="application/javascript";
		}
		else if (url.equals("/main.css")) {
			reply = com.androidthermostat.server.utils.Utils.readRawResource(context, R.raw.webserver_css);
			contentType="text/css";
		}
		else if (url.equals("/log.txt")) {
			reply = com.androidthermostat.server.utils.Utils.readLogFile();
			contentType="text/text";
		}
		
		
		StringEntity body = new StringEntity(reply);
        body.setContentType(contentType);
        response.setEntity(body); 
		
    }
	
}
