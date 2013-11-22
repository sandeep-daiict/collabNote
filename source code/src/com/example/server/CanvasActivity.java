package com.example.server;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.example.server.R;
import com.example.server.CanvasActivity;
import com.example.server.CanvasSurface;
import com.example.server.ColorPickerDialog;
import com.example.server.Drawables;
import com.example.server.ColorPickerDialog.OnColorChangedListener;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
//import android.widget.Toast;

public class CanvasActivity extends Activity implements OnColorChangedListener, OnValueChangeListener 
{
	//public DataReciver messagehandler;
	
	public static  HashSet<String> clientList = new HashSet<String>();
	ArrayList<String> note_List = new ArrayList<String>();
	private final Context context = this;
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setTitle ("Canvas");
		getActionBar().hide();
		registerReceiver (onBroadcast, new IntentFilter ("msg"));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canvas);
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.canvas, menu);
		return true;
		//new Discoverer ( (WifiManager)this.getSystemService (Context.WIFI_SERVICE),this).start ();
		
//		return true;
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

    public void callClient (View v) 
    {
    	Log.d ("CANVAS ACT", "inside CALL CLIENT");
	}
	
    public void callServer (View v) 
    {
		//new Discoverer ( (WifiManager)this.getSystemService (Context.WIFI_SERVICE),this).start ();		
	}	


    private BroadcastReceiver onBroadcast = new BroadcastReceiver () 
    {
        @Override
        public void onReceive (Context ctxt, Intent i) 
        {
        	Bundle bdl = i.getExtras ();
        	String jsonMsg = bdl.getString ("msg");

        	Message message = Message.parseJSON (jsonMsg);
        	if (message.getType() == MessageType.GET_LIST_BROADCAST)
        	{
        		String selfIP = Wifi.selfip;
        		try
        		{
        			InetAddress toIP = Utilities.toInetAddress (message.getString ("ip"));
        			String responseMsg = Message.create_SEND_SERVER_STATUS (selfIP).toJSONString();

        			Log.d ("Server", jsonMsg);
        			System.out.println ("N: SEND_SERVER_STATUS: sending");
        			
        			Communicator.reply (Wifi.wf, toIP, false, responseMsg);
        		} 
        		catch (Exception e)
        		{
        			e.printStackTrace ();
        		}
        	}
        	else if (message.getType() == MessageType.JOIN_PERMISSION_ASK)
        	{
        		Log.d ("Server", "yes you can join by server to client : " + bdl.getString ("msg"));
        		System.out.println("Server yes you can join by server to client : ");
        		showAcceptSessionRequestDialog (message.getString ("ip"));
        	}
        	else if (message.getType().typeId >= 4)
        	{
        		CanvasSurface can = (CanvasSurface) findViewById(R.id.canvas_view);
        		if(MainActivity.isMaster)
        		{
            		Log.d ("Server", "Master received drawing type message" );
        			Iterator<String> itr = CanvasActivity.clientList.iterator();
    				while(itr.hasNext())
    				{	
    					String ip =itr.next();
    					if(!ip.equals(message.getString("ip")))
    					{
    						Log.d("LIST:::", ip);
        					Communicator.reply(Wifi.wf, Utilities.toInetAddress(ip), false, message.toJSONString());
    					}
    					
    				}
        			can.drawEvent(message);
        		}
        		else
        		{
        			Log.d("Server", "In slave receive drawing ");
        			can.drawEvent(message);
        		}
        	}
        }
	};
	
	
	private void showAcceptSessionRequestDialog (final String clientIP) 
	{
		LayoutInflater inflater = LayoutInflater.from (context);
    	View dialogView = inflater.inflate (R.layout.accept_connection_dialog, null);
    	
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (context);
    	alertDialogBuilder.setView (dialogView);
    	
    	alertDialogBuilder
    		.setCancelable (false)
    		.setPositiveButton ("OK", 
    				new DialogInterface.OnClickListener() 
    				{
						@Override
						public void onClick (DialogInterface dialog, int id) 
						{
							if (clientList == null)
								clientList = new HashSet<String>();
							clientList.add (clientIP);
	                		Log.d ("Server", "reached inside onclick ");
	                		System.out.println("popup of server ");
							sendAcceptToClient  (clientIP);
						}
					})
			.setNegativeButton ("Cancel", 
					new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick (DialogInterface dialog, int id) 
						{
							dialog.cancel();
						}
					});
    	
    	AlertDialog joinSessionDialog = alertDialogBuilder.create();
    	joinSessionDialog.show();
	}

	protected void sendAcceptToClient (String clientIP) 
	{
		
		String joinResponse = Message.create_JOIN_PERMISSION_RESPONSE (Wifi.selfip).toJSONString();
		InetAddress toIP = Utilities.toInetAddress (clientIP);
		Communicator.reply (Wifi.wf, toIP, true, joinResponse);
	}
	
	
	public void action_pb_line(View view){
		CanvasSurface.mode = Drawables.LINE;
	}
	
	public void action_pb_circle(View view){
		CanvasSurface.mode = Drawables.CIRCLE;
	}
	
	public void action_pb_rect(View view){
		CanvasSurface.mode = Drawables.RECTANGLE;
	}
	
	public void action_pb_color(View view) {
		int color = PreferenceManager.
						getDefaultSharedPreferences(CanvasActivity.this).
									getInt("color", Color.BLACK);
		new ColorPickerDialog(CanvasActivity.this, CanvasActivity.this, color).show();
	}
	
	public void action_pb_text(View view) {
		CanvasSurface.mode = Drawables.TEXT;
		AlertDialog.Builder alert = 
				new AlertDialog.Builder(CanvasActivity.this);

		alert.setTitle("Text");
		 
		final EditText input = new EditText(CanvasActivity.this);
		alert.setView(input);
		
		alert.setPositiveButton("Ok", 
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						CanvasSurface.textVal = input.getText().toString();
					}
				});

		alert.setNegativeButton("Cancel", 
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});
		
		alert.show();
	}

	public void action_pb_erase(View view) {
		CanvasSurface.mode = Drawables.ERASER;
	}
	
	public void action_pb_line_size(View view) {
		final Dialog d = new Dialog(CanvasActivity.this);
        
		d.setTitle("Line Size");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(30); // max value 30
        np.setMinValue(1);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
                
        b1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CanvasSurface.paint.setStrokeWidth(np.getValue());
					d.dismiss();
				}    
			});
        
		b2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					d.dismiss(); // dismiss the dialog
				}    
			});
      d.show();
	}
	
	public void action_pb_text_size(View view) {
		final Dialog d = new Dialog(CanvasActivity.this);
        
		d.setTitle("Text Size");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(30); // max value 20
        np.setMinValue(1);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
                
        b1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CanvasSurface.paint.setTextSize(20 + np.getValue());
					d.dismiss();
				}    
			});
        
		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss(); // dismiss the dialog
			}    
		});
		d.show();
	}


	public void action_pb_note_add(View view) {
		LayoutInflater inflater = LayoutInflater.from (context);
		View dialogView = inflater.inflate (R.layout.add_note_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (context);
		alertDialogBuilder.setView (dialogView);

		final EditText newNote =  (EditText) dialogView.findViewById(R.id.textnoteid);

		alertDialogBuilder
		.setCancelable (false)
		.setPositiveButton ("OK", 
				new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick (DialogInterface dialog, int id) 
			{
				if (note_List == null)
					note_List = new ArrayList <String> ();

				note_List.add (newNote.getText().toString());
				Log.d ("Server", "reached inside onclick ");
				System.out.println("popup of server ");

			}
		})
		.setNegativeButton ("Cancel", 
				new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick (DialogInterface dialog, int id) 
			{
				dialog.cancel();
			}
		});

		AlertDialog joinSessionDialog = alertDialogBuilder.create();
		joinSessionDialog.show();

	}


	public void action_pb_note_view(View view) {

		LayoutInflater inflater = LayoutInflater.from (context);
		View dialogView = inflater.inflate (R.layout.view_note_dialog, null);

		System.out.println("our sticky note list is : ");

		for(String s: note_List) {
			System.out.println("List has " + s);
		}

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (context);
		alertDialogBuilder.setView (dialogView);
				
		System.out.println("note list is : " + R.id.noteList);
		
		
		ListView notesListView = (ListView) dialogView.findViewById (R.id.noteList);

		notesListView.setAdapter (new NoteAdapter(this, note_List));

		alertDialogBuilder
		.setCancelable (false)
		.setPositiveButton ("OK", 
				new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick (DialogInterface dialog, int id) 
			{
				Log.d ("Server", "reached inside onclick ");
				System.out.println("popup of server ");

			}
		});
	
			AlertDialog joinSessionDialog = alertDialogBuilder.create();
			joinSessionDialog.show();

	}

	public void action_pb_save(View view) {
		AlertDialog.Builder alert = 
				new AlertDialog.Builder(CanvasActivity.this);

		alert.setTitle("Save File name");

		final CanvasSurface cs = (CanvasSurface)CanvasActivity.this.findViewById(R.id.canvas_view);
		final EditText input = new EditText(CanvasActivity.this);
		alert.setView(input);
		
		alert.setPositiveButton("Ok", 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				if(cs == null)
					Log.d("SAVE1", "before save NULL");
				else
					Log.d("SAVE1", "before save");
				
				cs.saveBitmapToFile(value);
			}
		});

		alert.setNegativeButton("Cancel", 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});
		
		alert.show();
	}
	
	public void action_pb_clear(View view) {
		CanvasSurface.mode = Drawables.CLEAR;
	}
	
	@Override
	public void colorChanged(int color) {
		CanvasSurface.paint.setColor(color);
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) 
	{
		
	}
	
}
