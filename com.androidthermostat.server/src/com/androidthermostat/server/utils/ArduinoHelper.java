package com.androidthermostat.server.utils;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.os.Handler;

public class ArduinoHelper {
	/*
	private static final int VID = 0x2341;
	private static final int PID = 0x0001;
	private static UsbController sUsbController;
	private Activity activity;
	
	
	Handler queueHandler;
	
	private static ArrayList<String> commandQueue;
	
	
	
	public void toggleFan(boolean on)
	{
		String command="f0";
		if (on) command="f1";
		queueCommand(command);
	}
	
	public void toggleHeat(boolean on)
	{
		String command="h0";
		if (on) command="h1";
		queueCommand(command);
	}
	
	public void toggleCool(boolean on)
	{
		String command="c0";
		if (on) command="c1";
		queueCommand(command);
	}
	
	public void init(Activity activity)
	{
		this.activity = activity;
		if (sUsbController == null) 
		{
			sUsbController = new UsbController(this.activity, mConnectionHandler, VID, PID);
		} else {
			sUsbController.stop();
			sUsbController = new UsbController(this.activity, mConnectionHandler, VID, PID);
		}
		
		commandQueue = new ArrayList<String>();
		queueHandler = new Handler();
		queueHandler.postDelayed(queueRunnable, 1000);
	}
	
	
	
	private int sendCommand(String command)
	{
		sUsbController.send(command + "\n");
		return sUsbController.read();
	}
	
	private void queueCommand(String command)
	{
		commandQueue.add(command);
	}
	
	private void processQueue()
	{
		if (commandQueue.size()>0)
		{
			String command=commandQueue.get(0);
			commandQueue.remove(0);
			sendCommand(command);
		}
	}
	
	public double getTemperature()
	{
		double result = 0;
		int tempRaw = sendCommand("t");
		try{
			result = ((double)tempRaw) / 2.0;
		} catch (Exception e) {}
		return result;
	}
	
	private final IUsbConnectionHandler mConnectionHandler = new IUsbConnectionHandler() {
		@Override
		public void onUsbStopped() {
			//L.e("Usb stopped!");
		}
		
		@Override
		public void onErrorLooperRunningAlready() {
			//L.e("Looper already running!");
		}
		
		@Override
		public void onDeviceNotFound() {
			if(sUsbController != null){
				sUsbController.stop();
				sUsbController = null;
			}
		}
	};
	
	private Runnable queueRunnable = new Runnable() {
	   public void run() {
		   processQueue();
		   queueHandler.postDelayed(this, 100);
	    }
	};
	*/
}
