package com.androidthermostat.server.utils;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

import com.androidthermostat.server.data.Settings;

public class IOIOHelper extends BaseIOIOLooper {

	AnalogInput tempIn;
	private DigitalOutput fan;
	private DigitalOutput heat;
	private DigitalOutput cool;
	private DigitalOutput heartbeat;
	private boolean heartbeatVal = false;
	

	private static IOIOHelper current;
	public static IOIOHelper getCurrent() { return current; }
	public static void setCurrent(IOIOHelper value) { current = value; }
	private int repeatSamples = 0;
	private double lastSample = 0;
	
	
	public void toggleFan(boolean on) { 
		try { fan.write(on); } catch (Exception e) {  Utils.logError(e.toString(), "utils.IOIOHelper.toggleFan");  }
	}
	
	public void toggleHeat(boolean on)
	{
		try { heat.write(on); } catch (Exception e) {  Utils.logError(e.toString(), "utils.IOIOHelper.toggleHeat");  }
	}
	
	public void toggleCool(boolean on)
	{
		try { cool.write(on); } catch (Exception e) { Utils.logError(e.toString(), "utils.IOIOHelper.toggleCool"); }
	}
	
	
	//The IOIO appears to stop returning new samples after a while.  This is a temp patch to reset the IOIO when this happens.
	private void checkReset(double volts)
	{
		if (volts == lastSample) repeatSamples++; else repeatSamples=0;
		lastSample = volts;
		if (repeatSamples>300) { 
			reset();
			repeatSamples = 0;
		}
	}
	
	private void reset()
	{
		Utils.logInfo("Resetting IOIO", "utils.IOIOHelper.reset");
		try{
			tempIn=null;
			ioio_.hardReset();
		} catch (Exception e) {
			Utils.logError(e.toString(), "utils.IOIOHelper.reset");
			resetRecover();
		}
	}
	
	//If reset throws Connection Lost Exception.  Wait and try again.
	private void resetRecover()
	{
		try {
			ioio_.waitForConnect();
			reset();
		} catch (Exception e) {
			Utils.logError(e.toString(), "utils.IOIOHelper.resetRecover");
		}
	}
	
		
	public double getTemperature()
	{
		if (tempIn!=null)
		{
			try{
				heartbeat.write(heartbeatVal);
				heartbeatVal = !heartbeatVal;
				
				
				double averageVolts = tempIn.getVoltage();
				checkReset(averageVolts);
	//			if (new Date().getSeconds()==0)	Utils.logInfo("Average volts - " + String.valueOf(averageVolts), "utils.IOIOHelper.getTemperature");
				double mv = averageVolts * 1000.0;
				double c = (mv - 500.0)/10.0;
				double f = Math.round( (c * 9.0 / 5.0 + 32.0) * 10.0 ) / 10.0;
				return f;
			} catch (ConnectionLostException e) {
				Utils.logError("Connection Lost", "utils.IOIOHelper.getTemperature");
				this.reset();
			} catch (Exception e) {
				Utils.logError(e.toString(), "utils.IOIOHelper.getTemperature");
			}
		}
		return -99;
	}
	
		
		@Override
		protected void setup() {
			Utils.logInfo("Starting IOIO setup", "utils.IOIOHelper.setup");
			try {
				//DigitalOutput led_ = ioio_.openDigitalOutput(0, true);
				
				String hw = Settings.getCurrent().getHardwareRevision();
				if (hw.equals("B"))
				{
					heat = ioio_.openDigitalOutput(5, false);
					fan = ioio_.openDigitalOutput(6, false);
					cool = ioio_.openDigitalOutput(7, false);
					heartbeat = ioio_.openDigitalOutput(4, false);
					tempIn = ioio_.openAnalogInput(43);
					
				} else {
					fan = ioio_.openDigitalOutput(10, false);
					heat = ioio_.openDigitalOutput(12, false);
					cool = ioio_.openDigitalOutput(7, false);
					heartbeat = ioio_.openDigitalOutput(4, false);
					tempIn = ioio_.openAnalogInput(46);
				}
				
				//this.
				//tempIn.setBuffer(5000);
				//tempIn.setBuffer(1);
			} catch (Exception e) {
				Utils.logError(e.toString(), "utils.IOIOHelper.setup");
			}
			
		}
	
		@Override
		public void loop() throws ConnectionLostException {
			try {Thread.sleep(10);} catch (InterruptedException e) {}
		}
	

	

}
