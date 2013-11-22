package com.example.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

//import android.util.Log;

/*
 * This class tries to send a broadcast UDP packet over your wifi network to discover the boxee service.
 */

@SuppressLint ("DefaultLocale")
public class Discoverer extends Thread {
  private static final String TAG = "Discovery";
  private static final String REMOTE_KEY = "b0xeeRem0tE!";
  private static final int DISCOVERY_PORT = 7505;
  private static final int TIMEOUT_MS = 1000;

  // TODO: Vary the challenge, or it's not much of a challenge :)
  private static final String mChallenge = "myvoice";
  private WifiManager mWifi;
  private MainActivity m;
  public String recivedData = "";
  interface DiscoveryReceiver {
    void addAnnouncedServers (InetAddress[] host, int port[]);
  }

  Discoverer (WifiManager wifi, MainActivity mainActivity) {
    mWifi = wifi;
    m = mainActivity;
  }

  public void run () {
    try {
      Log.d (TAG, "Started Run");
      DatagramSocket socket = new DatagramSocket (DISCOVERY_PORT);
      
      //cket.setBroadcast (true);
      //socket.setSoTimeout (TIMEOUT_MS);
      //sendDiscoveryRequest (socket);
      listenForResponses (socket);
    } catch  (IOException e) {
      Log.e (TAG, "Could not send discovery request", e);
    }
  }

  /**
   * Send a broadcast UDP packet containing a request for boxee services to
   * announce themselves.
   *
   * @throws IOException
   */
  private void sendDiscoveryRequest (DatagramSocket socket) throws IOException {
    String data = String
        .format (
            "<bdp1 cmd=\"discover\" application=\"iphone_remote\" challenge=\"%s\" signature=\"%s\"/>",
            mChallenge, getSignature (mChallenge));
    Log.d (TAG, "Sending data " + data);
    
    DatagramPacket packet = new DatagramPacket (data.getBytes (), data.length (),
        getBroadcastAddress (), DISCOVERY_PORT);
    socket.send (packet);
  }

  /**
   * Calculate the broadcast IP we need to send the packet along. If we send it
   * to 255.255.255.255, it never gets sent. I guess this has something to do
   * with the mobile network not wanting to do broadcast.
   */
  private InetAddress getBroadcastAddress () throws IOException {
	  if (mWifi==null){
		  Log.d (TAG, "mwifi is null");
	  }
    DhcpInfo dhcp = mWifi.getDhcpInfo ();
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

  /**
   * Listen on socket for responses, timing out after TIMEOUT_MS
   *
   * @param socket
   *          socket on which the announcement request was sent
   * @throws IOException
   */
  private void listenForResponses (DatagramSocket socket) throws IOException {
    byte[] buf = new byte[1024];
    Log.d (TAG, "server Started ");
    try {
      while  (true) {
    	  Log.d (TAG, "BROADCAST IP"+getBroadcastAddress ());
        DatagramPacket packet = new DatagramPacket (buf, buf.length);
        socket.receive (packet);
       //byte bytes []= packet.getData ();
       //int len = packet.getLength ();
       String requestString =new String (packet.getData (), 0, packet.getLength ());
       int ip = Integer.parseInt (requestString);
       String ipString = Utilities.toIPString (ip);
       
       //requestString
       String s = new String (packet.getData (), 0, packet.getLength ());
        Log.d (TAG, "Received response from pallavi this is the ip address from dhcp" + ipString+" IP of requestor : "+packet.getAddress ().toString () );
        recivedData=s;
        String data = String.format (
                    "<bdp1 cmd=\"discover\" application=\"MEHNAAZ SENDING\" challenge=\"%s\" signature=\"%s\"/>",
                    mChallenge, getSignature (mChallenge));
        DatagramPacket packet1 = new DatagramPacket (data.getBytes (), data.length (),
                InetAddress.getByName (ipString), DISCOVERY_PORT);
           
        socket.send (packet1);
            
            Log.d (TAG, "Sending YES to requestor " + data);
     //   Toast.makeText (m, s, Toast.LENGTH_SHORT);
      }
    } catch  (SocketTimeoutException e) {
     Log.d (TAG,e.toString ()); 	
      Log.d (TAG, "Receive timed out");
    }
  }

  /**
   * Calculate the signature we need to send with the request. It is a string
   * containing the hex md5sum of the challenge and REMOTE_KEY.
   *
   * @return signature string
   */
  private String getSignature (String challenge) {
    MessageDigest digest;
    byte[] md5sum = null;
    try {
      digest = java.security.MessageDigest.getInstance ("MD5");
      digest.update (challenge.getBytes ());
      digest.update (REMOTE_KEY.getBytes ());
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


