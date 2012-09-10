package os.letsplay.mongo.ops;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import os.letsplay.bson.BsonByteArray;
import os.letsplay.mongo.Message;


public abstract class BaseMsg implements Message {
	
	private static final Map<String,byte[]> checkMsgs = new HashMap<String, byte[]>();
	public static final byte[] getCheckMsg(String db) throws Exception {
		if(!checkMsgs.containsKey(db)){
			Map<String,Object> query = new HashMap<String, Object>();
			query.put("getlasterror", 1);
			OpQuery q = new OpQuery(db+".$cmd", query, null, -1, 0, 0);
			checkMsgs.put(db, q.toBinaryMsg());
		}
		return checkMsgs.get(db);
	}
	
	private static final AtomicInteger ID = new AtomicInteger(0);
	public synchronized static int id(){
		return ID.incrementAndGet();
	}
	
	private int requestId;
	private int responseTo;
	
	protected byte[] bytes;
	
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	public int getResponseTo() {
		return responseTo;
	}
	public void setResponseTo(int responseTo) {
		this.responseTo = responseTo;
	}
	
	public int getLength() {
		return (bytes!=null?bytes.length:0);
	}
	
	public BaseMsg(){
		this.requestId = this.responseTo = BaseMsg.id();
	}
	
	public BaseMsg(byte[] bytes) throws Exception{
		this.bytes = bytes;
		fromBinaryMsg(this.bytes);
	}
	
	private void writeHeader(BsonByteArray bin){
		bin.writeInt( 0 );
		bin.writeInt( getRequestId() );
		bin.writeInt( getRequestId() );
		bin.writeInt( getCode() );
	}
	
	private void readHeader(BsonByteArray bin){
		int size = bin.readInt();
		if(size<=bytes.length){
			requestId 	= bin.readInt();
			responseTo 	= bin.readInt();
			if(getCode() != bin.readInt()){
				System.err.println("Invalid Size");
			}
		}else{
			System.err.println("Invalid Size");
		}
	}
	
	private void writeLength(BsonByteArray bin){
		int length = bin.position();
		bin.position(0);
		bin.writeInt( length );
		bin.position(length);
	}
	
	public byte[] toBinaryMsg() throws Exception {
		if(bytes == null){
			BsonByteArray bin = new BsonByteArray();
			writeHeader(bin);
			writeBody(bin);
			writeLength(bin);
			writeFooter(bin);
			bytes = bin.array();
		}
		return bytes;
	}
	
	public void fromBinaryMsg(byte[] msg) throws Exception {
		BsonByteArray bin = new BsonByteArray();
		bin.writeBytes(msg);
		bin.position(0);
		readHeader(bin);
		readBody(bin);
	}
	
	@Override
	public boolean equals(Object obj) {
		Boolean res = true;
		if(obj != null && (obj.getClass().equals(this.getClass()))){
			BaseMsg target = (BaseMsg)obj;
			res = target!=null;
			res = this.getRequestId()==target.getRequestId();
			res = this.getResponseTo()==target.getResponseTo();
		}else{
			res = false;
		}
		return res;
	}
	abstract public int getCode();
	abstract protected void readBody(BsonByteArray bin) throws Exception;
	abstract protected void writeBody(BsonByteArray bin) throws Exception;
	protected void writeFooter(BsonByteArray bin) throws Exception{};
	abstract public Boolean isSafe();
}
