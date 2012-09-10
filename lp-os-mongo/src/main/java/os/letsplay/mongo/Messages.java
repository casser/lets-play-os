package os.letsplay.mongo;

import os.letsplay.mongo.ops.OpDelete;
import os.letsplay.mongo.ops.OpGetMore;
import os.letsplay.mongo.ops.OpInsert;
import os.letsplay.mongo.ops.OpKillCursors;
import os.letsplay.mongo.ops.OpQuery;
import os.letsplay.mongo.ops.OpReply;
import os.letsplay.mongo.ops.OpUpdate;


public enum Messages {
	
	OP_REPLY			(1, 	OpReply.class),
	OP_UPDATE			(2001, 	OpUpdate.class),
	OP_INSERT			(2002, 	OpInsert.class),
	OP_QUERY			(2004, 	OpQuery.class),
	OP_GET_MORE			(2005, 	OpGetMore.class),
	OP_DELETE			(2006, 	OpDelete.class),
	OP_KILL_CURSORS		(2007, 	OpKillCursors.class);
	
	private int code;
	private Class<?> type;
	
	public int getCode() {
		return code;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Message getInstance() throws Exception {
		return (Message) getType().getConstructor(new Class[]{int.class}).newInstance(getCode());
	}
	
	private Message getInstance(byte[] bytes) throws Exception {
		return (Message) getType().getConstructor(new Class[]{byte[].class}).newInstance(bytes);
	}
	
	private Messages(int code, Class<?> type) {
		this.code = code;
		this.type = type;
	}
	
	public static Messages valueOf(int code) throws Exception{
		switch (code) {
		case 1		: return OP_REPLY;
		case 2001	: return OP_UPDATE;
		case 2002	: return OP_INSERT;
		case 2004	: return OP_QUERY;
		case 2005	: return OP_GET_MORE;
		case 2006	: return OP_DELETE;
		case 2007	: return OP_KILL_CURSORS;
		default:
			throw new Exception("Unknown Operation <"+code+">");
		} 
	}
	
	public static Message valueOf(byte[] bytes) throws Exception{
		if(bytes.length>=4){
			int length = readInt(bytes,0);
			if(bytes.length>=length){
				return valueOf(readInt(bytes,12)).getInstance(bytes);
			}
		}
		throw new Exception("Invalid Message Data");
	}
	
	

	public static int readInt( byte[] data , int offset ) {
        int x = 0;
        x |= ( 0xFF & data[offset+0] ) << 0;
        x |= ( 0xFF & data[offset+1] ) << 8;
        x |= ( 0xFF & data[offset+2] ) << 16;
        x |= ( 0xFF & data[offset+3] ) << 24;
        return x;
    }
}
