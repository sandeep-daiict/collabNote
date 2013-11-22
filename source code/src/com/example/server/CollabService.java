package com.example.server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
//import android.widget.Toast;

public class CollabService extends Service implements Runnable
{
    @Override
    public void onCreate()
    {
    	super.onCreate();
    	new Thread(this).start();
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startID)
    {   
       
    	//Toast.makeText(getApplicationContext(), "marleeee", Toast.LENGTH_SHORT).show();
    }

    public void run()
    {
    	DatagramSocket socket;
		try 
		{
			socket = new DatagramSocket(Global.port);
			//WifiManager mWifi= (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
	    	listenForResponses(socket);
		} 
		catch (SocketException e) 
		{			
			e.printStackTrace();
		} 
		catch (IOException e) 
		{	
			e.printStackTrace();
		}
    	

    }

	private void listenForResponses(DatagramSocket socket) throws IOException 
	{
		byte[] buf = new byte[1024];
		Log.d("Discovery", "Check if service is up");
	    
	    ArrayList <String> listOfIP = new ArrayList<String>();
		while (true) 
	    {	    	  
	    		DatagramPacket packet = new DatagramPacket(buf, buf.length);
	    		Log.d("Discovery", "Before a packet is received");
	    		socket.receive(packet);	      
	    		
	    		String jsonMsg = new String(packet.getData(), 0, packet.getLength());
	    		Log.d ("address",packet.getAddress().toString());
	    		Log.d ("Discovery", jsonMsg);

	    		Message received = Message.parseJSON (jsonMsg);
	    		Message response = null;
	    			    	
	    		
	    		if (received.getType() == MessageType.GET_LIST_BROADCAST)
	    		{
	    			String ip = received.getString ("ip");
	    			if (MainActivity.isMaster) 
	    			{
	    				System.out.println("GET_LIST_BROADCAST received");
	    				response = received;
	    			}
	    		}
	    		else if (received.getType() == MessageType.SEND_SERVER_STATUS 
	    				&& DiscoverClient.waitingForResponse)
	    		{
	    			String ip = received.getString ("ip");
	    			Log.d ("Client", ip);
	    			listOfIP.add (ip);
	    			response = received;
    				System.out.println("SEND_SERVER_STATUS received");
    				DiscoverClient.waitingForResponse = false;
	    		}
	    		else if (received.getType() == MessageType.JOIN_PERMISSION_ASK) 
	    		{
	    			String ip = received.getString ("ip");
	    			Log.d ("Server", "message to be received by server for join request : " + ip);
	    	    	System.out.println ("service J: received");
	    	    	response = received;
	    		}
	    		else if (received.getType() == MessageType.JOIN_PERMISSION_RESPONSE)
	    		{
	    			String ip = received.getString ("ip");
	    			Log.d ("Client", ip);
	    			System.out.println ("final client accept");
	    			response = received;
	    		}
	    		else if (received.getType().typeId>=4)
	    		{
	    			Log.d ("CanvasLinet", received.toJSONString());
	    			response=received;
	    		}
	    		if(response != null) 
	    		{
		    		Intent intent = new Intent("msg");               
	                intent.putExtra ("msg", received.toJSONString());
	                Log.d ("RESPONSE","sending this " + response.toJSONString());
	                sendBroadcast (intent);
	    		}
	    }
	}
}