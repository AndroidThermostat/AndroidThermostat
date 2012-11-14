package com.androidthermostat.client.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.androidthermostat.client.R;

public class HelpControl extends LinearLayout {

	Context context;
	ImageButton helpButton;
	String message="";
	String title="";
	
	
	
	public HelpControl(Context context) 
	{
		super(context);
		this.context = context;
		loadViews();
	}
	
	public HelpControl(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.context = context;
		TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.HelpControl);
		title = a.getString(R.styleable.HelpControl_title_id);
		message = a.getString(R.styleable.HelpControl_message_id);
		loadViews();
	}
	
	private void loadViews()
	{	
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.help_control, this);
		helpButton = (ImageButton) this.findViewById(R.id.helpButton);
		helpButton.setOnClickListener(new OnClickListener() {  public void onClick(View v) { showHelp(); } });
	}
	

	
	private void showHelp()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton("OK", null);
		builder.create().show();
	}
}
