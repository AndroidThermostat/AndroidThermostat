package com.androidthermostat.client.data;

public class Server {

	private String ipAddress = "127.0.0.1";
	private String name = "Localhost";
	private String password = "";
	private int port = 8080;
	
	public String getBaseUrl() { return "http://" + ipAddress + ":" + String.valueOf(port); }
	public String getBaseParams() { return "?password=" + password; }
	public String getDisplayName() { return name + " (" + ipAddress + ")"; }
	
	
	

	public String getIpAddress() { return ipAddress; }
	public String getName() { return name; }
	public String getPassword() { return password; }
	public int getPort() { return port; }
	
	public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
	public void setName(String name) { this.name = name; }
	public void setPassword(String password) { this.password = password; }
	public void setPort(int port) { this.port = port; }
	
}
