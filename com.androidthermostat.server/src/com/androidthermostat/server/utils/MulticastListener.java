package com.androidthermostat.server.utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.androidthermostat.server.data.Settings;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.text.format.Formatter;


public class MulticastListener {

	private static MulticastSocket ms;
	public static String ipAddress = "";
	
	public static void listen(Context context)
	{
		//Utils.debugText = "listening";
		
		//WifiManager wm = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE); 
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		int ip = wm.getConnectionInfo().getIpAddress();
		ipAddress = Formatter.formatIpAddress(ip); 
		
		

		WifiManager.MulticastLock multicastLock = wm.createMulticastLock("androidthermostat"); 
		multicastLock.acquire(); 
		
		
		
	    
		//Utils.debugText = "multicast lock acquired";
		
	    try{
	    	InetAddress sessAddr = InetAddress.getByName("239.255.255.250");
			ms = new MulticastSocket(1900);
			ms.joinGroup(sessAddr);
			
			byte[] buffer = new byte[10*1024];
			DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);
			while (true)
			{
				ms.receive(dpIn);
				String response = new String(buffer, 0, dpIn.getLength());
	            
	            
	            if (response.equals("Greetings from Android Thermostat Client"))
	            {
	            	Utils.debugText="received data - " + response;
		            byte[] message = new byte[1024];
		            String output = "Greetings from Android Thermostat Server @" + ipAddress + "/" + Settings.getCurrent().getName();
					message = output.getBytes();
					DatagramPacket dpOut = new DatagramPacket(message, message.length,sessAddr,1900);
					ms.send(dpOut);
	            }
			}
	    }
	    catch (Exception ex) {
	    	Utils.debugText = "MulticastListener.listen - " + ex.toString();
	    }
	    //ms.leaveGroup(sessAddr);
	    
	}
	
	
	
	
}
