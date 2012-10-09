package com.androidthermostat.server;

import com.androidthermostat.server.utils.IOIOHelper;

import android.content.Intent;
import android.os.IBinder;
import ioio.lib.util.android.IOIOService;

public class IOIOServiceHelper extends IOIOService {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected IOIOHelper createIOIOLooper() {
		IOIOHelper.setCurrent(new IOIOHelper());
		return IOIOHelper.getCurrent();
	}

}
