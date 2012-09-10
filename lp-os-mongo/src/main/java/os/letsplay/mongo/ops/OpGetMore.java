package os.letsplay.mongo.ops;

import os.letsplay.bson.BsonByteArray;
import os.letsplay.mongo.Messages;

public class OpGetMore extends CollMsg {
	
	public int limit;
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getLimit() {
		return limit;
	}
	
	public Long cursorId;
	public void setCursorId(Long cursorId) {
		this.cursorId = cursorId;
	}
	public Long getCursorId() {
		return cursorId;
	}
	
	
	public OpGetMore(String collection, int limit, Long cursorId) {
		super(collection,0);
		this.limit 		= limit;
		this.cursorId 	= cursorId;
	}
	
	@Override
	public int getCode() {
		return Messages.OP_GET_MORE.getCode();
	}
	
	@Override
	public Boolean isSafe() {
		return true;
	}
		
	@Override
	protected void writeBody(BsonByteArray bin)  {
		bin.writeInt( 0 );// ZERO
		bin.writeCString( getCollection() );
		bin.writeInt( getLimit() );
		bin.writeLong( getCursorId() );
	}
	
	@Override
	protected void readBody(BsonByteArray bin) {
		bin.readInt();// ZERO
		setCollection(bin.readCString());
		setLimit(bin.readInt());
		setCursorId(bin.readLong());
	}
}