package com.example.server;

public enum MessageType 
{
	GET_LIST_BROADCAST(0),
	SEND_SERVER_STATUS (1),
	JOIN_PERMISSION_ASK(2),
	JOIN_PERMISSION_RESPONSE(3),
	CANVAS_EVENT_LINE(4),
	CANVAS_EVENT_CIRCLE(5),
	CANVAS_EVENT_TEXT(6),
	CANVAS_EVENT_FREE(7),
	CANVAS_EVENT_RECT(8),
	CANVAS_EVENT_ERASER(9),
	OTHER(10);
	
	final int typeId;
	
	public int getTypeId(){
		return typeId;
	}
	
	private MessageType(int x){
		typeId = x;
	}
	
	public static MessageType getType(long x){
		for(MessageType m : MessageType.values()){
			if(m.typeId == x)
				return m;
		}
		
		return OTHER;
	}
}
