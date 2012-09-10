package os.letsplay.mongo.ops;

import os.letsplay.bson.BSON;
import os.letsplay.bson.BsonByteArray;
import os.letsplay.mongo.Messages;


public class OpUpdate extends CollMsg {
	
	public static final int OP_UPDATE = 2001;
	
	// flags
	public static int Upsert = 1;
	public static int MultiUpdate = 2;
	
	private Object query;
	public void setQuery(Object query) {
		this.query = query;
	}
	public Object getQuery() {
		return query;
	}
	
	private Object update;
	public void setUpdate(Object update) {
		this.update = update;
	}
	public Object getUpdate() {
		return update;
	}
	
	public OpUpdate(String collection, Object query, Object update) {
		this(collection,query,update,true);
	}
	
	public OpUpdate(String collection, Object query, Object update, boolean safe) {
		this(collection,query,update,safe,2);
	}
	
	public OpUpdate(String collection, Object query, Object update, boolean safe, int flags) {
		super(collection,safe,flags);
		this.query 	= query;
		this.update = update;
	}
	
	public OpUpdate(byte[] bytes) throws Exception {
		super(bytes);
	}
	
	@Override
	public int getCode() {
		return Messages.OP_UPDATE.getCode();
	}
	
	@Override
	protected void writeBody(BsonByteArray bin) throws Exception  {
		if(getQuery()!=null && getUpdate()!=null){
			bin.writeInt( 0 );//ZERO
			bin.writeCString( getCollection() );
			bin.writeInt( getFlags() );
			bin.writeBytes( BSON.encode( getQuery() ));
			bin.writeBytes( BSON.encode( getUpdate() ));
		}else{
			throw new Exception("Invalid Query/Update objects");
		}
	}
	
	@Override
	protected void readBody(BsonByteArray bin) {
		bin.readInt();//ZERO
		setCollection(bin.readCString());
		setFlags(bin.readInt());
		setQuery(BSON.decode(bin.readBytes()));
		setUpdate(BSON.decode(bin.readBytes()));
	}
	
}
