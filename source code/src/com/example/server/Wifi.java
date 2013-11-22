package com.example.server;
import java.io.IOException;
import java.net.InetAddress;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
@SuppressWarnings("deprecation")
public class Wifi 
{
	public static InetAddress broadcastip = null;
	public static String selfip = null;
	public static WifiManager wf = null;
	public Wifi(Context c)
	{
		try 
		{
			broadcastip = getBroadcastAddress(c);
			selfip =  Utilities.toIPString (wf.getConnectionInfo().getIpAddress());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private InetAddress getBroadcastAddress (Context c) throws IOException 
	{
		wf = ((WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
		DhcpInfo dhcp = wf.getDhcpInfo();
		if  (dhcp == null) 
		{
			return null;
		}
		else
		{
			int broadcast =  (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
			byte[] quads = new byte[4];
			for  (int k = 0; k < 4; k++)
				quads[k] =  (byte)  ( (broadcast >> k * 8) & 0xFF);
			return InetAddress.getByAddress (quads);
		}
	}
}
