package com.example.server;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
//import android.widget.Toast;

public class MainActivity extends Activity 
{
	public static boolean isMaster = false;
	WifiManager wifi = null;
	//public DataReciver messagehandler;

	@SuppressWarnings("unused")
	@Override	
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		Wifi w = new Wifi(this);
		
		Intent i = new Intent (this, CollabService.class);
		startService (i);
		setTitle  ("CollabNote");
		setContentView (R.layout.activity_main);
		getActionBar().hide();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.main, menu);
		
		return true;
	}
	
	public void onPause () 
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
    
	
    
    // called by client
    public void requestJoinServer (View v)
	{
    	String broadcastMessage = Message.create_GET_LIST_BROADCAST (Wifi.selfip).toJSONString();
    	Communicator.reply (Wifi.wf, null, true, broadcastMessage);
		startActivity (new Intent (this,ActiveSessionActivity.class));
	}
	
	
	// called by server
	public void sendResponseToClient (View v)
	{
		isMaster = true;
		startActivity (new Intent (this,CanvasActivity.class));
	}	

	private BroadcastReceiver onBroadcast = new BroadcastReceiver () {
        @Override
        public void onReceive (Context ctxt, Intent i) 
        {
        		Bundle bdl = i.getExtras ();
                String message = bdl.getString ("msg");                    
        }
};
}
