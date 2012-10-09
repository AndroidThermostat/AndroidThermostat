package com.androidthermostat.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockFragmentActivity {

	Fragment currentFragment;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.settings);
		
		Bundle extras = getIntent().getExtras(); 
		switchTab(new GeneralSettingsFragment());
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.settings, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {

		switch (menu.getItemId())
		{
			case R.id.menu_general:
				switchTab(new GeneralSettingsFragment());
				break;
			case R.id.menu_usage:
				switchTab(new UsageSettingsFragment());
				break;
			case R.id.menu_furnace:
				switchTab(new FurnaceSettingsFragment());
				break;
		}
		return super.onOptionsItemSelected(menu);
	}
	
	
	
	
	private void switchTab(Fragment fragment)
	{
		currentFragment = fragment;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentHolder, fragment);
		transaction.commit();
		
	}
	
	
}
