package os.letsplay.mongo.ops;

import os.letsplay.bson.BSON;
import os.letsplay.bson.BsonByteArray;
import os.letsplay.bson.BsonParseError;
import os.letsplay.mongo.Messages;

public class OpQuery extends CollMsg {
	
	
	
	public static final int OP_QUERY = 2004;
	
	// flags
	public static final int TailableCursor 	= 2;
	public static final int SlaveOK 		= 4;
	public static final int OplogReplay 	= 8; // ignore
	public static final int NoCursorTimeout = 16;
	public static final int AwaitData 		= 32;
	public static final int Exhaust 		= 64;
	
	private int skip;
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public int getSkip() {
		return skip;
	}
	
	private int limit;
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getLimit() {
		return limit;
	}
	
	public Object query;
	public void setQuery(Object query) {
		this.query = query;
	}
	public Object getQuery() {
		return query==null?BSON.EMPTY_DOC:query;
	}
	
	public Object selector;
	public void setSelector(Object selector) {
		this.selector = selector;
	}
	public Object getSelector() {
		return selector;
	}
	
	public OpQuery(String collection, Object query) {
		this(collection,query,null);
	}
	
	public OpQuery(String collection, Object query, Object selector) {
		this(collection,query,selector,1000,0,0);
	}
	
	public OpQuery(String collection, Object query, Object selector, int limit, int skip) {
		this(collection,query,selector,limit,skip,0);
	}
	
	public OpQuery(String collection, Object query, Object selector, int limit, int skip, int flags) {
		super(collection,flags);
		this.skip 		= skip;
		this.limit 		= limit;
		this.query 		= query;
		this.selector 	= selector;
	}
	
	public OpQuery(byte[] bytes) throws Exception{
		super(bytes);
	}
	
	@Override
	public int getCode() {
		return Messages.OP_QUERY.getCode();
	}
	
	@Override
	public Boolean isSafe() {
		return true;
	}
	
	@Override
	protected void writeBody(BsonByteArray bin)  {
		bin.writeInt( getFlags() );
		bin.writeCString( getCollection() );
		bin.writeInt( getSkip() );
		bin.writeInt( getLimit() );
		bin.writeBytes( BSON.encode(getQuery()));
		if( getSelector() != null ) {
			bin.writeBytes( BSON.encode(getSelector()));
		}
	}
	
	@Override
	protected void writeFooter(BsonByteArray bin) {
	}
	
	@Override
	protected void readBody(BsonByteArray bin) throws BsonParseError {
		setFlags(bin.readInt());
		setCollection(bin.readCString());
		setSkip(bin.readInt());
		setLimit(bin.readInt());
		setQuery(BSON.decode(bin.readBytes()));
		try{
			setSelector(BSON.decode(bin.readBytes()));
		}catch(Exception ex){
			setSelector(null);
		}
	}
		
}
