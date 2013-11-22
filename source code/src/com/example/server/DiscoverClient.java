package com.example.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.net.DhcpInfo;
import android.text.style.LeadingMarginSpan;
import android.util.Log;



public class DiscoverClient extends Thread 
{
	public static int num=0;
	private static final String TAG = "Discovery";

	public static boolean waitingForResponse = false;
	public Response response = null;
	
	interface DiscoveryReceiver {
		void addAnnouncedServers (InetAddress[] host, int port[]);
	}


	DiscoverClient (Response response) {
		this.response= response; 
	}


	public void run () {

		try {
			Log.d (TAG, "Started Run");
			DatagramSocket socket = new DatagramSocket ();

			if (response.getIp () == null) 
			{
				socket.setBroadcast (true);      
				sendBroadcast (socket);
				waitingForResponse = true;
				
			}
			else
			{
				//socket.setBroadcast (false);      
				sendToMachine (socket, response.getIp ());
			}
		}
		catch  (IOException e) 
		{
			Log.e (TAG, "Could not send discovery request", e);
		}
	}

	// corrected broadcast message	
	private void sendBroadcast (DatagramSocket socket) throws IOException 
	{
		String data = response.getData ();
		Log.d (TAG, "Sending data " + data);
		System.out.println("TAG, Sending data " + data);
		DatagramPacket packet = new DatagramPacket (data.getBytes (), data.length(),
				getBroadcastAddress (), Global.port);
		socket.send (packet);
	}

	private void sendToMachine (DatagramSocket socket,InetAddress ip) throws IOException 
	{
		String data=response.getData ();
		Log.d (TAG, "Sending data " + data);
		
		DatagramPacket packet = new DatagramPacket(data.getBytes (), 
				data.length (),ip, Global.port);
		socket.send (packet);
	}

	private InetAddress getBroadcastAddress () throws IOException {
		if (response.getmWifi ()==null){
			Log.d (TAG, "mwifi is null");
		}
		DhcpInfo dhcp = response.getmWifi ().getDhcpInfo ();
		if  (dhcp == null) {
			//Toast.makeText (getc, text, duration)
			Log.d (TAG, "Could not get dhcp info");
			return null;
		}
		else{
			Log.d (TAG, "Got dhcp info");
			int broadcast =  (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
			byte[] quads = new byte[4];
			for  (int k = 0; k < 4; k++)
				quads[k] =  (byte)  ( (broadcast >> k * 8) & 0xFF);
			return InetAddress.getByAddress (quads);
		}
	}

	private void listenForResponses (DatagramSocket socket) throws IOException {
		byte[] buf = new byte[1024];
		try {
			while  (true) {
				DatagramPacket packet = new DatagramPacket (buf, buf.length);
				socket.receive (packet);
				String s = new String (packet.getData (), 0, packet.getLength ());
				Log.d (TAG, "Received response " + s);
			}
		} catch  (SocketTimeoutException e) {
			Log.d (TAG,e.toString ()); 	
			Log.d (TAG, "Receive timed out");
		}
	}

	private String getSignature  (String challenge) {
		MessageDigest digest;
		byte[] md5sum = null;
		try {
			digest = java.security.MessageDigest.getInstance ("MD5");
			digest.update (challenge.getBytes ());
			md5sum = digest.digest ();
		} catch  (NoSuchAlgorithmException e) {
			e.printStackTrace ();
		}

		StringBuffer hexString = new StringBuffer ();
		for  (int k = 0; k < md5sum.length; ++k) {
			String s = Integer.toHexString ( (int) md5sum[k] & 0xFF);
			if  (s.length () == 1)
				hexString.append ('0');
			hexString.append (s);
		}
		return hexString.toString ();
	}


}