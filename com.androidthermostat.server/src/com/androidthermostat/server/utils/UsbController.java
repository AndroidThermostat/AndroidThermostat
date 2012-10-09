package com.androidthermostat.server.utils;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
/*
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
*/
import android.os.Handler;
import android.util.Log;


public class UsbController {
	/*

	private final Context mApplicationContext;
	private final UsbManager mUsbManager;
	private final IUsbConnectionHandler mConnectionHandler;
	private final int VID;
	private final int PID;
	protected static final String ACTION_USB_PERMISSION = "ch.serverbox.android.USB";

	private UsbRunnable mLoop;
	private Thread mUsbThread;
	private static final Object[] sSendLock = new Object[]{};
	private static final Object[] sReadLock = new Object[]{};
	
	private boolean mStop = false;
	private String outMessage = "";
	private int responseCode = 0;
	public final static String TAG = "USBController";
	private boolean readRequested = false;
	
	public void send(String data)
	{
		outMessage = data;
		synchronized (sSendLock) { sSendLock.notify(); }
	}

	public int read()
	{
		try{Thread.sleep(50);}catch(Exception e){} //Give arduino a chance to respond.		
		

		synchronized (sReadLock) {
			try {
				//Utils.debugText = "blocked by readlock";
				readRequested=true;
				synchronized (sSendLock) { sSendLock.notify(); }
				sReadLock.wait();
			} catch (InterruptedException e) {
				if (mStop) { mConnectionHandler.onUsbStopped(); }
				//e.printStackTrace();
			}
		}
		
		return responseCode;
	}
	

	private class UsbRunnable implements Runnable {
		private final UsbDevice mDevice;
		UsbRunnable(UsbDevice dev) { mDevice = dev; }
	
		@Override
		public void run() {//here the main USB functionality is implemented
			UsbDeviceConnection conn = mUsbManager.openDevice(mDevice);
			if (!conn.claimInterface(mDevice.getInterface(1), true)) { return; }

			// Arduino Serial usb Conv
			conn.controlTransfer(0x21, 34, 0, 0, null, 0, 0);
			conn.controlTransfer(0x21, 32, 0, 0, new byte[] { (byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08 }, 7, 0);
	
			UsbEndpoint epIN = null;
			UsbEndpoint epOUT = null;
	
			UsbInterface usbIf = mDevice.getInterface(1);
			for (int i = 0; i < usbIf.getEndpointCount(); i++) {
				if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
					if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
						epIN = usbIf.getEndpoint(i);
					else
						epOUT = usbIf.getEndpoint(i);
				}
			}
	
			for (;;) {
				synchronized (sSendLock) {
					try {
						//Utils.debugText = "blocked by sendlock";
						sSendLock.wait();
					} catch (InterruptedException e) {
						if (mStop) {
							mConnectionHandler.onUsbStopped();
							return;
						}
						e.printStackTrace();
					}
				}
								

				if (!outMessage.equals(""))
				{
					byte[] byteArray = new byte[0];
					try{
						byteArray = outMessage.getBytes("US-ASCII");
					} catch (Exception ex) {}
					conn.bulkTransfer(epOUT, byteArray, byteArray.length, 0);
					outMessage = "";
				}
				if (readRequested)
				{
					byte[] byteArray = new byte[1];
					try{
						conn.bulkTransfer(epIN, byteArray, 1, 0);
						responseCode = (int)byteArray[0] & 0xFF;
					} catch (Exception ex) {}
					synchronized (sReadLock) { sReadLock.notify(); }
				}
				
	
				if (mStop) {
					mConnectionHandler.onUsbStopped();
					return;
				}
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public UsbController(Activity parentActivity, IUsbConnectionHandler connectionHandler, int vid, int pid) {
		mApplicationContext = parentActivity.getApplicationContext();
		mConnectionHandler = connectionHandler;
		mUsbManager = (UsbManager) mApplicationContext.getSystemService(Context.USB_SERVICE);
		VID = vid;
		PID = pid;
		init();
	}

	private void init() {
		enumerate(new IPermissionListener() {
			@Override
			public void onPermissionDenied(UsbDevice d) {
				UsbManager usbman = (UsbManager) mApplicationContext.getSystemService(Context.USB_SERVICE);
				PendingIntent pi = PendingIntent.getBroadcast(mApplicationContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
				mApplicationContext.registerReceiver(mPermissionReceiver,new IntentFilter(ACTION_USB_PERMISSION));
				usbman.requestPermission(d, pi);
			}
		});
	}

	public void stop() {
		mStop = true;
		synchronized (sSendLock) { sSendLock.notify(); }
		try {
			if(mUsbThread != null) mUsbThread.join();
		} catch (InterruptedException e) { e(e); }
		mStop = false;
		mLoop = null;
		mUsbThread = null;
		try{ 
			mApplicationContext.unregisterReceiver(mPermissionReceiver);
		} catch (IllegalArgumentException e){};
	}

	private void startHandler(UsbDevice d) {
		if (mLoop != null) {
			mConnectionHandler.onErrorLooperRunningAlready();
			return;
		}
		mLoop = new UsbRunnable(d);
		mUsbThread = new Thread(mLoop);
		mUsbThread.start();
	}

	private void enumerate(IPermissionListener listener) {
		l("enumerating");
		HashMap<String, UsbDevice> devlist = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviter = devlist.values().iterator();
		while (deviter.hasNext()) {
			UsbDevice d = deviter.next();
			l("Found device: " + String.format("%04X:%04X", d.getVendorId(), d.getProductId()));
			if (d.getVendorId() == VID && d.getProductId() == PID) {
				l("Device under: " + d.getDeviceName());
				if (!mUsbManager.hasPermission(d)) listener.onPermissionDenied(d);
				else
				{
					startHandler(d);
					return;
				}
				break;
			}
		}
		l("no more devices found");
		mConnectionHandler.onDeviceNotFound();
	}

	private class PermissionReceiver extends BroadcastReceiver {
		private final IPermissionListener mPermissionListener;

		public PermissionReceiver(IPermissionListener permissionListener) { mPermissionListener = permissionListener; }

		@Override
		public void onReceive(Context context, Intent intent) {
			mApplicationContext.unregisterReceiver(this);
			if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
				if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) 
				{
					mPermissionListener.onPermissionDenied((UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE));
				} else {
					l("Permission granted");
					UsbDevice dev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (dev != null) {
						if (dev.getVendorId() == VID && dev.getProductId() == PID) {
							startHandler(dev);// has new thread
						}
					} else {
						e("device not present!");
					}
				}
			}
		}
	}

	private BroadcastReceiver mPermissionReceiver = new PermissionReceiver(
		new IPermissionListener() {
			@Override
			public void onPermissionDenied(UsbDevice d) { l("Permission denied on " + d.getDeviceId()); }
		}
	);

	private static interface IPermissionListener { void onPermissionDenied(UsbDevice d); }

	private void l(Object msg) { Log.d(TAG, ">==< " + msg.toString() + " >==<"); }

	private void e(Object msg) { Log.e(TAG, ">==< " + msg.toString() + " >==<"); }
	*/
}

