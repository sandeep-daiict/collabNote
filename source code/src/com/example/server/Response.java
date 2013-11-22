package com.example.server;

import java.net.InetAddress;

import android.net.wifi.WifiManager;

public class Response 
{
	private WifiManager mWifi;
	private InetAddress ip;
	private boolean mode;
	private String data;
	
	public Response (WifiManager wifi, InetAddress ip, boolean mode, String data) 
	{
		super ();
		this.mWifi = wifi;
		this.ip = ip;
		this.mode = mode;
		this.setData (data);
	}
	public WifiManager getmWifi () {
		return mWifi;
	}
	public void setmWifi (WifiManager mWifi) {
		this.mWifi = mWifi;
	}
	public InetAddress getIp () {
		return ip;
	}
	public void setIp (InetAddress ip) {
		this.ip = ip;
	}
	public boolean isMode () {
		return mode;
	}
	public void setMode (boolean mode) {
		this.mode = mode;
	}
	public String getData () {
		return data;
	}
	public void setData (String data) {
		this.data = data;
	}
	
	

}
