package com.androidthermostat.client.data;

public class Server {

	private String ipAddress = "127.0.0.1";
	private String name = "Localhost";
	
	public String getBaseUrl() { return "http://" + ipAddress + ":8080"; }
	public String getDisplayName() { return name + " (" + ipAddress + ")"; }
	

	public String getIpAddress() { return ipAddress; }
	public String getName() { return name; }

	public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
	public void setName(String name) { this.name = name; }
	
	
}
