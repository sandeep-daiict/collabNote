package com.example.server;

import java.net.InetAddress;
import android.net.wifi.WifiManager;


public class Communicator 
{
	
	public static void reply (WifiManager wifi, InetAddress toIP, boolean mode, String data)
	{
		Response response = new Response (wifi, toIP, mode, data);
		new DiscoverClient (response).start ();
	}
}
