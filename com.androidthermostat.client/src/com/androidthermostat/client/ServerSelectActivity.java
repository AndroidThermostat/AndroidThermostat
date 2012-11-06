package com.androidthermostat.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.androidthermostat.client.data.Conditions;
import com.androidthermostat.client.data.Schedules;
import com.androidthermostat.client.data.Server;
import com.androidthermostat.client.data.Servers;
import com.androidthermostat.client.data.Settings;
import com.androidthermostat.utils.SnmpHelper;

public class ServerSelectActivity extends SherlockActivity {

	
	EditText serverIp;
	Button scanButton;
	Button connectButton;
	Handler refreshHandler=null;
	ListView serverList;
	String[] previousServers = new String[0];
	
	private static final int MY_PASSWORD_DIALOG_ID = 4;
	

	@Override
    protected Dialog onCreateDialog(int id) {
     
	    // This example shows how to add a custom layout to an AlertDialog
	    LayoutInflater factory = LayoutInflater.from(this);
	    final View textEntryView = factory.inflate(R.layout.dialog_password, null);
	    return new AlertDialog.Builder(ServerSelectActivity.this)
	        .setTitle("Enter Password")
	        .setView(textEntryView)
	        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	EditText passwordText = (EditText) textEntryView.findViewById(R.id.passwordText);
	            	Servers.getCurrent().getSelectedServer().setPassword(passwordText.getText().toString());
	            	connect();
	            }
	        })
	        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        })
	        .create();
    }
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.server_select);
		
		serverList = (ListView) findViewById(R.id.serverList);
		serverIp = (EditText) findViewById(R.id.serverIp);
		scanButton = (Button) findViewById(R.id.scanButton);
		connectButton = (Button) findViewById(R.id.connectButton);
		
		//serverIp.setText(Utils.getRemoteServer().replace("http://", "").replace(":8080",""));
		serverIp.setText(Servers.getCurrent().getSelectedServer().getIpAddress());
		scanButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {scan();}});
		connectButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {connectClick();}});
		
		serverList.setOnItemClickListener(new ListView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	        	Servers.getCurrent().setSelectedServer(Servers.getCurrent().get(position));
	        	connect();
	        }
	    });
		
		updateScreen();
		
	}
	
	
	private void scan()
	{
		android.net.wifi.WifiManager m = (WifiManager) getSystemService(WIFI_SERVICE);
		android.net.wifi.SupplicantState s = m.getConnectionInfo().getSupplicantState();
		NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(s);

		//temp patch: my device perpetually shows obtaining ip address
		if( state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR){
			performScan();
        } else {
        	AlertDialog alertDialog;
        	alertDialog = new AlertDialog.Builder(this).create();
        	alertDialog.setTitle("Not connected to WiFi");
        	alertDialog.setMessage("You must be connected to WiFi to search for devices. " + s.toString());
        	alertDialog.show();
        }
		
	}
	
	private void performScan()
	{
		
		final Servers servers = Servers.getCurrent();
		servers.clear();
		
		//if (refreshHandler==null)
		//{
			refreshHandler= new Handler();
		    refreshHandler.postDelayed(refreshRunnable, 1000);
		//}
        
		new Thread(new Runnable() {
		    public void run() {
		    	SnmpHelper.discoverDevices(ServerSelectActivity.this, servers);
		    }
		  }).start();

	}
	
	private void updateScreen()
	{		
		String[] availableServers = Servers.getCurrent().toStringArray();
		
		//Rebinding is CPU intensive.  Only do it when there's new data.
		if (availableServers!=null && !availableServers.equals(previousServers))
		{
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, availableServers);
		    serverList.setAdapter(adapter);
		    previousServers = availableServers;
		}	
	}
	

	private void connectClick()
	{
		//Utils.setRemoteServer("http://" + serverIp.getText().toString() + ":8080");
		
		Server s = new Server();
		
		String ipAddress = serverIp.getText().toString();
		int port = 8080;
		if (ipAddress.contains(":"))
		{
			String[] parts = ipAddress.split(":");
			ipAddress=parts[0];
			port=Integer.parseInt(parts[1]);
		}
		
		s.setIpAddress(ipAddress);
		s.setPort(port);
		s.setName("Manual");
		
		if (Servers.getCurrent().getByIpPort(s.getIpAddress(), s.getPort()) == null ) Servers.getCurrent().add(s);
		
		Servers.getCurrent().setSelectedServer(s);
		
		connect();
	}
	
	private void connect()
	{
		new Thread(new Runnable() {
		    public void run() {
		    	Servers.getCurrent().save(ServerSelectActivity.this);
		    	
		    	if (Conditions.getCurrent().load())
		    	{
			    	Schedules.load();
			    	Settings.load();
			    	finish();
		    	} else {
		    		
		    	}
		    }
		  }).start();

		showPasswordDialog();
	}
	
	private void showPasswordDialog()
	{
		showDialog(1);
	}
	
	private Runnable refreshRunnable = new Runnable() {
	   public void run() {
		   updateScreen();
		   refreshHandler.postDelayed(this, 1000);
	    }
	};
	
}
