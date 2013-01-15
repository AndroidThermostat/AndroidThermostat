package com.androidthermostat.server.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.res.Resources;

import com.androidthermostat.server.MainService;
import com.androidthermostat.server.data.Conditions;

public class Utils {
	
	private static String debugText = "";
	
	public static String getLastLogMessage() { return debugText; }
	
	public static void logError(String message, String method)
	{
		debugText = message;
		writeLogEntry(message, method, true);
	}
	
	public static void logInfo(String message, String method)
	{
		debugText = message;
		writeLogEntry(message, method, false);
	}
	
	public static void clearLogFile()
	{
		try {
			MainService.getContext().deleteFile("log.txt");
		} catch (Exception e) { debugText = e.toString(); }
	}
	
	public static String readLogFile()
	{
		try {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(MainService.getContext().openFileInput("log.txt")));
		    String inputString;
		    StringBuffer stringBuffer = new StringBuffer();                
		    while ((inputString = inputReader.readLine()) != null) {
		        stringBuffer.append(inputString + "\n");
		    }
		    return stringBuffer.toString();
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	private static void writeLogEntry(String message, String method, boolean error)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		String output = dateFormat.format(date);
		if (error) output += " Error:";
		output += " " + method + " - " + message + "\n";
		
		try {
			FileOutputStream fos = MainService.getContext().openFileOutput("log.txt", Context.MODE_APPEND);
			fos.write(output.getBytes());
			fos.close();
		} catch (Exception e) { debugText = e.toString(); }
	}
	

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
	
	public static byte[] readResourceBytes(Context context, int resourceId)
	{
		byte[] b = null;
        try {
    		Resources res = context.getResources();
            InputStream in_s = res.openRawResource(resourceId);
        	b = new byte[in_s.available()];
        	in_s.read(b);
        	in_s.close();
        } catch (IOException e) { }
        return b;	
	}
	
	public static String readRawResource(Context context, int resourceId)
	{
		String result = "";
	
        try {
    		Resources res = context.getResources();
            InputStream in_s = res.openRawResource(resourceId);
        	byte[] b = new byte[in_s.available()];
        	in_s.read(b);
        	in_s.close();
        	result = new String(b);
        } catch (IOException e) { }
        
        return result;
	}
	
	
}
