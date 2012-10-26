package com.androidthermostat.server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.androidthermostat.server.data.Conditions;

public class Utils {
	
	public static String debugText = "";

	public static String getUrlContents(String url) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		
		StringBuilder builder = new StringBuilder();
		//HttpClient client = new DefaultHttpClient();
		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) { builder.append(line); }
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	public static int getDayOfWeek(Date d)
	{
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	public static void pingOut(String url)
	{
		if (url==null || url=="") return;
		try {
			if (url.contains("[outsideTemp]")) url = url.replace("[outsideTemp]", String.valueOf(Conditions.getCurrent().getOutsideTemperature()) );
			if (url.contains("[insideTemp]")) url = url.replace("[insideTemp]", String.valueOf(Conditions.getCurrent().getInsideTemperature()) );
			
			Utils.getUrlContents(url);
		} catch (Exception e) {
			Utils.debugText="Utils.pingout - " + e.toString();
		}
	}
	
}
