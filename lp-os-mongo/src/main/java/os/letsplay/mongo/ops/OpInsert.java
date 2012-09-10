package os.letsplay.mongo.ops;

import java.util.ArrayList;

import os.letsplay.bson.BSON;
import os.letsplay.bson.BsonByteArray;
import os.letsplay.mongo.Messages;



public class OpInsert extends CollMsg {
	
	public Object[] documents;
	
	public void setDocuments(Object[] documents) {
		this.documents = documents;
	}
	
	public Object[] getDocuments() {
		return documents;
	}
	
	public OpInsert(String collection, Object[] documents) {
		this(collection,documents,true);
	}
	
	public OpInsert(String collection, Object[]  documents, boolean safe) {
		this(collection,documents,safe,1);
	}
	
	public OpInsert(String collection, Object[]  documents, boolean safe, int flags) {
		super(collection,safe,flags);
		this.documents = documents;
	}
	
	public OpInsert(byte[] bytes) throws Exception {
		super(bytes);
	}
	
	@Override
	public int getCode() {
		return Messages.OP_INSERT.getCode();
	}
	
	@Override
	protected void writeBody(BsonByteArray bin)  {
		bin.writeInt( getFlags() ); 
		bin.writeCString( getCollection() );
		for( Object document : documents ) {
			byte[] bytes = BSON.encode( document );
			bin.writeBytes(bytes);
		}
	}
	
	@Override
	protected void readBody(BsonByteArray bin) {
		setFlags(bin.readInt());
		setCollection(bin.readCString());
		ArrayList<Object> docs = new ArrayList<Object>();
		while(true){
			try{
				byte[] docBytes = bin.readBytes();
				if(docBytes.length>0){
					docs.add(BSON.decode(docBytes));
				}else{
					break;
				}
			}catch(Exception ex){
				break;
			}
		}
		documents = docs.toArray();
	}
	
}