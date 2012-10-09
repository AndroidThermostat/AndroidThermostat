package com.androidthermostat.client;

import android.app.AlertDialog;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
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
import com.androidthermostat.utils.Utils;

public class ServerSelectActivity extends SherlockActivity {

	
	EditText serverIp;
	Button scanButton;
	Button connectButton;
	Handler refreshHandler=null;
	ListView serverList;

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
		
		if (availableServers!=null)
		{
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, availableServers);
		    serverList.setAdapter(adapter);
		}	
	}
	

	private void connectClick()
	{
		//Utils.setRemoteServer("http://" + serverIp.getText().toString() + ":8080");
		
		Server s = new Server();
		s.setIpAddress(serverIp.getText().toString());
		s.setName("Manual");
		Servers.getCurrent().setSelectedServer(s);
		connect();
	}
	
	private void connect()
	{
		new Thread(new Runnable() {
		    public void run() {
		    	Servers.getCurrent().save(ServerSelectActivity.this);
		    	Schedules.load();
		    	Settings.load();
		    	Conditions.getCurrent().load();
		    }
		  }).start();

		finish();
	}
	
	
	private Runnable refreshRunnable = new Runnable() {
	   public void run() {
		   updateScreen();
		   refreshHandler.postDelayed(this, 1000);
	    }
	};
	
}
