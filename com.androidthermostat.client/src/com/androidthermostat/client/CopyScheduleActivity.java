package com.androidthermostat.client;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.actionbarsherlock.app.SherlockActivity;
import com.androidthermostat.client.data.Schedule;
import com.androidthermostat.client.data.Schedules;


public class CopyScheduleActivity extends SherlockActivity {

	int dayOfWeek;
	int scheduleIndex;
	
	CheckBox sundayCheck;
	CheckBox mondayCheck;
	CheckBox tuesdayCheck;
	CheckBox wednesdayCheck;
	CheckBox thursdayCheck;
	CheckBox fridayCheck;
	CheckBox saturdayCheck;
	Button copyButton;
	Button cancelButton;
	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    setContentView(R.layout.copy_schedule);

	    
	    
	    Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			scheduleIndex = extras.getInt("SCHEDULE_INDEX");
			dayOfWeek = extras.getInt("DAY_OF_WEEK");
		}
		
		
		sundayCheck = (CheckBox) findViewById(R.id.sundayCheck);
		mondayCheck = (CheckBox) findViewById(R.id.mondayCheck);
		tuesdayCheck = (CheckBox) findViewById(R.id.tuesdayCheck);
		wednesdayCheck = (CheckBox) findViewById(R.id.wednesdayCheck);
		thursdayCheck = (CheckBox) findViewById(R.id.thursdayCheck);
		fridayCheck = (CheckBox) findViewById(R.id.fridayCheck);
		saturdayCheck = (CheckBox) findViewById(R.id.saturdayCheck);
		copyButton = (Button) findViewById(R.id.copyButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		
		copyButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {copy();}});
		cancelButton.setOnClickListener(new View.OnClickListener() {public void onClick(View view) {cancel();}});
		
		
		switch (dayOfWeek)
		{
			case 1:
				sundayCheck.setVisibility(View.GONE);
				break;
			case 2:
				mondayCheck.setVisibility(View.GONE);
				break;
			case 3:
				tuesdayCheck.setVisibility(View.GONE);
				break;
			case 4:
				wednesdayCheck.setVisibility(View.GONE);
				break;
			case 5:
				thursdayCheck.setVisibility(View.GONE);
				break;
			case 6:
				fridayCheck.setVisibility(View.GONE);
				break;
			case 7:
				saturdayCheck.setVisibility(View.GONE);
				break;
		}
	}
	
	private void copy()
	{
		Builder d = new AlertDialog.Builder(this);
		d.setIcon(android.R.drawable.ic_dialog_alert);
		d.setTitle("Copy Day");
        d.setMessage("Are you sure you wish to copy today's schedule to these days?  It will override any existing schedules on those days.");
        d.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	performCopy();
                CopyScheduleActivity.this.finish();    
            }

        });
        d.setNegativeButton("No", null);
        d.show();
	}
	
	private void cancel() 
	{
		this.finish();
	}
	
	private void performCopy()
	{
		Schedule s = Schedules.getCurrent().get(scheduleIndex);
		if (sundayCheck.isChecked()) s.copyDay(dayOfWeek, 1);
		if (mondayCheck.isChecked()) s.copyDay(dayOfWeek, 2);
		if (tuesdayCheck.isChecked()) s.copyDay(dayOfWeek, 3);
		if (wednesdayCheck.isChecked()) s.copyDay(dayOfWeek, 4);
		if (thursdayCheck.isChecked()) s.copyDay(dayOfWeek, 5);
		if (fridayCheck.isChecked()) s.copyDay(dayOfWeek, 6);
		if (saturdayCheck.isChecked()) s.copyDay(dayOfWeek, 7);
	}
	
	
}

