package com.androidthermostat.server.web;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ByteArrayEntity;
import android.content.Context;

import com.androidthermostat.server.R;

public class Static {
	

	public static void handleGet(String url, String[] params, HttpResponse response, Context context) throws HttpException, IOException
    {
		int resource = 0;
		if (url.contains("/img/"))
		{
			//Weather icons provided for free for non-commercial use by http://iconbest.com/2009/01/07/waether-iconset/
			if (url.equals("/img/01d.png")) resource = R.drawable.i01d;
			else if (url.equals("/img/02d.png")) resource = R.drawable.i02d;
			else if (url.equals("/img/03d.png")) resource = R.drawable.i03d;
			else if (url.equals("/img/04d.png")) resource = R.drawable.i04d;
			else if (url.equals("/img/09d.png")) resource = R.drawable.i09d;
			else if (url.equals("/img/10d.png")) resource = R.drawable.i10d;
			else if (url.equals("/img/11d.png")) resource = R.drawable.i11d;
			else if (url.equals("/img/13d.png")) resource = R.drawable.i13d;
			else if (url.equals("/img/50d.png")) resource = R.drawable.i50d;
			else if (url.equals("/img/01n.png")) resource = R.drawable.i01n;
			else if (url.equals("/img/02n.png")) resource = R.drawable.i02n;
			else if (url.equals("/img/03n.png")) resource = R.drawable.i03n;
			else if (url.equals("/img/04n.png")) resource = R.drawable.i04n;
			else if (url.equals("/img/09n.png")) resource = R.drawable.i09n;
			else if (url.equals("/img/10n.png")) resource = R.drawable.i10n;
			else if (url.equals("/img/11n.png")) resource = R.drawable.i11n;
			else if (url.equals("/img/13n.png")) resource = R.drawable.i13n;
			else if (url.equals("/img/50n.png")) resource = R.drawable.i50n;
			
			if (resource > 0)
			{
				byte[] bytes = com.androidthermostat.server.utils.Utils.readResourceBytes(context, resource);
				ByteArrayEntity entity = new ByteArrayEntity(bytes);
				entity.setContentType("image/png");
				response.setEntity(entity);
			}
		} else 
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
	
}
