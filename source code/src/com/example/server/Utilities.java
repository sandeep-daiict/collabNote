package com.example.server;

import android.annotation.SuppressLint;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SuppressLint("DefaultLocale")
public class Utilities 
{
	public static String toIPString (int ip)
	{
		String ipString = String.format (
	    		   "%d.%d.%d.%d",
	    		   (ip & 0xff),
	    		   (ip >> 8 & 0xff),
	    		   (ip >> 16 & 0xff),
	    		   (ip >> 24 & 0xff));
		return ipString;
	}
	
	public static InetAddress toInetAddress (int ip)
	{
		String ipString = toIPString (ip);
		System.out.println("IP 123 : " + ipString);
		return toInetAddress (ipString);
	}
	
	public static InetAddress toInetAddress (String ip)
	{
		try 
		{
			System.out.println("IP is :"+ip);
			return InetAddress.getByName (ip);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
