package com.example.server;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message 
{
	private Map<String, String> data;
	private MessageType type;
	static private JSONParser parser = new JSONParser();
	
	private Message(MessageType mType) {
		type = mType;
		data = new HashMap<String, String>();
	}
	
	public String getString(String key)
	{
		return data.get(key);
	}
	
	public String[] getArray(String key)
	{
		return data.get(key).split(",");
	}

	public MessageType getType() 
	{
		return type;
	}
	
	public static synchronized Message parseJSON(String json){
		Message msg = null;
		
		try {
			JSONObject obj = (JSONObject)parser.parse(json);
			
			MessageType m = MessageType.getType((Long)obj.get("t"));
			
			msg = new Message(m);
			JSONObject dat = (JSONObject)obj.get("d");
			
			switch(m){
				case GET_LIST_BROADCAST: 
						msg.data = parseGetListBroadcast(dat); 
						break;
				case SEND_SERVER_STATUS:
						msg.data = parseSendServerStatus (dat); 
						break;
				case JOIN_PERMISSION_ASK: 
						msg.data = parseJoinPermissionAsk(dat);
						break;
						
				case JOIN_PERMISSION_RESPONSE: 
						msg.data = parseJoinPermissionResponse(dat);
						break;
						
				case CANVAS_EVENT_LINE: 
						msg.data = parseCanvasEventLine(dat);
						break;
						
				case CANVAS_EVENT_CIRCLE:
						msg.data = parseCanvasEventCircle(dat);
						break;
						
				case CANVAS_EVENT_RECT:
						msg.data = parseCanvasEventRect(dat);
						break;
						
				case CANVAS_EVENT_TEXT:
						msg.data = parseCanvasEventText(dat);
						break;
				
				case CANVAS_EVENT_FREE:
						msg.data = parseCanvasEventFree(dat);
						break;
				
				case CANVAS_EVENT_ERASER:
						msg.data = pareseCanvasEventEraser(dat);
						break;
						
				case OTHER: 
						break;
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return msg;
		
	}
	
	private static Map<String, String> pareseCanvasEventEraser(JSONObject dat) {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("x1", (String)dat.get("x1"));
		map.put("x2", (String)dat.get("x2"));
		map.put("y1", (String)dat.get("y1"));
		map.put("y2", (String)dat.get("y2"));
		map.put("c", (String)dat.get("c"));
		map.put("lsize", (String)dat.get("lsize"));
		map.put("tsize", (String)dat.get("tsize"));
		
		return map;
	}

	private static Map<String, String> parseGetListBroadcast(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("ip", (String)dat.get("ip"));
		
		return map;
	}
	
	private static Map<String, String> parseSendServerStatus(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("ip", (String)dat.get("ip"));
		
		return map;
	}
	
	
	private static Map<String, String> parseJoinPermissionAsk(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("ip", (String)dat.get("ip"));
		
		return map;
	}
	
	private static Map<String, String> parseJoinPermissionResponse(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("ip", (String)dat.get("ip"));
		
		return map;
	}
	
	private static Map<String, String> parseCanvasEventLine(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("x1", (String)dat.get("x1"));
		map.put("x2", (String)dat.get("x2"));
		map.put("y1", (String)dat.get("y1"));
		map.put("y2", (String)dat.get("y2"));
		map.put("c", (String)dat.get("c"));
		map.put("lsize", (String)dat.get("lsize"));
		map.put("tsize", (String)dat.get("tsize"));
		
		return map;
	}
	
	private static Map<String, String> parseCanvasEventCircle(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("x1", (String)dat.get("x1"));
		map.put("x2", (String)dat.get("x2"));
		map.put("y1", (String)dat.get("y1"));
		map.put("y2", (String)dat.get("y2"));
		map.put("c", (String)dat.get("c"));
		map.put("lsize", (String)dat.get("lsize"));
		map.put("tsize", (String)dat.get("tsize"));
		
		return map;
	}
	
	private static Map<String, String> parseCanvasEventRect(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("x1", (String)dat.get("x1"));
		map.put("x2", (String)dat.get("x2"));
		map.put("y1", (String)dat.get("y1"));
		map.put("y2", (String)dat.get("y2"));
		map.put("c", (String)dat.get("c"));
		map.put("lsize", (String)dat.get("lsize"));
		map.put("tsize", (String)dat.get("tsize"));
		
		return map;
	}
	
	private static Map<String, String> parseCanvasEventText(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("x1", (String)dat.get("x1"));
		map.put("y1", (String)dat.get("y1"));
		map.put("text", (String)dat.get("text"));
		map.put("c", (String)dat.get("c"));
		map.put("lsize", (String)dat.get("lsize"));
		map.put("tsize", (String)dat.get("tsize"));
		
		return map;
	}
	
	private static Map<String, String> parseCanvasEventFree(JSONObject dat){
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("x1", (String)dat.get("x1"));
		map.put("y1", (String)dat.get("y1"));
		
		return map;
	}
	
	public static Message create_GET_LIST_BROADCAST(String ip){
		Message m = new Message(MessageType.GET_LIST_BROADCAST);
		m.data.put("ip", ip);
		return m;
	}
	
	public static Message create_SEND_SERVER_STATUS(String ip){
		Message m = new Message(MessageType.SEND_SERVER_STATUS);
		m.data.put("ip", ip);
		return m;
	}
	
	public static Message create_JOIN_PERMISSION_ASK(String ip){
		Message m = new Message(MessageType.JOIN_PERMISSION_ASK);
		m.data.put("ip", ip);
		return m;
	}
	
	public static Message create_JOIN_PERMISSION_RESPONSE(String ip){
		Message m = new Message(MessageType.JOIN_PERMISSION_RESPONSE);
		m.data.put("ip", ip);
		return m;
	}
	
	public static Message create_CANVAS_EVENT_LINE(float x1, float y1, float x2, float y2,
													int color, int lineSize, int textSize){
		
		Message m = new Message(MessageType.CANVAS_EVENT_LINE);
		m.data.put("ip", Wifi.selfip);
		m.data.put("x1", "" + x1);
		m.data.put("x2", "" + x2);
		m.data.put("y1", "" + y1);
		m.data.put("y2", "" + y2);
		
		m.data.put("c", "" + color);
		m.data.put("lsize", "" + lineSize);
		m.data.put("tsize", "" + textSize);
		return m;
	}
	
	public static Message create_CANVAS_EVENT_CIRCLE(float x, float y, float x1, float y1, 
														int color, int lineSize, int textSize){
		Message m = new Message(MessageType.CANVAS_EVENT_CIRCLE);
		m.data.put("ip", Wifi.selfip);
		m.data.put("x1", "" + x);
		m.data.put("y1", "" + y);
		m.data.put("x2", "" + x1);
		m.data.put("y2", "" + y1);
		
		m.data.put("c", "" + color);
		m.data.put("lsize", "" + lineSize);
		m.data.put("tsize", "" + textSize);
		return m;
	}
	
	public static Message create_CANVAS_EVENT_TEXT(float x, float y, String text,
													int color, int lineSize, int textSize){
		Message m = new Message(MessageType.CANVAS_EVENT_TEXT);
		m.data.put("ip", Wifi.selfip);
		m.data.put("x1", "" + x);
		m.data.put("y1", "" + y);
		m.data.put("text", text);
		
		m.data.put("c", "" + color);
		m.data.put("lsize", "" + lineSize);
		m.data.put("tsize", "" + textSize);
		return m;
	}
	
	public static Message create_CANVAS_EVENT_RECT(float x1, float y1, float x2, float y2,
													int color, int lineSize, int textSize){
		Message m = new Message(MessageType.CANVAS_EVENT_RECT);
		m.data.put("ip", Wifi.selfip);
		m.data.put("x1", "" + x1);
		m.data.put("x2", "" + x2);
		m.data.put("y1", "" + y1);
		m.data.put("y2", "" + y2);
		
		m.data.put("c", "" + color);
		m.data.put("lsize", "" + lineSize);
		m.data.put("tsize", "" + textSize);
		return m;
	}
	
	public static Message create_CANVAS_EVENT_FREE(int []x, int []y){
		Message m = new Message(MessageType.CANVAS_EVENT_LINE);
		
		String xs = "";
		m.data.put("ip", Wifi.selfip);
		for(int i =0 ; i < x.length - 1; i++)
			xs += (x[i]+",");
		xs+=(x[x.length-1]);
		
		String ys = "";
		
		for(int i =0 ; i < y.length - 1; i++)
			ys += (y[i]+",");
		ys+=(y[y.length-1]);
		
		m.data.put("x", xs);
		m.data.put("y", ys);
		
		return m;
	}
	
	public static Message create_CANVAS_EVENT_ERASER(float x1, float y1, float x2, float y2) {
		Message m = new Message(MessageType.CANVAS_EVENT_ERASER);
		m.data.put("ip", Wifi.selfip);
		m.data.put("x1", "" + x1);
		m.data.put("x2", "" + x2);
		m.data.put("y1", "" + y1);
		m.data.put("y2", "" + y2);
	
		m.data.put("c", "" + 0);
		m.data.put("lsize", "" + 0);
		m.data.put("tsize", "" + 0);
	
		return m;
	}
	
	@SuppressWarnings("unchecked")
	public String toJSONString(){
		JSONObject js = new JSONObject();
		js.put("t", type.getTypeId());
		
		JSONObject d = new JSONObject();
		switch(type){
			case GET_LIST_BROADCAST:
				d.put("ip", data.get("ip"));
				break;
				
			case SEND_SERVER_STATUS:
				d.put("ip", data.get("ip"));
				break;	
				
			case JOIN_PERMISSION_ASK:
				d.put("ip", data.get("ip"));
				break;
			
			case JOIN_PERMISSION_RESPONSE:
				d.put("ip", data.get("ip"));
				break;
				
			case CANVAS_EVENT_CIRCLE:
				d.put("ip", data.get("ip"));
				d.put("x1", data.get("x1"));
				d.put("y1", data.get("y1"));
				d.put("x2", data.get("x2"));
				d.put("y2", data.get("y2"));
				d.put("c", data.get("c"));
				d.put("lsize", data.get("lsize"));
				d.put("tsize", data.get("tsize"));
				break;
			
			case CANVAS_EVENT_LINE:
							
			case CANVAS_EVENT_RECT:
				d.put("ip", data.get("ip"));
				d.put("x1", data.get("x1"));
				d.put("x2", data.get("x2"));
				d.put("y1", data.get("y1"));
				d.put("y2", data.get("y2"));
				d.put("c", data.get("c"));
				d.put("lsize", data.get("lsize"));
				d.put("tsize", data.get("tsize"));
				break;
			
			case CANVAS_EVENT_FREE:
				d.put("ip", data.get("ip"));
				d.put("x", data.get("x"));
				d.put("y", data.get("y"));
				break;
			
			case CANVAS_EVENT_TEXT:
				d.put("ip", data.get("ip"));
				d.put("x1", data.get("x1"));
				d.put("y1", data.get("y1"));
				d.put("text", data.get("text"));
				d.put("c", data.get("c"));
				d.put("lsize", data.get("lsize"));
				d.put("tsize", data.get("tsize"));
				break;
			
			case CANVAS_EVENT_ERASER:
				d.put("ip", data.get("ip"));
				d.put("x1", data.get("x1"));
				d.put("x2", data.get("x2"));
				d.put("y1", data.get("y1"));
				d.put("y2", data.get("y2"));
				d.put("c", data.get("c"));
				d.put("lsize", data.get("lsize"));
				d.put("tsize", data.get("tsize"));
				break;
		default:
			break;
		}
		
		js.put("d", d);
		return js.toJSONString();
	}
	
	public static void main(String []args){
		Message msg = create_CANVAS_EVENT_CIRCLE(1.34f, 52.1f, 22.6f, 18.9f, 12, 15, 19);
		
		System.out.println(msg.toJSONString());
	}
	
}
