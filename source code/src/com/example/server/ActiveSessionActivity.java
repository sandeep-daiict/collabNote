package com.example.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ActiveSessionActivity extends Activity 
{

	private final Context context = this; 
	
	WifiManager wifi = null;
	ArrayList <String> sessionList = new ArrayList <String>  ();
	ArrayAdapter <String> sessionAdapter;
	
	String selfIP;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) 
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_active_session);
		setTitle ("List Session");
		System.out.println("actice session activity entered");
		registerReceiver (onBroadcast, new IntentFilter ("msg"));
		setContentView (R.layout.activity_active_session);

		//uncomment for debug
		//Toast.makeText (this, "in Array Activity", Toast.LENGTH_SHORT).show ();
		wifi =  (WifiManager) this.getSystemService (Context.WIFI_SERVICE);
		
		selfIP = Utilities.toIPString (wifi.getConnectionInfo().getIpAddress());
		
		ListView sessionLinks =  (ListView) findViewById (R.id.sessionLinks);
		if (sessionLinks == null)
		{
			//uncomment for debug
			//Toast.makeText (this,"Session Link is null" , Toast.LENGTH_SHORT).show ();
		}
		sessionAdapter = new ArrayAdapter <String> (this, android.R.layout.simple_list_item_1, sessionList);
		sessionLinks.setAdapter  (sessionAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.active_session, menu);
		return true;
	} public void onPause () 
	{
		super.onPause ();
		unregisterReceiver (onBroadcast);
	}  
	@Override
	protected void onResume ()
	{
		super.onResume ();
		
		registerReceiver (onBroadcast, new IntentFilter ("msg"));
	};

	private BroadcastReceiver onBroadcast = new BroadcastReceiver () 
	{
		@Override
		public void onReceive (Context ctxt, Intent i) 
		{
			
			System.out.println("inside on receive of active session activity");
			Bundle bdl = i.getExtras ();
			String jsonMsg = bdl.getString ("msg");    
			Message message = Message.parseJSON (jsonMsg);
			//uncomment for debug
			//Toast.makeText (getApplicationContext (), message.toJSONString(), Toast.LENGTH_SHORT).show ();
			Log.d ("Client", "Received List");

			String ip = message.getString ("ip");
			
			if (message.getType() == MessageType.SEND_SERVER_STATUS) // status from server
			{
				Log.d("Client", message.getString ("ip"));
        		
				try 
				{
					Global.serverIP = InetAddress.getByName (ip);
				} 
				catch (UnknownHostException e) 
				{
					e.printStackTrace();
				}
				showAcceptConnectionDialog (Global.serverIP);			
			}
			else if (message.getType() == MessageType.JOIN_PERMISSION_RESPONSE) // server accepted join request 
			{
				Intent intent = new Intent (getApplicationContext(), CanvasActivity.class);
				intent.putExtra ("serverIP", ip);
				startActivity (intent);
			}
		}
	};

	
	private void showAcceptConnectionDialog (final InetAddress serverIP) 
	{
		LayoutInflater inflater = LayoutInflater.from (context);
    	View dialogView = inflater.inflate (R.layout.accept_connection_dialog, null);
    	System.out.println("active session act dialog");
    	//uncomment for debug
    	//Toast.makeText(getApplicationContext(), "inside show server on client", Toast.LENGTH_SHORT).show();
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (context);
    	alertDialogBuilder.setView (dialogView);
    	
    	System.out.println("active session act dialog 1");
    	alertDialogBuilder
    		.setCancelable (false)
    		.setPositiveButton ("OK", 
    				new DialogInterface.OnClickListener() 
    				{
						@Override
						public void onClick (DialogInterface dialog, int id) 
						{
							Log.d ("Client", "sending join request from client to the server");
					    	System.out.println("active session act dialog 2");
							sendJoinRequest (serverIP);
						}
					})
			.setNegativeButton ("Cancel", 
					new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick (DialogInterface dialog, int id) 
						{
							dialog.cancel();
							returnToMainActivity ();
						}
					});
    	
    	AlertDialog acceptConnectionDialog = alertDialogBuilder.create();
    	acceptConnectionDialog.show();
	}

	private void sendJoinRequest (InetAddress serverIP) 
	{
		String selfIP = Utilities.toIPString (wifi.getConnectionInfo().getIpAddress());
		String joinRequest = Message.create_JOIN_PERMISSION_ASK (selfIP).toJSONString();
		
		Response response = new Response ( (WifiManager) this.getSystemService 
				(Context.WIFI_SERVICE), serverIP, true, joinRequest);
		System.out.println("sending J to server");
		new DiscoverClient (response).start();
	}
	
	
	private void returnToMainActivity () 
	{
		Intent intent = new Intent (this, MainActivity.class);
		startActivity (intent);
	}	

}
