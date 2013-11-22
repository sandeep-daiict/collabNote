package com.example.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("DrawAllocation")
public class CanvasSurface extends View 
{ 
	static Paint paint = new Paint();
	static Paint erasePaint = new Paint();
	static String textVal = "";
		
	float X, Y, X1, Y1;
	
	static Drawables mode = Drawables.LINE;
	
	boolean drawNow = false;
	boolean drawOther = false;
	
	Bitmap bitmap;
	Canvas can;
	
	static {
		paint.setColor(Color.BLACK);
	    paint.setStyle(Style.STROKE);
	    paint.setTextSize(28);
	    
	    paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
	    
	    paint.setStrokeWidth(4);
		paint.setAntiAlias(true);
		
		erasePaint.setStyle(Style.FILL);
		erasePaint.setColor(Color.WHITE);
	}
	
	private void init(){
		
	}
	
	public CanvasSurface(Context context) {
		super(context);
		init();
	}

	public CanvasSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CanvasSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void drawEvent(Message draw) 
	{
		MessageType mt = draw.getType();

		float x, y, x1, y1;
		int color = Integer.parseInt(draw.getString("c"));
		int textSize = Integer.parseInt(draw.getString("tsize"));
		int lineSize = Integer.parseInt(draw.getString("lsize"));
		
		Paint p1 = new Paint();
		p1.setColor(color);
		p1.setTextSize(textSize);
		p1.setStrokeWidth(lineSize);
		p1.setStyle(Style.STROKE);
		
		switch(mt) 
		{
			case CANVAS_EVENT_LINE:
				x = Float.parseFloat(draw.getString("x1"));
				y = Float.parseFloat(draw.getString("y1"));
				x1 = Float.parseFloat(draw.getString("x2"));
				y1 = Float.parseFloat(draw.getString("y2"));
				
				can.drawLine(x, y, x1, y1, p1);
				break;
			case CANVAS_EVENT_CIRCLE :
				x = Float.parseFloat(draw.getString("x1"));
				y = Float.parseFloat(draw.getString("y1"));
				x1 = Float.parseFloat(draw.getString("x2"));
				y1 = Float.parseFloat(draw.getString("y2"));
				RectF rect = new RectF(x, y, x1, y1);
				can.drawOval(rect, p1);
				break;
			case CANVAS_EVENT_RECT:
				x = Float.parseFloat(draw.getString("x1"));
				y = Float.parseFloat(draw.getString("y1"));
				x1 = Float.parseFloat(draw.getString("x2"));
				y1 = Float.parseFloat(draw.getString("y2"));
				can.drawRect(x, y, x1, y1, p1);
				break;
			case CANVAS_EVENT_TEXT:
				x = Float.parseFloat(draw.getString("x1"));
				y = Float.parseFloat(draw.getString("y1"));
				String text = draw.getString("text");
				can.drawText(text, x, y, p1);
				break;
			case CANVAS_EVENT_ERASER:
				x = Float.parseFloat(draw.getString("x1"));
				y = Float.parseFloat(draw.getString("y1"));
				x1 = Float.parseFloat(draw.getString("x2"));
				y1 = Float.parseFloat(draw.getString("y2"));
				can.drawRect(x, y, x1, y1, erasePaint);
				break;
		default:
			break;
		}
		
		drawOther = true;
		invalidate();
	}
	
	@Override
	public void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		
		if(bitmap == null) 
		{
			bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
			can = new Canvas(bitmap);
			can.drawRect(0, 0, getWidth(), getHeight(), erasePaint);
		}
		
		if(drawOther) {
			canvas.drawBitmap(bitmap, new Matrix(), paint);		
			drawOther = false;
		}
		
		if(!drawNow)
			return;
		
		int color = paint.getColor();
		float lineSize = paint.getStrokeWidth();
		float textSize = paint.getTextSize();
		paint.setStyle(Style.STROKE);
		
		Message message = null;
		switch(mode) 
		{
			case LINE: 
				can.drawLine(X, Y, X1, Y1, paint);
				message = Message.create_CANVAS_EVENT_LINE(X, Y, X1, Y1, color, (int)lineSize, (int)textSize);
				break;
			case CIRCLE :
				RectF rect = new RectF(X, Y, X1, Y1);
				message = Message.create_CANVAS_EVENT_CIRCLE(X, Y, X1, Y1, color, (int)lineSize, (int)textSize);
				can.drawOval(rect, paint);
				break;
			case RECTANGLE:
				can.drawRect(X, Y, X1, Y1, paint);
				message = Message.create_CANVAS_EVENT_RECT(X, Y, X1, Y1, color, (int)lineSize, (int)textSize);
				break;
			case TEXT:
				can.drawText(textVal, X1, Y1, paint);
				message = Message.create_CANVAS_EVENT_TEXT(X1, Y1, textVal, color, (int)lineSize, (int)textSize);
				break;
			case ERASER:
				can.drawRect(X, Y, X1, Y1, erasePaint);
				message = Message.create_CANVAS_EVENT_ERASER(X, Y, X1, Y1);
				break;
			case CLEAR:
				can.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), erasePaint);
				message = Message.create_CANVAS_EVENT_ERASER(0, 0, bitmap.getWidth(), bitmap.getHeight());
				break;
		default:
			break;
		}
		if(message!=null)
		{
			
			Log.d("Canvas", message.toJSONString());
			if(MainActivity.isMaster)
			{
				Iterator<String> itr = CanvasActivity.clientList.iterator();
				while(itr.hasNext())
				{	
					String ip =itr.next();
					Log.d("LIST", ip);
					Communicator.reply(Wifi.wf, Utilities.toInetAddress(ip), false, message.toJSONString());
				}
			}
			else
			{
				System.out.println("Server IP" + Global.serverIP);
				Communicator.reply(Wifi.wf, Global.serverIP, false, message.toJSONString());
			}
		}
		canvas.drawBitmap(bitmap, new Matrix(), paint);		
		drawNow = false;
	}
	
	/*synchronized protected void drawEvent() {
		switch(mode) {
			case LINE: 
				canvas.drawLine(X, Y, X1, Y1, paint);
				break;
			case CIRCLE :
				RectF rect = new RectF(X, Y, X1, Y1);
				canvas.drawOval(rect, paint);
				break;
			case RECTANGLE:
				canvas.drawRect(X, Y, X1, Y1, paint);
				break;
			case TEXT:
				canvas.drawText(textVal, X1, Y1, paint);
				break;
			case ERASER:
				canvas.drawRect(X, Y, X1, Y1, erasePaint);
				break;
		}
		
		//this.draw(canvas);
	}*/
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float curx = event.getX(); 
		float cury = event.getY();
		
		switch(event.getActionMasked()){
			case MotionEvent.ACTION_DOWN: 
				X = curx;
				Y = cury;
				break;
			case MotionEvent.ACTION_UP :
				X1 = curx;
				Y1 = cury;
				drawNow = true;
				break;
			
			case MotionEvent.ACTION_MOVE:
				drawOther = true;
				break;
				
		}
		invalidate();
		return true;
	}	
	public void saveBitmapToFile(String filename) {
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/collabnote/" + filename + ".png";
		Log.d("canvas", filePath);
		
		File file = new File(filePath);
		
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		
		try {
			FileOutputStream fout = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 85, fout);
			fout.flush();
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}