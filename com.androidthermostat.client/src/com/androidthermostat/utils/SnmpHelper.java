package com.androidthermostat.utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.androidthermostat.client.data.Server;
import com.androidthermostat.client.data.Servers;

public class SnmpHelper {

	
	/*
	public static String getBroadcastAddress(Context context)
	{
		InetAddress result = null;
		try{
		    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    // handle null somehow
	
		    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		        quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		    result = InetAddress.getByAddress(quads);
		} catch (IOException ex) {
			Utils.debugText = "SnmpHelper.getBroadcastAddress - " + ex.toString();
		}
		return result.toString();
	}
	*/
	
	public static void discoverDevices(Activity activity, Servers servers)
	{
		WifiManager wm = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE); 
		WifiManager.MulticastLock multicastLock = wm.createMulticastLock("androidthermostat"); 
		multicastLock.acquire(); 
	    
		Utils.debugText = "multicast lock acquired";
		MulticastSocket ms=null;
		
		InetAddress sessAddr=null;
	    try{
	    	sessAddr = InetAddress.getByName("239.255.255.250");
			ms = new MulticastSocket(1900);
			ms.joinGroup(sessAddr);
			
			
			byte[] message = new byte[1024];
			message = "Greetings from Android Thermostat Client".getBytes();
			DatagramPacket dp = new DatagramPacket(message, message.length,sessAddr,1900);
			ms.send(dp);
			
			long endTime=System.currentTimeMillis() + 5000;
			
			byte[] buffer = new byte[10*1024];
			DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);
			while( System.currentTimeMillis() < endTime )
			{
				
				ms.receive(dpIn);
				String response = new String(buffer, 0, dpIn.getLength());
				if (response.contains("Greetings from Android Thermostat Server @"))
				{
					Utils.debugText = response;
					
					String data = response.replace("Greetings from Android Thermostat Server @", "");
					String[] dataParts = data.split("/");
					String ipAddress = dataParts[0];
					String name = dataParts[1];
					
					Server s = new Server();
					s.setIpAddress(ipAddress);
					s.setName(name);
					servers.add(s);
				}
			}

	    }
	    catch (Exception ex) {
	    	Utils.debugText = "MulticastListener.listen - " + ex.toString();
	    }
	    
	    
	    try{
	    	if (sessAddr!=null) ms.leaveGroup(sessAddr);
	    } catch (Exception ex) {}
	    
	    multicastLock.release();
	    
	    //Utils.debugText = "multicast lock released";
   
	}
	
	/*
	public static ArrayList<String> discoverDevices()
	{
		
		ArrayList<String> result = new ArrayList<String>();
		
		/**
		 * SSDP is a text-based protocol based on the Hypertext Transfer Protocol (RFC 2616). 
		 * However, it uses the User Datagram Protocol (UDP) as underlying transport protocol.
		 *  Services are announced by the hosting system with multicast addressing to a 
		 *  specifically designated IP multicast address at port number 1900. In IPv4, 
		 *  the multicast address is 239.255.255.250.
	
		
		
		DatagramSocket clientSocket = null;
		
		try {
			clientSocket = new DatagramSocket(8888); 
			clientSocket.setSoTimeout(30000);
		} catch (SocketException ex)
		{
			result.add("Socket exception");
		}
		
		//getByName(host)   //host  the hostName to be resolved to an address or null.
		InetAddress group = null;
		try {
			group = InetAddress.getByName("239.255.255.250");
		} catch (UnknownHostException ex) {
			result.add("Unknown host - " + ex.toString() + "...");
		}

		//host can be null which means that an address of the loopback interface is returned.
		if(group == null){
		    result.add("Group was null");
		}
		byte[] sendData;
		byte[] receiveData = new byte[128];

		//+ "HOST: " + broadcastAddress + ":1900\r\n"
	    //+ "HOST: 239.255.255.250:1900\r\n"
		
		String sentence = "M-SEARCH * HTTP/1.1\r\n"
			+ "HOST: 239.255.255.250:1900\r\n"
		    + "MAN: \"ssdp:discover\"\r\n"
		    + "MX: 10\r\n"
		    + "ST: ssdp:all\r\n"
		    + "\r\n";

		sendData = sentence.getBytes();
		//public DatagramPacket (byte[] data, int length, InetAddress host, int port) 
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, 1900);

		try {
		    clientSocket.send(sendPacket);
		} catch (Exception e) {
			result.add("send failed");
		    e.getMessage();
		    e.printStackTrace();
		}
		result.add("sent packet...");
		boolean isc = true;
		
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.SECOND, 10);
		
		while( new Date().getTime() < endTime.getTime().getTime() )
		{
		    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		    try
		    {
		        isc = clientSocket.isConnected();
		        if (isc) result.add("Socket was connected");
		        clientSocket.receive(receivePacket);
		    }
		    catch ( Exception Ex)
		    {
		        result.add("Time out Exception");
		    }
		    if (receivePacket.getAddress() == null)
		    {
		        result.add("receivePacket.getAddress() == null");
		        break;
		    }
		    result.add("Senders Address : " + receivePacket.getAddress().getHostAddress());
		    String controllerResponse = new String(receivePacket.getData());    
		    result.add(controllerResponse);
		} //end of while()
		clientSocket.close();
		return result;
	}
*/	
	
}
