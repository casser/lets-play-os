package os.letsplay.mongo.ops;

import os.letsplay.bson.BsonByteArray;

abstract class CollMsg extends BaseMsg {
	
	private String collection; // cstring
	private int flags ;
	private boolean safe ;
	
	public CollMsg(byte[] bytes) throws Exception {
		super(bytes);
	}
	
	public CollMsg(String collection,boolean safe, int flags) {
		super();
		this.collection = collection;
		this.flags 		= flags;
		this.safe		= safe;
	}
	
	public CollMsg(String collection, int flags) {
		super();
		this.collection = collection;
		this.flags 		= flags;
		this.safe		= false;
	}
	
	
	public String getCollection() {
		return collection;
	}
	
	public String getDatabase() {
		return collection.split("\\.")[0];
	}
	
	public void setCollection(String collection) {
		this.collection = collection;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	@Override
	public Boolean isSafe(){
		return safe;
	}
	
	protected void writeFooter(BsonByteArray bin) throws Exception{
		if(isSafe()){
			bin.writeBytes(BaseMsg.getCheckMsg(getDatabase()));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		Boolean res = super.equals(obj);

		CollMsg target = (CollMsg)obj;
		res = res && target!=null;
		
		res = res && this.getCollection()!=null; 
		res = res && target.getCollection()!=null; 
		res = res && this.getCollection().equals(target.getCollection()); 
		res = res && this.getFlags()==target.getFlags(); 
		
		return res;
	}
}
