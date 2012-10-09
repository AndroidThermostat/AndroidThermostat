package com.androidthermostat.server.utils;

public interface IUsbConnectionHandler {
	void onUsbStopped();
	void onErrorLooperRunningAlready();
	void onDeviceNotFound();
}

