package com.androidthermostat.server.web;

import com.androidthermostat.server.data.Settings;

public class Utils {


    public static boolean verifyPassword(String[] params)
    {
    	String password = getParamValue(params, "password");
    	if (password.equals(Settings.getCurrent().getPassword())) return true; else return false;
    }
    
    public static String getParamValue(String[] params, String paramName)
    {
    	String result = "";
    	for (String param : params)
    	{
    		if (param.contains(paramName + "="))
    		{
    			result = param.replace(paramName + "=", "");
    		}
    	}
    	return result;
    }
	
}
